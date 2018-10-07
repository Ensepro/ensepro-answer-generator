package com.ensepro.answer.generator.data.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AnswerDetails implements Serializable {

    private AnswerMetrics metrics;
    private WeightClasses weightClasses;
    private List<Length> lentghs;
    private Integer matches;
    private Integer elements;
    private Integer nouns;
    private Integer nounsMatch;

}
