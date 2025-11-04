"""
Auto Compiler Script
Created by: Theodor Malmgren (GitHub: T-Mose)
Date: September 13, 2024 (Modified on current date for Go integration)
Description:
This script automates the process of cloning and compiling student assignments for both Java and Go.
For Java tasks (Task-1 to Task-19), the repository suffix is as before (with a special case for Task-19).
For Go tasks (Task-20 and above), the repository suffix becomes palinda-<n> (e.g. Task-20 → palinda-1).
It fetches student repositories, compiles their code, and optionally runs unit tests.
It also creates issues on GitHub and can generate detailed feedback using GPT analysis.
GitHub Repository: https://github.com/T-Mose/AutomatedGrading
"""

import os
import subprocess
import git
import pandas as pd
import threading
import sys
import time
from github import Github
from openai import OpenAI
from datetime import datetime, timedelta
import shutil
from dotenv import load_dotenv

FAIL_TIMER = 100
TIMEOUT_SECONDS = 60  # Timeout value for subprocesses (in seconds)

# Check for input parameters
if len(sys.argv) < 2 or not sys.argv[1].isdigit() or int(sys.argv[1]) <= 0:
    print("Usage: python script_name.py <task_number> [Y/N] [Y/N] [Y/N]")
    print("The first Y/N is for running unit tests (default: Y)")
    print("The second Y/N is for auto-creating issues (default: N)")
    print("The third Y/N is for using GPT analysis (default: N)")
    sys.exit(1)

task_number = sys.argv[1]  # The task number, e.g., "2"

# Determine language and repository suffix based on the task number
if int(task_number) < 20:
    language = "java"
    repo_suffix = "quicksort" if task_number == "19" else f"task-{task_number}"
else:
    language = "go"
    repo_suffix = f"palinda-{int(task_number) - 19}"

# Determine whether to run unit tests
run_tests = True  # Default is to run tests
if len(sys.argv) >= 3:
    run_tests_input = sys.argv[2].strip().upper()
    if run_tests_input == "Y":
        run_tests = True
    elif run_tests_input == "N":
        run_tests = False
    else:
        print("Invalid parameter for running unit tests. Use 'Y' or 'N'.")
        sys.exit(1)

# Determine whether to auto-create issues
auto_create_issues = False  # Default is not to create issues
if len(sys.argv) >= 4:
    create_issues_input = sys.argv[3].strip().upper()
    if create_issues_input == "Y":
        auto_create_issues = True
    elif create_issues_input == "N":
        auto_create_issues = False
    else:
        print("Invalid parameter for auto-creating issues. Use 'Y' or 'N'.")
        sys.exit(1)

# Determine whether to use GPT analysis
use_gpt = False  # Default is not to use GPT
if len(sys.argv) >= 5:
    use_gpt_input = sys.argv[4].strip().upper()
    if use_gpt_input == "Y":
        use_gpt = True
    elif use_gpt_input == "N":
        use_gpt = False
    else:
        print("Invalid parameter for GPT analysis. Use 'Y' or 'N'.")
        sys.exit(1)

# Load environment variables
load_dotenv(".env")
load_dotenv("API_TOKENS.env")  # For backwards compatibility

github_token = os.getenv("GITHUB_TOKEN")
openai_api_key = os.getenv("OPENAI_API_KEY")

if not github_token and not openai_api_key:
    print(
        "No API tokens found in environment. Issues will not be auto-created, GPT analysis will be disabled."
    )
    auto_create_issues = False
    use_gpt = False

# Initialize GitHub API client if token is available
if github_token and auto_create_issues:
    GITHUB_ENTERPRISE_URL = "https://gits-15.sys.kth.se"
    g = Github(base_url=GITHUB_ENTERPRISE_URL + "/api/v3", login_or_token=github_token)
else:
    g = None

