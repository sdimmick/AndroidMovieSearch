# AndroidMovieSearch
Search for movies via the OMDb API.

# Building from the command line

```
./gradlew assembleDebug
```

# Tests

I opted to add instrumentation tests for the main search / movie details
result parsing service, since it relies heavily on Android standard
JSONObject implementation.

# Security

All log statements below WARN are stripped from release builds via Proguard
to avoid logging sensitive information.

To build a release version of the APK from the command line:

```
./gradlew assembleRelease
```
