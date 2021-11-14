package com.umg.isrcapp;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.springframework.data.domain.Example;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.special.SearchResult;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.search.SearchItemRequest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class SpotifyMetadataController {
    private final SpotifyMetadataRepository repository;
    private final SpotifyMetadataAssembler assembler;
    private RestTemplate restTemplate;

    SpotifyMetadataController(SpotifyMetadataRepository repository, SpotifyMetadataAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/metadata")
    CollectionModel<EntityModel<SpotifyMetadata>> all() {

        List<EntityModel<SpotifyMetadata>> metadata = repository.findAll().stream() //
                .map(assembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(metadata, linkTo(methodOn(SpotifyMetadataController.class).all()).withSelfRel());
    }
    // end::get-aggregate-root[]

    @PostMapping("/metadata")
    SpotifyMetadata newSpotifyMetadata(@RequestBody SpotifyMetadata newMetadata) {
        return repository.save(newMetadata);
    }

    // Single item

    @GetMapping("/metadata/{id}")
    EntityModel<SpotifyMetadata> one(@PathVariable Long id) {

        SpotifyMetadata spotifyMetadata = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(spotifyMetadata);
    }

    @GetMapping("/getTrack")
    @ResponseBody
    EntityModel<SpotifyMetadata> oneISRC(@RequestParam("isrc") String isrc) {
        //Example<SpotifyMetadata> example = Example.of(new SpotifyMetadata(null, isrc, null, null));
        long id;
        try {
            id = repository.findIdByISRC(isrc);
        } catch(Exception e) {
            throw new EmployeeNotFoundException(isrc);
        }
        SpotifyMetadata spotifyMetadata = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(isrc));

        return assembler.toModel(spotifyMetadata);
    }

    @PutMapping("/metadata/{id}")
    SpotifyMetadata replaceMetadata(@RequestBody SpotifyMetadata newSpotifyMetadata, @PathVariable Long id) {

        return repository.findById(id)
                .map(spotifyMetadata -> {
                    spotifyMetadata.setName(spotifyMetadata.getName());
                    spotifyMetadata.setDuration_ms(spotifyMetadata.getDuration_ms());
                    spotifyMetadata.setExplicit(spotifyMetadata.isExplicit());
                    spotifyMetadata.setISRC(spotifyMetadata.getISRC());
                    return repository.save(spotifyMetadata);
                })
                .orElseGet(() -> {
                    newSpotifyMetadata.setId(id);
                    return repository.save(newSpotifyMetadata);
                });
    }

    @DeleteMapping("/metadata/{id}")
    void deleteMetadata(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/createTrack")
    EntityModel<SpotifyMetadata> newSpotifyMetadata(@RequestParam("isrc") String isrc) {
        boolean trackFound = true;
        final String clientId = "506d430c4eb54fbe816597603a820f21";
        final String clientSecret = "8f3ca7c2168f4f1283db384e3a659485";
        String q = "isrc:" + isrc;
        String type = ModelObjectType.TRACK.getType();
        String accessToken = "";
        ClientCredentials clientCredentials = null;

        // Build Spotify API object with client credentials
        final SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        // Build request to create object that will hold the access token
        final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();

        // Execute the request to get access token
        try {
            clientCredentials = clientCredentialsRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }

        // Set the API's access token using client credentials
        spotifyApi.setAccessToken(clientCredentials.getAccessToken());

        // Build the search query to send to Spotify
        SearchItemRequest searchItemRequest = spotifyApi.searchItem(q,type).limit(1).build();

        // Execute search
        SearchResult searchResult = null;
        try {
            searchResult = searchItemRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }

        // Create array of the results (should only be one result due to limit)
        Track trackArray[] = searchResult.getTracks().getItems();

        // Throw error if no results were found
        if (trackArray.length == 0) {
            throw new EmployeeNotFoundException(isrc);
        }

        // Get metadata from track
        String trackName = trackArray[0].getName();
        int trackDuration = trackArray[0].getDurationMs();
        boolean isTrackExplicit = trackArray[0].getIsExplicit();

        // Create new entity with track data
        SpotifyMetadata newEntry = new SpotifyMetadata(trackName, isrc, trackDuration, isTrackExplicit);

        // Add to db and return its info
        repository.save(newEntry);
        return assembler.toModel(newEntry);
    }
}