# Initialize OpenAI API client if API key is available
if openai_api_key and use_gpt:
    OpenAI.api_key = openai_api_key
    client = OpenAI(api_key=openai_api_key)
else:
    if use_gpt:
        print("OpenAI API key not found or invalid. GPT analysis will be disabled.")
        use_gpt = False

# Path to the Excel file (assuming it's in the same directory as the script)
excel_path = os.path.join(os.getcwd(), "students.xlsx")
# For Java tasks, these JAR paths are used; for Go tasks they won’t be used.
junit_jar = os.path.join(os.getcwd(), "junit-4.13.2.jar")
hamcrest_jar = os.path.join(os.getcwd(), "hamcrest-core-1.3.jar")

# Base URL for the GitHub repos
base_url = "git@gits-15.sys.kth.se:inda-25/"

# Read student names from the Excel file (assuming names are in column A)
df = pd.read_excel(excel_path, usecols="A", header=None, engine="openpyxl")
student_names = df[df.columns[0]].dropna()

# Read non-participating student names from the Excel file (assuming names are in column A)
non_participating_path = os.path.join(os.getcwd(), "non_participating.xlsx")
non_participating_students = set()
if os.path.exists(non_participating_path):
    non_participating_df = pd.read_excel(
        non_participating_path, usecols="A", header=None, engine="openpyxl"
    )
    non_participating_students = set(
        non_participating_df[non_participating_df.columns[0]].dropna()
    )
    print(f"Found {len(non_participating_students)} non-participating students")

# Generate GitHub URLs based on student names and repo_suffix
urls = [f"{base_url}{name.strip()}-{repo_suffix}.git" for name in student_names]

# Folder to clone repositories
base_folder = os.path.join(os.getcwd(), "student_repos")
if os.path.exists(base_folder):
    shutil.rmtree(base_folder)
os.mkdir(base_folder)


# Function to detect if a Java file is a JUnit test
def is_junit_test(java_path):
    """
    Detect if a Java file is a JUnit test by checking for JUnit imports or @Test annotations.
    Returns True if the file is a test file, False otherwise.
    """
    try:
        with open(java_path, "r", encoding="utf-8") as f:
            content = f.read()
            return ("@Test" in content) or ("import org.junit." in content)
    except Exception:
        return False


# Function to run a command with timeout
def run_with_timeout(command, cwd, timeout):
    process = subprocess.Popen(
        command,
        cwd=cwd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        stdin=subprocess.DEVNULL,
        text=True,
        shell=False,
    )
    timer = threading.Timer(timeout, process.kill)
    try:
        timer.start()
        stdout, stderr = process.communicate()
        return process.returncode, stdout, stderr
    finally:
        timer.cancel()


