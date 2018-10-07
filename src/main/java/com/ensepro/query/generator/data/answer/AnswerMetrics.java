package com.ensepro.query.generator.data.answer;

import com.ensepro.query.generator.data.Metric;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AnswerMetrics implements Serializable {

    @Singular
    private List<Double> scoreMetrics;
    @Singular
    private List<Metric> metrics;

}
