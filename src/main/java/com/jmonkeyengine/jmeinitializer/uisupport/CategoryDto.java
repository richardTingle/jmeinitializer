package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This dto represents json sent to the front end
 */
@Data
@AllArgsConstructor
public class CategoryDto {
    String key;
    String categoryDisplayName;
    String categoryDescription;
    boolean onlyOneAllowed;

    public CategoryDto (LibraryCategory category) {
        this(category.name(), category.getDisplayName(), category.getDescription(), category.isOnlyOneAllowed());
    }
}
