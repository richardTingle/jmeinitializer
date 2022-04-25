package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;
import com.jmonkeyengine.jmeinitializer.uisupport.UiLibraryDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Represents endpoints used by the front end
 */
@RestController
public class InitializerRestController {

    private static final String GAME_NAME_DOC_STRING = "The name of the game, will be sanitised to something like MyExcellentGame. Caller is not required to sanitise";
    private static final String GAME_DESCRIPTION_DOC_STRING = "The proposed package for the games source, e.g. com.example. Can be blank. Caller is not required to sanitise";
    private static final String REQUIRED_LIBRARIES_DOC_STRING = "A comma delimited list of the library keys for the libraries the user requests. E.g. `JME_DESKTOP,LEMUR,LOG4J2`";

    private final InitializerZipService initializerZipService;
    private final LibraryService libraryService;

    public InitializerRestController (InitializerZipService initializerZipService, LibraryService libraryService) {
        this.initializerZipService = initializerZipService;
        this.libraryService = libraryService;
    }

    @Operation( summary = "The available library description json", description = "Returns a json packet that includes data on all the libraries that the initializer has to offer. \n\nIntended to be used by a UI application to display the options available to the user")
    @GetMapping("/jme-initializer/libraries")
    public UiLibraryDataDto getDataForUi(){
        return libraryService.getUiLibraryDataDto();
    }

    @Operation(summary = "Build Starter zip", description = "Given details about the game/application will return a zip file containing a starter project")
    @ResponseBody
    @GetMapping("/jme-initializer/zip")
    public ResponseEntity<Resource> buildStarterZip(
            @Parameter(description=GAME_NAME_DOC_STRING) @RequestParam String gameName,
            @Parameter(description=GAME_DESCRIPTION_DOC_STRING) @RequestParam String packageName,
            @Parameter(description=REQUIRED_LIBRARIES_DOC_STRING) @RequestParam String libraryList)
            throws IOException {

        try(ByteArrayOutputStream byteArrayOutputStream = initializerZipService.produceZipInMemory( gameName, packageName, Arrays.asList(libraryList.split(",")) )){

            ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename( Merger.sanitiseToJavaClass(gameName) + ".zip") //a java class name is a valid file name as well. Seems a reasonable name for the zip
                                    .build().toString())
                    .body(resource);
        }
    }

    @Operation(summary = "Build Starter's build.gradle files", description = "Given details about the game/application will return a map of the names of the build.gradle files (including paths if appropriate) to their contents.\n\n Is intended to give end users a preview of libraries/structure they have requested before they get the full zip")
    @ResponseBody
    @GetMapping("/jme-initializer/gradle-preview")
    public ResponseEntity<Map<String, String>> previewGradleFile(
            @Parameter(description=GAME_NAME_DOC_STRING) @RequestParam String gameName,
            @Parameter(description=GAME_DESCRIPTION_DOC_STRING) @RequestParam String packageName,
            @Parameter(description=REQUIRED_LIBRARIES_DOC_STRING) @RequestParam String libraryList) throws IOException {
        Map<String, String> gradleFile = initializerZipService.produceGradleFilePreview(gameName, packageName, Arrays.asList(libraryList.split(",")));

        return ResponseEntity.ok().body(gradleFile);
    }
 }
