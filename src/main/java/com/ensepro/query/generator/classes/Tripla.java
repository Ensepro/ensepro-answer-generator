package com.ensepro.query.generator.classes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tripla {

    private final String sujeito;
    private final String predicado;
    private final String objeto;

}
