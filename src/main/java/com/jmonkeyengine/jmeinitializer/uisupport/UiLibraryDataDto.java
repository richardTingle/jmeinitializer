package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is all the information the front end will need to render its options in a single packet
 */
@Data
@AllArgsConstructor
public class UiLibraryDataDto {

    public static UiLibraryDataDto INSTANCE;

    static{
        List<CategoryAndLibrariesDto> specialCategories = new ArrayList<>();
        Arrays.stream(LibraryCategory.values())
                .filter(c -> c!= LibraryCategory.JME_PLATFORM && c!= LibraryCategory.JME_GENERAL && c!= LibraryCategory.GENERAL)
                .forEach(c -> specialCategories.add(new CategoryAndLibrariesDto(new CategoryDto(c), Library.librariesOfCategory(c).stream().map(LibraryDto::new).collect(Collectors.toList()))));


        INSTANCE = new UiLibraryDataDto(
                Library.librariesOfCategory(LibraryCategory.JME_PLATFORM).stream().map(LibraryDto::new).collect(Collectors.toList()),
                Library.librariesOfCategory(LibraryCategory.JME_GENERAL).stream().map(LibraryDto::new).collect(Collectors.toList()),
                specialCategories,
                Library.librariesOfCategory(LibraryCategory.GENERAL).stream().map(LibraryDto::new).collect(Collectors.toList())
        );
    }

    List<LibraryDto> jmePlatforms;

    List<LibraryDto> jmeGeneralLibraries;

    List<CategoryAndLibrariesDto> specialCategories;

    List<LibraryDto> generalLibraries;
}
