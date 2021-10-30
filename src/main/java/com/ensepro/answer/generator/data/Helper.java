package com.ensepro.answer.generator.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Helper {

    @JsonProperty("map_resource_to_var")
    private Map<String, Integer> resource2var;

    @JsonProperty("map_var_to_resource")
    private Map<String, String> var2resource;

    @JsonProperty("map_resource_to_tr")
    private Map<String, Keyword> resource2keyword;

    @JsonProperty("termos_relevantes")
    private List<Keyword> relevantKeywords;

    @JsonProperty("sinonimos")
    private Map<String, String> synonyms;

    @JsonProperty("substantivos_proprios_frase")
    private List<String> properNouns;

    @JsonProperty("metricas")
    private Map<String, Metric> metrics;

}
