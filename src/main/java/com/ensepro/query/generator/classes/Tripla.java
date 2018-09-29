package com.ensepro.query.generator.classes;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

import static java.util.Arrays.asList;

@Getter
@Builder
@EqualsAndHashCode
public class Tripla {

    private final String sujeito;
    private final String predicado;
    private final String objeto;

    public List<String> asStringList() {
        return asList(sujeito, predicado, objeto);
    }

}
