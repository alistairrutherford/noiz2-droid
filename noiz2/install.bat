setlocal

call droid.bat

%ANDROID_SDK_TOOLS%/adb -d install bin/noiz2-droid-release.apk

endlocal