# Java testing function remains unchanged
def run_java_class(
    repo_path, src_path, unit_test_files, test_class_names, unit_tests_dir
):
    java_files = []
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if not file.endswith(".java"):
                continue
            file_full_path = os.path.join(root, file)
            # Skip JUnit test files by checking content, not filename
            if is_junit_test(file_full_path):
                continue
            file_relative_path = os.path.relpath(file_full_path, src_path)
            java_files.append(file_relative_path)

    if not java_files:
        return "No Java files found to compile (excluding test files)", None

    compile_command = ["javac", "-d", repo_path] + java_files
    try:
        returncode, stdout, stderr = run_with_timeout(
            compile_command, cwd=src_path, timeout=TIMEOUT_SECONDS
        )
        if returncode != 0:
            unit_test_result = "Unit tests could not be run because compilation failed."
            return f"Compilation Failed: {stderr.strip()}", unit_test_result
    except Exception as e:
        return f"Compilation Error: {str(e)}", None

    if run_tests:
        classpath = os.pathsep.join([repo_path, junit_jar, hamcrest_jar])
        unit_test_relative_files = []
        for unit_test_file in unit_test_files:
            unit_test_relative_path = os.path.relpath(unit_test_file, unit_tests_dir)
            unit_test_relative_files.append(unit_test_relative_path)

        compile_test_command = (
            ["javac", "-cp", classpath] + unit_test_relative_files + ["-d", repo_path]
        )
        try:
            returncode, stdout, stderr = run_with_timeout(
                compile_test_command, cwd=unit_tests_dir, timeout=TIMEOUT_SECONDS
            )
            if returncode != 0:
                return "Success", f"Unit Test Compilation Failed: {stderr.strip()}"
        except Exception as e:
            return "Success", f"Unit Test Compilation Error: {str(e)}"

        try:
            run_test_command = [
                "java",
                "-cp",
                classpath,
                "org.junit.runner.JUnitCore",
            ] + test_class_names
            returncode, test_stdout, test_stderr = run_with_timeout(
                run_test_command, cwd=repo_path, timeout=TIMEOUT_SECONDS
            )
            if returncode == 0:
                unit_test_result = "Unit Tests Passed"
            else:
                unit_test_result = (
                    f"Unit Tests Failed:\n{test_stdout.strip()}\n{test_stderr.strip()}"
                )
        except Exception as e:
            unit_test_result = f"Unit Tests Failed: {str(e)}"

        return "Success", unit_test_result
    else:
        return "Success", "Unit tests not run"


def run_go_tests(repo_path, src_path, unit_tests_dir):
    # Create a temporary directory for testing
    go_test_temp = os.path.join(repo_path, "go_test_temp")
    if os.path.exists(go_test_temp):
        shutil.rmtree(go_test_temp)
    os.mkdir(go_test_temp)

    # Gather instructor test files (those ending with _test.go)
    instructor_test_files = [
        f for f in os.listdir(unit_tests_dir) if f.endswith("_test.go")
    ]
    if not instructor_test_files:
        return "No instructor test files found", None

    # For each instructor test file, copy the corresponding student source file and the test file
    for test_file in instructor_test_files:
        # Determine the corresponding student source file name by removing the _test suffix.
        base_name = test_file.replace("_test.go", ".go")
        student_file_path = os.path.join(src_path, base_name)
        # If not found directly, try searching recursively.
        if not os.path.exists(student_file_path):
            found = False
            for root, dirs, files in os.walk(src_path):
                if base_name in files:
                    student_file_path = os.path.join(root, base_name)
                    found = True
                    break
            if not found:
                print(
                    f"Warning: Student file {base_name} not found. Skipping this test."
                )
                continue

        # Copy the student source file into the temporary test directory.
        shutil.copy(student_file_path, os.path.join(go_test_temp, base_name))
        # Copy the corresponding instructor test file into the temporary test directory.
        instructor_test_file_path = os.path.join(unit_tests_dir, test_file)
        shutil.copy(instructor_test_file_path, os.path.join(go_test_temp, test_file))

    # Run go tests in the temporary directory.
    test_command = ["go", "test", "-v", "./..."]
    try:
        returncode, test_stdout, test_stderr = run_with_timeout(
            test_command, cwd=go_test_temp, timeout=TIMEOUT_SECONDS
        )
        if returncode == 0:
            unit_test_result = "Unit Tests Passed"
        else:
            unit_test_result = (
                f"Unit Tests Failed:\n{test_stdout.strip()}\n{test_stderr.strip()}"
            )
    except Exception as e:
        unit_test_result = f"Unit Tests Failed: {str(e)}"

    try:
        shutil.rmtree(go_test_temp)
    except Exception as e:
        print(f"Warning: Could not remove temporary directory {go_test_temp}: {str(e)}")

    return "Success", unit_test_result


