package com.umg.isrcapp;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
class SpotifyMetadataAssembler implements RepresentationModelAssembler<SpotifyMetadata, EntityModel<SpotifyMetadata>> {

    @Override
    public EntityModel<SpotifyMetadata> toModel(SpotifyMetadata metadata) {

        return EntityModel.of(metadata, //
                linkTo(methodOn(SpotifyMetadataController.class).oneISRC(metadata.getISRC())).withSelfRel(),
                linkTo(methodOn(SpotifyMetadataController.class).all()).withRel("metadata"));
    }
}
