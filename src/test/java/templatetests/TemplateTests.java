package templatetests;

import com.jmonkeyengine.jmeinitializer.InitializerZipService;
import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;
import com.jmonkeyengine.jmeinitializer.versions.VersionService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * These tests physically create a template and then run gradle compile on that template.
 *
 * A successful build of the template passes the test. This is to catch compile failures in the templates
 */
@Tag("TemplateTests")
//@Log4j2
public class TemplateTests{

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(InitializerZipService.class);


    static InitializerZipService initializerZipService;
    static File tempFolder;

    @BeforeAll
    public static void init() throws IOException{

        LibraryService libraryService = new LibraryService("https://raw.githubusercontent.com/jMonkeyEngine/jme-initializer/master/libraries.json");
        libraryService.fetchNewLibraries();
        VersionService versionService = new VersionService(libraryService);
        versionService.fetchNewVersions();
        initializerZipService = new InitializerZipService(versionService, libraryService);
        initializerZipService.setStrictValidate(true);
        tempFolder = new File(System.getProperty("java.io.tmpdir"), "jmeinitialiser");
        if (tempFolder.exists()){
            FileUtils.deleteDirectory(tempFolder);
        }
    }

    @AfterAll
    public static void closeDown() throws IOException{
        FileUtils.deleteDirectory(tempFolder);
    }

    @Test
    public void testMultiProjectTemplate_withTamarin() throws Exception{
        testTemplate("MultiTamarinTest", "com.example", "JME_DESKTOP", "JME_ANDROID", "JME_EFFECTS", "JME_VR", "MINIE", "TAMARIN" );
    }

    @Test
    public void testMultiProjectTemplate_withoutTamarin() throws Exception{
        testTemplate("MultiNoTamarinTest", "com.example", "JME_DESKTOP", "JME_ANDROID", "JME_EFFECTS", "JME_VR", "MINIE" );
    }

    @Test
    public void testDesktopTemplate() throws Exception{
        testTemplate("DesktopTest", "com.example", "JME_DESKTOP", "JME_EFFECTS", "LEMUR" );
    }

    @Test
    public void testAndroidTemplate() throws Exception{
        testTemplate("AndroidTest", "com.example", "JME_ANDROID", "JME_EFFECTS", "MINIE" );
    }

    @Test
    public void testVrTemplate_withoutTamarin() throws Exception{
        testTemplate("VrNoTamarinTest", "com.example", "JME_VR", "JME_EFFECTS", "LEMUR" );
    }

    @Test
    public void testVrTemplate_withTamarin() throws Exception{
        testTemplate("VrTamarinTest", "com.example", "JME_VR", "JME_EFFECTS", "TAMARIN", "LEMUR" );
    }

    public static void testTemplate(String gameName, String packageName, String... listOfLibraries ) throws Exception{
        Map<String,byte[]> template = initializerZipService.produceTemplate(gameName, packageName, Arrays.asList(listOfLibraries));

        File folder = new File(tempFolder, gameName);
        log.info("Using temp folder " + folder);
        folder.mkdirs();

        for(Map.Entry<String,byte[]> templateEntry : template.entrySet()){
            File outputFile = new File(folder, templateEntry.getKey());
            outputFile.getParentFile().mkdirs();
            Files.write(outputFile.toPath(), templateEntry.getValue());
            if (outputFile.toString().endsWith("gradlew")){
                outputFile.setExecutable(true); //only needed for linux
            }

        }

        log.info("Begin gradle task");

        Runtime runtime = Runtime.getRuntime();

        String command = System.getProperty("os.name").toLowerCase().contains("windows") ? "gradlew.bat build" : "./gradlew build";

        Process process = runtime.exec( command , null , folder);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
        BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = errorInput.readLine()) != null) {
            System.out.println(line);
        }

        int returnValue = process.waitFor();
        if (returnValue != 0){
            fail("Gradle compile failed, see logs");
        }
    }

}
