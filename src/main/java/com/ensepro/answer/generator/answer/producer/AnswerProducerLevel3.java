package com.ensepro.answer.generator.answer.producer;

import java.util.List;
import java.util.concurrent.Callable;

import com.ensepro.answer.generator.answer.ScoreCalculation;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnswerProducerLevel3 extends AnswerProducer {

    @Builder
    public AnswerProducerLevel3(final Triples triples,
        final List<Callable<Answer>> callables, final ScoreCalculation scoreCalculation) {
        super(triples, callables, scoreCalculation);
        setName("AnswerProducerLevel3");
    }

    @Override
    public void run() {
        log.info("Starting");
        triples.getTriples().forEach(triple1 -> {
            triples.getTriples().forEach(triple2 -> {
                triples.getTriples().forEach(triple3 -> {
                    if (shouldAdd(triple1, triple2, triple3)) {
                        put(triple1, triple2, triple3);
                    }
                });
            });
        });
        log.info("Finished");
    }

    private boolean shouldAdd(final Triple tripla1, final Triple tripla2, final Triple tripla3) {
        boolean subject1_igual_subject2, subject2_igual_subject3;
        boolean predicate1_igual_predicate2, predicate2_igual_predicate3, predicate1_igual_predicate3;
        boolean object1_igual_object2, object2_igual_object3, object1_igual_object3;
        boolean object1_igual_subject2, object2_igual_subject3;
        boolean subjects_iguais, predicates_iguais, objects_iguais;
        boolean predicates_todos_diferentes, objects_todos_diferentes;

        subject1_igual_subject2 = tripla1.getSubject().equals(tripla2.getSubject());
        subject2_igual_subject3 = tripla2.getSubject().equals(tripla3.getSubject());
//            boolean subject1_igual_subject3 = tripla1.get(0).equals(tripla3.get(0));

        predicate1_igual_predicate2 = tripla1.getPredicate().equals(tripla2.getPredicate());
        predicate2_igual_predicate3 = tripla2.getPredicate().equals(tripla3.getPredicate());
        predicate1_igual_predicate3 = tripla1.getPredicate().equals(tripla3.getPredicate());

        object1_igual_object2 = tripla1.getObject().equals(tripla2.getObject());
        object2_igual_object3 = tripla2.getObject().equals(tripla3.getObject());
        object1_igual_object3 = tripla1.getObject().equals(tripla3.getObject());

        object1_igual_subject2 = tripla1.getObject().equals(tripla2.getSubject());
        object2_igual_subject3 = tripla2.getObject().equals(tripla3.getSubject());

        subjects_iguais = subject1_igual_subject2 && subject2_igual_subject3;
        predicates_iguais = predicate1_igual_predicate2 && predicate2_igual_predicate3;
        objects_iguais = object1_igual_object2 && object2_igual_object3;

//            boolean subjects_todos_diferentes = !subject1_igual_subject2 && !subject2_igual_subject3 && !subject1_igual_subject3;
        predicates_todos_diferentes =
            !predicate1_igual_predicate2 && !predicate2_igual_predicate3 && !predicate1_igual_predicate3;
        objects_todos_diferentes = !object1_igual_object2 && !object2_igual_object3 && !object1_igual_object3;

        if (subjects_iguais && predicates_iguais && objects_iguais) {
            return true;
        }

        if (subject1_igual_subject2 && object2_igual_object3 && predicates_todos_diferentes && !object1_igual_object2) {
            return true;
        }

        if (subject1_igual_subject2 && object2_igual_subject3 && predicates_todos_diferentes && objects_todos_diferentes
            &&
            !subject2_igual_subject3) {
            return true;
        }

        if (object1_igual_subject2 &&
            subject2_igual_subject3 &&
            predicate2_igual_predicate3 &&
            objects_todos_diferentes &&
            !predicate1_igual_predicate2 &&
            !subjects_iguais) {
            return true;
        }

        return false;
    }


}
