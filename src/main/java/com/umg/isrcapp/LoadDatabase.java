package com.umg.isrcapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(SpotifyMetadataRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new SpotifyMetadata("Seven Nation Army", "USVT10300001", 2320000, true)));
            log.info("Preloading " + repository.save(new SpotifyMetadata("Yesterday", "GBAYE0601477", 123000, false)));
        };
    }
}
