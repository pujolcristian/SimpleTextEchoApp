# Simple Text Echo

A minimal **Kotlin + Jetpack Compose** app that lets a user enter text, simulates a server validation, and then either displays the **echoed text** or an **inline error**. The project includes **unit tests** (repository, use case, ViewModel) and a **Compose UI test**.

> **Spec focus**: Provide an input field and a submit action. On submit, **pretend to call an external server** (simulated latency).  
> **Success** → show the **trimmed** text below the input.  
> **Failure** → show an **inline error** in the UI.

---

## Structure

- `presentation/echo` — Compose UI: `EchoScreen`, `InputCard`, `ResultCard`; `EchoViewModel` for state and events.
- `domain/usecase` — `ValidateStringUseCase` orchestrates the validation intent.
- `data/repository` — `EchoRepositoryImpl` simulates a server with latency and simple rules.

---

## Architecture (Clean MVVM)

- **State**: `EchoViewModel` exposes `StateFlow<EchoUiState>` (input, loading, result, error).
- **Use case**: `ValidateStringUseCase(text)` calls the repository and returns the echoed value or throws.
- **Repository**: `EchoRepositoryImpl` simulates latency (`delay(400)`), trims input, applies demo failure rules.
- **UI**: `EchoScreen` renders state; `InputCard` and `ResultCard` keep single-responsibility boundaries.

---

## Behavior

- **Input**: user types in `CustomTextField`.
- **Submit**: triggers the fake validation.
- **Success**: `ResultCard` shows a success banner and the **trimmed** text.
- **Failure**: inline error message under the text field.
- **UX**: `verticalScroll(rememberScrollState())` + `.imePadding()` to avoid keyboard overlap; button shows loading during submission.

**Validation rules (demo):**
- Empty after `trim()` → error.
- Contains `"fail"` (case-insensitive) → error.
- Otherwise → return trimmed text.

---

## Key Choices

- **Compose** for clear, state-driven UI.
- **MVVM + one use case** to keep intent explicit and testable.
- **Light DI (Hilt)** for simple wiring; easily removable.

---

## Running & Testing

**Requirements**: Android Studio (Hedgehog or newer), JDK 17.

```bash
# JVM unit tests
./gradlew testDebugUnitTest

# UI tests (instrumentation; emulator or device required)
./gradlew connectedAndroidTest
```