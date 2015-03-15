# How To Build #

These are instructions on how to build the application using the build script.

# Details #

The ant script to build the application is in the base of the source code.

You will need Ant 1.7+, a java JDK and the Android SDK.

A DOS batch file build.bat contains settings to point to your installation of Ant and Java. The batch file uses "setlocal" and "endlocal" and will not modify your global settings.

The build script is the bog standard ant script generated with any new Anrdoid project with one slight change in that it loads my local keystore settings. Obviously these are kept private and secure local to my system.

On windows open a DOS prompt and change directory to point to the base of the project.

Modify the build.bat file to point the the relevant installations.

Type

>build debug

to build the unsigned version of the application.

**NOTE**

The default target for the application is SDK 1.5. This means the build script does not support the automatic keytool implementation by defining the keytool properties (how annoying). To remedy this I have had to add a signing target to the build script.