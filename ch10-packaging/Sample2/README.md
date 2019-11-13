## Modular samples

Download [JavaFX SDK](https://gluonhq.com/products/javafx/) for your operating 
system and unzip to a desired location.

Download [JavaFX SDK Mods](https://gluonhq.com/products/javafx/) for your operating 
system and unzip to a desired location.

### Linux, Mac

    export JAVA_HOME=/Users/<user>/Downloads/jdk-13.jdk/Contents/Home/
    export PATH_TO_FX=/Users/<user>/Downloads/javafx-sdk-12/lib/
    export PATH_TO_FX_MODS=/Users/<user>/Downloads/javafx-jmods-12/

Compile

    javac --module-path $PATH_TO_FX -d mods/modernclients $(find src -name "*.java")
    cp src/org/modernclients/scene.fxml src/org/modernclients/styles.css mods/modernclients/org/modernclients/

Run

    java --module-path $PATH_TO_FX:mods -m modernclients/org.modernclients.Main
    
Custom image

    $JAVA_HOME/bin/jlink --module-path $PATH_TO_FX_MODS:mods --add-modules modernclients --output image
    
Run image

    image/bin/java -m modernclients/org.modernclients.Main

jpackage

    $JAVA_HOME/bin/jpackage create-installer --installer-type dmg -o installer -n Sample2 -m modernclients/org.modernclients.Main --runtime-image image

Produces: `installer/Sample2-1.0.dmg` (double click to install)

### Windows

    set JAVA_HOME="C:\Users\<user>\Downloads\jdk-13"
    set PATH_TO_FX="C:\Users\<user>\Downloads\javafx-sdk-12\lib"
    set PATH_TO_FX_MODS="C:\Users\<user>\Downloads\javafx-jmods-12"

Compile

    dir /s /b src\*.java > sources.txt & javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -d mods\modernclients @sources.txt & del sources.txt
    copy src\org\modernclients\scene.fxml mods\modernclients\org\modernclients\ & copy src\org\modernclients\styles.css mods\modernclients\org\modernclients\

Run

    java --module-path %PATH_TO_FX%;mods -m modernclients/org.modernclients.Main
    
Custom image

    %JAVA_HOME%\bin\jlink --module-path %PATH_TO_FX_MODS%;mods --add-modules modernclients --output image
    
Run image

    image\bin\java -m modernclients/org.modernclients.Main

jpackage

    %JAVA_HOME%\bin\jpackage create-installer --installer-type exe -o installer -n Sample2 -m modernclients/org.modernclients.Main --runtime-image image

Produces: `installer\Sample2-1.0.exe` (double click to install)
