package com.ensepro.answer.generator.data;

import java.util.List;

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
public class PythonResult {

    @JsonProperty("values")
    private List<List<Integer>> triples;

    @JsonProperty("helper")
    private Helper helper;

    @JsonProperty("frase")
    private String phrase;

}
