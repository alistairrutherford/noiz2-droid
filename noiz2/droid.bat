@echo off
rem --------------------------------------------------------
rem This batch file will let you call the android batch
rem from anywhere as long as you set the path up.
rem --------------------------------------------------------

setlocal
if not "%ANDROID_SDK_TOOLS%" == "" goto gotANDROIDSDKTOOLS
set ANDROID_SDK_TOOLS=C:\Development\java\android\sdk\android-sdk-windows\tools

:gotANDROIDSDKTOOLS
set SDK_TOOLS=%ANDROID_SDK_TOOLS%
goto gotTools

:gotTools
set PATH=%PATH%;%SDK_TOOLS%

if not "%SMART_JAVA_HOME%" == "" goto gotJavaHOME
set SMART_JAVA_HOME=C:\Program Files\Java\jdk\jdk1.6.0_17

:gotJavaHOME
set JAVA_HOME=%SMART_JAVA_HOME%
goto gotJava

:gotJava
set PATH=%PATH%;%JAVA_HOME%\bin

call android.bat %*

endlocal