# Modular GPT analysis function for both Java and Go
def analyze_with_gpt(
    code_contents,
    compilation_result,
    unit_test_result,
    analysis_type,
    language,
    assignment_instructions=None,
):
    # Format assignment instructions if available
    assignment_context = ""
    if assignment_instructions and assignment_instructions.strip():
        assignment_context = f"""
Uppgiftsbeskrivning från README.md:

{assignment_instructions}
När du ger feedback ska det vara tydligt vad som är förväntat av studenten, såsom från uppgiftsbeskrivningen och potentiellt misslyckade tester eller felaktig kompilering. 
Förbättringsmöjligheter utöver vad som krävs av uppgiftsbeskrivningen ska vara tydligt separerade så att studenten inte missförstår vad som är obligatoriskt och vad som är frivilligt.
"""

    if language == "java":
        if analysis_type == "pass":
            prompt = f"""
            Du är en Java-expert som hjälper en student att förbättra sin kod.

            {assignment_context}Studentens kod är som följer:

            {code_contents}

            Resultatet av kompileringen är:
            {compilation_result}

            Resultatet av enhetstesterna är:
            {unit_test_result}

            Programmet fungerar och alla enhetstester har klarats.

            Ge konstruktiv feedback om hur studenten kan förbättra sin kod, till exempel förbättring av kodstruktur, prestanda eller läsbarhet. Använd uppgiftsbeskrivningen för att kontrollera om studenten har implementerat alla krav korrekt.

            Ge svaret kort, max 100 ord.
            """
        elif analysis_type == "test_failure":
            prompt = f"""
            Du är en Java-expert som hjälper en student att debugga sin kod.

            {assignment_context}Studentens kod är som följer:

            {code_contents}

            Resultatet av kompileringen är:
            {compilation_result}

            Resultatet av enhetstesterna är:
            {unit_test_result}

            Hjälp studenten att identifiera var i koden det kan finnas fel, och ge förslag på förbättringar utan att ge direkta lösningar. Fokusera på att leda studenten till rätt del av koden och uppmuntra dem att analysera det. Använd uppgiftsbeskrivningen för att förstå vad som förväntas av koden.
            Anta testerna som perfekta och undvik att nämn dess existens.
            Ge svaret kort, max 100 ord och fokusera på att hjälpa studenten att förstå.
            """
        elif analysis_type == "compile_failure":
            prompt = f"""
            Du är en Java-expert som hjälper en student att debugga sin kod.

            {assignment_context}Studentens kod är som följer:

            {code_contents}

            Resultatet av kompileringen är:
            {compilation_result}

            Resultatet av enhetstesterna är:
            {unit_test_result}

            Hjälp studenten att förstå varför koden inte fungerar. Använd uppgiftsbeskrivningen för att förstå vad som förväntas av koden.

            Ge svaret kort, max 100 ord och fokusera mer på vad studenten kan göra för att lära sig hur man löser det.
            """
    elif language == "go":
        if analysis_type == "pass":
            prompt = f"""
            Du är en Go-expert som hjälper en student att förbättra sin kod.

            {assignment_context}Studentens kod är som följer:

            {code_contents}

            Resultatet av byggprocessen är:
            {compilation_result}

            Resultatet av enhetstesterna är:
            {unit_test_result}

            Programmet fungerar och alla enhetstester har klarats.

            Ge konstruktiv feedback om hur studenten kan förbättra sin kod, till exempel förbättring av kodstruktur, prestanda eller läsbarhet. Använd uppgiftsbeskrivningen för att kontrollera om studenten har implementerat alla krav korrekt.

            Ge svaret kort, max 100 ord.
            """
        elif analysis_type == "test_failure":
            prompt = f"""
            Du är en Go-expert som hjälper en student att debugga sin kod.

            {assignment_context}Studentens kod är som följer:

            {code_contents}

            Resultatet av byggprocessen är:
            {compilation_result}

            Resultatet av enhetstesterna är:
            {unit_test_result}

            Hjälp studenten att identifiera var i koden det kan finnas fel, och ge förslag på förbättringar utan att ge direkta lösningar. Fokusera på att leda studenten till rätt del av koden och uppmuntra dem att analysera det. Använd uppgiftsbeskrivningen för att förstå vad som förväntas av koden.
            Anta testerna som perfekta och undvik att nämn dess existens.
            Ge svaret kort, max 100 ord och fokusera på att hjälpa studenten att förstå.
            """
        elif analysis_type == "compile_failure":
            prompt = f"""
            Du är en Go-expert som hjälper en student att debugga sin kod.

            {assignment_context}Studentens kod är som följer:

            {code_contents}

            Resultatet av byggprocessen är:
            {compilation_result}

            Resultatet av enhetstesterna är:
            {unit_test_result}

            Hjälp studenten att förstå varför koden inte fungerar. Använd uppgiftsbeskrivningen för att förstå vad som förväntas av koden.

            Ge svaret kort, max 100 ord och fokusera mer på vad studenten kan göra för att lära sig hur man löser det.
            """
    try:
        response = client.responses.create(
            model="gpt-5-nano-2025-08-07",
            input=[{"role": "user", "content": prompt}],
            reasoning={"effort": "minimal"},
            text={"verbosity": "low"},
        )
        return response.output_text
    except Exception as e:
        print(f"Error generating analysis with GPT: {e}")
        return "Kunde inte generera analys på grund av ett fel med OpenAI API."


