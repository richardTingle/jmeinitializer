package com.jmonkeyengine.jmeinitializer;

import com.jmonkeyengine.jmeinitializer.libraries.LibraryService;
import com.jmonkeyengine.jmeinitializer.versions.VersionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class Schedular{

    private final LibraryService libraryService;
    private final VersionService versionService;

    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    public void refreshLibraryData(){
        libraryService.fetchNewLibraries();
        versionService.fetchNewVersions();
    }

}
