# Hangman

A classic word-guessing game for Android, built with modern Jetpack Compose. Race against the gallows — guess the hidden word one letter at a time before you run out of attempts.

---

## About the Game

Hangman is the timeless pen-and-paper game, reimagined for Android. A secret word is chosen at random from one of five categories — Animals, Countries, Technology, Food, or Sports. Your goal is to reveal it by guessing letters one at a time. Every wrong guess adds another piece to the hangman drawing. Guess all the letters before the figure is complete and you win; run out of attempts and the game is over.

All your results are automatically saved so you can track your progress over time on the Leaderboard.

### Features

- **50 curated words** across 5 categories
- **Progressive hangman drawing** rendered with Canvas — 6 stages from rope to full figure
- **A–Z on-screen keyboard** with colour-coded feedback (green = correct, red = wrong)
- **Persistent leaderboard** — every game result is saved locally and displayed with the word, category, outcome, attempts used, and timestamp
- **Instant replay** — start a new game or jump straight to the leaderboard at the end of each round
- **No internet required** — fully offline, no accounts, no ads

---

## Screenshots

> _Add screenshots here once the app is running on a device or emulator._

| Home | Game Play | Leaderboard |
|------|-----------|-------------|
| ![Home](docs/home.png) | ![Game](docs/game.png) | ![Leaderboard](docs/leaderboard.png) |

---

## Technical Specification

### Architecture

The project follows **Clean Architecture** with an **MVI (Model–View–Intent)** presentation pattern, implemented as a single Android module (`:app`). Layers are enforced by package structure rather than Gradle module boundaries, keeping the project lean without sacrificing separation of concerns.

```
com.hangman.app/
├── core/
│   ├── domain/util/          ← Result<D,E>, DataError, EmptyResult
│   └── presentation/         ← ObserveAsEvents, UiText, design system
└── game/
    ├── domain/               ← GameEngine, GameResult, LeaderboardRepository (interface)
    ├── data/                 ← Room DB, DAO, entity, mapper, RoomLeaderboardRepository
    └── presentation/
        ├── navigation/       ← Type-safe routes, HangmanNavHost
        ├── home/             ← Home screen MVI
        ├── game_play/        ← Game Play screen MVI + Canvas components
        └── leaderboard/      ← Leaderboard screen MVI
```

#### Layer dependency rules

```
presentation → domain ← data
```

- `domain` is pure Kotlin — zero Android or framework imports.
- `data` implements `domain` interfaces; `presentation` depends only on `domain`.
- `core` packages hold shared utilities used across features.

---

### Tech Stack

| Concern | Library / Tool | Version |
|---|---|---|
| Language | Kotlin | 2.0.21 |
| UI toolkit | Jetpack Compose | BOM 2024.09.03 |
| Material Design | Material 3 | (via Compose BOM) |
| Navigation | Compose Navigation (type-safe) | 2.8.3 |
| Dependency injection | Koin | 3.5.6 |
| Local database | Room | 2.6.1 |
| Async | Kotlin Coroutines + Flow | 1.9.0 |
| Serialization | KotlinX Serialization | 1.7.3 |
| Build tooling | KSP | 2.0.21-1.0.25 |
| Unit testing | JUnit 5 + AssertK + Turbine | 5.10.3 / 0.28.1 / 1.1.0 |
| UI testing | Compose Test (JUnit 4 rule) | (via Compose BOM) |
| Min SDK | 26 (Android 8.0) | |
| Target SDK | 35 (Android 15) | |

---

### Presentation Layer — MVI

Each screen is composed of four elements:

| Element | Purpose |
|---|---|
| `State` | Single immutable data class representing the complete UI state |
| `Action` | Sealed interface of all user-triggered intents |
| `Event` | Sealed interface of one-time side effects (navigation, snackbar) |
| `ViewModel` | Holds `StateFlow<State>`, processes `Action`, emits `Event` via `Channel` |

