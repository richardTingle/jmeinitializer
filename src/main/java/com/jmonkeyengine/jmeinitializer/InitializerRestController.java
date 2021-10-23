package com.jmonkeyengine.jmeinitializer;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
public class InitializerRestController {

    private final InitializerZipService initializerZipService;

    public InitializerRestController (InitializerZipService initializerZipService) {
        this.initializerZipService = initializerZipService;
    }

    @ResponseBody
    @GetMapping("/jme-initialiser/zip")
    public ResponseEntity<Resource> serveFile() throws IOException {

        try(ByteArrayOutputStream byteArrayOutputStream = initializerZipService.produceZipInMemory()){

            ByteArrayResource resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("gameName.zip")
                                    .build().toString())
                    .body(resource);
        }
    }

 }
