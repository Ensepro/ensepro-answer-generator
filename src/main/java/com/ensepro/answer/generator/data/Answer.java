package com.ensepro.answer.generator.data;

import java.util.List;

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

    @Override
    public int compareTo(final Answer other) {
        return other.score.compareTo(this.score);
    }
}
