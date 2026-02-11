# Drug Logger - Android App

A simple Android application for logging medications/drugs with dosage and notes.

## Features

- ✅ Add drug entries with name, dosage, and notes
- ✅ View all logged entries with timestamps
- ✅ Delete individual entries with confirmation
- ✅ Persistent storage using SQLite database
- ✅ Material Design UI with modern look
- ✅ Supports Android 5.0 (API 21) and higher
- ✅ No internet connection required
- ✅ All data stored locally for privacy

## Quick Start

### For Users

1. Download the APK from releases
2. Install on your Android device
3. Open the app and start logging!

### For Developers

See [BUILDING.md](BUILDING.md) for detailed build instructions.

**Quick build**:
```bash
# Clone the repository
git clone https://github.com/mahaque786/drug_log_android_app.git
cd drug_log_android_app

# Build with Gradle
./gradlew assembleDebug

# Or open in Android Studio and click Run
```

## Documentation

- 📖 [Building Guide](BUILDING.md) - How to build and run the app
- 🔧 [Technical Documentation](TECHNICAL.md) - Architecture and implementation details
- 🎨 [UI Guide](UI_GUIDE.md) - Screenshots and user interface documentation

## Requirements

- **Minimum Android Version**: Android 5.0 (API 21)
- **Target Android Version**: Android 13 (API 33)
- **Permissions**: None required

## Usage

1. **Add Entry**: Tap the floating action button (+) to add a new drug log
2. **Fill Details**: Enter drug name (required), dosage, and any notes
3. **View History**: All entries are displayed in chronological order (newest first)
4. **Delete Entry**: Tap the delete button on any entry to remove it

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

## Technology Stack

- **Language**: Java
- **UI Framework**: Android SDK with Material Design Components
- **Database**: SQLite
- **Architecture**: MVC (Model-View-Controller)
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 33 (Android 13)

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## Privacy

- All data is stored locally on your device
- No internet connection required
- No data collection or sharing
- No advertisements

## License

This project is open source and available for personal use.