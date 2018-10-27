package com.ensepro.answer.generator.data;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Triples {

    @Singular
    private final List<Triple> triples;

    public static Triples fromNormalizedValues(List<List<Integer>> normalizedJsonValues) {
        final TriplesBuilder triples = Triples.builder();
        normalizedJsonValues.stream().map(Triple::fromList).forEach(triples::triple);
        return triples.build();
    }

}
