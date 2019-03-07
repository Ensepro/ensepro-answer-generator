package com.ensepro.answer.generator.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class TripleDetail {

    @Singular
    private final List<Keyword> keywords;
    @JsonProperty("score_detail")
    private final ScoreDetail scoreDetail;
    @JsonProperty("proper_nouns_count")
    private final Integer properNounsCount;
    @JsonProperty("proper_nouns_matched_count")
    private final Integer properNounsMatchedCount;

}