Each screen file contains two composables:
- **`<Screen>Root`** — injects the ViewModel via `koinViewModel()`, collects state with `collectAsStateWithLifecycle()`, observes events via `ObserveAsEvents`.
- **`<Screen>Screen`** — stateless, receives only `state` and `onAction`. Fully previewable.

#### Game Play State (example)

```kotlin
enum class GameStatus { Playing, Won, Lost }

@Stable
data class GamePlayState(
    val word: String = "",
    val category: String = "",
    val displayWord: String = "",
    val guessedLetters: Set<Char> = emptySet(),
    val wrongGuesses: Int = 0,
    val maxAttempts: Int = 6,
    val gameStatus: GameStatus = GameStatus.Playing,
    val isLoading: Boolean = false
)
```

---

### Navigation

Type-safe navigation using `@Serializable` route objects (KotlinX Serialization):

```kotlin
@Serializable data object HomeRoute
@Serializable data object GamePlayRoute
@Serializable data object LeaderboardRoute
```

All routes are wired in a single `HangmanNavHost` composable in `MainActivity`. Cross-screen navigation is expressed as lambda callbacks — screens never import each other's routes directly.

---

### Dependency Injection — Koin

Two Koin modules assembled in `HangmanApp`:

**`gameDataModule`**
- `HangmanDatabase` (Room singleton)
- `GameResultDao`
- `LeaderboardRepository` → `RoomLeaderboardRepository`

**`gamePresentationModule`**
- `GameEngine` (singleton)
- `HomeViewModel`, `GamePlayViewModel`, `LeaderboardViewModel`

Constructor-reference forms (`singleOf`, `viewModelOf`) are used throughout; lambda overloads only where a factory method is needed.

---

### Data Layer

#### Domain model

```kotlin
data class GameResult(
    val id: Long = 0,
    val word: String,
    val category: String,
    val attemptsUsed: Int,
    val maxAttempts: Int,
    val won: Boolean,
    val playedAt: Long          // epoch millis
)
```

#### Repository interface (domain)

```kotlin
interface LeaderboardRepository {
    fun getResults(): Flow<List<GameResult>>
    suspend fun saveResult(result: GameResult): EmptyResult<DataError.Local>
}
```

#### Room

- `GameResultEntity` — `@Entity("game_results")` with auto-generated primary key
- `GameResultDao` — `insert` + `getAllResults(): Flow<List<GameResultEntity>>` ordered by `playedAt DESC`
- `HangmanDatabase` — `@Database(version = 1)`
- Mappers are plain extension functions (`GameResultEntity.toGameResult()`, `GameResult.toGameResultEntity()`)

#### Error handling

A generic `Result<D, E>` sealed class is used at all data boundaries:

```kotlin
sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()
}
```

`DataError.Local` enumerates local storage error cases (`DISK_FULL`, `UNKNOWN`).

---

### Game Engine

`GameEngine` is pure Kotlin with no Android dependencies, making it trivially unit-testable:

```kotlin
class GameEngine {
    val maxAttempts: Int = 6
    fun getRandomWord(): Pair<String, String>           // word (uppercase), category
    fun displayWord(word: String, guessed: Set<Char>): String  // "H _ N G M _ N"
    fun isWon(word: String, guessed: Set<Char>): Boolean
    fun wrongGuessCount(word: String, guessed: Set<Char>): Int
}
```

The word bank contains **50 words** across five categories: Animals, Countries, Technology, Food, Sports.

---

### Hangman Drawing

The gallows and figure are drawn on a `Canvas` composable using `drawLine`, `drawCircle`, and `drawArc`. The drawing is driven purely by `wrongGuesses: Int` from the ViewModel state:

| Stage | Element drawn |
|---|---|
| 0 | Gallows frame + rope |
| 1 | Head |
| 2 | Body |
| 3 | Left arm |
| 4 | Right arm |
| 5 | Left leg |
| 6 | Right leg |

