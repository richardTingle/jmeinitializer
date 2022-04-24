package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import com.jmonkeyengine.jmeinitializer.libraries.LibraryCategory;
import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;
import org.apache.commons.text.CaseUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
    private Pattern mergeIfConditionPattern = Pattern.compile("\\[IF=([^\\]]*)]");
    /**
     * After the allowed ifs have been processed this is used to eliminate forbidden ifs
     */
    private Pattern mergeIfInFileEliminationPattern = Pattern.compile("\\[IF=([^=]*)]");

    private final Map<MergeField, String> mergeData = new HashMap<>();

    Set<String> libraryKeysAndProfilesInUse;

    /**
     * Given the information provided by the user will evaluate merge fields in files and paths.
     *
     * libraryVersions is a map of a string of the form groupId:artifactId -> version
     *
     * additionalProfiles are things that can be used in [IF=] conditions in addition to libraries (things like MULTIPLATFORM)
     */
    public Merger(String gameName, String gamePackage, List<Library> librariesRequired, Collection<String> additionalProfiles, String jmeVersion, Map<String,String> libraryVersions){
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
        mergeData.put(MergeField.VR_SPECIFIC_DEPENDENCIES, formPlatformSpecificLibrariesMergeField(librariesRequired, libraryVersions, LibraryService.JME_VR));
        mergeData.put(MergeField.ANDROID_SPECIFIC_DEPENDENCIES, formPlatformSpecificLibrariesMergeField(librariesRequired, libraryVersions, LibraryService.JME_ANDROID));
        mergeData.put(MergeField.DESKTOP_SPECIFIC_DEPENDENCIES, formPlatformSpecificLibrariesMergeField(librariesRequired, libraryVersions, LibraryService.JME_DESKTOP));
        mergeData.put(MergeField.OTHER_DEPENDENCIES, formNonJmeRequiredAnyPlatformLibrariesMergeField(librariesRequired, libraryVersions));
        mergeData.put(MergeField.ALL_NON_JME_DEPENDENCIES, mergeData.get(MergeField.VR_SPECIFIC_DEPENDENCIES)+"\n"+mergeData.get(MergeField.ANDROID_SPECIFIC_DEPENDENCIES)+"\n"+mergeData.get(MergeField.DESKTOP_SPECIFIC_DEPENDENCIES)+"\n"+mergeData.get(MergeField.OTHER_DEPENDENCIES));
        mergeData.put(MergeField.MAVEN_REPOS, formMavenRepos(librariesRequired));

        libraryKeysAndProfilesInUse = librariesRequired.stream().map(Library::getKey).collect(Collectors.toSet());
        libraryKeysAndProfilesInUse.addAll(additionalProfiles);
    }

    public boolean pathShouldBeAllowed(String pathTemplate){
        Matcher matcher = mergeIfConditionPattern.matcher(pathTemplate);

        while( matcher.find() ){
            String requiredLibrary = matcher.group(1);
            if (!libraryKeysAndProfilesInUse.contains(requiredLibrary)){
                return false;
            }
        }

        return true;
    }

    public String mergePath (String pathTemplate){
        String path = pathTemplate;
        for(Map.Entry<MergeField, String> merges : mergeData.entrySet()){
            path = path.replace(merges.getKey().getMergeFieldInText(), merges.getValue());
        }
        //any "ifs" are removed from the path (they should have already been used to assess if the file should be included
        path = path.replaceAll("\\[IF=([^=]*)]", "");
        path = path.replaceAll("//+", "/"); //if the if is the entirety of the folder then redundant folder is collapsed
        path = path.replace(".jmetemplate", "");
        path = path.replaceAll("^/", ""); //empty conditional folders at the start of the path can cause a preceding / which makes the zips "all weird". This removes that
        path = path.replace("[DOT]", ".");
        return path;
    }

    /**
     * Treats the byte array as a UTF-8 String and merges it
     */
    public byte[] mergeFileContents(byte[] fileContents){
        String fileContentsAsString = new String(fileContents, StandardCharsets.UTF_8);

        for(Map.Entry<MergeField, String> merges : mergeData.entrySet()){
            fileContentsAsString = fileContentsAsString.replace(merges.getKey().getMergeFieldInText(), merges.getValue());
        }

        /*
         * Now run the if statement merges. This is achieved by first removing all [IF] and [/IF] blocks for everything
         * that should be included, then eliminating everything thats left within an if block
         */
        for(String validProfile : libraryKeysAndProfilesInUse){
            fileContentsAsString = fileContentsAsString.replace("[IF=" + validProfile + "]", "");
            fileContentsAsString = fileContentsAsString.replace("[/IF=" + validProfile + "]", "");
        }

        //I suspect a single really advanced regex could do the below in one go, but its a painful double "not this" so I've gone for this probably less efficient approach
        Set<String> remainingIfs = new HashSet<>();
        Matcher remainingIfsMatcher = mergeIfConditionPattern.matcher(fileContentsAsString);
        while(remainingIfsMatcher.find()){
            remainingIfs.add(remainingIfsMatcher.group(1));
        }

        for(int i=0;i<2;i++){
            for(String remainingIf : remainingIfs){
                //this 2 step process of first eliminating down to a _eliminated_ string and then removing that is to get any whitespace eliminated nicely
                String regex = "\\[IF=" + remainingIf + "]((?!IF=).)*\\[/IF=" + remainingIf + "]";
                fileContentsAsString = Pattern.compile(regex, Pattern.DOTALL).matcher(fileContentsAsString).replaceAll("_eliminated_");
            }
            //kill including the new line after the closure, if thats the only thing on the lin
            fileContentsAsString = fileContentsAsString.replaceAll("\\R *_eliminated_ *\\R", "\n");
            //then get rid of anything thats on a line with allowed text
            fileContentsAsString = fileContentsAsString.replace("_eliminated_", "");
        }

        return fileContentsAsString.getBytes(StandardCharsets.UTF_8);
    }

    protected static String formJmeRequiredLibrariesMergeField(List<Library> librariesRequired){
        return librariesRequired.stream()
                .filter(Library::isUsesJmeVersion)
                .filter(l -> l.getCategory() != LibraryCategory.JME_PLATFORM) //platforms are hard coded into the templates to better support multimodule
                .flatMap(l ->
                    l.getArtifacts().stream()
                            .map(artifact -> "    implementation '" + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":'"+ artifact.getPinVersionOpt().map(pv -> "'" + pv + "'").orElse( "jmonkeyengineVersion") )
                ).collect(Collectors.joining("\n"));

    }

    protected static String formNonJmeRequiredAnyPlatformLibrariesMergeField(List<Library> librariesRequired, Map<String,String> libraryVersions){
        return librariesRequired.stream()
                .filter(l -> !l.isUsesJmeVersion())
                .filter(l -> l.getRequiredPlatforms().isEmpty())
                .flatMap(l ->
                        l.getArtifacts().stream()
                                .map(artifact -> {
                                    String mavenCoordinate = artifact.getGroupId() + ":" + artifact.getArtifactId();
                                    return "    implementation '" + mavenCoordinate + ":" + artifact.getPinVersionOpt().orElse(libraryVersions.getOrDefault(mavenCoordinate, artifact.getFallbackVersion()))  + "'";
                                })
                ).collect(Collectors.joining("\n"));
    }

    protected static String formMavenRepos(List<Library> librariesRequired){
        Set<String> mavenRepos = new HashSet<>();
        mavenRepos.add("mavenCentral()");
        mavenRepos.add("mavenLocal()");

        librariesRequired.forEach(l -> mavenRepos.addAll(l.getAdditionalMavenRepos()));

        return mavenRepos.stream()
                .map(mr -> "        " + mr)
                .sorted() //sorting them makes testing this easier
                .collect(Collectors.joining("\n"));
    }

    protected static String formPlatformSpecificLibrariesMergeField(List<Library> librariesRequired, Map<String,String> libraryVersions, String platform){
        return librariesRequired.stream()
                .filter(l -> !l.isUsesJmeVersion())
                .filter(l -> l.getRequiredPlatforms().contains(platform))
                .flatMap(l ->
                        l.getArtifacts().stream()
                                .map(artifact -> {
                                    String mavenCoordinate = artifact.getGroupId() + ":" + artifact.getArtifactId();
                                    return "    implementation '" + mavenCoordinate + ":" + artifact.getPinVersionOpt().orElse(libraryVersions.getOrDefault(mavenCoordinate, artifact.getFallbackVersion()))  + "'";
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
