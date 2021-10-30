package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import org.apache.commons.text.CaseUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The merger is responsible for replacing merge fields in text documents and paths with their merged data.
 *
 * Merge fields are of the form
 *
 * [PROPERTY]
 *
 * For which the data for the merge field as specified in {@link MergeField}
 *
 * Or (in a file path)
 *
 * [IF=LIBRARY]
 *
 * In which case the file is only included if that library is active
 */
public class Merger {

    //the "anything but = is to avoid double ifs merging
    private Pattern mergeIfConditionPattern = Pattern.compile("\\[IF=([^=]*)]");

    private final Map<MergeField, String> mergeData = new HashMap<>();

    Set<String> libraryKeysInUse;

    /**
     * Given the information provided by the user will evaluate merge fields in files and paths.
     *
     * libraryVersions is a map of a string of the form groupId:artifactId -> version
     */
    public Merger(String gameName, String gamePackage, List<Library> librariesRequired, String jmeVersion, Map<String,String> libraryVersions){
        mergeData.put(MergeField.GAME_NAME_FULL, gameName);
        mergeData.put(MergeField.GAME_NAME, sanitiseToJavaClass(gameName));

        String proposedPackage = sanitiseToPackage(gamePackage);
        if (proposedPackage.isBlank()){
            proposedPackage = sanitiseToPackage(mergeData.get(MergeField.GAME_NAME));
        }
        mergeData.put(MergeField.GAME_PACKAGE, proposedPackage);
        mergeData.put(MergeField.GAME_PACKAGE_FOLDER, convertPackageToFolder(mergeData.get(MergeField.GAME_PACKAGE)));
        mergeData.put(MergeField.JME_VERSION, jmeVersion);
        mergeData.put(MergeField.JME_DEPENDENCIES, formJmeRequiredLibrariesMergeField(librariesRequired));
        mergeData.put(MergeField.OTHER_DEPENDENCIES, formNonJmeRequiredLibrariesMergeField(librariesRequired, libraryVersions));

        libraryKeysInUse = librariesRequired.stream().map(Library::key).collect(Collectors.toSet());
    }

    public boolean pathShouldBeAllowed(String pathTemplate){
        Matcher matcher = mergeIfConditionPattern.matcher(pathTemplate);

        if ( matcher.find() ){
            String requiredLibrary = matcher.group(1);
            return libraryKeysInUse.contains(requiredLibrary);
        }else{
            return true; //no if condition, just allowed
        }
    }

    public String mergePath (String pathTemplate){
        String path = pathTemplate;
        for(Map.Entry<MergeField, String> merges : mergeData.entrySet()){
            path = path.replace(merges.getKey().getMergeFieldInText(), merges.getValue());
        }
        //any "ifs" are removed from the path (they should have already been used to assess if the file should be included
        path = path.replaceAll("\\[IF=([^=]*)]", "");
        return path;
    }

    /**
     * Treats the byte array as a UTF-8 String and merges it
     */
    public byte[] mergeFileContents(byte[] fileContents){
        String fileContentsAsString = new String(fileContents, StandardCharsets.UTF_8 );

        for(Map.Entry<MergeField, String> merges : mergeData.entrySet()){
            fileContentsAsString = fileContentsAsString.replace(merges.getKey().getMergeFieldInText(), merges.getValue());
        }
        return fileContentsAsString.getBytes(StandardCharsets.UTF_8);
    }

    protected static String formJmeRequiredLibrariesMergeField(List<Library> librariesRequired){
        return librariesRequired.stream()
                .filter(Library::usesJmeVersion)
                .flatMap(l ->
                    l.artifacts().stream()
                            .map(artifact -> "    implementation '" + l.groupId() + ":" + artifact.artifactId() + ":'+ jmonkeyengineVersion")
                ).collect(Collectors.joining("\n"));

    }

    protected static String formNonJmeRequiredLibrariesMergeField(List<Library> librariesRequired, Map<String,String> libraryVersions){
        return librariesRequired.stream()
                .filter(l -> !l.usesJmeVersion())
                .flatMap(l ->
                        l.artifacts().stream()
                                .map(artifact -> {
                                    String mavenCoordinate = l.groupId() + ":" + artifact.artifactId();
                                    return "    implementation '" + mavenCoordinate + ":" + libraryVersions.getOrDefault(mavenCoordinate, artifact.fallbackVersion())  + "'";
                                })
                ).collect(Collectors.joining("\n"));
    }

    protected static String sanitiseToPackage(String proposedPackage){
        proposedPackage = proposedPackage.toLowerCase();
        proposedPackage = proposedPackage.replace(" ", ".");
        proposedPackage = proposedPackage.replaceAll("\\.\\.+", "."); //remove double dots or similar
        proposedPackage = proposedPackage.replaceAll("\\.$", ""); //remove trailing dots
        proposedPackage = proposedPackage.replaceAll("^\\.", ""); //remove prefix dots
        proposedPackage = proposedPackage.replaceAll("[^a-z.]", "");//remove illegal characters

        return proposedPackage;
    }

    protected static String convertPackageToFolder(String fullPackage){
        fullPackage = fullPackage.replace(".", "/");
        return fullPackage;
    }

    /**
     * Given a string that could be used as a java class name sanitises it so its a standard java
     * class name
     */
    protected static String sanitiseToJavaClass(String proposedName){
        //remove illegal characters (possibly a bit overaggressive, but whatever
        proposedName = proposedName.replaceAll("[^a-zA-Z ]", "");

        //in case the regex killed the whole string, produce a fall back
        if (proposedName.isBlank()){
            proposedName = "MyGame";
        }
        //camelcase a sentence
        if (proposedName.contains(" ") || Character.isLowerCase(proposedName.charAt(0))) {
            proposedName = CaseUtils.toCamelCase(proposedName, true, ' ', '_');
        }
        return proposedName;
    }

}
