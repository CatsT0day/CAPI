# CAPI - a lots of commands, API and other!

# What is it?
  CAPI is open source minecraft plugin, 
  made by one developer, inpired by CMI and essentials
  currenlty has 24 commands, and powerful API
### building project from src:
```bash

# Build the plugin (creates shadowed jar with all NMS versions)
./gradlew build

# Build without running tests
./gradlew build -x test

# Clean build
./gradlew clean build

# Run a test server (Paper 1.21.11)
./gradlew runServer
```
### Testing
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:test

# Run PackMerger debug tool
./gradlew :core:runPackMergerDebug --args="path/to/pack"
```
### how to use API:
