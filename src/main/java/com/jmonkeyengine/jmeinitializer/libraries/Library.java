package com.jmonkeyengine.jmeinitializer.libraries;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Library {
    //included by default jme3-core, jme3-jogg, jme3-plugins, jme3-lwjgl3
    JME_DESKTOP("org.jmonkeyengine","jme3-desktop", true, LibraryCategory.JME_PLATFORM, true),
    JME_EFFECTS("org.jmonkeyengine","jme3-effects", true, LibraryCategory.JME_GENERAL, true),
    JME_TERRAIN("org.jmonkeyengine","jme3-terrain", true, LibraryCategory.JME_GENERAL, false),
    JME_NETWORKING("org.jmonkeyengine","jme3-networking", true, LibraryCategory.NETWORKING, false),
    JME_NIFTY("org.jmonkeyengine","jme3-niftygui", true, LibraryCategory.GUI, false),
    JME_ANDROID("org.jmonkeyengine","jme3-android", true, LibraryCategory.JME_PLATFORM, false),
    JME_JBULLET("org.jmonkeyengine","jme3-jbullet", true, LibraryCategory.PHYSICS, false),
    JME_IOS("org.jmonkeyengine","jme3-ios", true, LibraryCategory.JME_GENERAL, false),
    JME_ANDROID_NATIVE("org.jmonkeyengine","jme3-android-native", true, LibraryCategory.JME_GENERAL, false),
    JME_VR("org.jmonkeyengine","jme3-vr", true, LibraryCategory.JME_PLATFORM, false),

    MINIE("com.github.stephengold", "Minie", false, LibraryCategory.PHYSICS, true),
    //lemur is not currently on maven.org so can't get a version for it. Is it moving off jCentre?
    //LEMUR("com.simsilica", List.of("lemur", "lemur-proto"), false, LibraryCategory.GUI, true, Optional.empty())
    LOG4j2("org.apache.logging.log4j", List.of("log4j-core", "log4j-api"), false, LibraryCategory.GENERAL, false, "[\\.\\d]*")
    ;

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

    Library (String groupId, String artifactId, boolean usesJmeVersion, LibraryCategory category, boolean defaultSelected) {
        this.groupId = groupId;
        this.artifactIds = List.of(artifactId);
        this.usesJmeVersion = usesJmeVersion;
        this.category = category;
        this.defaultSelected = defaultSelected;
    }

    public static List<Library> nonJmeLibraries(){
        return Arrays.stream(values()).filter(l -> !l.usesJmeVersion).collect(Collectors.toList());
    }
}
