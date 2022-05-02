package com.jmonkeyengine.jmeinitializer.uisupport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * This dto represents json sent to the front end
 */
@AllArgsConstructor
@Data
public class CategoryAndLibrariesDto {
    @Schema( description = "Details on the category itself")
    CategoryDto category;
    @Schema( description = "The libraries in this category")
    List<LibraryDto> libraries;
    @Schema( description = "For radio types only, the library with this key is pre selected. May be null for no preselection")
    String defaultLibrary;
}
