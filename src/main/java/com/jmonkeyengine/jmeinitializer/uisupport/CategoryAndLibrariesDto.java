package com.jmonkeyengine.jmeinitializer.uisupport;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CategoryAndLibrariesDto {
    CategoryDto category;
    List<LibraryDto> libraries;
}
