# Weather App Compose

Проект на основе исходного `shablon.zip`: UI переведён на Jetpack Compose, архитектура приведена к MVVM, добавлены отдельные экраны и навигация.

## Что уже есть

- `MainActivity` на `ComponentActivity` + `setContent`.
- UI на Jetpack Compose без Fragment/XML в рабочей схеме.
- `Navigation Compose` для переходов между экранами.
- `WeatherScreen` — погода, прогноз, поиск, геолокация, swipe refresh.
- `SettingsScreen` — адрес сервера, токен сервера, тема, единицы измерения.
- `CitiesScreen` — сохранённые города.
- `AboutScreen` — версия приложения и стек.
- `StateFlow` для состояния экранов.
- `UiEvent` для одноразовых сообщений через snackbar.
- Retrofit API на `suspend fun`.
- Room DAO на `suspend`/`Flow`.
- KSP вместо kapt для Room compiler.
- Koin для ViewModel и репозиториев.
- Coil Compose для погодных иконок.
- SharedPreferences-репозитории для настроек и списка городов.

## Backend proxy

Приложение переведено на схему с сервером:

```text
Android app -> WeatherProxy -> OpenWeatherMap
```

OpenWeatherMap API-key больше не хранится в приложении и не попадает в APK. Ключ хранится только на сервере Windows. В приложении указываются:

```text
Адрес сервера
Токен сервера
```

Сервер находится в каталоге:

```text
server/WeatherProxy
```

Инструкция для Windows 11:

```text
server/WeatherProxy/README_WINDOWS.md
```

## Настройки приложения

На экране настроек укажите:

```text
Адрес сервера: http://192.168.1.10:5055/
Токен сервера: токен из переменной WEATHER_PROXY_TOKEN
```

Для белого IP:

```text
Адрес сервера: http://ВАШ_БЕЛЫЙ_IP:5055/
```

Для HTTPS можно указать:

```text
https://ваш-домен.example/
```

## Единицы измерения

На экране настроек доступны:

- метрические: `°C`, `м/с`;
- имперские: `°F`, `mph`.

После смены единиц измерения обновите погоду свайпом вниз или повторным поиском города.

## Gradle

Проект настроен под AGP 9 и built-in Kotlin.

- Android Gradle Plugin: `9.1.1`
- Gradle Wrapper: `9.3.1`
- Kotlin Compose Plugin: `2.3.21`
- KSP: `2.3.7`
- Compose BOM: `2026.05.00`

Важно: при AGP 9 не подключается `org.jetbrains.kotlin.android` и не используется `kapt`. Kotlin support встроен в AGP, а Room compiler подключён через `ksp`.

Если в архиве нет `gradle/wrapper/gradle-wrapper.jar`, восстановите wrapper через Android Studio или консоль при установленном Gradle:

```bash
gradle wrapper --gradle-version 9.3.1
```

После этого проект собирается обычным способом:

```bash
./gradlew assembleDebug
```
