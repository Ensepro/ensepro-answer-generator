package com.ensepro.answer.generator.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Answer implements Comparable<Answer> {

    @Singular
    private final List<List<Integer>> triples;
    private final Float score;
    private final TripleDetail detail;

    @Singular
    @JsonIgnore
    private final List<Triple> originalTriples;

    @Override
    public int compareTo(final Answer other) {
        return other.score.compareTo(this.score);
    }

}
