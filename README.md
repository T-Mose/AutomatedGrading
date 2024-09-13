# Auto Compiler & Unit Tester

## Overview
This repository contains Python scripts for automating the process of cloning, compiling, and running unit tests on student Java repositories. It works with repositories hosted on `gits-15.sys.kth.se` and reads student IDs from an Excel file.

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
- JUnit (Download: [JUnit 4.13.2](https://repo1.maven.org/maven2/junit/junit/4.13.2/))
- Hamcrest (Download: [Hamcrest 1.3](https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/))

## How to Use
1. **Prepare the Excel File**: The `students.xlsx` file should have student IDs in the first column.
2. **Set Up the Unit Test**: Place the `UnitTests.java` file in the same directory as the scripts.
3. **Run the Script**: 
   - For unit testing: `python AutoCompilerUnitTest.py <task_number>`
   - For compile-only: `python AutoCompilerTest.py <task_number>`

   Replace `<task_number>` with the appropriate task (e.g., 2 for Task 2).
