﻿# Auto Compiler & Unit Tester

## Overview
This repository contains Python scripts for automating the process of cloning, compiling, and running unit tests on student Java repositories. It works with repositories hosted on `gits-15.sys.kth.se` and reads student IDs from an Excel file.

## Directory Before and After Script Execution

### Before Running the Script:
Here is how the directory structure looks before cloning the repositories:

![Before Directory](images/before_directory.png)

### After Running the Script:
After running the script, the cloned repositories and compiled classes appear as follows:

![After Directory](images/after_directory.png)

## Output Files

### Output from Running Unit Tests:
The final grading results will look like this in the generated Excel file when both compilation and unit tests are run:

![Excel Results with Unit Tests](images/results_excel.png)

### Output from Running Only Compilation Tests:
If you run only the compilation tests (without unit tests), the results will look like this:

![Excel Results with Compilation Only](images/results_compilation.png)

## Files
- `AutoCompilerUnitTest.py`: Script for cloning, compiling, and running unit tests.
- `AutoCompilerTest.py`: Script for cloning and compiling Java code (without running unit tests).
- `students.xlsx`: Template for inputting student IDs (empty for privacy reasons).
- `UnitTests.java`: Placeholder unit test file to be replaced for each assignment.

## Prerequisites
Make sure you have the following installed:
- Python 3.x
- GitPython: `pip install gitpython`
- Pandas: `pip install pandas`
- JUnit (Download: [JUnit 4.13.2](https://search.maven.org/artifact/junit/junit/4.13.2/jar))
- Hamcrest (Download: [Hamcrest 1.3](https://search.maven.org/artifact/org.hamcrest/hamcrest-core/1.3/jar))

### **Important**: Place the JAR files (`junit-4.13.2.jar` and `hamcrest-core-1.3.jar`) in the same directory as the Python scripts for the compilation and unit testing to work.

## How to Use
1. **Prepare the Excel File**: The `students.xlsx` file should have student IDs in the first column.
2. **Set Up the Unit Test**: Place the `UnitTests.java` file in the same directory as the scripts.
3. **Ensure the JAR Files Are in Place**: The `junit-4.13.2.jar` and `hamcrest-core-1.3.jar` must be in the same directory as the Python scripts.
4. **Run the Script**: 
   - For unit testing: `python AutoCompilerUnitTest.py <task_number>`
   - For compile-only: `python AutoCompilerTest.py <task_number>`

   Replace `<task_number>` with the appropriate task (e.g., 2 for Task 2).
