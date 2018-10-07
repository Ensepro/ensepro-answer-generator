package com.ensepro.answer.generator.data;

import com.ensepro.answer.generator.domain.MetricPolicy;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Metrica {

    private Double peso;
    private MetricPolicy policy;

}