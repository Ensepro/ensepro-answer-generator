package com.ensepro.answer.generator.data;

import com.ensepro.answer.generator.domain.MetricPolicy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Metric {

    private Double weight;
    private MetricPolicy policy;

}
