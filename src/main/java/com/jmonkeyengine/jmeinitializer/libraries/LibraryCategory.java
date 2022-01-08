package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Categories are used to group what is offered by the UI. Sometimes a group will only allow 1 item to be selected
 * within it (e.g. it only makes sense to have 1 physics engine)
 */
@AllArgsConstructor
@Getter
public enum LibraryCategory {

    /**
     * Most JME libraries that don't fit into other categories
     */
    JME_GENERAL( "Other JME libraries", "These are general purpose libaries that may be useful in JME games",false),

    /**
     * Contains the targeted platform (Desktop, VR, android etc)
     */
    JME_PLATFORM( "JME Platform", "JME can target many platforms, select the platform your game will target", false),

    PHYSICS( "Physics Libraries", "A physics library handles collisions and forces like gravity", true),

    GUI( "GUI libraries", "A Gui library will provide the 2D interface over your 3D world",  true),

    NETWORKING( "Networking", "A Networking library will help with multiplayer games",  true),

    GENERAL("Other Libraries", "Libraries often found to be useful in JME games", false);

    String displayName;

    /**
     * Used in the UI to describe the general purpose of the category
     */
    String description;

    /**
     * If true a radio selector will be presented in the UI. If false checkboxes are presented.
     *
     * NOT ACTUALLY CURRENTLY RESPECTED BY THE UI. Currently, some categories are manually set as checkbox in the UI and
     * the rest are radio
     */
    boolean onlyOneAllowed;
}
