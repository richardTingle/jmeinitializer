package com.jmonkeyengine.jmeinitializer.libraries;

import java.util.Collection;
import java.util.List;

public record Library (
        /*
         * Used to uniquely represent the library in the UI and elsewhere
         */
        String key,
        String displayName,

        String groupId,
        Collection<Artifact> artifacts,
        boolean usesJmeVersion,
        LibraryCategory category,
        boolean defaultSelected,
        String descriptionText,

        /*
         * When searching for library versions the application uses this regex to determine if its a "release" version
         * (And not a beta, release candidate etc)
         */
        String libraryVersionRegex
) {

    public Library ( String key, String displayName, String groupId, String artifactIds, boolean usesJmeVersion, LibraryCategory category, boolean defaultSelected, String descriptionText) {
        this(key, displayName, groupId, List.of(new Artifact(artifactIds, null)), usesJmeVersion, category, defaultSelected, descriptionText, "[\\.\\d]*");
    }

    public Library ( String key, String displayName, String groupId, Collection<Artifact> artifactIds, boolean usesJmeVersion, LibraryCategory category, boolean defaultSelected, String descriptionText) {
        this(key, displayName, groupId, artifactIds, usesJmeVersion, category, defaultSelected, descriptionText, "[\\.\\d]*");
    }

}
