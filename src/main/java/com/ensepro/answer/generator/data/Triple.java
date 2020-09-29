package com.ensepro.answer.generator.data;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return triple.stream()
            .map(Object::toString)
            .collect(joining(","));
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Triple triple1 = (Triple) o;
        return getSubject().equals(triple1.getSubject())
            && getPredicate().equals(triple1.getPredicate())
            && getObject().equals(triple1.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(triple);
    }
}
