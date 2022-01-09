# [GAME_NAME_FULL]

This is the readme for [GAME_NAME_FULL], try to keep it up to date with any information future-you will wish past-you
remembered to write down

## Project set up
This is a gradle project using JMonkey Engine, Android and other java libraries

# Running Game : 

Install the app on a connected device via adb.

```bash
    $./gradlew :app:installDebug
```
If you are using Android Studio launch and debug options will be available directly within the IDE

# Building Game :

```gradle
./gradlew :app:assemble
```

Note that if you are releasing your app on the android play store it must be signed with an appropriate key, see
https://developer.android.com/studio/build/building-cmdline for more details on how to produce such apks

References : 

=> gradlew for android:
https://developer.android.com/studio/build/building-cmdline

=> Gradle DSL : https://docs.gradle.org/current/dsl/index.html

=> Gradle for java : https://docs.gradle.org/current/userguide/multi_project_builds.html

=> Gradle/Groovy Udacity course by google : https://github.com/udacity/ud867/blob/master/1.11-Exercise-ConfigureFileSystemTasks/solution.gradle

=> See JMonkeyEngine Android Examples : https://github.com/Scrappers-glitch/jme3-Simple-Examples <br/>
https://github.com/Scrappers-glitch/DBTraining <br/>
https://github.com/Scrappers-glitch/Superior-Extended-Engine/tree/master/demoApp <br/>

## txt vs md

This readme is provided as a .txt as that is a common format openable on any machine. However, it would more normally be a .md, this will allow it to be nicely formatted by most git repositories (assuming you commit it to git). Just change the extension from .txt to .md, the syntax is already correct for an md file
