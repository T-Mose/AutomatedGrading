"""
Auto Compiler Script
Created by: Theodor Malmgren (GitHub: T-Mose)
Date: September 13, 2024

Description:
This script automates the process of cloning and compiling Java student assignments. 
It fetches student repositories and compiles the Java files without running unit tests.

GitHub Repository: https://github.com/T-Mose/AutomatedGrading
"""

import os
import subprocess
import git
import pandas as pd
import threading
import tempfile
import sys

# Check for input parameter for task number
if len(sys.argv) < 2 or not (1 <= int(sys.argv[1]) <= 9):
    print("Usage: python script_name.py <task_number> (task_number must be between 1 and 9)")
    sys.exit(1)

task_number = sys.argv[1]  # The task number, e.g., "2"

# Path to the Excel file (assuming it's in the same directory as the script)
excel_path = os.path.join(os.getcwd(), 'students.xlsx')  # Replace with a generic filename for all TAs

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

# Function to compile the Java class
def run_java_class(repo_path, src_path):
    print(f"Compiling Java files in {src_path}...")

    # Collect all Java file names relative to src_path
    java_files = []
    for root, dirs, files in os.walk(src_path):
        for file in files:
            if file.endswith('.java'):
                # Get relative path to src_path
                file_relative_path = os.path.relpath(os.path.join(root, file), src_path)
                # Replace backslashes with forward slashes
                file_relative_path = file_relative_path.replace('\\', '/')
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

    return 'Success', None

# Iterate over each student and generate the GitHub URL dynamically
results = []
for i, student_name in enumerate(student_names):
    print(f"Processing repository for student: {student_name}")
    repo_name = f"repo_{i}"
    git_url = f"{base_url}{student_name.strip()}-task-{task_number}.git"
    web_url = f"https://gits-15.sys.kth.se/inda-24/{student_name.strip()}-task-{task_number}"  # The website URL format
    repo_path = os.path.join(base_folder, repo_name)

    try:
        print(f"Cloning {git_url}...")
        git.Repo.clone_from(git_url, repo_path)
        print(f"Successfully cloned {git_url}")

        # Navigate to the src directory if it exists
        src_path = os.path.join(repo_path, 'src')
        print(f"Looking for src directory at {src_path}")
        if not os.path.exists(src_path):
            print(f"No src directory found in {repo_path}")
            results.append((web_url, 'No src directory found', 'No src directory found'))
            continue

        # Compile the Java class
        program_result, _ = run_java_class(repo_path, src_path)
        results.append((web_url, program_result, "No Unit Test"))

    except Exception as e:
        print(f"Error processing {git_url}: {str(e)}")
        results.append((web_url, f'Error: {str(e)}', 'No Unit Test'))

# Save results back to a new Excel file, replacing the Git URL with the web URL
result_df = pd.DataFrame(results, columns=['Web URL', 'Compilation/Execution Result', 'Unit Test Result'])
result_df.to_excel('grading_results_compilation_only.xlsx', index=False)

print("Grading complete. Results saved to 'grading_results_compilation_only.xlsx'.")
