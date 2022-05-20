package com.jmonkeyengine.jmeinitializer.uisupport;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
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

    @Schema( example = "[\"JME_DESKTOP\"]", description =
    """
        If this library should only be presented as an option if a certain platform has been selected (e.g. only VR libraries if the VR platform has been selected). If empty the library will be available for all platforms. 
        
        If populated the UI should only make the value available for selection if the platform is selected.
        
        Note that this includes both libraries that are "specialised" to a platform and just only relevant if a platform
        is selected. The UI shouldn't care about the difference but the initializer itself will when creating the 
        template
    """)
    Collection<String> requiredPlatforms;

    @Schema( example = "[\"JME_ANDROID\"]", description = "If a platform is selected this library will not be allowed to be selected. If any ONE of the required platforms is present the library is unavailable. Note; this is platforms and deployment options", defaultValue = "No incompatible platform")
    Collection<String> incompatiblePlatformsAndDeployments;


    public static LibraryDto libraryDtoFromLibrary (Library library, Collection<String> allPlatforms, Collection<String> allDeployments) {
        return new LibraryDto(library.getKey(), library.getDisplayName(), library.getDescriptionText(), library.isDefaultSelected(), ListUtils.union(library.getRequiredPlatforms(), library.getSpecialisedToPlatforms()), formIncompatibleDeploymentsAndPlatforms(library, allPlatforms, allDeployments));
    }

    public static List<String> formIncompatibleDeploymentsAndPlatforms(Library library, Collection<String> allPlatforms, Collection<String> allDeployments ){
        Collection<String> compatiblePlatforms = library.getCompatiblePlatforms();
        Collection<String> compatibleDeployments = library.getCompatibleDeployments();
        List<String> incompatibleList = new ArrayList<>();
        if (!compatiblePlatforms.isEmpty()){
            incompatibleList.addAll(allPlatforms);
            incompatibleList.removeAll(compatiblePlatforms);
        }
        if (!compatibleDeployments.isEmpty()){
            incompatibleList.addAll(allDeployments);
            incompatibleList.removeAll(compatibleDeployments);
        }
        return incompatibleList;
    }
}
