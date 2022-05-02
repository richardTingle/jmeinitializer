package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This dto represents json sent to the front end
 */
@Data
@AllArgsConstructor
public class CategoryDto {
    @Schema( example = "PHYSICS", description = "The key for the category")
    String key;
    @Schema( example = "Physics Libraries", description = "The human readable name for the category")
    String categoryDisplayName;
    @Schema( example = "A physics library handles collisions and forces like gravity", description = "The human readable description for the category")
    String categoryDescription;
    @Schema( description = "If true the category should be presented as radio buttons, if false then as check boxes")
    boolean onlyOneAllowed;

    public CategoryDto (LibraryCategory category) {
        this(category.name(), category.getDisplayName(), category.getDescription(), category.isOnlyOneAllowed());
    }
}
