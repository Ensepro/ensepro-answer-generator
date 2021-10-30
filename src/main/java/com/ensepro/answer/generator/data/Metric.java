package com.ensepro.answer.generator.data;

import com.ensepro.answer.generator.domain.MetricPolicy;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metric {

    public static String M1_KEY = "m1";
    public static String M2_KEY = "m2";
    public static String M3_KEY = "m3";

    @JsonProperty("peso")
    private Float weight;
    @JsonProperty("policy")
    private MetricPolicy policy;

}
