package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * This dto represents json sent to the front end
 */
@Data
@AllArgsConstructor
public class LibraryDto {

    String key;

    String libraryName;

    String libraryDescription;

    /**
     * If true this library will be preselected in the UI
     */
    boolean selectedByDefault;

    /**
     * If this library will only be available if one of these platforms is selected
     */
    Collection<String> requiredPlatforms;

    public LibraryDto (Library library) {
        this(library.getKey(), library.getDisplayName(), library.getDescriptionText(), library.isDefaultSelected(), library.getRequiredPlatforms());
    }
}
