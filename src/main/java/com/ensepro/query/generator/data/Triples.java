package com.ensepro.query.generator.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class Triples {

    @Singular
    private final List<Triple> triples;

    public static Triples fromNormalizedValues(List<List<String>> normalizedJsonValues) {
        final TriplesBuilder triples = Triples.builder();
        normalizedJsonValues.stream().map(Triple::fromList).forEach(triples::triple);
        return triples.build();
    }

}
