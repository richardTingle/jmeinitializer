package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import org.apache.commons.text.CaseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Merger {

    Map<MergeField, String> mergeData = new HashMap<>();

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
    }
    
    protected static String formJmeRequiredLibrariesMergeField(List<Library> librariesRequired){
        return librariesRequired.stream()
                .filter(Library::isUsesJmeVersion)
                .flatMap(l ->
                    l.getArtifactIds().stream()
                            .map(artifactId -> "    implementation '" + l.getGroupId() + ":" + artifactId + "'+ jmonkeyengineVersion")
                ).collect(Collectors.joining("\n"));

    }

    protected static String formNonJmeRequiredLibrariesMergeField(List<Library> librariesRequired, Map<String,String> libraryVersions){
        return librariesRequired.stream()
                .filter(Library::isUsesJmeVersion)
                .flatMap(l ->
                        l.getArtifactIds().stream()
                                .map(artifactId -> {
                                    String mavenCoordinate = l.getGroupId() + ":" + artifactId;
                                    return "    implementation '" + mavenCoordinate + ":" + libraryVersions.getOrDefault(mavenCoordinate, "[MISSING_VERSION]")  + "'";
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
        fullPackage = fullPackage + "/";
        return fullPackage;
    }

    /**
     * Given a string that could be used as a java class name sanitises it so its a standard java
     * class name
     */
    protected static String sanitiseToJavaClass(String proposedName){
        //remove illegal characters (possibly a bit overaggressive, but whatever
        proposedName = proposedName.replaceAll("[^a-zA-Z]", "");

        //in case the regex killed the whole string, produce a fall back
        if (proposedName.isBlank()){
            proposedName = "MyGame";
        }
        //camelcase a sentence
        if (proposedName.contains(" ")) {
            proposedName = CaseUtils.toCamelCase(proposedName, true, ' ', '_');
        }
        return proposedName;
    }

}
