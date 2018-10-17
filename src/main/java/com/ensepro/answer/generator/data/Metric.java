package com.ensepro.answer.generator.data;

import com.ensepro.answer.generator.domain.MetricPolicy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class Metric {

    private Double weight;
    private MetricPolicy policy;

}
