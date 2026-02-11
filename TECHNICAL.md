# Drug Logger Android App - Technical Documentation

## Architecture Overview

The Drug Logger app follows a simple MVC (Model-View-Controller) architecture with the following components:

### Components

```
┌─────────────────────────────────────────────┐
│           MainActivity (Controller)          │
│  - Manages UI interactions                  │
│  - Handles user input                       │
│  - Coordinates data flow                    │
└───────────┬─────────────────────────────────┘
            │
            ├─────────────────┐
            │                 │
┌───────────▼───────┐  ┌──────▼───────────────┐
│   DrugLogDatabase │  │  DrugEntryAdapter    │
│   (Data Layer)    │  │  (View Layer)        │
│                   │  │                      │
│ - SQLite database │  │ - RecyclerView       │
│ - CRUD operations │  │ - List display       │
└───────────────────┘  └──────────────────────┘
            │
            │
    ┌───────▼────────┐
    │   DrugEntry    │
    │   (Model)      │
    │                │
    │ - Data class   │
    └────────────────┘
```

## Class Details

### 1. DrugEntry (Model)

**Purpose**: Represents a single drug log entry

**Fields**:
- `id` (long): Unique identifier (database primary key)
- `drugName` (String): Name of the drug/medication
- `dosage` (String): Dosage information
- `notes` (String): Additional notes
- `timestamp` (long): Unix timestamp when entry was created

**Methods**:
- `getFormattedDate()`: Returns human-readable date string

### 2. DrugLogDatabase (Data Layer)

**Purpose**: Manages SQLite database operations

**Key Methods**:
- `addEntry(DrugEntry)`: Inserts new entry into database
- `getAllEntries()`: Retrieves all entries sorted by timestamp (newest first)
- `deleteEntry(long id)`: Deletes entry by ID
- `deleteAllEntries()`: Clears all entries

**Database Schema**:
```sql
CREATE TABLE entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drug_name TEXT NOT NULL,
    dosage TEXT,
    notes TEXT,
    timestamp INTEGER NOT NULL
)
```

### 3. DrugEntryAdapter (View Layer)

**Purpose**: Binds drug entry data to RecyclerView items

**Key Methods**:
- `onCreateViewHolder()`: Creates view holder for each item
- `onBindViewHolder()`: Binds data to views
- `updateEntries()`: Refreshes list with new data

**Interface**:
- `OnDeleteClickListener`: Callback for delete button clicks

### 4. MainActivity (Controller)

**Purpose**: Main app activity that orchestrates the UI

**Key Methods**:
- `onCreate()`: Initializes UI components and database
- `showAddEntryDialog()`: Displays dialog for adding new entry
- `deleteEntry()`: Handles entry deletion with confirmation
- `loadEntries()`: Refreshes the list from database

**UI Components**:
- RecyclerView: Displays list of entries
- FloatingActionButton: Add new entry
- MaterialToolbar: App title bar

## UI Layouts

### activity_main.xml

Main screen layout using CoordinatorLayout:
- MaterialToolbar at the top
- RecyclerView for scrollable list
- FloatingActionButton for adding entries

### item_drug_entry.xml

Individual list item using MaterialCardView:
- Drug name (bold, 18sp)
- Dosage (gray, 14sp)
- Notes (14sp)
- Timestamp (gray, 12sp)
- Delete button (ImageButton)

### dialog_add_entry.xml

Dialog for adding new entries:
- Drug name input (required)
- Dosage input (optional)
- Notes input (multiline, optional)

## Data Flow

### Adding an Entry

```
User clicks FAB
    ↓
showAddEntryDialog() displays form
    ↓
User fills form and clicks "Add"
    ↓
Create DrugEntry object
    ↓
database.addEntry(entry)
    ↓
loadEntries() refreshes UI
    ↓
adapter.updateEntries(entries)
    ↓
RecyclerView displays updated list
```

### Deleting an Entry

```
User clicks delete button on item
    ↓
Show confirmation dialog
    ↓
User confirms
    ↓
database.deleteEntry(id)
    ↓
loadEntries() refreshes UI
    ↓
adapter.updateEntries(entries)
    ↓
RecyclerView displays updated list
```

## Dependencies

### AndroidX Libraries
- `appcompat`: Backward compatibility support
- `material`: Material Design components
- `constraintlayout`: Advanced layout positioning
- `recyclerview`: Efficient list display
- `coordinatorlayout`: Advanced layout coordination

### SDK Versions
- `minSdk`: 21 (Android 5.0)
- `targetSdk`: 33 (Android 13)
- `compileSdk`: 33

## Storage

The app uses SQLite for local data persistence:
- **Database Name**: `drug_log.db`
- **Location**: `/data/data/com.druglogger.app/databases/`
- **Persistence**: Data persists across app restarts
- **Privacy**: Data stored locally on device only

## Future Enhancements

Potential features for future versions:
1. Edit existing entries
2. Search/filter entries
3. Export data (CSV, PDF)
4. Reminders/notifications
5. Multiple user profiles
6. Cloud backup/sync
7. Statistics and reports
8. Drug interaction warnings
9. Photo attachment support
10. Prescription tracking

## Security Considerations

- **Data Storage**: All data stored locally in SQLite
- **Permissions**: No special permissions required
- **Privacy**: No network access, no data sharing
- **Backup**: Uses Android's auto-backup if enabled by user

## Testing

To test the app manually:
1. Launch the app
2. Add various drug entries with different data
3. Verify entries appear in list
4. Test deletion with confirmation
5. Close and reopen app to verify persistence
6. Test with empty fields (drug name required)
7. Test with long text in notes field
8. Test rapid additions/deletions

## Performance

- **Startup**: Fast (<1 second on modern devices)
- **Database**: Optimized queries with indexes
- **UI**: RecyclerView for efficient scrolling
- **Memory**: Minimal memory footprint
- **Battery**: No background services, no battery drain
