package com.ensepro.answer.generator.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoreDetail {

    private final Float m1;
    private final Float m2;
    private final Float m3;
    @JsonProperty("matches")
    private final Map<Keyword, Match> matches;


}
