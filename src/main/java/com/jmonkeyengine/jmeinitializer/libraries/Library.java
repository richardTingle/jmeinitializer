package com.jmonkeyengine.jmeinitializer.libraries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "The structure of json as expected to be supplied to the initializer by JMonkey store to inform it of available libraries ")
public class Library {

    /*
     * Used to uniquely represent the library in the UI and elsewhere
     */
    @JsonProperty(required = true)
    @Schema( example = "TAMARIN",  description = "A key for the library, most obviously used in [IF=???] conditional merge fields in templates but used to identify the library wherever a machine cares about that")
    String key;

    @JsonProperty(required = true)
    @Schema( example = "Tamarin", description ="A human readable short name for the library")
    String displayName;

    @Singular
    @JsonProperty(required = true)
    @Schema( description = "The actual maven artifacts that this library implies (usually just one but some libraries have multiple artifacts)")
    Collection<Artifact> artifacts;

    /**
     * Maven central is always available but additional repos may be provided.
     *
     * E.g. jcenter()
     */
    @Singular
    @Schema( example = "[\"jcenter()\"]", description = "Optional. Can pass additional maven repositories e.g. jcenter() if this library cannot be found on mavenCentral (which is provided by default)")
    Collection<String> additionalMavenRepos = List.of();

    @Schema( description = "True if this is a JMonkey library and so uses the unified JMonkeyEngine version.", defaultValue = "false")
    boolean usesJmeVersion = false;

    @Schema( description = "Used to divide the libraries up in the UI. JME_PLATFORM is a special category that can control if other libraries are available")
    @JsonProperty(required = true)
    LibraryCategory category;

    @Schema( description = "If this library is presented pre ticked in the UI", defaultValue = "false")
    boolean defaultSelected = false;

    @Schema( example = "A VR Library with hands and OpenVr support", description = "A longer piece of text (e.g. a sentence or two) describing the library")
    @JsonProperty( required = true)
    String descriptionText;

    /**
     * If a library requires a particular platform (e.g. Tamarin requires VR) then this will prevent it being selected
     * unless that platform is selected. Additionally, in multiplatform projects it will appear only in relevant modules.
     *
     * Only the keys are listed here
     */
    @Singular()
    @Schema( example = "[\"JME_DESKTOP\"]", description = "If this library should only be presented as an option if a certain platform has been selected (e.g. only VR libraries if the VR platform has been selected). If empty the library will be available for all platforms", defaultValue = "No required platform")
    Collection<String> requiredPlatforms = List.of();

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return key.equals(library.key);
    }

    @Override
    public int hashCode(){
        return Objects.hash(key);
    }

    public static Library.LibraryBuilder builder(String key, String displayName, LibraryCategory category, String descriptionText ){
        Library.LibraryBuilder builder = new Library.LibraryBuilder();
        builder.key(key)
                .displayName(displayName)
                .descriptionText(descriptionText)
                .category(category);
        return builder;
    }

}
