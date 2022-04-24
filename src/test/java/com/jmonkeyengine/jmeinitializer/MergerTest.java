package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.Artifact;
import com.jmonkeyengine.jmeinitializer.libraries.Library;
import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MergerTest {

    @Test
    void mergePath(){
        Merger merger = new Merger("MyGame", "my.excellent.company", List.of(), List.of(), "1", Map.of());
        assertEquals("src/main/java/my/excellent/company/MyGame.java", merger.mergePath("src/main/java/[GAME_PACKAGE_FOLDER]/[GAME_NAME].java"));
        assertEquals("path/something.java", merger.mergePath("path/something.java.jmetemplate"));
        assertEquals(".gitignore",  merger.mergePath("[DOT]gitignore"));

    }

    @Test
    void mergeText(){
        Merger merger = new Merger("My Game!!", "my.excellent.company", List.of(), List.of(), "1", Map.of());

        String testString = """
                This is a test string for [GAME_NAME_FULL]. Open [GAME_NAME].java to start work.
                Also, the package is [GAME_PACKAGE], fyi
                """;

        String expectedString = """
                This is a test string for My Game!!. Open MyGame.java to start work.
                Also, the package is my.excellent.company, fyi
                """;

        assertEquals(expectedString, new String(merger.mergeFileContents(testString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    }

    @Test
    void mergeText_ifStatements(){
        Library testLibraryA = Library.builder("testLibraryA", "A test library",  LibraryCategory.GENERAL, "description").build();

        Library testLibraryB =  Library.builder("testLibraryB", "B test library",  LibraryCategory.GENERAL, "description").build();

        Merger merger = new Merger("My Game!!", "my.excellent.company", List.of(testLibraryA, testLibraryB), List.of("SINGLEPLATFORM"), "1", Map.of());

        String testString2 = """
                                 Bob
                             alice Bob
                             Sam
                             """;

        String out = testString2.replaceAll("^ *Bob\\R", "Bob");


        String testString = """
                [IF=testLibraryA][IF=testLibraryB]A test library and B test library[/IF=testLibraryB][/IF=testLibraryA]
                
                [IF=testLibraryA]A test library
                multiline[/IF=testLibraryB]
                
                [IF=nonExistent]This should not show[/IF=nonExistent]
                
                [IF=nonExistent]This should not show
                multiline
                [/IF=nonExistent]
                
                [IF=nonExistent]
                [IF=nonExistent2]
                
                This should not show
                
                [/IF=nonExistent2]
                [/IF=nonExistent]
                
                
                [IF=SINGLEPLATFORM]This text uses a profile rather than a library[/IF=SINGLEPLATFORM]
                
                [IF=nonExistent]This should not show[/IF=nonExistent][IF=testLibraryA]But this should[/IF=testLibraryA][IF=nonExistent]This should not show[/IF=nonExistent]
                """;

        String expectedString = """
                A test library and B test library
                
                A test library
                multiline
                
                
                
                
                
                This text uses a profile rather than a library
                
                But this should
                """;

        assertEquals(expectedString, new String(merger.mergeFileContents(testString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    }


    @Test
    void pathShouldBeAllowed(){
        Library testLibraryA = Library.builder("testLibraryA", "A test library",  LibraryCategory.GENERAL, "description").build();
        Library testLibraryB = Library.builder("testLibraryB", "B test library",  LibraryCategory.GENERAL, "description").build();

        Merger merger = new Merger("My Game!!", "my.excellent.company", List.of(testLibraryA, testLibraryB), List.of("SINGLEPLATFORM"), "1", Map.of());

        assertTrue(merger.pathShouldBeAllowed("common/or/garden/path"));
        assertTrue(merger.pathShouldBeAllowed("path/[IF=testLibraryA]/path"));
        assertTrue(merger.pathShouldBeAllowed("path/something[IF=testLibraryA]/path/[IF=testLibraryB]/path"));
        assertFalse(merger.pathShouldBeAllowed("path/something[IF=testLibraryA]/path/[IF=testLibraryC]/path"));
        assertFalse(merger.pathShouldBeAllowed("path/[IF=testLibraryC]/path"));

        assertTrue(merger.pathShouldBeAllowed("path/[IF=SINGLEPLATFORM]/path"));

        assertTrue(merger.pathShouldBeAllowed("[IF=testLibraryA][IF=testLibraryB]/src/main/java/[GAME_PACKAGE_FOLDER]/[GAME_NAME].java"));

    }

    @Test
    void mergePath_librariesAndProfiles(){
        Library testLibraryA = Library.builder("testLibraryA", "A test library",  LibraryCategory.GENERAL, "description").build();
        Library testLibraryB = Library.builder("testLibraryB", "B test library",  LibraryCategory.GENERAL, "description").build();

        Merger merger = new Merger("My Game!!", "my.excellent.company", List.of(testLibraryA, testLibraryB), List.of("SINGLEPLATFORM"), "1", Map.of());

        assertEquals("common/or/garden/path", merger.mergePath("common/or/garden/path"));
        assertEquals("path/path", merger.mergePath("path/[IF=testLibraryA]/path"));
        assertEquals("path/path", merger.mergePath("path/[IF=testLibraryA]/[IF=testLibraryB]/path"));
        assertEquals("path/something/path/path", merger.mergePath("/path/something[IF=testLibraryA]/path/[IF=testLibraryB]/path"));
        assertEquals("path/path", merger.mergePath("path/[IF=SINGLEPLATFORM]/path"));
        assertEquals("path/path", merger.mergePath("path/[IF=SINGLEPLATFORM][IF=JME_DESKTOP]/path"));
        assertEquals("path1/path2", merger.mergePath("[IF=SINGLEPLATFORM][IF=JME_DESKTOP]/path1/path2"));

    }


    @Test
    void sanitiseToPackage () {
        assertEquals("mysuggestedpackage", Merger.sanitiseToPackage("mySuggestedPackage£$"));
        assertEquals("co.uk.company", Merger.sanitiseToPackage("co.uk.company"));
        assertEquals("co.uk.company", Merger.sanitiseToPackage("Co.Uk.Company"));
        assertEquals("co.uk.company", Merger.sanitiseToPackage("..co..uk..company.."));
    }

    @Test
    void convertPackageToFolder () {
        assertEquals("mysuggestedpackage", Merger.convertPackageToFolder("mysuggestedpackage"));
        assertEquals("my/suggested/package", Merger.convertPackageToFolder("my.suggested.package"));
    }

    @Test
    void sanitiseToJavaClass () {
        assertEquals("MyGame", Merger.sanitiseToJavaClass("!!!{}@~:@:@"));
        assertEquals("MyAmazingGame", Merger.sanitiseToJavaClass("%My Amazing Game!!"));
        assertEquals("MyGame", Merger.sanitiseToJavaClass(""));
        assertEquals("AlreadyCamelCase", Merger.sanitiseToJavaClass("AlreadyCamelCase"));
        assertEquals("LowerCaseSentence", Merger.sanitiseToJavaClass("lower case sentence"));
        assertEquals("Lowercaseword", Merger.sanitiseToJavaClass("lowercaseword"));
        assertEquals("SentenceWithExcessiveSpace", Merger.sanitiseToJavaClass("  Sentence  with   excessive space  "));
    }

    @Test
    void artifactsAddedCorrectly () {
        String testString = """
                            [ALL_NON_JME_DEPENDENCIES]
                            """;

        String expectedString = """
                                    implementation 'group:artA:1.2.3'
                                    implementation 'group:artB:1.2.4'
                                """;

        Library testLibraryA = Library.builder("testLibraryA", "A test library",  LibraryCategory.GENERAL, "description").build();
        Artifact artifactA = new Artifact();
        artifactA.setGroupId("group");
        artifactA.setArtifactId("artA");
        artifactA.setPinVersion("1.2.3");
        Artifact artifactB = new Artifact();
        artifactB.setGroupId("group");
        artifactB.setArtifactId("artB");
        artifactB.setFallbackVersion("1.2.3");
        testLibraryA.setArtifacts(List.of(artifactA, artifactB));

        Merger merger = new Merger("", "", List.of(testLibraryA), List.of("SINGLEPLATFORM"), "1", Map.of("group:artA", "1.2.4", "group:artB", "1.2.4"));
        assertEquals(expectedString.trim(), new String(merger.mergeFileContents(testString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8).trim());
    }

    @Test
    void additionalRepositoriesAddedCorrectly() {
        String testString = """
                            buildscript {
                                repositories {
                                    [MAVEN_REPOS]
                                }
                            }
                            """;
        String expectedString = """
                            buildscript {
                                repositories {
                                    jcentre()
                                    mavenCentral()
                                    mavenLocal()
                                }
                            }
                            """;

        Library testLibraryA = Library.builder("testLibraryA", "A test library",  LibraryCategory.GENERAL, "description").build();
        testLibraryA.setAdditionalMavenRepos(List.of("jcentre()"));

        Merger merger = new Merger("", "", List.of(testLibraryA), List.of("SINGLEPLATFORM"), "1", Map.of());
        assertEquals(expectedString, new String(merger.mergeFileContents(testString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    }
}