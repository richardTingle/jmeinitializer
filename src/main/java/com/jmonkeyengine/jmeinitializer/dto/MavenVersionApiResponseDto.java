package com.jmonkeyengine.jmeinitializer.dto;

import lombok.Data;

/**
 * This represents the json returned from the api at https://search.maven.org/solrsearch.
 *
 * There are more keys than are represented here, but these are all that we needed.
 *
 * This is the top level object
 */
@Data
public class MavenVersionApiResponseDto {
    ResponseHeaderDto responseHeader;
    ResponseDto response;
}
