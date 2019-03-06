package com.ensepro.answer.generator.data;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class TripleDetail {

    @Singular
    private final List<Keyword> keywords;
    private final ScoreDetail scoreDetail;

}
