# README #

### What is this repository for? ###

This is a server side application to support rest calls (probably from a web UI "somewhere") that will produce a starter
gradle JMonkey project as a zip based on user choices.  

It also contains an optional UI written in react, the styling for this is an "interesting" mix of bootstrap and
the JMonkey engine website styling. So it looks in keeping with the rest of Jmonkey.org, but is a little eccentric and 
a lot of bootstrap stuff doesn't work properly

There is a general approach of merge fields which allow both file path segments and file content to be replaced with user data

### How do I get set up? ###

* You will need a java 17 JDK & IDE of your choice that supports gradle
* Start the application by running JmeInitializerApplication
* Go to localhost:8080 to see its UI

### How do I run it in prod

Run the gradle task bootjar, that will produce a jar under build\libs\. Rename the jar jmeinitializer.jar then put it 
whereever you want it. Then run:

java -jar -Dspring.profiles.active=prod jmeinitializer.jar

The -Dspring.profiles.active=prod changes the post it boots on to 80.

### How does templating work

See the folder jmetemplate, fundamentally that is what ends up in the zip, but:

* File paths can have [IF=????]
  * If the library in the ???? is active that path is included, otherwise it isn't
  * Special cases of [IF=SINGLEPLATFORM] and [IF=MULTIPLATFORM] are also supported
  * Where a folder only has an if statement, e.g. jmetemplate/[IF=SINGLEPLATFORM]/stuff then (if it is included at all) the empty folder is eliminated, becoming jmetemplate/stuff
* File paths are scanned for merge fields. e.g. java/[GAME_PACKAGE_FOLDER]/[GAME_NAME] 
* Text files are scanned for merge fields (see MergeField.java) and replaced with their contents. Eg. mainClassName = '[GAME_PACKAGE].[GAME_NAME]'
* Text files can also have [IF=????] statements in them. Ended by [/IF=????]
  * E.g. [IF=JME_ANDROID]Android module :app : holds build.gradle for the android dependencies & implements the :game module, this module can hold android dependent gui.[/IF=JME_ANDROID]
  * These can be nested, and can be multiline

Additionally, optionally, you can end any file .jmetemplate. This has no actual function, but it is stripped from the 
output file name. Its purpose is to stop IDEs from trying to do error highlighting on known file types (e.g. gradle files)
where the IF and merge fields make the files look to the IDE as errored

### Who do I talk to? ###

* Original creator: richtea