# Function to extract assignment instructions from README.md
def extract_assignment_instructions(repo_path):
    """
    Extract assignment instructions from README.md file in the repository root.
    Returns the content of README.md or a default message if not found.
    """
    readme_path = os.path.join(repo_path, "README.md")
    if os.path.exists(readme_path):
        try:
            with open(readme_path, "r", encoding="utf-8") as f:
                content = f.read().strip()
                return content if content else "README.md file is empty."
        except Exception as e:
            return f"Error reading README.md: {str(e)}"
    else:
        return "No README.md file found in repository."


# Function to strip comments from code
def strip_comments(code, language):
    """
    Strip comments from Java or Go code.
    Returns (stripped_code, comments_found) for debugging.
    """

    lines = code.split("\n")
    stripped_lines = []
    comments_found = []

    for line_num, line in enumerate(lines, 1):
        original_line = line

        if language == "java":
            # Remove single-line comments (//)
            if "//" in line:
                comment_pos = line.find("//")
                comment_text = line[comment_pos:]
                if comment_text.strip():
                    comments_found.append(f"Line {line_num}: {comment_text.strip()}")
                line = line[:comment_pos].rstrip()

            # Handle multi-line comments (/* */) - simple approach
            while "/*" in line and "*/" in line:
                start = line.find("/*")
                end = line.find("*/", start) + 2
                comment_text = line[start:end]
                if comment_text.strip():
                    comments_found.append(f"Line {line_num}: {comment_text.strip()}")
                line = line[:start] + line[end:]

        elif language == "go":
            # Remove single-line comments (//)
            if "//" in line:
                comment_pos = line.find("//")
                comment_text = line[comment_pos:]
                if comment_text.strip():
                    comments_found.append(f"Line {line_num}: {comment_text.strip()}")
                line = line[:comment_pos].rstrip()

            # Handle multi-line comments (/* */) - simple approach
            while "/*" in line and "*/" in line:
                start = line.find("/*")
                end = line.find("*/", start) + 2
                comment_text = line[start:end]
                if comment_text.strip():
                    comments_found.append(f"Line {line_num}: {comment_text.strip()}")
                line = line[:start] + line[end:]

        stripped_lines.append(line)

    return "\n".join(stripped_lines), comments_found


