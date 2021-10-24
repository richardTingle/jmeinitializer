package com.jmonkeyengine.jmeinitializer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MergerTest {

    @Test
    void sanitiseToPackage () {
        assertEquals("mysuggestedpackage", Merger.sanitiseToPackage("mySuggestedPackage£$"));
        assertEquals("co.uk.company", Merger.sanitiseToPackage("co.uk.company"));
        assertEquals("co.uk.company", Merger.sanitiseToPackage("Co.Uk.Company"));
        assertEquals("co.uk.company", Merger.sanitiseToPackage("..co..uk..company.."));
    }

    @Test
    void convertPackageToFolder () {
        assertEquals("mysuggestedpackage/", Merger.convertPackageToFolder("mysuggestedpackage"));
        assertEquals("my/suggested/package/", Merger.convertPackageToFolder("my.suggested.package"));
    }

    @Test
    void sanitiseToJavaClass () {
        assertEquals("MyGame", Merger.sanitiseToJavaClass("!!!{}@~:@:@"));
        assertEquals("MyAmazingGame", Merger.sanitiseToJavaClass("%My Amazing Game!!"));
        assertEquals("MyGame", Merger.sanitiseToJavaClass(""));
        assertEquals("AlreadyCamelCase", Merger.sanitiseToJavaClass("AlreadyCamelCase"));
    }
}