package com.ensepro.answer.generator.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class TripleDetail {

    @Singular
    @JsonIgnore
    private final List<Keyword> listKeywords;
    private final List<String> keywords;
    @JsonProperty("score_detail")
    private final ScoreDetail scoreDetail;
    @JsonProperty("proper_nouns_count")
    private final Integer properNounsCount;
    @JsonProperty("proper_nouns_matched_count")
    private final Integer properNounsMatchedCount;


}
