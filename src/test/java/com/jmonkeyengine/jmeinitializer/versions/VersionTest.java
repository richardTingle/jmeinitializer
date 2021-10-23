package com.jmonkeyengine.jmeinitializer.versions;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    /**
     * Tests that the version comparator correctly sorts versions with a variety of lengths and suffixes
     * (Suffixes are ignored)
     */
    @Test
    public void testSortsCorrectly(){
        List<String> toBeSortedList = new ArrayList<>();
        toBeSortedList.add("2.4.0-stable");
        toBeSortedList.add("3.4.0-situpVersion");
        toBeSortedList.add("3.3.0-baboon");
        toBeSortedList.add("3.3.1");
        toBeSortedList.add("3.2-stable");
        toBeSortedList.add("3.2.1");
        toBeSortedList.add("3.4.2-badger");
        toBeSortedList.add("1-orien");

        Collections.sort(toBeSortedList);

        List<String> correctOrderList = new ArrayList<>();
        correctOrderList.add("1-orien");
        correctOrderList.add("2.4.0-stable");
        correctOrderList.add("3.2-stable");
        correctOrderList.add("3.2.1");
        correctOrderList.add("3.3.0-baboon");
        correctOrderList.add("3.3.1");
        correctOrderList.add("3.4.0-situpVersion");
        correctOrderList.add("3.4.2-badger");

        assertEquals(correctOrderList, toBeSortedList);
    }

}