# StockMate

StockMate is an Android application for managing stock, recording sales, viewing sales summaries, and receiving low-stock notifications.

## Project structure

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

The application follows an MVVM-style structure:

```text
Jetpack Compose UI
        ↓
StockViewModel
        ↓
StockDao
        ↓
Room database
```

- `ui/` contains the Compose screens and reusable UI helpers.
- `viewmodel/` exposes observable state and coordinates user actions.
- `data/` contains Room entities, the database singleton, and DAO operations.
- `notifications/` contains the WorkManager worker for low-stock alerts.
- `MainActivity.kt` starts the app, requests notification permission, schedules the worker, and defines navigation.

## Implemented features

- Add, edit, and delete stock items.
- Validate item names, quantities, prices, and low-stock limits.
- Display stock quantities and low-stock warnings.
- Record sales and reduce stock in one Room transaction.
- Prevent invalid sales and sales exceeding available stock.
- Display today's sales and total.
- Display sales totals for the last seven days.
- Display a custom Compose bar chart.
- Run daily and post-sale low-stock checks.
- Request notification permission on Android 13 and later.

## Data storage

Room stores data locally in `stock.db`.

### `Item`

Stores the item ID, name, quantity, unit price, and low-stock limit.

### `Sale`

Stores the sale ID, item ID, item name, quantity, total amount, and timestamp.

`Sale.itemId` refers to the corresponding item ID conceptually. There is no explicit Room foreign-key declaration. The sale also stores the item name so historical sale information remains readable if an item is deleted.

## Technologies

- Kotlin `2.2.21`
- Android Gradle Plugin `9.0.1`
- Jetpack Compose and Material 3
- AndroidX Activity, Lifecycle, and Navigation Compose
- Room `2.8.4`
- Kotlin Symbol Processing (KSP)
- WorkManager `2.10.0`
- Gradle Wrapper
- Java 11 source and target compatibility

The project is configured for compile SDK 36, target SDK 36, and minimum SDK 24.

## Setup and usage

### Requirements

- Android Studio
- Android SDK
- A compatible JDK
- An Android emulator or physical Android device

### Open the project

1. Open Android Studio.
2. Select **Open**.
3. Choose the repository root.
4. Allow Gradle synchronization to finish.
5. Start an emulator or connect an Android device.
6. Run the `app` configuration.

### Build from PowerShell

```powershell
.\gradlew.bat :app:assembleDebug
```

### Run available tests

```powershell
.\gradlew.bat test
.\gradlew.bat connectedAndroidTest
```

The project currently declares testing dependencies but does not contain test source files. Automated test coverage therefore has not been established.

## Current build status

KSP source generation is enabled through the `android.disallowKotlinSourceSets=false` Gradle property. This is required by the current Android Gradle Plugin and KSP combination because KSP adds generated sources to the built-in Kotlin source sets.

Run the debug build command above to verify the local Android SDK and JDK installation. Build output is generated under `app/build/` and is not part of the source tree.

## Application workflow

1. The app opens on the stock list.
2. Use **Add** to create an item.
3. Tap an item to edit it, or use the delete action to remove it.
4. Use **Sell** to select an item and record a quantity sold.
5. The app updates stock and stores the sale transactionally.
6. Use **Summary** to view today's sales and the seven-day chart.
7. WorkManager checks for items at or below their configured low-stock limits and can show a notification.

## Team information

The original project documentation listed the following team members:

- Thomas Phiri
- Elijah Nonde
- Emmanuel Mwamba
- Sibon'gile Tembo
- Nseko Lombe

Individual responsibilities are not documented in the repository and should be confirmed by the team.

## Scope and limitations

The repository does not currently contain evidence of:

- User authentication or accounts.
- Cloud synchronization.
- Online backup.
- Multi-user support.
- Exported reports.
- Automated unit or UI tests.
- Database migration versions beyond Room schema version 1.

## Version control

The project uses Git and is hosted at:

https://github.com/Thomas54479/stockmate
