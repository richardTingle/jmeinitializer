package com.jmonkeyengine.jmeinitializer;

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
     * This is everything thats not under the jmonkeyengineVersion tag.
     * This is expected to be a multiline, indented merge field (I.e. each line needs 4 spaces at the front) and refer
     * to the versions explicitly
     * E.g.:
     *     implementation 'com.github.stephengold:Minie:4.4.0
     *     com.simsilica:lemur:1.15.0
     */
    OTHER_DEPENDENCIES;

    /**
     * What should be searched for in strings to be replaced by this merge field's data
     * @return
     */
    public String getMergeFieldInText(){
        return "[" + name() + "]";
    }

}
