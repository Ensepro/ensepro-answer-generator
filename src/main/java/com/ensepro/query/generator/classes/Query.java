package com.ensepro.query.generator.classes;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

@Getter
@Builder
public class Query implements Comparable<Query> {

    private final List<Tripla> triplas;
    private final Integer editDistance;
    private final Integer trCount;
    private final Integer varCount;
    private final Float m1;
    private final Float m2;
    private final Float m3;
    private final Float score;

    private Set<String> elements;

    @Override
    public int compareTo(Query other) {
        return other.score.compareTo(score);
    }

    public Set<String> getElements() {
        if (nonNull(elements)) {
            return this.elements;
        }
        this.elements = new HashSet<>();
        triplas.forEach(tripla -> elements.addAll(tripla.asStringList()));
        return this.elements;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query that = (Query) o;
        return this.getElements().containsAll(that.getElements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(triplas);
    }
}
