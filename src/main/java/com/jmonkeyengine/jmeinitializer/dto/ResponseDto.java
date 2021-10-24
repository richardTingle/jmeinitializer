package com.jmonkeyengine.jmeinitializer.dto;

import lombok.Data;

import java.util.List;

/**
 * The api we are calling confusingly has a key called response, within its response. This represents that object, and
 * is not the top level response. Sorry
 */
@Data
public class ResponseDto {
    int numFound;
    int start;
    List<DocsDto> docs;
}
