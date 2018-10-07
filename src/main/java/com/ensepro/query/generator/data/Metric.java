package com.ensepro.query.generator.data;

import com.ensepro.query.generator.domain.MetricPolicy;
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
