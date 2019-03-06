package com.ensepro.answer.generator.data;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Triple {

    private List<Integer> triple;
    private Float score;
    private TripleDetail detail;


    public Integer getSubject() {
        return triple.get(0);
    }

    public Integer getPredicate() {
        return triple.get(1);
    }

    public Integer getObject() {
        return triple.get(2);
    }

}
