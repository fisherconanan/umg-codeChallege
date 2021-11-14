package com.umg.isrcapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

interface SpotifyMetadataRepository extends JpaRepository<SpotifyMetadata, Long> {

    String FIND_BY_ISRC = "SELECT id FROM SpotifyMetadata WHERE isrc = :isrc";

    @Query (FIND_BY_ISRC)
    long findIdByISRC (@Param("isrc") String isrc);
}