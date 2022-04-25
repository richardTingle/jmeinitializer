package com.jmonkeyengine.jmeinitializer.example;

import com.jmonkeyengine.jmeinitializer.libraries.Library;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExampleEndPoints{

    @Operation(summary = "An EXAMPLE of how an endpoint should be that the initializer calls to get libraries", description = "This application can have an endpoint specified by providing a -Dlibraries.fetchUrl argument. It will call that endpoint periodically to ask what libraries it should offer. This is documentation on what that endpoint should return (not an endpoint this application actually provides)")
    @GetMapping("/example/libraries.json")
    public List<Library> getProduct() {
        throw new UnsupportedOperationException("This is an example endpoint, for swagger documentation, not intended to be used");
    }

}
