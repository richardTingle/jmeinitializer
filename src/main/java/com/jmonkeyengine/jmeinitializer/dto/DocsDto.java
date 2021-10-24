package com.jmonkeyengine.jmeinitializer.dto;

import lombok.Data;

@Data
public class DocsDto {
    /**
     * The full maven coordinate
     */
    String id;

    /**
     * The group
     */
    String g;

    /**
     * The artifact
     */
    String a;

    /**
     * The version
     */
    String v;

    //there are other unmapped pieces of data in this Dto that we didn't need
}
