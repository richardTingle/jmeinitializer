package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Library {

    /*
     * Used to uniquely represent the library in the UI and elsewhere
     */
    String key;
    String displayName;

    @Singular
    Collection<Artifact> artifacts;

    boolean usesJmeVersion = false;
    LibraryCategory category;
    boolean defaultSelected;
    String descriptionText;

    /*
     * When searching for library versions the application uses this regex to determine if it's a "release" version
     * (And not a beta, release candidate etc)
     */
    String libraryVersionRegex = "[\\.\\d]*";

    /**
     * If a library requires a particular platform (e.g. Tamarin requires VR) then this will prevent it being selected
     * unless that platform is selected. Additionally, in multiplatform projects it will appear only in relevant modules.
     *
     * Only the keys are listed here
     */
    @Singular()
    Collection<String> requiredPlatforms = List.of();

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return key.equals(library.key);
    }

    @Override
    public int hashCode(){
        return Objects.hash(key);
    }
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
