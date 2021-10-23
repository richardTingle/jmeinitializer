package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDto {
    String categoryDisplayName;
    String categoryDescription;
    boolean onlyOneAllowed;

    public CategoryDto (LibraryCategory category) {
        this(category.getDisplayName(), category.getDescription(), category.isOnlyOneAllowed());
    }
}
