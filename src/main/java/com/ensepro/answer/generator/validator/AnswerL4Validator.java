package com.ensepro.answer.generator.validator;

import com.ensepro.answer.generator.data.Triple;

import java.util.List;

public class AnswerL4Validator {

  public boolean validate(final List<Triple> triples) {
    return this.validate(triples.get(0), triples.get(1), triples.get(2), triples.get(3));
  }

  public boolean validate(
      final Triple triple1, final Triple triple2, final Triple triple3, final Triple triple4) {
    boolean triple1_2_same, triple1_3_same, triple1_4_same;
    boolean triple2_3_same, triple2_4_same;
    boolean triple3_4_same;
    boolean duplicated_triple;

    triple1_2_same = triple1.equals(triple2);
    triple1_3_same = triple1.equals(triple3);
    triple1_4_same = triple1.equals(triple4);
    triple2_3_same = triple2.equals(triple3);
    triple2_4_same = triple2.equals(triple4);
    triple3_4_same = triple3.equals(triple4);

    duplicated_triple =
        triple1_2_same
            || triple1_3_same
            || triple1_4_same
            || triple2_3_same
            || triple2_4_same
            || triple3_4_same;

    return !duplicated_triple;
  }
}
