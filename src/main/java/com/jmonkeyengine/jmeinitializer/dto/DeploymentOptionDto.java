package com.jmonkeyengine.jmeinitializer.dto;

import com.jmonkeyengine.jmeinitializer.deployment.DeploymentOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DeploymentOptionDto{
    @Schema( example = "WINDOWS",  description = "A key for the deployment options, used in merge fields and when other libraries refer to this option")
    String key;

    @Schema( example = "Windows",  description = "A human readable name for the deployment option")
    String name;

    @Schema( example = "[\"JME_DESKTOP\"]", description = "The platforms this option is available for")
    Collection<String> applicablePlatforms;

    public DeploymentOptionDto(DeploymentOption rawOption){
        key = rawOption.name();
        name = rawOption.getOptionName();
        applicablePlatforms = rawOption.getRelevantToPlatforms();
    }

    public static List<DeploymentOptionDto> wrapAsDto(DeploymentOption[] rawOptions){
        return Arrays.stream(rawOptions)
                .map(DeploymentOptionDto::new)
                .collect(Collectors.toList());
    }
}
