package com.ensepro.answer.generator.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

import static java.util.Arrays.asList;

@Getter
@Builder
@EqualsAndHashCode
public class Triple {

    private final String subject;
    private final String predicate;
    private final String object;

    public List<String> asStringList() {
        return asList(subject, predicate, object);
    }

    /**
     * Only will consider the 3 first values.
     * 0 - subject
     * 1 - predicate
     * 2 - object
     *
     * @param triple
     * @return
     */
    public static Triple fromList(List<String> triple) {
        return Triple.builder()
                .subject(triple.get(0))
                .predicate(triple.get(1))
                .object(triple.get(2))
                .build();
    }
}
