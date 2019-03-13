package com.ensepro.answer.generator.validator;


import static com.ensepro.answer.generator.domain.GrammarClass.PROP;
import static com.ensepro.answer.generator.domain.GrammarClass.SUB;
import static com.ensepro.answer.generator.domain.GrammarClass.VERB;
import static com.ensepro.answer.generator.domain.Position.OBJECT;
import static com.ensepro.answer.generator.domain.Position.PREDICATE;
import static com.ensepro.answer.generator.domain.Position.SUBJECT;

import java.util.HashMap;
import java.util.Map;

import com.ensepro.answer.generator.domain.GrammarClass;
import com.ensepro.answer.generator.domain.Position;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public class PositionValidator {

    private final Map<PositionKey, Boolean> validPositions;

    public PositionValidator() {
        this.validPositions = new HashMap<>();

        this.validPositions.put(position(SUBJECT, PROP), true);
        this.validPositions.put(position(SUBJECT, SUB), true);

        this.validPositions.put(position(PREDICATE, VERB), true);
        this.validPositions.put(position(PREDICATE, SUB), true);

        this.validPositions.put(position(OBJECT, PROP), true);
        this.validPositions.put(position(OBJECT, SUB), true);

    }

    private PositionKey position(final Position position, final GrammarClass grammarClass) {
        return new PositionKey(position, grammarClass);
    }

    public boolean validate(final Position position, final GrammarClass grammarClass) {
        return validPositions.getOrDefault(position(position, grammarClass), false);
    }


}

@EqualsAndHashCode
@AllArgsConstructor
class PositionKey {

    private final Position position;
    private final GrammarClass grammarClass;

}
