## Gradle + modular samples

### Linux, Mac

    export JAVA_HOME=/Users/<user>/Downloads/jdk-13.jdk/Contents/Home/

Run

    ./gradlew clean run
    
Custom image

    ./gradlew jlink
    
Run image

    build/image/bin/sample3

jpackage

    ./gradlew jpackage

Produces: `build/jpackage/Sample3-1.0.dmg` (double click to install)

### Windows

    set JAVA_HOME=C:\Users\<user>\Downloads\jdk-13

Run

    gradlew clean run
    
Custom image

    gradlew jlink
    
Run image

    build\image\bin\sample3

jpackage

    gradlew jpackage

Produces: `build\jpackage\Sample3-1.0.exe` (double click to install)