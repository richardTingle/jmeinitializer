package com.jmonkeyengine.jmeinitializer;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class InitializerZipService {

    /**
     * Most file types are treated as text and scanned for merge fields, but for binary files thats "not a great idea"
     * those file types that shouldn't be scanned are listed here and they are included in the bundle as is
     */
    private static Set<String> fileExtensionsToTreatAsBlobs = Set.of(".jar");

    public ByteArrayOutputStream produceZipInMemory(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            ZipEntry entry = new ZipEntry("test/test.txt");
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write("hello".getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();

        }catch(IOException ioe) {
            throw new RuntimeException("Exception while forming zip", ioe);
        }
        return byteArrayOutputStream;
    }

}
