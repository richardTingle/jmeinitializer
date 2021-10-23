package com.jmonkeyengine.jmeinitializer.versions;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import com.jmonkeyengine.jmeinitializer.versions.dto.DocsDto;
import com.jmonkeyengine.jmeinitializer.versions.dto.MavenVersionApiResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This uses the API documented at https://search.maven.org/classic/#api to determine the most up to date
 * stable releases of thing. Which are used as dependencies.
 */
@Service
@Log4j2
public class VersionService {

    /**
     * A search URL (With merge fields) that will return json as to all the versions of that artifact
     * E.g. https://search.maven.org/solrsearch/select?q=g:%22org.jmonkeyengine%22+AND+a:%22jme3-core%22&core=gav&rows=20&wt=json
     *
     * The max rows allowed to query seems to be 200. This may become a problem at some point (although it does seem to return most recent first, so maybe it's fine)
     */
    String versionSearchUrl = "https://search.maven.org/solrsearch/select?q=g:\"[GROUP]\"+AND+a:\"[ARTIFACT]\"&core=gav&rows=200&wt=json";

    RestTemplate restTemplate = new RestTemplate();

    /**
     * Periodically the application will scan for the most recent version of a library and caches it here. Then
     * requests to build starter zips using that library will use this cached version
     */
    Map<String,String> versionCache = new ConcurrentHashMap<>();

    /**
     * Because many libraries use the same JME version this is a special case held separately
     */
    String jmeVersion;

    @Scheduled(fixedDelay = 24, timeUnit = TimeUnit.HOURS)
    public void fetchNewVersions(){
        log.info("fetching new library versions");

        fetchMostRecentStableVersion("org.jmonkeyengine", "jme3-core", ".*-stable").ifPresent(newVersion -> this.jmeVersion=newVersion);

        for(Library library : Library.nonJmeLibraries()) {
            String group = library.getGroupId();
            for (String artifactId : library.getArtifactIds()) {
                fetchMostRecentStableVersion(group, artifactId, library.getLibraryVersionRegex()).ifPresent(newVersion -> versionCache.put(group + ":" + artifactId, newVersion));
            }
        }
        int a=0;
    }


    /**
     * Will make an api call to attempt to get the most recent version of a library. If the api call fails Optional.empty will
     * be returned, in which case the old cached value should be retained.
     *
     * The acceptableLibraryRegex is used to determine if its a "release" version
     */
    public Optional<String> fetchMostRecentStableVersion(String group, String artifact, String acceptableLibraryRegex){
        return fetchRawVersionsForLibrary(group, artifact, acceptableLibraryRegex)
                .filter( listOfVersions -> !listOfVersions.isEmpty())
                .map( listOfVersions ->
                {
                    List<Version> listOfVersionObjects = new ArrayList<>();
                    listOfVersions.forEach(versionString -> listOfVersionObjects.add(new Version(versionString)));
                    return Collections.max(listOfVersionObjects).fullVersionString;
                });
    }

    /**
     * Will make an api call to attempt to get all the versions of a library. If the api call fails Optional.empty will
     * be returned, in which case the old cached value should be retained.
     */
    private Optional<List<String>> fetchRawVersionsForLibrary(String group, String artifact, String acceptableLibraryRegex){
        String searchUrl = versionSearchUrl.replace("[GROUP]",group).replace("[ARTIFACT]", artifact);

        ResponseEntity<MavenVersionApiResponseDto> apiResponse = restTemplate.getForEntity(searchUrl, MavenVersionApiResponseDto.class);

        if (apiResponse.getStatusCode().is2xxSuccessful()){
            return Optional.of(apiResponse.getBody().getResponse().getDocs().stream()
                    .map(DocsDto::getV)
                    .filter(version -> version.matches(acceptableLibraryRegex))
                    .collect(Collectors.toList()));
        }else{
            log.error("Failed to get a version response for " + group + ":" + artifact);
            log.error(apiResponse);
            return Optional.empty();
        }
    }


}
