package com.ensepro.query.generator.data.answer;

import com.ensepro.query.generator.data.Triple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

@Getter
@Builder
public class Answer implements Comparable<Answer>, Serializable {

    private final List<Triple> triples;
    private final Double score;
    private final AnswerDetails details;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private Set<String> elements;

    @Override
    public int compareTo(Answer o) {
        return o.score.compareTo(this.score);
    }

    private Set<String> getElements() {
        if (nonNull(elements)) {
            return this.elements;
        }
        this.elements = new HashSet<>();
        triples.forEach(tripla -> elements.addAll(tripla.asStringList()));
        return this.elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer that = (Answer) o;
        return this.getElements().containsAll(that.getElements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(triples);
    }
}
