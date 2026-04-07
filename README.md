# Nexa Launcher

Lightweight Android launcher built with Kotlin + Jetpack Compose.

## Highlights
- Home replacement launcher (`CATEGORY_HOME` + `CATEGORY_DEFAULT`)
- Fast app grid with configurable layout and icon size
- Swipe up app drawer, swipe down quick search
- Long press edit mode (favorite / hide apps)
- Basic app widget host support
- MVVM + Hilt + Room + DataStore
- Backup/restore launcher config in JSON
- Dark / light / system theme

## Stack
- Kotlin
- Jetpack Compose
- MVVM
- Hilt DI
- Room (app prefs cache)
- DataStore (settings)
- Coroutines + Flow

## Project Structure
```
launcher-app/
├── app/
│   ├── ui/
│   ├── viewmodel/
│   ├── data/
│   ├── domain/
│   ├── di/
│   ├── utils/
│   └── main/
├── assets/
├── icons/
├── themes/
├── keystore/
└── .github/workflows/
```

## Build Local
1. Generate keystore (optional for local release signing):
```bash
./scripts/generate_keystore.sh release.keystore launcher_key
```
2. Build debug:
```bash
./gradlew assembleDebug
```
3. Build release (signed if env vars are provided):
```bash
KEYSTORE_FILE=$PWD/release.keystore \
KEYSTORE_PASSWORD=*** \
KEY_ALIAS=launcher_key \
KEY_PASSWORD=*** \
./gradlew assembleRelease
```

## GitHub Actions + Secrets
Required repo secrets:
- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

Workflow file: `.github/workflows/android.yml`

## Permissions
- `QUERY_ALL_PACKAGES` is used to list launchable apps for full launcher behavior.

## Performance Notes
- Lazy list rendering for app grid and drawer
- No background service loops
- Flow-driven UI state updates

## Testing Checklist
- Cold start on low-end emulator
- App drawer search responsiveness
- Gesture latency (swipe and long press)
- Widget pick and restore flow
