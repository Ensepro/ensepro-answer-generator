package com.ensepro.answer.generator.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.data.Triple;

public class TripleMapper {

    public List<Triple> map(List<List<Integer>> triples) {

        return triples.stream()
            .map(this::newTriple)
            .collect(Collectors.toList());

    }

    private Triple newTriple(final List<Integer> triple) {
        return Triple.builder().triple(triple).build();
    }

}
