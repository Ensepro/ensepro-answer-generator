package com.ensepro.answer.generator.data.answer;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Length implements Serializable {

    private Map<String, Integer> keyword;
    private Map<String, Integer> match;


}
