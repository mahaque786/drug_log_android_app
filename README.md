# Drug Logger - Android App

A simple Android application for logging medications/drugs with dosage and notes.

## Features

- Add drug entries with name, dosage, and notes
- View all logged entries with timestamps
- Delete individual entries
- Persistent storage using SQLite database
- Material Design UI

## Requirements

- Android SDK 21 or higher
- Android Studio (recommended) or Gradle

## Building the App

### Using Android Studio
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to this directory
4. Wait for Gradle sync to complete
5. Click Run (or press Shift+F10)

### Using Gradle Command Line
```bash
# Build APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`

## Usage

1. Launch the app
2. Click the floating action button (+) to add a new entry
3. Fill in the drug name (required), dosage, and notes
4. Click "Add" to save the entry
5. View all entries in the main list
6. Click the delete button on any entry to remove it

## Project Structure

```
app/
├── src/main/
│   ├── java/com/druglogger/app/
│   │   ├── MainActivity.java          # Main activity
│   │   ├── DrugEntry.java             # Data model
│   │   ├── DrugLogDatabase.java       # SQLite database helper
│   │   └── DrugEntryAdapter.java      # RecyclerView adapter
│   ├── res/
│   │   ├── layout/                    # XML layouts
│   │   ├── values/                    # Strings, colors, themes
│   │   └── mipmap/                    # App icons
│   └── AndroidManifest.xml
└── build.gradle
```

## License

This project is open source and available for personal use.