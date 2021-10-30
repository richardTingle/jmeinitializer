package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;
import com.jmonkeyengine.jmeinitializer.uisupport.UiLibraryDataDto;
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

/**
 * Represents endpoints used by the front end
 */
@RestController
public class InitializerRestController {

    private final InitializerZipService initializerZipService;
    private final LibraryService libraryService;

    public InitializerRestController (InitializerZipService initializerZipService, LibraryService libraryService) {
        this.initializerZipService = initializerZipService;
        this.libraryService = libraryService;
    }

    @GetMapping("/jme-initialiser/libraries")
    public UiLibraryDataDto getDataForUi(){
        return libraryService.getUiLibraryDataDto();
    }

    @ResponseBody
    @GetMapping("/jme-initialiser/zip")
    public ResponseEntity<Resource> serveFile(@RequestParam String gameName,@RequestParam String packageName, @RequestParam String libraryList) throws IOException {

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

 }
