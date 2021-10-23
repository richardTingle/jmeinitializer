package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Library {
    //included by default jme3-core, jme3-jogg, jme3-plugins, jme3-lwjgl3
    JME_DESKTOP("JME Desktop", "org.jmonkeyengine","jme3-desktop", true, LibraryCategory.JME_PLATFORM, true, "Desktop Game development including Windows, Mac and Linux"),
    JME_EFFECTS("JME Effects","org.jmonkeyengine","jme3-effects", true, LibraryCategory.JME_GENERAL, true, "A JME library for effects, like explosions, smoke etc"),
    JME_TERRAIN("JME Terrain", "org.jmonkeyengine","jme3-terrain", true, LibraryCategory.JME_GENERAL, false, " A JME library for terrain"),
    JME_NETWORKING("JME Networking", "org.jmonkeyengine","jme3-networking", true, LibraryCategory.NETWORKING, false, "A JME library to support multiplayer games' network communication "),
    JME_NIFTY("Nifty Gui","org.jmonkeyengine","jme3-niftygui", true, LibraryCategory.GUI, false, "Nifty GUI, a Gui library. No longer actively supported by JME but still available"),
    JME_ANDROID("JME Android", "org.jmonkeyengine","jme3-android", true, LibraryCategory.JME_PLATFORM, false, "Android Game development"),
    JME_JBULLET("JBullet", "org.jmonkeyengine","jme3-jbullet", true, LibraryCategory.PHYSICS, false, "A Java port of the popular C++ bullet physics library"),
    JME_IOS("JME iOS", "org.jmonkeyengine","jme3-ios", true, LibraryCategory.JME_GENERAL, false, "A JME library supporting iOS deployment"),
    JME_VR("JME VR","org.jmonkeyengine","jme3-vr", true, LibraryCategory.JME_PLATFORM, false, "Virtual reality support"),

    MINIE("Minie", "com.github.stephengold", "Minie", false, LibraryCategory.PHYSICS, true, "An alternative binding to the C++ bullet library, produced by a member of the JMonkey community"),
    //lemur is not currently on maven.org so can't get a version for it. Is it moving off jCentre?
    //LEMUR("com.simsilica", List.of("lemur", "lemur-proto"), false, LibraryCategory.GUI, true, Optional.empty())
    LOG4j2("Log4j2", "org.apache.logging.log4j", List.of("log4j-core", "log4j-api"), false, LibraryCategory.GENERAL, false, "[\\.\\d]*", "A popular java logging library, useful to produce text logs of whats going on in your game" )
    ;

    String displayName;
    String groupId;
    Collection<String> artifactIds;
    boolean usesJmeVersion;
    LibraryCategory category;
    boolean defaultSelected;

    /**
     * When searching for library versions the application uses this regex to determine if its a "release" version
     * (And not a beta, release candidate etc)
     */
    String libraryVersionRegex = "[\\.\\d]*";

    /**
     * Shown in the UI as to what this library might be for
     */
    String descriptionText;

    Library (String displayName, String groupId, String artifactId, boolean usesJmeVersion, LibraryCategory category, boolean defaultSelected, String descriptionText) {
        this.displayName = displayName;
        this.groupId = groupId;
        this.artifactIds = List.of(artifactId);
        this.usesJmeVersion = usesJmeVersion;
        this.category = category;
        this.defaultSelected = defaultSelected;
        this.descriptionText= descriptionText;
    }

    public static List<Library> nonJmeLibraries(){
        return Arrays.stream(values()).filter(l -> !l.usesJmeVersion).collect(Collectors.toList());
    }

    public static List<Library> librariesOfCategory(LibraryCategory category){
        return Arrays.stream(values()).filter(l -> l.getCategory().equals(category)).collect(Collectors.toList());
    }
}
