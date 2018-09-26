package com.ensepro.query.generator.classes;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Query implements Comparable<Query> {

    private final List<Tripla> triplas;
    private final Integer editDistance;
    private final Integer trCount;
    private final Integer varCount;


    @Override
    public int compareTo(Query other) {
        int compared = other.getTrCount().compareTo(getTrCount());

        if(compared == 0) {
            compared = getEditDistance().compareTo(other.getEditDistance());
        }

        if(compared == 0) {
            compared = getVarCount().compareTo(other.getVarCount());
        }

        return compared;


    }
}
