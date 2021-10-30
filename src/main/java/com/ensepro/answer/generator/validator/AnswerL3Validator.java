package com.ensepro.answer.generator.validator;

import com.ensepro.answer.generator.data.Triple;

import java.util.List;

public class AnswerL3Validator {

    public boolean validate(final List<Triple> triples) {
        return this.validate(triples.get(0), triples.get(1), triples.get(2));
    }

    public boolean validate(final Triple triple1, final Triple triple2, final Triple triple3) {

        boolean subject_1_igual_2, subject_1_igual_3, subject_2_igual_3;
        boolean predicate_1_igual_2, predicate_1_igual_3, predicate_2_igual_3;
        boolean object_1_igual_2, object_1_igual_3, object_2_igual_3;

        boolean triple1_2_same, triple1_3_same, triple2_3_same;

        subject_1_igual_2 = triple1.getSubject().equals(triple2.getSubject());
        subject_1_igual_3 = triple1.getSubject().equals(triple3.getSubject());
        subject_2_igual_3 = triple2.getSubject().equals(triple3.getSubject());

        predicate_1_igual_2 = triple1.getPredicate().equals(triple2.getPredicate());
        predicate_1_igual_3 = triple1.getPredicate().equals(triple3.getPredicate());
        predicate_2_igual_3 = triple2.getPredicate().equals(triple3.getPredicate());

        object_1_igual_2 = triple1.getObject().equals(triple2.getObject());
        object_1_igual_3 = triple1.getObject().equals(triple3.getObject());
        object_2_igual_3 = triple2.getObject().equals(triple3.getObject());

        triple1_2_same = subject_1_igual_2 && predicate_1_igual_2 && object_1_igual_2;
        triple1_3_same = subject_1_igual_3 && predicate_1_igual_3 && object_1_igual_3;
        triple2_3_same = subject_2_igual_3 && predicate_2_igual_3 && object_2_igual_3;

        return !(triple1_2_same || triple2_3_same || triple1_3_same);
    }

}