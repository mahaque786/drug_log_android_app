# Drug Logger - App Screenshots and UI

## Main Screen

The main screen displays all logged drug entries in a scrollable list.

```
╔══════════════════════════════════════════╗
║          Drug Logger        [≡]          ║
╠══════════════════════════════════════════╣
║                                          ║
║  ┌────────────────────────────────┐     ║
║  │ Aspirin                    [🗑] │     ║
║  │ 500mg                           │     ║
║  │ For headache                    │     ║
║  │ Feb 11, 2026 01:30 PM          │     ║
║  └────────────────────────────────┘     ║
║                                          ║
║  ┌────────────────────────────────┐     ║
║  │ Ibuprofen                  [🗑] │     ║
║  │ 200mg                           │     ║
║  │ Muscle pain relief              │     ║
║  │ Feb 10, 2026 09:15 AM          │     ║
║  └────────────────────────────────┘     ║
║                                          ║
║  ┌────────────────────────────────┐     ║
║  │ Vitamin D3                 [🗑] │     ║
║  │ 1000 IU                         │     ║
║  │ Daily supplement                │     ║
║  │ Feb 09, 2026 08:00 AM          │     ║
║  └────────────────────────────────┘     ║
║                                          ║
║                                          ║
║                                   [+]    ║
╚══════════════════════════════════════════╝
```

**Key Elements**:
- **Top Bar**: App title "Drug Logger"
- **Entry Cards**: Each drug entry shown in a Material card with:
  - Drug name (bold)
  - Dosage
  - Notes
  - Timestamp
  - Delete button (trash icon)
- **Floating Action Button** (+): Bottom right, adds new entry

## Add Entry Dialog

Clicking the + button opens a dialog to add a new drug entry.

```
╔══════════════════════════════════════════╗
║         Add Drug Entry          [×]      ║
╠══════════════════════════════════════════╣
║                                          ║
║  Drug Name *                             ║
║  ┌──────────────────────────────────┐   ║
║  │ [Enter drug name here...]        │   ║
║  └──────────────────────────────────┘   ║
║                                          ║
║  Dosage                                  ║
║  ┌──────────────────────────────────┐   ║
║  │ [Enter dosage...]                │   ║
║  └──────────────────────────────────┘   ║
║                                          ║
║  Notes                                   ║
║  ┌──────────────────────────────────┐   ║
║  │                                  │   ║
║  │ [Enter any notes...]             │   ║
║  │                                  │   ║
║  └──────────────────────────────────┘   ║
║                                          ║
║                                          ║
║          [CANCEL]      [ADD]             ║
║                                          ║
╚══════════════════════════════════════════╝
```

**Fields**:
- **Drug Name**: Required field (marked with *)
- **Dosage**: Optional field for dosage amount
- **Notes**: Optional multiline field for additional notes
- **Buttons**:
  - CANCEL: Dismisses dialog without saving
  - ADD: Saves entry to database and updates list

## Delete Confirmation

Clicking a delete button shows a confirmation dialog.

```
╔══════════════════════════════════════════╗
║            Delete Entry                  ║
╠══════════════════════════════════════════╣
║                                          ║
║  Are you sure you want to delete         ║
║  this entry?                             ║
║                                          ║
║                                          ║
║          [CANCEL]     [DELETE]           ║
║                                          ║
╚══════════════════════════════════════════╝
```

## Empty State

When no entries exist, the list is empty (first launch).

```
╔══════════════════════════════════════════╗
║          Drug Logger        [≡]          ║
╠══════════════════════════════════════════╣
║                                          ║
║                                          ║
║                                          ║
║              (empty list)                ║
║                                          ║
║         Tap + to add your first          ║
║            drug entry                    ║
║                                          ║
║                                          ║
║                                          ║
║                                          ║
║                                   [+]    ║
╚══════════════════════════════════════════╝
```

## Features Visualization

### Material Design
- Uses Material Design 3 components
- Rounded corners on cards (8dp radius)
- Elevation/shadows for depth
- Material color scheme (Purple primary theme)
- Ripple effects on touch

### Colors
- **Primary**: Purple (#6200EE)
- **Primary Variant**: Dark Purple (#3700B3)
- **Secondary**: Teal (#03DAC5)
- **Background**: White/Light gray
- **Text**: Black/Dark gray

### Typography
- **Drug Name**: 18sp, Bold
- **Dosage**: 14sp, Regular, Gray
- **Notes**: 14sp, Regular, Black
- **Timestamp**: 12sp, Regular, Light gray

### Layout
- **Card Padding**: 16dp
- **List Item Margin**: 4dp
- **FAB Margin**: 16dp from edges
- **Input Field Height**: Standard Material height

## User Interactions

1. **Adding Entry**: 
   - Tap FAB → Fill form → Tap ADD → Entry appears at top of list

2. **Viewing Entries**: 
   - Scroll through list
   - Most recent entries appear first

3. **Deleting Entry**: 
   - Tap trash icon → Confirm → Entry removed from list

4. **Validation**: 
   - Drug name is required (shows toast if empty)
   - Other fields are optional

## Accessibility

- All buttons have content descriptions
- Proper touch target sizes (48dp minimum)
- Clear visual hierarchy
- Support for screen readers
- Dark mode compatible theme

## Responsive Design

- Adapts to different screen sizes
- Portrait and landscape orientations
- Works on phones and tablets
- Scrollable content for any screen height
