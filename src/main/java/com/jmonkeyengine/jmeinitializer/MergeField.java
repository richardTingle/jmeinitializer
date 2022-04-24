package com.jmonkeyengine.jmeinitializer;

/**
 * These are the supported merge fields that the {@link Merger} will insert information into. They allow fields
 * with these names (represented by [NAME]) in both paths and file contents in the template to be replaced by
 * dynamic content
 */
public enum MergeField {

    /**
     * The game name as it appears in Class names and gradle artifact. A sanitised version of the true name
     * e.g. MyFirstGame
     */
    GAME_NAME,

    /**
     * The game name as it appears in marketing material. Can contain spaces etc
     *
     * e.g. My First Game
     */
    GAME_NAME_FULL,

    /**
     * The games package in "dot style". E.g. com.myexcellentgame
     */
    GAME_PACKAGE,

    /**
     * The games package in folder style. E.g. com/myexcellentgame
     * Note the no trailing slash and no preceding slash
     */
    GAME_PACKAGE_FOLDER,

    /**
     * The version of JME that is being used.
     *
     * This isn't based on use input, but is a merge field so it can be updated easily without editing the template.
     *
     * E.g. 3.4.0-stable
     */
    JME_VERSION,

    /**
     * Dependencies selected by the user that are not mandatory.
     * This is expected to be a multiline, indented merge field (I.e. each line needs 4 spaces at the front) and refer
     * to the jmonkeyengineVersion parameter for its version.
     * E.g:
     *     implementation 'org.jmonkeyengine:jme3-effects:' + jmonkeyengineVersion
     *     implementation 'org.jmonkeyengine:jme3-networking:' + jmonkeyengineVersion
     */
    JME_DEPENDENCIES,

    /**
     * Dependencies that are only used on the VR platform
     */
    VR_SPECIFIC_DEPENDENCIES,

    /**
     * Dependencies that are only used on the android platform
     */
    ANDROID_SPECIFIC_DEPENDENCIES,

    /**
     * Dependencies that are only used on the android platform
     */
    DESKTOP_SPECIFIC_DEPENDENCIES,

    /**
     * This is everything thats not under the jmonkeyengineVersion tag.
     * This is expected to be a multiline, indented merge field (I.e. each line needs 4 spaces at the front) and refer
     * to the versions explicitly
     * E.g.:
     *     implementation 'com.github.stephengold:Minie:4.4.0
     *     com.simsilica:lemur:1.15.0
     */
    OTHER_DEPENDENCIES,

    /**
     * This is VR_SPECIFIC_DEPENDENCIES + ANDROID_SPECIFIC_DEPENDENCIES + DESKTOP_SPECIFIC_DEPENDENCIES + OTHER_DEPENDENCIES
     */
    ALL_NON_JME_DEPENDENCIES,

    /**
     * Maven repos required by the libraries. E.g. jcenter()
     *
     * This is a deduped list of all the libraries required by all the libraries
     */
    MAVEN_REPOS
    ;

    /**
     * What should be searched for in strings to be replaced by this merge field's data
     * @return
     */
    public String getMergeFieldInText(){
        return "[" + name() + "]";
    }

}