All measurements are expressed as fractions of `Canvas.size` so the drawing scales correctly on any screen size.

---

### Testing

#### Unit tests (`src/test/`)

| Test class | Cases | Notes |
|---|---|---|
| `GameEngineTest` | 10 | Pure JUnit 5, no coroutines |
| `GamePlayViewModelTest` | 9 | JUnit 5, Turbine, `UnconfinedTestDispatcher`, `FakeLeaderboardRepository` |
| `LeaderboardViewModelTest` | 4 | JUnit 5, Turbine, `FakeLeaderboardRepository` |

`FakeLeaderboardRepository` is an in-memory implementation of `LeaderboardRepository` with a `shouldReturnError` flag to simulate failures.

#### UI tests (`src/androidTest/`)

| Test class | Cases | Notes |
|---|---|---|
| `GamePlayScreenTest` | 4 | Robot pattern via `GamePlayRobot` |
| `LeaderboardScreenTest` | 2 | Direct `ComposeContentTestRule` |

The **Robot pattern** (`GamePlayRobot`) encapsulates all Compose test interactions for the game screen, keeping test intent readable and eliminating duplication.

#### Running the tests

```bash
# Unit tests
./gradlew test

# UI tests (requires connected emulator or device)
./gradlew connectedAndroidTest
```

---

### Project Structure (full)

```
Hangman/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml              ← Version catalog (all deps & versions)
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── res/values/
        │   │   ├── strings.xml
        │   │   └── themes.xml
        │   └── java/com/hangman/app/
        │       ├── HangmanApp.kt
        │       ├── MainActivity.kt
        │       ├── core/
        │       │   ├── domain/util/
        │       │   │   ├── Result.kt
        │       │   │   └── DataError.kt
        │       │   └── presentation/
        │       │       ├── ObserveAsEvents.kt
        │       │       ├── UiText.kt
        │       │       └── designsystem/
        │       │           ├── theme/{Color,Type,HangmanTheme}.kt
        │       │           └── components/{HangmanButton,HangmanScaffold}.kt
        │       └── game/
        │           ├── domain/
        │           │   ├── GameEngine.kt
        │           │   ├── model/GameResult.kt
        │           │   └── data/LeaderboardRepository.kt
        │           ├── data/
        │           │   ├── local/{HangmanDatabase,GameResultDao,GameResultEntity}.kt
        │           │   ├── repository/RoomLeaderboardRepository.kt
        │           │   └── di/GameDataModule.kt
        │           └── presentation/
        │               ├── navigation/{Routes,HangmanNavHost}.kt
        │               ├── home/{HomeState,HomeAction,HomeEvent,HomeViewModel,HomeScreen}.kt
        │               ├── game_play/
        │               │   ├── {GamePlayState,GamePlayAction,GamePlayEvent,GamePlayViewModel,GamePlayScreen}.kt
        │               │   └── components/{HangmanDrawing,WordDisplay,LetterKeyboard}.kt
        │               ├── leaderboard/
        │               │   └── {GameResultUi,LeaderboardState,LeaderboardAction,LeaderboardEvent,LeaderboardViewModel,LeaderboardScreen}.kt
        │               └── di/GamePresentationModule.kt
        ├── test/
        │   └── java/com/hangman/app/game/
        │       ├── domain/GameEngineTest.kt
        │       └── presentation/{FakeLeaderboardRepository,GamePlayViewModelTest,LeaderboardViewModelTest}.kt
        └── androidTest/
            └── java/com/hangman/app/game/presentation/
                ├── GamePlayRobot.kt
                ├── GamePlayScreenTest.kt
                └── LeaderboardScreenTest.kt
```

---

### Building & Running

Requirements: Android Studio Hedgehog or later, JDK 11+.

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Full verification
./gradlew assembleDebug test
```

---

## License

This project is provided for educational purposes.
