package com.ensepro.answer.generator.data;

import java.util.Objects;

import com.ensepro.answer.generator.domain.GrammarClass;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RelevantKeyword {

    private final String keyword;
    private final Float weight;
    private final GrammarClass grammarClass;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RelevantKeyword that = (RelevantKeyword) o;
        return Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }
}