# Function to collect student's code (filters by language)
def collect_student_code(src_path, language):
    code_contents = ""
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if language == "java":
                if file.endswith(".java"):
                    file_full_path = os.path.join(root, file)
                    # Skip JUnit test files by checking content, not filename
                    if is_junit_test(file_full_path):
                        continue
                    with open(file_full_path, "r", encoding="utf-8") as f:
                        code_contents += f"// File: {file}\n" + f.read() + "\n\n"
            elif language == "go":
                if file.endswith(".go") and not file.endswith("_test.go"):
                    with open(os.path.join(root, file), "r", encoding="utf-8") as f:
                        code_contents += f"// File: {file}\n" + f.read() + "\n\n"
    return code_contents


# Collect unit test files from 'UnitTests/Task-{task_number}' directory
unit_tests_dir = os.path.join(os.getcwd(), "UnitTests", f"Task-{task_number}")
unit_test_files = []
test_class_names = []
if os.path.exists(unit_tests_dir):
    if language == "java":
        for file in os.listdir(unit_tests_dir):
            if not file.endswith(".java"):
                continue
            unit_test_file = os.path.join(unit_tests_dir, file)
            unit_test_files.append(unit_test_file)
            class_name = os.path.splitext(file)[0]
            if class_name == "TextFileTest":   # <-- hard skip the abstract base
                continue
            test_class_names.append(class_name)
else:
    if run_tests:
        print(f"No unit tests found for Task {task_number} in {unit_tests_dir}")
        print("Unit tests are enabled, but no unit tests were found. Exiting.")
        sys.exit(1)


# Iterate over each student and generate the GitHub URL dynamically
results = []

