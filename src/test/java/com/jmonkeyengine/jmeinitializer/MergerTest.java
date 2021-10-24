package com.jmonkeyengine.jmeinitializer;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MergerTest {

    @Test
    void mergePath(){
        Merger merger = new Merger("MyGame", "my.excellent.company", List.of(), "1", Map.of());
        assertEquals("/src/main/java/my/excellent/company/MyGame.java", merger.mergePath("/src/main/java/[GAME_PACKAGE_FOLDER]/[GAME_NAME].java"));
    }

    @Test
    void mergeText(){
        Merger merger = new Merger("My Game!!", "my.excellent.company", List.of(), "1", Map.of());

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
    }
}