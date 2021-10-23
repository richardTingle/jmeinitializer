package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LibraryCategory {

    /**
     * Most JME libraries that don't fit into other categories
     */
    JME_GENERAL( false),

    /**
     * Contains the targeted platform (Desktop, VR, android etc)
     */
    JME_PLATFORM( true),

    PHYSICS( true),

    GUI( true),

    NETWORKING( true),

    GENERAL(false);
    /**
     * Not really used, but informational, items of this type should only really have 1 in play at once (e.g. gui
     * libraries or physics engines
     */
    boolean onlyOneAllowed;
}
