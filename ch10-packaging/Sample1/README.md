## Non-modular samples

Download [JavaFX SDK](https://gluonhq.com/products/javafx/) for your operating 
system and unzip to a desired location.

Download [JavaFX SDK Mods](https://gluonhq.com/products/javafx/) for your operating 
system and unzip to a desired location.

### Linux, Mac

    export JAVA_HOME=/Users/<user>/Downloads/jdk-13.jdk/Contents/Home/
    export PATH_TO_FX=/Users/<user>/Downloads/javafx-sdk-12/lib/
    export PATH_TO_FX_MODS=/Users/<user>/Downloads/javafx-jmods-12/

Compile

    javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")
    cp src/org/modernclients/scene.fxml src/org/modernclients/styles.css out/org/modernclients/

Run

    java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp out org.modernclients.Main

jar

    mkdir libs
    jar --create --file=libs/sample1.jar --main-class=org.modernclients.Main -C out .

jpackage

    $JAVA_HOME/bin/jpackage create-installer --installer-type dmg -o installer -i libs --main-jar sample1.jar -n Sample1 --module-path $PATH_TO_FX_MODS --add-modules javafx.controls,javafx.fxml --main-class org.modernclients.Main

Produces: `installer/Sample1-1.0.dmg` (double click to install)

Optional:

jpackage with custom icon

    $JAVA_HOME/bin/jpackage create-installer --installer-type dmg -o installer -i libs --main-jar sample1.jar -n Sample1 --module-path $PATH_TO_FX_MODS --add-modules javafx.controls,javafx.fxml --main-class org.modernclients.Main --icon assets/mac/openduke.icns

### Windows

    set JAVA_HOME="C:\Users\<user>\Downloads\jdk-13"
    set PATH_TO_FX="C:\Users\<user>\Downloads\javafx-sdk-12\lib"
    set PATH_TO_FX_MODS="C:\Users\<user>\Downloads\javafx-jmods-12"

Compile

    dir /s /b src\*.java > sources.txt & javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -d out @sources.txt & del sources.txt
    copy src\org\modernclients\scene.fxml out\org\modernclients\ & copy src\org\modernclients\styles.css out\org\modernclients\

Run

    java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp out org.modernclients.Main

jar

    mkdir libs
    jar --create --file=libs\sample1.jar --main-class=org.modernclients.Main -C out .

jpackage

    %JAVA_HOME%\bin\jpackage create-installer --installer-type exe -o installer -i libs --main-jar sample1.jar -n Sample1 --module-path %PATH_TO_FX_MODS% --add-modules javafx.controls,javafx.fxml --main-class org.modernclients.Main

Produces: `installer\Sample1-1.0.exe` (double click to install)

Optional:

jpackage with custom icon, menu, shortcut and installation directory options

    %JAVA_HOME%\bin\jpackage create-installer --installer-type exe -o installer -i libs --main-jar sample1.jar -n Sample1 --module-path %PATH_TO_FX_MODS% --add-modules javafx.controls,javafx.fxml --main-class org.modernclients.Main --win-menu --win-shortcut --win-dir-chooser --icon assets\win\openduke.icns

