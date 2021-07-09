# NativeBinariesManager
This class will be the manager for all access to the native binaries such as `aapt2`, `zipalign`, and etc. This class is made because we wanted to support both the old (extracting binaries to app private folder and executing it there) and the new method of accessing native binaries (by including it in the app's native libs and by using the `android:extractNativeLibs` flag, the android OS will automatically extract binaries with the regex of `lib.+\.so` into `Context#getApplicationInfo()#nativeLibraryDir`).

## Implementation
There will be a constant value named `useLegacyMethod`, used to indicate what method do you want to use in the compiled app.

`initialize(Context)` should only be called at the start of the launcher activity, what it does is to initialize the manager and to check if the binaries are already extracted or not, and if they're not, extract them (of course, it depends on the `useLegacyMethod` constant value).

`executeCommand(NativeBinaries, List<String>, OutputStream)` is used to execute a binary with the specified argument and an output stream of that binary, very simple.

Every binaries will be packaged with the apk as a zip inside as a raw resource named "binaries.zip". This will be extracted by `initialize(Context)` at `{appDataDir}/binaries/` folder.

## Problem
 - What if the user installed an apk with a different method? We should add a value in sharedpref that indicates what the last installation method uses.
