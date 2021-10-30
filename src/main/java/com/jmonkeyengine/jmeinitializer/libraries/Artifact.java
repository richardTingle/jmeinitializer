package com.jmonkeyengine.jmeinitializer.libraries;

public record Artifact(
        String artifactId,
        /*
         * Fallback versions for each of the artifacts declared. Can be null, in which case no fallback is possible.
         *
         * They are used for libraries not yet on maven central (which the tool polls for)
         */
        String fallbackVersion
) {

    public Artifact (String artifactId) {
        this(artifactId, "[MISSING_VERSION]");
    }
}
