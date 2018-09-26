package com.ensepro.query.generator.classes;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(of = "termo")
public class TermoRelevante {

    private final String termo;
    private final Integer peso;

}
