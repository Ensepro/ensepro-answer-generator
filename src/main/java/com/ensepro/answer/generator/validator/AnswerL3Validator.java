package com.ensepro.answer.generator.validator;

import com.ensepro.answer.generator.data.Triple;

import java.util.List;

public class AnswerL3Validator {

  public boolean validate(final List<Triple> triples) {
    return this.validate(triples.get(0), triples.get(1), triples.get(2));
  }

  public boolean validate(final Triple triple1, final Triple triple2, final Triple triple3) {
    boolean triple1_2_same, triple1_3_same;
    boolean triple2_3_same;
    boolean duplicated_triple;

    triple1_2_same = triple1.equals(triple2);
    triple1_3_same = triple1.equals(triple3);
    triple2_3_same = triple2.equals(triple3);

    duplicated_triple = triple1_2_same || triple1_3_same || triple2_3_same;

    return !duplicated_triple;
  }
}
