package com.ensepro.answer.generator.data;

import com.ensepro.answer.generator.domain.MetricPolicy;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Metrica {

    private Float peso;
    private MetricPolicy policy;

}