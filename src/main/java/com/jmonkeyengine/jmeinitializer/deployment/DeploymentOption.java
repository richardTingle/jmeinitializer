package com.jmonkeyengine.jmeinitializer.deployment;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DeploymentOption{
    WINDOWS("Windows", LibraryService.JME_DESKTOP, LibraryService.JME_VR),
    LINUX("Linux", LibraryService.JME_DESKTOP, LibraryService.JME_VR),
    MACOS("MacOs", LibraryService.JME_DESKTOP, LibraryService.JME_VR);

    /**
     * The human-readable name
     */
    String optionName;

    List<String> relevantToPlatforms;

    DeploymentOption(String optionName, String... relevantToPlatforms){
        this.optionName = optionName;
        this.relevantToPlatforms = Arrays.asList(relevantToPlatforms);
    }

    public static List<DeploymentOption> valuesOf(List<String> names){
        List<DeploymentOption> options = new ArrayList<>(names.size());
        for(String name : names){
            options.add(valueOf(name));
        }
        return options;
    }
}
