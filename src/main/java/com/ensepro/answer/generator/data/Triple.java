package com.ensepro.answer.generator.data;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Triple {

    private final Integer subject;
    private final Integer predicate;
    private final Integer object;

    public List<Integer> asList() {
        return Arrays.asList(subject, predicate, object);
    }

    /**
     * Only will consider the 3 first values. 0 - subject 1 - predicate 2 - object
     */
    public static Triple fromList(List<Integer> triple) {
        return Triple.builder()
            .subject(triple.get(0))
            .predicate(triple.get(1))
            .object(triple.get(2))
            .build();
    }
}
