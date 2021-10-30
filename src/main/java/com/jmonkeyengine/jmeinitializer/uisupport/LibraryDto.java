package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public LibraryDto (Library library) {
        this(library.key(), library.displayName(), library.descriptionText(), library.defaultSelected());
    }
}
