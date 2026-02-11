# Building and Running the Drug Logger Android App

## Prerequisites

Before you can build and run the app, you need to have the following installed:

1. **Java Development Kit (JDK) 8 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use OpenJDK: https://openjdk.org/

2. **Android Studio** (Recommended)
   - Download from: https://developer.android.com/studio
   - Includes Android SDK, emulator, and all necessary tools

3. **Android SDK** (if not using Android Studio)
   - Can be installed via Android Studio or standalone
   - Minimum SDK version: 21 (Android 5.0 Lollipop)
   - Target SDK version: 33 (Android 13)

## Building with Android Studio

1. **Open the Project**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `drug_log_android_app` directory
   - Click "OK"

2. **Sync Gradle**
   - Android Studio will automatically start syncing Gradle
   - Wait for the sync to complete (this may take a few minutes on first run)
   - If prompted, accept any SDK licenses

3. **Connect a Device or Start Emulator**
   - **Physical Device**: Connect via USB with USB debugging enabled
   - **Emulator**: Click "Device Manager" → Create new virtual device or use existing

4. **Run the App**
   - Click the green "Run" button (▶) in the toolbar
   - Or press `Shift + F10`
   - Select the target device
   - The app will build and install automatically

## Building from Command Line

### On Linux/Mac:

```bash
# Make sure gradlew is executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install in one step
./gradlew installDebug
```

### On Windows:

```cmd
# Build debug APK
gradlew.bat assembleDebug

# Install on connected device
gradlew.bat installDebug
```

### Output Location

After building, the APK file will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Installing the APK Manually

If you have a pre-built APK:

```bash
# Using adb (Android Debug Bridge)
adb install app/build/outputs/apk/debug/app-debug.apk

# Or for reinstallation
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Troubleshooting

### Gradle Sync Issues

If Gradle sync fails:
1. Check your internet connection (Gradle needs to download dependencies)
2. In Android Studio: File → Invalidate Caches / Restart
3. Delete `.gradle` folder in project root and re-sync

### SDK Issues

If you get SDK version errors:
1. Open Android Studio → Tools → SDK Manager
2. Install the required SDK versions (API 21 through 33)
3. Accept all SDK licenses

### Device Not Detected

If your device isn't detected:
1. Enable USB debugging on your device (Settings → Developer Options)
2. Install device drivers (Windows only)
3. Try different USB cables/ports
4. Run `adb devices` to verify connection

### Build Errors

If you encounter build errors:
1. Clean the project: `./gradlew clean`
2. Rebuild: `./gradlew build --refresh-dependencies`
3. Check error messages for missing dependencies

## Building for Release

To build a release version:

```bash
# Create release APK (unsigned)
./gradlew assembleRelease
```

For production release, you'll need to:
1. Create a keystore for signing
2. Configure signing in `app/build.gradle`
3. Build signed APK through Android Studio or Gradle

## System Requirements

- **Minimum Android Version**: Android 5.0 (API 21)
- **Target Android Version**: Android 13 (API 33)
- **Disk Space**: ~500MB for dependencies
- **RAM**: 8GB recommended for Android Studio
