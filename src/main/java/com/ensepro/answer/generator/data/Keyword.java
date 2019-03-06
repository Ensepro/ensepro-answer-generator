package com.ensepro.answer.generator.data;

import java.util.Objects;

import com.ensepro.answer.generator.domain.GrammarClass;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Keyword implements Comparable<Keyword> {

    @JsonProperty("termo")
    private String keyword;
    @JsonProperty("peso")
    private Float weight;
    @JsonProperty("classe")
    private GrammarClass grammarClass;

    public Keyword clone() {
        return Keyword.builder()
            .keyword(this.getKeyword())
            .grammarClass(this.getGrammarClass())
            .weight(this.getWeight())
            .build();
    }

    @Override
    public boolean equals(final Object o) {
        //TODO check if these two IF's are really necessary
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Keyword keyword1 = (Keyword) o;
        return Objects.equals(keyword, keyword1.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }

    @Override
    public int compareTo(final Keyword other) {
        return other.weight.compareTo(this.weight);
    }
}
