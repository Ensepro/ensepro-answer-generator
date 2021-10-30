package com.ensepro.answer.generator.data;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class Answer implements Comparable<Answer> {

    @Singular
    private final List<List<Integer>> triples;
    private final Float score;
    private final TripleDetail detail;

    @Override
    public int compareTo(final Answer other) {
        return other.score.compareTo(this.score);
    }
}
