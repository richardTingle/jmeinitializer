package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "The structure of json returned for use by front ends to describe available libraries etc")
public class UiLibraryDataDto {

    @Schema(description = "The libraries that represent platforms JME applications can run with, e.g. desktop")
    List<LibraryDto> jmePlatforms;

    @Schema(description = "The libraries that JME provides that don't fit into another category")
    List<LibraryDto> jmeGeneralLibraries;

    @Schema(description = "A series of categories and the libraries in that category")
    List<CategoryAndLibrariesDto> specialCategories;

    @Schema(description = "Non JME libraries that don't fit into another category")
    List<LibraryDto> generalLibraries;

    @Schema(example = "[\"TAMARIN\"]", description = "These are the libraries that are selected by default in jmeGeneralLibraries and generalLibraries (ones where you can choose as many as you like")
    List<String> defaultSelectedFreeChoiceLibraries;

    @Schema(example = "JME_DESKTOP", description = "The platform that should be selected by default in the UI")
    String defaultPlatform;
}
