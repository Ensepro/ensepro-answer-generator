package com.ensepro.answer.generator.answer;

import static com.ensepro.answer.generator.domain.GrammarClass.PROP;
import static com.ensepro.answer.generator.domain.GrammarClass.SUB;
import static com.ensepro.answer.generator.domain.GrammarClass.VERB;
import static com.ensepro.answer.generator.domain.Position.OBJECT;
import static com.ensepro.answer.generator.domain.Position.PREDICATE;
import static com.ensepro.answer.generator.domain.Position.SUBJECT;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Metric;
import com.ensepro.answer.generator.data.RelevantKeyword;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.answer.Answer;
import com.ensepro.answer.generator.data.answer.AnswerDetails;
import com.ensepro.answer.generator.data.answer.AnswerMetrics;
import com.ensepro.answer.generator.data.answer.Length;
import com.ensepro.answer.generator.data.answer.WeightClasses;
import com.ensepro.answer.generator.domain.GrammarClass;
import com.ensepro.answer.generator.domain.Position;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class ScoreCalculation {

    private final Helper helper;
    private static final float FIXED_FACTOR = 1.1f;

    public Answer calculate(Triple... triples) {
        return calculate(asList(triples));
    }

    public Answer calculate(List<Triple> triples) {

        final Set<RelevantKeyword> keywords = new HashSet<>();
        final Map<RelevantKeyword, Float> m1Values = new HashMap<>();
        final List<Length> lengths = new ArrayList<>();

        final Metric peso_m1 = helper.getMetrics().getOrDefault("m1", null);
        final Metric peso_m2 = helper.getMetrics().getOrDefault("m2", null);
        final Metric peso_m3 = helper.getMetrics().getOrDefault("m3", null);

        triples.forEach(triple -> {
            final Map<String, Integer> lengthsKeyword = new HashMap<>();
            final Map<String, Integer> lengthsMatch = new HashMap<>();

            populateValues(
                keywords,
                m1Values,
                peso_m1,
                lengthsKeyword,
                lengthsMatch,
                triple.getSubject(),
                SUBJECT);

            populateValues(
                keywords,
                m1Values,
                peso_m1,
                lengthsKeyword,
                lengthsMatch,
                triple.getPredicate(),
                PREDICATE);

            populateValues(
                keywords,
                m1Values,
                peso_m1,
                lengthsKeyword,
                lengthsMatch,
                triple.getObject(),
                OBJECT);

            helper.getKeywords().stream()
                .filter(keyword -> GrammarClass.ADJ.equals(keyword.getGrammarClass()))
                .distinct()
                .forEach(adjKeyword -> {

                    String resource = helper.getVar2resource().get(triple.getPredicate().toString());
                    if (resource.contains(adjKeyword.getKeyword())) {
                        log.info("calculando ADJs (PREDICATE): {} - {} - {}", resource, adjKeyword);
                        calculateM1ADJ(m1Values, adjKeyword);
                        keywords.add(adjKeyword);
                    }

                    resource = helper.getVar2resource().get(triple.getSubject().toString());
                    if (resource.contains(adjKeyword.getKeyword())) {
                        log.info("calculando ADJs (SUBJECT): {} - {} - {}", resource, adjKeyword);
                        calculateM1ADJ(m1Values, adjKeyword);
                        keywords.add(adjKeyword);
                    }

                    resource = helper.getVar2resource().get(triple.getObject().toString());
                    if (resource.contains(adjKeyword.getKeyword())) {
                        log.info("calculando ADJs (OBJECT): {} - {} - {}", resource, adjKeyword);
                        calculateM1ADJ(m1Values, adjKeyword);
                        keywords.add(adjKeyword);
                    }

                });

            final Length length = Length.builder()
                .keyword(lengthsKeyword)
                .match(lengthsMatch)
                .build();

            lengths.add(length);

        });

        final Float properNouns = (float) keywords.stream().filter(tr -> PROP.equals(tr.getGrammarClass())).count();
        final Float elements = (float) (triples.size() * 3);
        final Float m1 = (float) m1Values.values().stream().mapToDouble(Float::doubleValue).sum();
        final Float m2 = (float) keywords.size() / elements;
        final Float m3 = helper.getProperNouns().size() == 0
            ? 1
            : properNouns / (float) helper.getProperNouns().size();

        final Float score = m1 + (m2 * peso_m2.getWeight()) + (m3 * peso_m3.getWeight());

        final WeightClasses wc = WeightClasses.builder().keyword(new ArrayList<>(keywords)).build();

        final AnswerMetrics am = AnswerMetrics.builder()
            .metric(peso_m1)
            .metric(peso_m2)
            .metric(peso_m3)
            .scoreMetric(m1)
            .scoreMetric(m2)
            .scoreMetric(m3)
            .build();

        final AnswerDetails details = AnswerDetails.builder()
            .elements(elements.intValue())
            .matches(keywords.size())
            .nouns(helper.getProperNouns().size())
            .nounsMatch(properNouns.intValue())
            .lengths(lengths)
            .weightClasses(wc)
            .metrics(am)
            .build();

        return Answer.builder()
            .score(score)
            .triples(triples)
            .details(details)
            .build();

    }

    private void populateValues(final Set<RelevantKeyword> keywords,
        final Map<RelevantKeyword, Float> m1Values,
        final Metric peso_m1,
        final Map<String, Integer> lengthKeyword,
        final Map<String, Integer> lengthMatch,
        final Integer resourceVarName,
        final Position position
    ) {
        final String resource = helper.getVar2resource().getOrDefault(resourceVarName.toString(), null);
        final RelevantKeyword rk = helper.getResource2keyword().getOrDefault(resource, null);

        if (isNull(rk)) {
            return;
        }

        if (!validPosition(rk, position)) {
            return;
        }

        final String original_tr = helper.getSynonyms().getOrDefault(rk.getKeyword(), rk.getKeyword());
        final RelevantKeyword _rk = RelevantKeyword.builder()
            .keyword(original_tr)
            .weight(rk.getWeight())
            .grammarClass(rk.getGrammarClass()).build();

        keywords.add(_rk);

        final Float m1 = calculateM1(m1Values, _rk, peso_m1, resource, rk);

        m1Values.put(_rk, m1);

        lengthMatch.put(resource, resource.length());
        lengthKeyword.put(rk.getKeyword(), rk.getKeyword().length());
    }

    private boolean validPosition(final RelevantKeyword rk, final Position position) {
        if (SUBJECT.equals(position)) {
            return PROP.equals(rk.getGrammarClass())
                || SUB.equals(rk.getGrammarClass());
        }
        if (PREDICATE.equals(position)) {
            return VERB.equals(rk.getGrammarClass())
                || SUB.equals(rk.getGrammarClass());
        }
        if (OBJECT.equals(position)) {
            return PROP.equals(rk.getGrammarClass())
                || SUB.equals(rk.getGrammarClass());
        }
        return true;
    }


    private void calculateM1ADJ(final Map<RelevantKeyword, Float> m1Values,
        final RelevantKeyword _rk) {

        final Float existentM1 = m1Values.getOrDefault(_rk, null);
        if (isNull(existentM1)) {
            m1Values.put(_rk, _rk.getWeight());
            return;
        }
        float sum = _rk.getWeight() + existentM1;
        m1Values.put(_rk, sum);
    }


    private Float calculateM1(final Map<RelevantKeyword, Float> m1Values, final RelevantKeyword mapKey, final Metric peso_m1, final String resource,
        final RelevantKeyword _rk) {
        final float currentM1 = _calculateM1(resource, _rk, peso_m1.getWeight());
        final Float existentM1 = m1Values.getOrDefault(mapKey, null);

        if (isNull(existentM1)) {
            return currentM1;
        }

        switch (peso_m1.getPolicy()) {
            case BEST_MATCH:
                if (currentM1 > existentM1) {
                    return currentM1;
                }
                return existentM1;
            case WORST_MATCH:
                if (currentM1 < existentM1) {
                    return currentM1;
                }
                return existentM1;
            case AVG:
                return (currentM1 + existentM1) / 2;
            case SUM:
                return currentM1 + existentM1;
        }
        return 0F;
    }

    /**
     * @param resource Termo que deu o match
     * @param rk TermoRelevante considerado para o metch (termo original e não o sinonimo)
     * @param peso_m1 (peso da métrica 1)
     */
    private float _calculateM1(final String resource, final RelevantKeyword rk, final float peso_m1) {
        float keywordLength = rk.getKeyword().length();
        float matchLength = resource.length();

        float dividend = rk.getWeight() * keywordLength;
        float divider = (float) ((keywordLength - 1) + Math.pow(FIXED_FACTOR, (matchLength - keywordLength)));

        return (dividend / divider) * peso_m1;
    }


}
