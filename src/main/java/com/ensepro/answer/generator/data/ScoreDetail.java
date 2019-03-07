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
    @JsonProperty("m1_values")
    private final Map<Keyword, Float> m1Values;


}
