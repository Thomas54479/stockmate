# StockMate

StockMate is an Android application designed to help small traders manage stock, record sales, view sales summaries, and receive low-stock notifications.

## Project Structure

```text
stockmate/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/stockmate/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── AppDatabase.kt
│       │   │   ├── Entities.kt
│       │   │   └── StockDao.kt
│       │   ├── notifications/
│       │   │   └── LowStockWorker.kt
│       │   ├── ui/
│       │   │   ├── AddEditItemScreen.kt
│       │   │   ├── Format.kt
│       │   │   ├── RecordSaleScreen.kt
│       │   │   ├── StockListScreen.kt
│       │   │   ├── SummaryScreen.kt
│       │   │   └── theme/
│       │   └── viewmodel/
│       │       └── StockViewModel.kt
│       └── res/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

## Architecture

StockMate follows an MVVM-style architecture:

```text
Jetpack Compose UI
        ↓
StockViewModel
        ↓
StockDao
        ↓
Room Database
```

- **UI:** Jetpack Compose screens and reusable UI components.
- **ViewModel:** Application state and user actions.
- **Data:** Room entities, database configuration, and DAO operations.
- **Notifications:** WorkManager worker for low-stock checks.
- **MainActivity:** Application startup, navigation, notification permission, and background-work scheduling.

## Implemented Features

- Add, edit, and delete stock items.
- Validate item names, quantities, prices, and low-stock limits.
- Display available stock and low-stock warnings.
- Record sales and automatically reduce stock.
- Prevent invalid sales and sales exceeding available stock.
- Store sales and update stock transactionally.
- Display today's sales and total sales amount.
- Display sales totals for the last seven days.
- Display a custom weekly sales bar chart using Jetpack Compose.
- Schedule daily and post-sale low-stock checks.
- Request notification permission on Android 13 and later.

## Data Storage

StockMate uses Room for local database storage. The database is named `stock.db`.

### Item

Stores the item ID, name, quantity, unit price, and low-stock limit.

### Sale

Stores the sale ID, item ID, item name, quantity sold, total sale amount, and sale timestamp.

The sale record stores the item name so historical sales remain readable if an item is deleted. There is currently no explicit Room foreign-key relationship between the `Item` and `Sale` entities.

## Technologies

- Kotlin `2.2.21`
- Android Gradle Plugin `9.0.1`
- Jetpack Compose
- Material 3
- AndroidX Activity and Lifecycle
- Navigation Compose `2.9.0`
- Room `2.8.4`
- Kotlin Symbol Processing (KSP)
- WorkManager `2.10.0`
- Gradle Wrapper
- Java 11 compatibility

Project SDK configuration:

- Compile SDK: 36
- Target SDK: 36
- Minimum SDK: 24

## Setup and Usage

### Requirements

- Android Studio
- Android SDK
- Compatible JDK
- Android emulator or physical Android device

### Open the Project

1. Open Android Studio.
2. Select **Open**.
3. Choose the StockMate repository folder.
4. Allow Gradle synchronization to complete.
5. Start an emulator or connect an Android device.
6. Run the `app` configuration.

### Build the Application

From PowerShell, run:

```powershell
.\gradlew.bat :app:assembleDebug
```

### Run Tests

```powershell
.\gradlew.bat test
.\gradlew.bat connectedAndroidTest
```

Testing dependencies are configured, but automated unit and UI test source files have not yet been added. Test coverage has therefore not been established.

## Application Workflow

1. The application opens on the stock list.
2. The trader can add a new stock item.
3. Existing items can be edited or deleted.
4. The trader selects an item and records a sale.
5. Stock quantities are automatically updated after a successful sale.
6. The Summary screen displays daily sales and the weekly chart.
7. WorkManager performs low-stock checks and can trigger notifications.

## Remaining Work

The following tasks still need to be completed and verified:

1. **Build configuration:** Resolve any remaining Gradle configuration issues and confirm that the application builds successfully.
2. **Unit testing:** Add tests for stock management and sales operations.
3. **Database testing:** Add tests for Room database operations and data integrity.
4. **Application testing:** Test the screens, stock management, and sales workflow on an emulator or physical device.
5. **Notification testing and team contributions:** Verify low-stock notifications and document each member's individual contribution.

## Current Verification Status

The project has undergone Gradle/KSP configuration work. However, a successful build and a complete application run must still be confirmed.

The debug APK is generated under:

```text
app/build/outputs/apk/debug/
```

Build verification command:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Project Team

| Team Member | Student ID | Responsibility |
|---|---:|---|
| Thomas Phiri | 2420995 | To be confirmed |
| Elijah Nonde | 2420934 | To be confirmed |
| Emmanuel Mwamba | 2420928 | To be confirmed |
| Sibon’gile Tembo | 2410291 | To be confirmed |
| Nseko Lombe | 2200020 | To be confirmed |

Individual responsibilities should be confirmed and updated by the team.

## Scope and Limitations

The current project does not include:

- User authentication or accounts.
- Cloud synchronization.
- Online backup.
- Multi-user support.
- Exportable reports.
- Automated unit or UI test coverage.
- Database migration testing beyond the current schema version.

## Version Control

The project uses Git for version control and GitHub for source code hosting.

Repository: https://github.com/Thomas54479/stockmate
