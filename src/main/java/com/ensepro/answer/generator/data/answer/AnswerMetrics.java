package com.ensepro.answer.generator.data.answer;

import java.io.Serializable;
import java.util.List;

import com.ensepro.answer.generator.data.Metric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
@AllArgsConstructor
public class AnswerMetrics implements Serializable {

    @Singular
    private List<Double> scoreMetrics;
    @Singular
    private List<Metric> metrics;

}
