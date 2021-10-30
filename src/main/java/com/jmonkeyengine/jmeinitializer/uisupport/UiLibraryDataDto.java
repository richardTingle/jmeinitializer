package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is all the information the front end will need to render its options in a single packet.
 *
 * This dto represents json sent to the front end
 */
@Data
@AllArgsConstructor
public class UiLibraryDataDto {

    List<LibraryDto> jmePlatforms;

    List<LibraryDto> jmeGeneralLibraries;

    List<CategoryAndLibrariesDto> specialCategories;

    List<LibraryDto> generalLibraries;

    /**
     * These are the libraries that are selected by default in jmeGeneralLibraries and generalLibraries (ones where you
     * can choose as many as you like)
     */
    List<String> defaultSelectedFreeChoiceLibraries;

    String defaultPlatform;
}
