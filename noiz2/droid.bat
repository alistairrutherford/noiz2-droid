@echo off
rem --------------------------------------------------------
rem Set up the appropriate paths.
rem --------------------------------------------------------

if not "%ANDROID_SDK_TOOLS%" == "" goto gotANDROIDSDKTOOLS
set ANDROID_SDK_TOOLS=C:\development\java\android\sdk\android-sdk-windows\platform-tools

:gotANDROIDSDKTOOLS
set SDK_TOOLS=%ANDROID_SDK_TOOLS%
goto gotTools

:gotTools
set PATH=%PATH%;%SDK_TOOLS%

if not "%JAVA_HOME%" == "" goto gotJavaHOME
set JAVA_HOME=C:\Program Files\Java\jdk\jdk1.6.0_22

:gotJavaHOME
set JAVA_HOME=%JAVA_HOME%
goto gotJava

:gotJava
set PATH=%PATH%;%JAVA_HOME%\bin

