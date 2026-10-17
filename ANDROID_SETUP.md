# Install the flash card app on an Android phone

This is for a first install. You do not need a developer account, and the app does not go through the Play Store.

The phone must run **Android 8.0 or newer**. The app does not use the network. Scores stay on the phone until you reset them in Progress or uninstall the app.

You need:

- A computer (Windows, macOS, or Linux)
- The phone or tablet, unlocked
- A USB cable that carries data. A charge-only cable can power the phone and still be invisible to the computer.

The project folder must contain `settings.gradle.kts` and `gradlew` (or `gradlew.bat` on Windows). Those files arrive with the app. If they are not in the folder yet, there is nothing to install.

## 1. Install Android Studio

1. Download Android Studio from [https://developer.android.com/studio](https://developer.android.com/studio).
2. Run the installer and accept the standard setup.
3. On first launch, choose the standard install and let it download the Android SDK. That includes the pieces this app needs (Android SDK Platform 35 and current build tools). If a later sync asks for a missing SDK or a license, accept it.

You do not need a separate JDK. Android Studio brings one.

## 2. Turn on developer options on the phone

The menu names differ slightly by brand.

1. Open **Settings**.
2. Open **About phone** (on Samsung: **About phone**, then **Software information**).
3. Find **Build number**.
4. Tap **Build number** seven times. Enter your PIN if the phone asks.
5. Go back. Open **Developer options**. It may sit under **System**, or you can search Settings for "Developer options".
6. Turn on **USB debugging**.

On some phones (Xiaomi, Oppo, and a few others) also turn on **Install via USB** inside Developer options. If that item is missing, skip it.

## 3. Connect the phone

1. Unlock the phone and leave it unlocked for the next prompt.
2. Plug it into the computer.
3. If a notification says the USB connection is charging only, tap it and choose **File transfer** (sometimes called MTP).
4. When the phone asks **Allow USB debugging?**, check **Always allow from this computer**, then tap **Allow**.

If that prompt never appears:

1. Unplug the phone.
2. In Developer options, tap **Revoke USB debugging authorizations**.
3. Plug it in again and watch for the prompt.

## 4. Open the project and run it

1. Start Android Studio.
2. Choose **Open** and select the folder that contains `settings.gradle.kts`. Do not open a parent folder above it.
3. Wait until Gradle sync finishes. The first sync downloads dependencies and can take several minutes.
4. In the device dropdown at the top, pick your phone. It should show the phone's name. An entry such as "Medium Phone" is an emulator, not your device.
5. Press the green **Run** button.

The first build is the slow one. When it finishes, the app opens on the phone. Later code changes install the same way: press Run again. A debug install replaces the previous debug install and keeps saved scores.

## 5. Install the APK from a terminal instead

Use this if you would rather not press Run. Android Studio still has to be installed, because it provides `adb`.

`adb` lives in the SDK `platform-tools` folder:

- Windows: `%LOCALAPPDATA%\Android\Sdk\platform-tools`
- macOS: `~/Library/Android/sdk/platform-tools`
- Linux: `~/Android/Sdk/platform-tools`

From the project folder:

```bash
# macOS or Linux
./gradlew assembleDebug
~/Library/Android/sdk/platform-tools/adb devices
~/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk
```

```bat
:: Windows, from the project folder
gradlew.bat assembleDebug
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" devices
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
```

On Linux, `adb` is usually `~/Android/Sdk/platform-tools/adb`.

`adb devices` must list the phone with the word `device`. Then `install -r` copies the debug APK onto the phone and replaces an older debug build.

Open the app from the phone's app list after a terminal install. It does not launch by itself.

## When the phone does not show up

- **`adb devices` is empty.** Try another cable and another USB port. Confirm USB debugging is on. On Windows, install the phone maker's USB driver (Samsung, Google, and others publish one) if the device never appears.
- **`adb devices` says `unauthorized`.** Unlock the phone, revoke USB debugging authorizations, plug in again, and tap Allow.
- **The dropdown only lists an emulator.** The computer is not talking to the phone yet. Check the cable, the USB mode, and the debugging prompt before pressing Run.
- **Install fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`.** An older copy was signed differently. Uninstall the app on the phone, then install again. Uninstalling deletes the scores.
- **The app will not install and the phone is older than Android 8.** This app cannot run there.

## Wireless debugging

USB is the reliable path. On Android 11 and newer you can skip the cable after the first setup:

1. Phone and computer on the same Wi-Fi.
2. Developer options, **Wireless debugging**, turn it on.
3. Tap **Pair device with pairing code**.
4. On the computer, from `platform-tools`: `adb pair <ip>:<pairing-port>` and enter the code, then `adb connect <ip>:<debug-port>`.
5. `adb devices` should show the phone. Use Run, or `adb install -r`, as above.

The IP and ports are on the Wireless debugging screen. They change, so pair again if a later connect fails.
