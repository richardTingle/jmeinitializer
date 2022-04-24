package com.jmonkeyengine.jmeinitializer.libraries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
public class Artifact{

    @JsonProperty(required = true)
    @JsonPropertyDescription("The maven group id of the artifact")
    String groupId;

    @JsonProperty(required = true)
    @JsonPropertyDescription("The maven artifact id of the artifact")
    String artifactId;

    @JsonPropertyDescription("""
                            If set then the version supplied here will be used and not the most recent version returned by maven.
                            
                            YOU USUALLY DON'T WANT THIS, usually use the fallbackVersion field instead.
                            
                            If you want to only have a 2.X version you can use the libraryVersionRegex to restrict the versions that
                            are automatically updated to. You only use this field if you want a specific, never changing, old version
                            to be used
                            """)
    String pinVersion = null;

    @JsonPropertyDescription("""
                             Fallback versions for each of the artifacts declared. Can be null, in which case no fallback is possible.
                             
                             They are used for libraries not yet on maven central (which the tool polls for)
                             """)
    String fallbackVersion = "[MISSING_VERSION]";

    /*
     * When searching for library versions the application uses this regex to determine if it's a "release" version
     * (And not a beta, release candidate etc).
     *
     * Equally, you can restrict to only a particular major version using a regex here
     */
    @JsonPropertyDescription("A regex used to restrict what versions are considered for the 'most recent version' usually used to eliminate release candidates etc but can also be used to restrict to only a particular major version etc.")
    String libraryVersionRegex = "[\\.\\d]*";

    @JsonIgnore
    public Optional<String> getPinVersionOpt(){
        return pinVersion == null || pinVersion.isBlank() ? Optional.empty() : Optional.of(pinVersion);
    }

}
