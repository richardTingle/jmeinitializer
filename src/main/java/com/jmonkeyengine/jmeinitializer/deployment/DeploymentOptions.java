package com.jmonkeyengine.jmeinitializer.deployment;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;

import java.util.Arrays;
import java.util.List;

public enum DeploymentOptions{
    WINDOWS(LibraryService.JME_DESKTOP, LibraryService.JME_VR),
    LINUX(LibraryService.JME_DESKTOP, LibraryService.JME_VR),
    MACOS(LibraryService.JME_DESKTOP);

    List<String> relevantToPlatforms;

    DeploymentOptions(String... relevantToPlatforms){
        this.relevantToPlatforms = Arrays.asList(relevantToPlatforms);
    }
    DeploymentOptions(List<String> relevantToPlatforms){
        this.relevantToPlatforms = relevantToPlatforms;
    }
}
