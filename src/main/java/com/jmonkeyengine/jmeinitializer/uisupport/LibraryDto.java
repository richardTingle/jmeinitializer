package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * This dto represents json sent to the front end
 */
@Data
@AllArgsConstructor
@Schema(description = "The structure of json as sent to the front end to be used in building a UI")
public class LibraryDto {

    @Schema( example = "TAMARIN",  description = "A key for the library, most obviously used when constructing the list of required libraries")
    String key;

    @Schema( example = "Tamarin", description ="A human readable short name for the library")
    String libraryName;

    @Schema( example = "A VR Library with hands and OpenVr support", description = "A longer piece of text (e.g. a sentence or two) describing the library")
    String libraryDescription;

    @Schema( description = "If this library should be presented pre ticked in the UI")
    boolean selectedByDefault;

    @Schema( example = "[\"JME_DESKTOP\"]", description = "If this library should only be presented as an option if a certain platform has been selected (e.g. only VR libraries if the VR platform has been selected). If empty the library will be available for all platforms. If populated the UI should only make the value available for selection if the platform is selected")
    Collection<String> requiredPlatforms;

    public LibraryDto (Library library) {
        this(library.getKey(), library.getDisplayName(), library.getDescriptionText(), library.isDefaultSelected(), library.getRequiredPlatforms());
    }
}
