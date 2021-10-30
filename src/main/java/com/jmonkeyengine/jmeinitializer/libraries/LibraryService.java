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

    /**
     * These are the tool provided libraries, it allows for non standard things like regexes to be set, or
     * where the library doesn't come from the jmonkey store
     */
    private final Collection<Library> toolProvidedLibraries = List.of(
            new Library("JME_DESKTOP", "JME Desktop", "org.jmonkeyengine","jme3-desktop", true, LibraryCategory.JME_PLATFORM, true, "Desktop Game development including Windows, Mac and Linux"),
            new Library("JME_EFFECTS","JME Effects","org.jmonkeyengine","jme3-effects", true, LibraryCategory.JME_GENERAL, true, "A JME library for effects, like explosions, smoke etc"),
            new Library("JME_TERRAIN","JME Terrain", "org.jmonkeyengine","jme3-terrain", true, LibraryCategory.JME_GENERAL, false, " A JME library for terrain"),
            new Library("JME_NETWORKING","JME Networking", "org.jmonkeyengine","jme3-networking", true, LibraryCategory.NETWORKING, false, "A JME library to support multiplayer games' network communication "),
            new Library("JME_NIFTY","Nifty Gui","org.jmonkeyengine","jme3-niftygui", true, LibraryCategory.GUI, false, "Nifty GUI, a Gui library. No longer actively supported by JME but still available"),
            new Library("JME_ANDROID","JME Android", "org.jmonkeyengine","jme3-android", true, LibraryCategory.JME_PLATFORM, false, "Android Game development"),
            new Library("JME_JBULLET","JBullet", "org.jmonkeyengine","jme3-jbullet", true, LibraryCategory.PHYSICS, false, "A Java port of the popular C++ bullet physics library"),
            new Library("JME_IOS","JME iOS", "org.jmonkeyengine","jme3-ios", true, LibraryCategory.JME_GENERAL, false, "A JME library supporting iOS deployment"),
            new Library("JME_VR","JME VR","org.jmonkeyengine","jme3-vr", true, LibraryCategory.JME_PLATFORM, false, "Virtual reality support"),
            new Library("MINIE","Minie", "com.github.stephengold", "Minie", false, LibraryCategory.PHYSICS, true, "An alternative binding to the C++ bullet library, produced by a member of the JMonkey community"),
            new Library("LEMUR","Lemur", "com.simsilica", List.of(new Artifact("lemur","1.15.0"),new Artifact( "lemur-proto","1.11.0")), false, LibraryCategory.GUI, true,"Lemur is GUI toolkit for making user interfaces in jMonkeyEngine applications. It supports standard 2D UIs as well as fully 3D UIs. The modular design allows an application to use all or some of it as needed or even to build a completely new custom GUI library on top.", "[\\.\\d]*"),
            new Library("LOG4J2","Log4j2", "org.apache.logging.log4j", List.of(new Artifact("log4j-core"), new Artifact("log4j-api")), false, LibraryCategory.GENERAL, false, "A popular java logging library, useful to produce text logs of whats going on in your game", "[\\.\\d]*" )
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
        toolProvidedLibraries.forEach(l -> currentAvailableLibraries.put(l.key(), l));
        categoriseLibraries();
        createUiLibraryDataDto();
    }

    /**
     * Expects the canonical currentAvailableLibraries to have already been created, then produces the other sorted
     * categories
     */
    private void categoriseLibraries (){
        currentAvailableLibraryByCategory = ArrayListMultimap.create();
        currentAvailableLibraries.values().forEach(l -> currentAvailableLibraryByCategory.put(l.category(), l));
        nonJmeLibraries = currentAvailableLibraries.values().stream().filter(l -> !l.usesJmeVersion()).collect(Collectors.toList());
        jmeLibraries = currentAvailableLibraries.values().stream().filter(l -> !l.usesJmeVersion()).collect(Collectors.toList());
    }

    private void createUiLibraryDataDto(){
        List<CategoryAndLibrariesDto> specialCategories = new ArrayList<>();
        Arrays.stream(LibraryCategory.values())
                .filter(c -> c!= LibraryCategory.JME_PLATFORM && c!= LibraryCategory.JME_GENERAL && c!= LibraryCategory.GENERAL)
                .forEach(c -> specialCategories.add(new CategoryAndLibrariesDto(
                        new CategoryDto(c),
                        librariesOfCategory(c).stream().map(LibraryDto::new).collect(Collectors.toList()),
                        defaultLibraryInExclusiveCategory(c).map(Library::key).orElse(null))
                ));


        uiLibraryDataDto = new UiLibraryDataDto(
                librariesOfCategory(LibraryCategory.JME_PLATFORM).stream().map(LibraryDto::new).collect(Collectors.toList()),
                librariesOfCategory(LibraryCategory.JME_GENERAL).stream().map(LibraryDto::new).collect(Collectors.toList()),
                specialCategories,
                librariesOfCategory(LibraryCategory.GENERAL).stream().map(LibraryDto::new).collect(Collectors.toList()),
                Stream.of(LibraryCategory.JME_GENERAL, LibraryCategory.GENERAL).flatMap(c -> librariesOfCategory(c).stream()).filter(Library::defaultSelected).map(Library::key).collect(Collectors.toList()),
                defaultLibraryInExclusiveCategory(LibraryCategory.JME_PLATFORM).map(Library::key).orElse(null)
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

        List<Library> defaultsInCategory = librariesOfCategory(category).stream().filter(Library::defaultSelected).collect(Collectors.toList());

        if (defaultsInCategory.size() > 1){
            throw new RuntimeException(category.name() + " has more than one default");
        }

        return defaultsInCategory.isEmpty() ? Optional.empty() : Optional.of(defaultsInCategory.get(0));
    }

    public Optional<Library> getLibraryFromKey (String key){
        return Optional.ofNullable(currentAvailableLibraries.get(key));
    }

}
