package com.jmonkeyengine.jmeinitializer.libraries;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jmonkeyengine.jmeinitializer.uisupport.CategoryAndLibrariesDto;
import com.jmonkeyengine.jmeinitializer.uisupport.CategoryDto;
import com.jmonkeyengine.jmeinitializer.uisupport.LibraryDto;
import com.jmonkeyengine.jmeinitializer.uisupport.UiLibraryDataDto;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LibraryService {

    public static final Library JME_DESKTOP = Library.builder("JME_DESKTOP",  "JME Desktop",  LibraryCategory.JME_PLATFORM, "Desktop Game development including Windows and Linux")
            .defaultSelected(true)
            .usesJmeVersion(true)
            .artifact(new Artifact( "org.jmonkeyengine", "jme3-desktop"))
            .build();

    public static final Library JME_VR = Library.builder("JME_VR","JME VR", LibraryCategory.JME_PLATFORM, "Virtual reality support")
            .usesJmeVersion(true)
            .artifact(new Artifact("org.jmonkeyengine","jme3-vr"))
            .build();


    public static final Library JME_ANDROID =Library.builder("JME_ANDROID","JME Android", LibraryCategory.JME_PLATFORM, "Android Game development")
            .usesJmeVersion(true)
            .artifact(new Artifact("org.jmonkeyengine","jme3-android"))
            .build();

    /**
     * These are the tool provided libraries, it allows for non standard things like regexes to be set, or
     * where the library doesn't come from the jmonkey store
     *
     * (At time of writing that is the only place libraries come from, but its intended to later pull from store)
     */
    private final Collection<Library> toolProvidedLibraries = List.of(
            JME_DESKTOP,
            JME_ANDROID,
            JME_VR,
            Library.builder("JME_EFFECTS","JME Effects", LibraryCategory.JME_GENERAL, "A JME library for effects, like explosions, smoke etc")
                    .usesJmeVersion(true)
                    .artifact(new Artifact("org.jmonkeyengine","jme3-effects"))
                    .build(),

            Library.builder("JME_TERRAIN","JME Terrain", LibraryCategory.JME_GENERAL, "A JME library for terrain")
                    .usesJmeVersion(true)
                    .artifact(new Artifact("org.jmonkeyengine","jme3-terrain"))
                    .build(),

            Library.builder("JME_NETWORKING","JME Networking",  LibraryCategory.NETWORKING, "A JME library to support multiplayer games' network communication ")
                    .usesJmeVersion(true)
                    .artifact(new Artifact("org.jmonkeyengine","jme3-networking"))
                    .build(),

            Library.builder("JME_NIFTY","Nifty Gui", LibraryCategory.GUI,"Nifty GUI, a Gui library. No longer actively supported by JME but still available")
                    .usesJmeVersion(true)
                    .artifact(new Artifact("org.jmonkeyengine","jme3-niftygui"))
                    .build(),

            Library.builder("JME_JBULLET","JBullet", LibraryCategory.PHYSICS, "A Java port of the popular C++ bullet physics library")
                    .usesJmeVersion(true)
                    .artifact(new Artifact("org.jmonkeyengine","jme3-jbullet"))
                    .build(),

            Library.builder("MINIE","Minie",LibraryCategory.PHYSICS, "An alternative binding to the C++ bullet library, produced by a member of the JMonkey community")
                    .artifact(new Artifact("com.github.stephengold", "Minie"))
                    .build(),

            Library.builder("LEMUR","Lemur", LibraryCategory.GUI, "Lemur is GUI toolkit for making user interfaces in jMonkeyEngine applications. It supports standard 2D UIs as well as fully 3D UIs. The modular design allows an application to use all or some of it as needed or even to build a completely new custom GUI library on top.")
                    .artifact(new Artifact("com.simsilica", "lemur", "1.15.0"))
                    .artifact(new Artifact("com.simsilica", "lemur-proto", "1.12.0"))
                    .build(),

            Library.builder("LOG4J2","Log4j2", LibraryCategory.GENERAL, "A popular java logging library, useful to produce text logs of whats going on in your game" )
                    .artifact(new Artifact("org.apache.logging.log4j", "log4j-core"))
                    .artifact(new Artifact("org.apache.logging.log4j", "log4j-api"))
                    .build(),

            Library.builder("TAMARIN","Tamarin", LibraryCategory.GENERAL, "A virtual reality support library, providing VR hands & action based openVr")
                    .defaultSelected(true) //really vr needs Tamarin until action based VR makes its way into jme3-vr core
                    .artifact(new Artifact("com.onemillionworlds", "tamarin"))
                    .requiredPlatform(JME_VR.getKey())
                    .build()
    );

    private Map<String, Library> currentAvailableLibraries = new HashMap<>();
    private Multimap<LibraryCategory, Library> currentAvailableLibraryByCategory;
    private List<Library> jmeLibraries;
    private List<Library> nonJmeLibraries;

    /**
     * This is what is used by the front end to render the available libraries. Because it only changes when new
     * libraries come available it is cached here
     */
    @Getter
    private UiLibraryDataDto uiLibraryDataDto;

    public LibraryService () {
        /*
         * When the jmonkey store has an API we can grab libraries from we would merge the result from that API call with
         * the toolProvidedLibraries. For now they are all just dumped in
         */
        toolProvidedLibraries.forEach(l -> currentAvailableLibraries.put(l.getKey(), l));
        categoriseLibraries();
        createUiLibraryDataDto();
    }

    /**
     * Expects the canonical currentAvailableLibraries to have already been created, then produces the other sorted
     * categories
     */
    private void categoriseLibraries (){
        currentAvailableLibraryByCategory = ArrayListMultimap.create();
        currentAvailableLibraries.values().forEach(l -> currentAvailableLibraryByCategory.put(l.getCategory(), l));
        nonJmeLibraries = currentAvailableLibraries.values().stream().filter(l -> !l.isUsesJmeVersion()).collect(Collectors.toList());
        jmeLibraries = currentAvailableLibraries.values().stream().filter(l -> !l.isUsesJmeVersion()).collect(Collectors.toList());
    }

    private void createUiLibraryDataDto(){
        List<CategoryAndLibrariesDto> specialCategories = new ArrayList<>();
        Arrays.stream(LibraryCategory.values())
                .filter(c -> c!= LibraryCategory.JME_PLATFORM && c!= LibraryCategory.JME_GENERAL && c!= LibraryCategory.GENERAL)
                .forEach(c -> specialCategories.add(new CategoryAndLibrariesDto(
                        new CategoryDto(c),
                        librariesOfCategory(c).stream().map(LibraryDto::new).collect(Collectors.toList()),
                        defaultLibraryInExclusiveCategory(c).map(Library::getKey).orElse(null))
                ));


        uiLibraryDataDto = new UiLibraryDataDto(
                librariesOfCategory(LibraryCategory.JME_PLATFORM).stream().map(LibraryDto::new).collect(Collectors.toList()),
                librariesOfCategory(LibraryCategory.JME_GENERAL).stream().map(LibraryDto::new).collect(Collectors.toList()),
                specialCategories,
                librariesOfCategory(LibraryCategory.GENERAL).stream().map(LibraryDto::new).collect(Collectors.toList()),
                Stream.of(LibraryCategory.JME_GENERAL, LibraryCategory.GENERAL).flatMap(c -> librariesOfCategory(c).stream()).filter(Library::isDefaultSelected).map(Library::getKey).collect(Collectors.toList()),
                JME_DESKTOP.getKey()
        );
    }

    public List<Library> nonJmeLibraries(){
        return nonJmeLibraries;
    }

    public Collection<Library> librariesOfCategory(LibraryCategory category){
        return currentAvailableLibraryByCategory.get(category);
    }

    /**
     * This is intended for {@link LibraryCategory}s which have either 1 or zero defaults (so are represented by radios.
     * This returns the library that is the default for that category
     */
    public Optional<Library> defaultLibraryInExclusiveCategory(LibraryCategory category){
        if (!category.isOnlyOneAllowed()){
            throw new RuntimeException("Method only applicable for exclusive categories but " + category.name() + " allows multiple selected values");
        }

        List<Library> defaultsInCategory = librariesOfCategory(category).stream().filter(Library::isDefaultSelected).collect(Collectors.toList());

        if (defaultsInCategory.size() > 1){
            throw new RuntimeException(category.name() + " has more than one default");
        }

        return defaultsInCategory.isEmpty() ? Optional.empty() : Optional.of(defaultsInCategory.get(0));
    }

    public Optional<Library> getLibraryFromKey (String key){
        return Optional.ofNullable(currentAvailableLibraries.get(key));
    }

}
