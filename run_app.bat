@echo off
cd /d "d:\Projects\Athleo"
call gradlew.bat installDebug
if %ERRORLEVEL% EQU 0 (
    echo Build and Install Successful!
    adb shell am start -n com.example.athleo/.LoginActivity
) else (
    echo Build Failed with error %ERRORLEVEL%
)
pause
