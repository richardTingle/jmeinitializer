package com.jmonkeyengine.jmeinitializer.versions;

import lombok.Getter;
import org.apache.logging.log4j.core.util.Integers;

import java.util.ArrayList;
import java.util.List;

/**
 * A class primarily to support sortability of version
 */
public class Version implements Comparable<Version> {

    @Getter
    String fullVersionString;
    List<Integer> numericVersionParts = new ArrayList<>();

    public Version (String fullVersionString) {
        this.fullVersionString = fullVersionString;

        String[] versionParts = fullVersionString.split("\\.");
        for (String versionPart : versionParts) {
            try {
                numericVersionParts.add(Integers.parseInt(versionPart));
            } catch (NumberFormatException e) {
                //this is the non-numeric end of the version number, drop it and stop processing
                break;
            }
        }
    }

    /**
     * Returns the version part at the requested index
     * (E.g. if the version is 3.1.4-Stable passing 2 returns 4)
     *
     * If no part exists at that index then 0 is returned
     */
    private int getVersionPartAtIndex( int index ){
        return numericVersionParts.size()>index?numericVersionParts.get(index):0;
    }

    @Override
    public int compareTo (Version other) {
        int maxPartsToCompare= Math.max(numericVersionParts.size(), other.numericVersionParts.size());
        for(int partIndex=0; partIndex<maxPartsToCompare;partIndex++){
            int thisPartIndex = getVersionPartAtIndex(partIndex);
            int otherPartIndex = other.getVersionPartAtIndex(partIndex);
            if (thisPartIndex != otherPartIndex){
                return Integer.compare(thisPartIndex, otherPartIndex);
            }
            //otherwise go to the next version part
        }
        return 0;
    }
}