for i, student_name in enumerate(student_names):
    repo_name_local = f"repo_{i}"
    git_url = f"{base_url}{student_name.strip()}-{repo_suffix}.git"
    web_url = f"https://gits-15.sys.kth.se/inda-24/{student_name.strip()}-{repo_suffix}"
    repo_path = os.path.join(base_folder, repo_name_local)
    issue_status = "not created"
    gpt_analysis = "No GPT analysis"

    try:
        # Clone the repository
        git.Repo.clone_from(git_url, repo_path)

        # Check the latest commit date across all branches
        repo = git.Repo(repo_path)
        latest_commit_date = None
        for branch in repo.branches:
            commits = list(repo.iter_commits(branch))
            if commits:
                branch_latest_commit = commits[0]
                branch_latest_commit_date = datetime.fromtimestamp(
                    branch_latest_commit.committed_date
                )
                if (latest_commit_date is None) or (
                    branch_latest_commit_date > latest_commit_date
                ):
                    latest_commit_date = branch_latest_commit_date

        if latest_commit_date is None:
            compilation_result = "No commits found"
            unit_test_result = "Unit test not run"
            issue_status = "Fail"
            issue_title = "Fail"
            issue_body = "Contact me for explanation."
            proceed_with_grading = False
        else:
            current_date = datetime.now()
            one_month_ago = current_date - timedelta(days=FAIL_TIMER)
            if latest_commit_date < one_month_ago:
                compilation_result = "No recent commits"
                unit_test_result = "Unit test not run"
                issue_status = "Fail"
                issue_title = "Fail"
                issue_body = "Contact me for explanation."
                proceed_with_grading = False
            else:
                proceed_with_grading = True

        if proceed_with_grading:
            src_path = os.path.join(repo_path, "src")
            if not os.path.exists(src_path):
                compilation_result = "No src directory found"
                unit_test_result = "No src directory found"
                issue_status = "Kompletering"
                issue_title = "Kompletering"
                issue_body = (
                    "No src directory found. Please check your repository structure."
                )
            else:
                if language == "java":
                    compilation_result, unit_test_result = run_java_class(
                        repo_path,
                        src_path,
                        unit_test_files,
                        test_class_names,
                        unit_tests_dir,
                    )
                elif language == "go":
                    compilation_result, unit_test_result = run_go_tests(
                        repo_path, src_path, unit_tests_dir
                    )
                if use_gpt:
                    # Check if student is in non-participating list
                    if student_name.strip() in non_participating_students:
                        print(
                            f"Student {student_name.strip()} is non-participating - using stack trace instead of GPT"
                        )
                        gpt_analysis = f"Compilation result: {compilation_result}\nUnit test result: {unit_test_result}"
                    else:
                        code_contents = collect_student_code(src_path, language)

                        # Strip comments from code before GPT analysis
                        stripped_code, comments_found = strip_comments(
                            code_contents, language
                        )

                        # Extract assignment instructions from README.md
                        assignment_instructions = extract_assignment_instructions(
                            repo_path
                        )

                        analysis_type = (
                            "pass"
                            if compilation_result == "Success"
                            and unit_test_result
                            in ["Unit Tests Passed", "Unit Tests Passed"]
                            else "test_failure"
                            if compilation_result == "Success"
                            and "Failed" in unit_test_result
                            else "compile_failure"
                        )
                        gpt_analysis = analyze_with_gpt(
                            stripped_code,
                            compilation_result,
                            unit_test_result,
                            analysis_type,
                            language,
                            assignment_instructions,
                        )
                if compilation_result == "Success" and (
                    unit_test_result == "Unit Tests Passed"
                    or unit_test_result == "Unit Tests Passed"
                ):
                    issue_title = "PASS!"
                    issue_status = "PASS"
                else:
                    issue_title = "Kompletering"
                    issue_status = "Kompletering"
                if use_gpt:
                    # Check if this is actual GPT analysis or just stack trace for non-participating student
                    if student_name.strip() in non_participating_students:
                        issue_body = gpt_analysis  # Already contains "Compilation result: ... Unit test result: ..."
                    else:
                        issue_body = f"GPT Analysis: {gpt_analysis}"
                else:
                    issue_body = f"Compilation result: {compilation_result}\nUnit test result: {unit_test_result}"
        if auto_create_issues and g is not None:
            try:
                repo_suffix_local = (
                    "quicksort"
                    if (language == "java" and task_number == "19")
                    else (f"task-{task_number}" if language == "java" else repo_suffix)
                )
                repo_name = f"inda-25/{student_name.strip()}-{repo_suffix_local}"
                repo_obj = g.get_repo(repo_name)
                repo_obj.create_issue(title=issue_title, body=issue_body)
            except Exception as e:
                print(f"Error creating issue for {student_name.strip()}: {e}")
        results.append(
            (web_url, compilation_result, unit_test_result, issue_status, gpt_analysis)
        )
        print(
            f"Graded repository for student: {student_name} and results are: {issue_status}"
        )
    except Exception as e:
        print(f"Error processing {student_name.strip()}: {str(e)}")
        compilation_result = f"Error: {str(e)}"
        unit_test_result = "Unit test not run"
        issue_status = "Fail"
        issue_title = "Fail"
        issue_body = "Contact me for explanation."
        if auto_create_issues and g is not None:
            try:
                repo_suffix_local = (
                    "quicksort"
                    if (language == "java" and task_number == "19")
                    else (f"task-{task_number}" if language == "java" else repo_suffix)
                )
                repo_name = f"inda-25/{student_name.strip()}-{repo_suffix_local}"
                repo_obj = g.get_repo(repo_name)
                repo_obj.create_issue(title=issue_title, body=issue_body)
            except Exception as e:
                print(f"Error creating issue for {student_name.strip()}: {e}")
        results.append(
            (web_url, compilation_result, unit_test_result, issue_status, gpt_analysis)
        )
        print(
            f"Graded repository for student: {student_name} and results are: {issue_status}"
        )

# Save results back to a new Excel file with both issue and GPT columns
result_df = pd.DataFrame(
    results,
    columns=[
        "Web URL",
        "Compilation Result",
        "Unit Test Result",
        "Issue Status",
        "GPT Analysis",
    ],
)
result_df.to_excel("grading_results.xlsx", index=False)

print("Grading complete. Results saved to 'grading_results.xlsx'.")
time.sleep(1)
