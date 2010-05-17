@echo off
rem --------------------------------------------------------
rem Android build batch file.
rem --------------------------------------------------------

setlocal

if not "%ANDROID_ANT_HOME%" == "" goto gotANTHOME
set ANDROID_ANT_HOME=C:\Program Files\java\ant\apache-ant-1.7.0

:gotANTHOME
set ANT_HOME=%ANDROID_ANT_HOME%
goto gotAnt

:gotAnt
set PATH=%PATH%;%ANT_HOME%\bin

if not "%ANDROID_SDK_TOOLS%" == "" goto gotANDROIDSDKTOOLS
set ANDROID_SDK_TOOLS=C:\Development\java\android\sdk\android-sdk-windows\tools

:gotANDROIDSDKTOOLS
set SDK_TOOLS=%ANDROID_SDK_TOOLS%
goto gotTools

:gotTools
set PATH=%PATH%;%SDK_TOOLS%

if not "%ANDROID_JAVA_HOME%" == "" goto gotJavaHOME
set ANDROID_JAVA_HOME=C:\Program Files\Java\jdk\jdk1.6.0_20

:gotJavaHOME
@echo on
set JAVA_HOME=%ANDROID_JAVA_HOME%
goto gotJava

:gotJava
set PATH=%PATH%;%JAVA_HOME%\bin

call ant -f "build.xml" %*

endlocal

