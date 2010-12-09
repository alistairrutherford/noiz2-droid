setlocal

call droid.bat

%ANDROID_SDK_TOOLS%/adb -d uninstall com.netthreads.android.noiz2

endlocal