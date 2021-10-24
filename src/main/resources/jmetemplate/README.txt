# [GAME_NAME_FULL]

This is the readme for [GAME_NAME_FULL], try to keep it up to date with any information future-you will wish past-you
remembered to write down

## Project set up
This is a gradle project using JMonkey Engine and other java libraries

## How to run (for development)
You'll want a java 11 JDK installed on your machine (Your IDE may do this for you, IntelliJ does)

Open this application in your preferred IDE (IntelliJ and Eclipse will support Gradle by default, netbeans will support it with a plugin). The remaining instructions are for IntelliJ but the basic principle will be the same for any IDE)

### Development in IntelliJ
- Download the latest version of IntelliJ Community (IntelliJ Ultimate is a paid for version the features of which you may consider useful but are not essential for a JMonkey project)
- File > Open > select the top level folder of this project ( i.e. [GAME_NAME]) > Ok.
- The project will open with your project files on the left had side (IntelliJ may need to "think" for a couple of seconds before they appear)
- IntelliJ may say "No SDK set up" and prompt you to download one, follow its instructions and allow it to download a java 11 JDK. A JDK is used for compiling java applications, a JRE is used for running them.
- You can now add more java source files or assets to the project
- To run the project find [GAME_NAME].java (which will be in under src/main/java/[GAME_PACKAGE_FOLDER]) and right click > Run '[GAME_NAME]'

## How to package the game

TO BE FILLED IN

## Next Steps
You may wish to commit your project to a git repository to keep track of your changes (so you can roll back if anything goes wrong)

## Adding more libraries
During the JMonkey Initializer you chose from a small subset of the available java libraries.
You can add more by editing the dependencies section in the build.gradle file

## txt vs md

This readme is provided as a .txt as that is a common format openable on any machine. However, it would more normally be a .md, this will allow it to be nicely formatted by most git repositories (assuming you commit it to git). Just change the extension from .txt to .md, the syntax is already correct for an md file

