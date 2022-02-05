package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Collection;

@Builder
@Value
public class Library {

    /*
     * Used to uniquely represent the library in the UI and elsewhere
     */
    String key;
    String displayName;

    @Singular
    Collection<Artifact> artifacts;

    boolean usesJmeVersion;
    LibraryCategory category;
    boolean defaultSelected;
    String descriptionText;

    /*
     * When searching for library versions the application uses this regex to determine if it's a "release" version
     * (And not a beta, release candidate etc)
     */
    String libraryVersionRegex;

    /**
     * If a library requires a particular platform (e.g. Tamarin requires VR) then this will prevent it being selected
     * unless that platform is selected. Additionally, in multiplatform projects it will appear only in relevant modules.
     *
     * Only the keys are listed here
     */
    @Singular()
    Collection<String> requiredPlatforms;

    public static Library.LibraryBuilder builder(String key, String displayName, LibraryCategory category, String descriptionText ){
        Library.LibraryBuilder builder = new Library.LibraryBuilder();
        builder.key(key)
                .displayName(displayName)
                .descriptionText(descriptionText)
                .category(category)
                .libraryVersionRegex("[\\.\\d]*");
        return builder;
    }

}
