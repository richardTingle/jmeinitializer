package com.jmonkeyengine.jmeinitializer;

import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class FragmentFetcher implements Function<String, String>{

    @SneakyThrows
    @Override
    public String apply(String fragmentName){
        Resource resource = ResourcePatternUtils.getResourcePatternResolver(null).getResource("jmefragments/" + fragmentName);
        if (resource.isReadable()){
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
        return null;
    }
}
