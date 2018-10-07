package com.ensepro.answer.generator.answer;

import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Builder
public class AnswerGenerator {

    private static final int THREADS = 20;

    private final Helper helper;
    private final Triples triples;
    private final ScoreCalculation scoreCalculator;
    private final Integer level;

    //threads
    private final List<Callable<List<Answer>>> callables = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService executor = Executors.newFixedThreadPool(THREADS);

    public List<Answer> execute() throws InterruptedException {
        return gereneteAnswer();
    }

    private List<Answer> gereneteAnswer() throws InterruptedException {
        final Set<Answer> answers = new HashSet<>();

        triples.getTriples().forEach(triple -> {
            answers.add(scoreCalculator.calculate(triple));
            populateCallables(triple);
        });

        executor.invokeAll(callables)
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception ignore) {
                    }
                    return emptyList();
                }).collect(toList())
                .forEach(listAnwsers -> {
                    answers.addAll((Collection<? extends Answer>) listAnwsers);
                });

        executor.shutdown();

        return new ArrayList<>(answers);
    }

    private void populateCallables(Triple triple) {
        if (this.level > 1) {
            callables.add(() -> {
                return generateLevel2(triple);
            });
        }

        if (this.level > 2) {
            triples.getTriples().forEach(tripla2 -> {
                callables.add(() -> {
                    return generateLevel3(triple, tripla2);
                });
            });
        }
    }

    private List<Answer> generateLevel2(Triple triple1) {
        final List<Answer> answers = new ArrayList<>();
        triples.getTriples().forEach(triple2 -> {
            if (shouldAdd(triple1, triple2)) {
                answers.add(scoreCalculator.calculate(triple1, triple2));
            }
        });

        return answers;
    }

    private boolean shouldAdd(Triple triple1, Triple triple2) {
        boolean subject_igual, predicate_igual, object_igual;
        boolean subject_diferente, predicate_diferente, object_diferente;
        boolean subject_igual_object;

        subject_igual = triple1.getSubject().equals(triple2.getSubject());
        predicate_igual = triple1.getPredicate().equals(triple2.getPredicate());
        object_igual = triple1.getObject().equals(triple2.getObject());

        subject_diferente = !subject_igual;
        predicate_diferente = !predicate_igual;
        object_diferente = !object_igual;

        subject_igual_object = triple1.getSubject().equals(triple2.getObject());


        if (subject_igual && predicate_diferente && object_diferente) {
            return true;
        }

        if (subject_diferente && predicate_diferente && object_diferente && subject_igual_object) {
            return true;
        }

        if (subject_igual && predicate_igual && object_diferente) {
            return true;
        }

        if (subject_diferente && predicate_igual && object_igual) {
            return true;
        }

        return false;
    }

    private List<Answer> generateLevel3(Triple triple1, Triple triple2) {
        final List<Answer> answers = new ArrayList<>();
        triples.getTriples().forEach(triple3 -> {
            if (shouldAdd(triple1, triple2, triple3)) {
                answers.add(scoreCalculator.calculate(triple1, triple2, triple3));
            }
        });
        return answers;
    }

    private boolean shouldAdd(Triple tripla1, Triple tripla2, Triple tripla3) {
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
        predicates_todos_diferentes = !predicate1_igual_predicate2 && !predicate2_igual_predicate3 && !predicate1_igual_predicate3;
        objects_todos_diferentes = !object1_igual_object2 && !object2_igual_object3 && !object1_igual_object3;

        if (subjects_iguais && predicates_iguais && objects_iguais) {
            return true;
        }

        if (subject1_igual_subject2 && object2_igual_object3 && predicates_todos_diferentes && !object1_igual_object2) {
            return true;
        }

        if (subject1_igual_subject2 && object2_igual_subject3 && predicates_todos_diferentes && objects_todos_diferentes &&
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
