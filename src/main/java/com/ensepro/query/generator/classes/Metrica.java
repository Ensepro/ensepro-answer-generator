package com.ensepro.query.generator.classes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Metrica {

    private Double peso;
    private MetricaPolicy policy;

}
