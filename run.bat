@echo off
setlocal enabledelayedexpansion

REM Check if the argument (number of processes) is provided
if "%1"=="" (
    echo Usage: %0 number_of_processes
    exit /b 1
)

REM Collect additional arguments
set additional_args=%*

REM Get the number of processes to run
set num_processes=%1
set /A end_process=num_processes-1

REM Loop to start each process in a separate window
for /L %%i in (0, 1, %end_process%) do (
    start "Java Process %%i" cmd /k java -XX:+ShowCodeDetailsInExceptionMessages -cp target\classes com.pmf.Main %%i %additional_args%
)

REM End of script
endlocal
