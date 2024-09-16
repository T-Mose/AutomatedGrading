"""
Auto Compiler Script
Created by: Theodor Malmgren (GitHub: T-Mose)
Date: September 13, 2024

Description:
This script automates the process of cloning and compiling Java student assignments. 
It fetches student repositories and compiles the Java files and optionally runs unit tests.
This script also automatically creates issues on the students' GitHub repositories for simpler results.
It can also generate detailed feedback using GPT analysis.

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

# Check for input parameters
if len(sys.argv) < 2 or not (1 <= int(sys.argv[1]) <= 9):
    print("Usage: python script_name.py <task_number> [Y/N] [Y/N] [Y/N]")
    print("The first Y/N is for running unit tests (default: Y)")
    print("The second Y/N is for auto-creating issues (default: N)")
    print("The third Y/N is for using GPT analysis (default: N)")
    sys.exit(1)

task_number = sys.argv[1]  # The task number, e.g., "2"

# Determine whether to run unit tests
run_tests = True  # Default is to run tests
if len(sys.argv) >= 3:
    run_tests_input = sys.argv[2].strip().upper()
    if run_tests_input == 'Y':
        run_tests = True
    elif run_tests_input == 'N':
        run_tests = False
    else:
        print("Invalid parameter for running unit tests. Use 'Y' or 'N'.")
        sys.exit(1)

# Determine whether to auto-create issues
auto_create_issues = False  # Default is not to create issues
if len(sys.argv) >= 4:
    create_issues_input = sys.argv[3].strip().upper()
    if create_issues_input == 'Y':
        auto_create_issues = True
    elif create_issues_input == 'N':
        auto_create_issues = False
    else:
        print("Invalid parameter for auto-creating issues. Use 'Y' or 'N'.")
        sys.exit(1)

# Determine whether to use GPT analysis
use_gpt = False  # Default is not to use GPT
if len(sys.argv) >= 5:
    use_gpt_input = sys.argv[4].strip().upper()
    if use_gpt_input == 'Y':
        use_gpt = True
    elif use_gpt_input == 'N':
        use_gpt = False
    else:
        print("Invalid parameter for GPT analysis. Use 'Y' or 'N'.")
        sys.exit(1)

# Load the tokens from API_TOKENS.env if it exists
github_token = None
openai_api_key = None
env_file_path = os.path.join(os.getcwd(), 'API_TOKENS.env')
if os.path.exists(env_file_path):
    with open(env_file_path, 'r') as env_file:
        for line in env_file:
            line = line.strip()
            if '=' in line:
                key, value = line.split('=', 1)
                if key == 'GITHUB_TOKEN':
                    github_token = value.strip()
                    print("GitHub token extracted of length: " + str(len(github_token)))
                elif key == 'OPENAI_API_KEY':
                    openai_api_key = value.strip()
                    print("OPENAI token extracted of length: " + str(len(openai_api_key)))
    print("Tokens loaded from API_TOKENS.env")
else:
    print("API_TOKENS.env file not found. Issues will not be auto-created, GPT analysis will be disabled.")
    auto_create_issues = False  # Ensure issues are not created without a token
    use_gpt = False  # Ensure GPT is not used without a token

# Initialize GitHub API client if token is available
if github_token and auto_create_issues:
    GITHUB_ENTERPRISE_URL = 'https://gits-15.sys.kth.se'
    g = Github(base_url=GITHUB_ENTERPRISE_URL + '/api/v3', login_or_token=github_token)
else:
    g = None  # GitHub client is not initialized

# Initialize OpenAI API client if API key is available
if openai_api_key and use_gpt:
    OpenAI.api_key = openai_api_key
    client = OpenAI(api_key=openai_api_key)
else:
    if use_gpt:
        print("OpenAI API key not found or invalid. GPT analysis will be disabled.")
        use_gpt = False

# Path to the Excel file (assuming it's in the same directory as the script)
excel_path = os.path.join(os.getcwd(), 'students.xlsx')  # Replace with a generic filename for all TAs
# Path to the unit test file
unit_test_path = os.path.join(os.getcwd(), 'UnitTests.java')  # Now named UnitTests.java

# Paths to the JUnit and Hamcrest JAR files (assuming they're in the current directory)
junit_jar = os.path.join(os.getcwd(), 'junit-4.13.2.jar')
hamcrest_jar = os.path.join(os.getcwd(), 'hamcrest-core-1.3.jar')

# Base URL for the GitHub repos
base_url = "git@gits-15.sys.kth.se:inda-24/"

# Read student names from the Excel file (assuming names are in column A)
df = pd.read_excel(excel_path, usecols='A', header=None)
student_names = df[df.columns[0]].dropna()  # Drop any empty rows, process all names in column A

# Generate GitHub URLs based on student names and task number
urls = [f"{base_url}{name.strip()}-task-{task_number}.git" for name in student_names]

# Folder to clone repositories
base_folder = os.path.join(os.getcwd(), 'student_repos')

# Create a folder to store the cloned repos
if not os.path.exists(base_folder):
    os.mkdir(base_folder)

# Timeout value for subprocesses (in seconds)
TIMEOUT_SECONDS = 60  # Adjust as needed

# Function to run a command with timeout
def run_with_timeout(command, cwd, timeout):
    process = subprocess.Popen(
        command,
        cwd=cwd,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        stdin=subprocess.DEVNULL,  # Redirect stdin to avoid waiting for input
        text=True,
        shell=False
    )
    timer = threading.Timer(timeout, process.kill)  # Kill the process after timeout
    try:
        timer.start()
        stdout, stderr = process.communicate()
        return process.returncode, stdout, stderr
    finally:
        timer.cancel()

# Function to detect the main class file by searching for 'public static void main(String[] args)'
def detect_main_class(src_path):
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if file.endswith(".java"):
                java_file_path = os.path.join(root, file)
                with open(java_file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    if 'public static void main(String[] args)' in content:
                        print(f"Main class detected: {file}")
                        return os.path.splitext(file)[0]  # Return the class name without .java
    return None

# Function to compile and run the Java class and unit tests
def run_java_class(repo_path, src_path):
    print(f"Compiling Java files in {src_path}...")

    # Collect all Java file names relative to src_path
    java_files = []
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if file.endswith('.java'):
                # Get relative path to src_path
                file_full_path = os.path.join(root, file)
                file_relative_path = os.path.relpath(file_full_path, src_path)
                java_files.append(file_relative_path)

    if not java_files:
        print("No Java files found to compile.")
        return "No Java files found", None

    # Compile the student's Java files
    compile_command = ['javac', '-d', repo_path] + java_files
    print(f"Compile command: {' '.join(compile_command)}")

    try:
        returncode, stdout, stderr = run_with_timeout(
            compile_command, cwd=src_path, timeout=TIMEOUT_SECONDS
        )
        if returncode != 0:
            print(f"Compilation failed")
            print(stderr)
            return f'Compilation Failed: {stderr.strip()}', None
    except Exception as e:
        print(f"Compilation error: {str(e)}")
        return f"Compilation Error: {str(e)}", None

    # Automatically detect the main class file
    main_class_name = detect_main_class(src_path)
    if main_class_name is None:
        print("Main class not found.")
        return "Main class not found", None

    # If unit tests should be run
    if run_tests:
        # Compile the unit test using the student's compiled files
        print(f"Compiling unit test UnitTests.java...")

        # Use os.pathsep to construct classpath
        classpath = os.pathsep.join([repo_path, junit_jar, hamcrest_jar])
        compile_test_command = ['javac', '-cp', classpath, unit_test_path, '-d', repo_path]
        print(f"Compile unit test command: {' '.join(compile_test_command)}")

        try:
            returncode, stdout, stderr = run_with_timeout(
                compile_test_command, cwd=src_path, timeout=TIMEOUT_SECONDS
            )
            if returncode != 0:
                print(f"Unit test compilation failed")
                print(stderr)
                return 'Success', f'Unit Test Compilation Failed: {stderr.strip()}'  # We return "Success" to show that the student's program compiled, but the unit test failed
        except Exception as e:
            print(f"Unit test compilation error: {str(e)}")
            return 'Success', f"Unit Test Compilation Error: {str(e)}"

        # Run the unit test using JUnitCore
        print(f"Running unit test UnitTests using JUnitCore...")
        try:
            run_test_command = ['java', '-cp', classpath, 'org.junit.runner.JUnitCore', 'UnitTests']
            print(f"Run unit test command: {' '.join(run_test_command)}")
            returncode, test_stdout, test_stderr = run_with_timeout(
                run_test_command, cwd=src_path, timeout=TIMEOUT_SECONDS
            )
            if returncode == 0:
                print(f"Unit test passed:\n{test_stdout}")
                unit_test_result = 'Unit Test Passed'
            else:
                print(f"Unit test failed:\n{test_stdout}\n{test_stderr}")
                unit_test_result = f'Unit Test Failed:\n{test_stdout.strip()}\n{test_stderr.strip()}'
        except Exception as e:
            print(f"Unit test failed: {str(e)}")
            unit_test_result = f'Unit Test Failed: {str(e)}'

        return 'Success', unit_test_result
    else:
        # If not running unit tests, just indicate compilation success
        return 'Success', 'Unit tests not run'

# Function to create an issue on GitHub
def create_github_issue(student_username, task_number, title, body):
    try:
        repo_name = f"inda-24/{student_username}-task-{task_number}"
        repo = g.get_repo(repo_name)
        issue = repo.create_issue(title=title, body=body)
        print(f"Issue '{title}' created for {student_username}.")
    except Exception as e:
        print(f"Error creating issue for {student_username}: {e}")

# Function to collect student's code into a single string
def collect_student_code(src_path):
    code_contents = ""
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if file.endswith('.java'):
                java_file_path = os.path.join(root, file)
                with open(java_file_path, 'r', encoding='utf-8') as f:
                    code_contents += f"// File: {file}\n"
                    code_contents += f.read()
                    code_contents += "\n\n"
    return code_contents

# Function to call GPT for analysis with different types of prompts
def analyze_with_gpt(code_contents, program_result, unit_test_result, analysis_type):
    if analysis_type == 'pass':
        prompt = f"""
        Du är en Java-expert som hjälper en student att förbättra sin kod.

        Studentens kod är som följer:

        {code_contents}

        Programmet fungerar och alla enhetstester har klarats.

        Ge konstruktiv feedback om hur studenten kan förbättra sin kod, till exempel förbättring av kodstruktur, prestanda eller läsbarhet.

        Ge svaret kort, max 100 ord.
        """
    elif analysis_type == 'test_failure':
        prompt = f"""
        Du är en Java-expert som hjälper en student att debugga sin kod.

        Studentens kod är som följer:

        {code_contents}

        Resultatet av kompileringen är:
        {program_result}

        Resultatet av enhetstesterna är:
        {unit_test_result}

        Var vänlig hjälp studenten att identifiera problem i sin kod som ledde till att testerna misslyckades, och ge förslag på hur de kan åtgärdas utan att lösa problemen åt dem. 
        Ge även ledtrådar till var i koden studenten bör undersöka.

        Ge svaret kort, max 100 ord och fokusera på att hjälpa studenten att förstå hur man fixar testfelet. Ge bara feedbackpå studentens kod och anta testerna som perfekta.
        """
    elif analysis_type == 'compile_failure':
        prompt = f"""
        Du är en Java-expert som hjälper en student att debugga sin kod.

        Studentens kod är som följer:

        {code_contents}

        Resultatet av kompileringen är:
        {program_result}

        Resultatet av enhetstesterna är:
        {unit_test_result}

        Hjälp studenten att förstå varför koden inte fungerar.

        Ge svaret kort, max 100 ord och fokusera mer på vad studenten kan göra för att lära sig hur man löser det.
        """
    # Call GPT for analysis
    try:
        print("Calling GPT for analysis...")
        response = client.chat.completions.create(
            model="gpt-4o-mini",  # or gpt-3.5-turbo depending on your access
            messages=[{"role": "user", "content": prompt}],
            max_tokens=500,
            temperature=0.7,
        )
        analysis = response.choices[0].message.content
        print(response.choices[0].message)
        print(analysis)
        print("GPT analysis received.")
        return analysis
    except Exception as e:
        print(f"Error calling OpenAI API: {e}")
        return "Kunde inte generera analys på grund av ett fel med OpenAI API."


# Iterate over each student and generate the GitHub URL dynamically
results = []
for i, student_name in enumerate(student_names):
    print(f"Processing repository for student: {student_name}")
    repo_name = f"repo_{i}"
    git_url = f"{base_url}{student_name.strip()}-task-{task_number}.git"
    web_url = f"https://gits-15.sys.kth.se/inda-24/{student_name.strip()}-task-{task_number}"  # The website URL format
    repo_path = os.path.join(base_folder, repo_name)
    issue_status = "not created"  # Default status for issue
    gpt_analysis = 'No GPT analysis'  # Default for GPT

    try:
        print(f"Cloning {git_url}...")
        git.Repo.clone_from(git_url, repo_path)
        print(f"Successfully cloned {git_url}")

        # Navigate to the src directory if it exists
        src_path = os.path.join(repo_path, 'src')
        print(f"Looking for src directory at {src_path}")
        if not os.path.exists(src_path):
            print(f"No src directory found in {repo_path}")
            results.append((web_url, 'No src directory found', 'No src directory found', issue_status))
            continue

        # Compile, run the Java class, and run unit tests (if specified)
        program_result, unit_test_result = run_java_class(repo_path, src_path)

        # Call GPT for analysis if enabled
        if use_gpt:
            code_contents = collect_student_code(src_path)
            analysis_type = (
                'pass' if program_result == 'Success' and unit_test_result == 'Unit Test Passed'
                else 'test_failure' if program_result == 'Success' and 'Unit Test Failed' in unit_test_result
                else 'compile_failure'
            )
            gpt_analysis = analyze_with_gpt(code_contents, program_result, unit_test_result, analysis_type)

        # Create the issue title and body based on the result
        issue_title = ''
        if use_gpt:
            # If GPT is enabled, use only GPT analysis as the issue body
            issue_body = f"GPT Analysis: {gpt_analysis}"
        else:   
            # If GPT is not enabled, include the program result and unit test result
            issue_body = f"Compilation result: {program_result}\nUnit test result: {unit_test_result}"


        # Create an issue for all outcomes (pass, test fail, compilation fail)
        if program_result == 'Success' and unit_test_result == 'Unit Test Passed':
            issue_title = 'PASS!'
            issue_status = "PASS"
        elif program_result == 'Success' and 'Unit Test Failed' in unit_test_result:
            issue_title = 'Kompletering!'
            issue_status = "Kompletering"
        else:
            issue_title = 'Compilation Failure'
            issue_status = "Compilation Failure"

        # Auto-create GitHub issues for all cases
        if auto_create_issues and g is not None:
            create_github_issue(student_name.strip(), task_number, issue_title, issue_body)
            print(f"Issue created for {student_name.strip()} with status {issue_status}")

        # Append the final results after issue processing
        results.append((web_url, program_result, unit_test_result, issue_status, gpt_analysis))

    except Exception as e:
        print(f"Error processing {git_url}: {str(e)}")
        results.append((web_url, f'Error: {str(e)}', 'Unit test not run', issue_status, gpt_analysis))

# Save results back to a new Excel file with both issue and GPT columns
result_df = pd.DataFrame(results, columns=['Web URL', 'Compilation/Execution Result', 'Unit Test Result', 'Issue Status', 'GPT Analysis'])
result_df.to_excel('grading_results.xlsx', index=False)

print("Grading complete. Results saved to 'grading_results.xlsx'.")
# Wait a bit before exiting to ensure all outputs are printed
time.sleep(1)