package com.jmonkeyengine.jmeinitializer.uisupport;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * This dto represents json sent to the front end
 */
@AllArgsConstructor
@Data
public class CategoryAndLibrariesDto {
    CategoryDto category;
    List<LibraryDto> libraries;

    /**
     * Can be null. In the category radio shown in the UI the library with this key is pre selected
     */
    String defaultLibrary;
}
