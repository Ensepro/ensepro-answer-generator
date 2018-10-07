package com.ensepro.query.generator.data.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class Length implements Serializable {

    private Map<String, Integer> keyword;
    private Map<String, Integer> match;


}
