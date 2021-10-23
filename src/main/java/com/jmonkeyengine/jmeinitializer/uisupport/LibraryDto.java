package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LibraryDto {

    String key;

    String libraryName;

    String libraryDescription;

    boolean selectedByDefault;

    public LibraryDto (Library library) {
        this(library.name(), library.getDisplayName(), library.getDescriptionText(), library.isDefaultSelected());
    }
}
