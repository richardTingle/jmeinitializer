package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    JME_PLATFORM( "JME Platform", "JME can target many platforms, select the platform your game will target", true),

    PHYSICS( "Physics Libraries", "A physics library handles collisions and forces like gravity", true),

    GUI( "GUI libraries", "A Gui library will provide the 2D interface over your 3D world",  true),

    NETWORKING( "Networking", "A Networking library will help with multiplayer games",  true),

    GENERAL("Other Libraries", "Libraries often found to be useful in JME games", false);

    String displayName;

    String description;

    boolean onlyOneAllowed;
}
