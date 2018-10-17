package com.ensepro.answer.generator.answer;

import static com.ensepro.answer.generator.domain.GrammarClass.PROP;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

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

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class ScoreCalculation {

    private final Helper helper;
    private static final double FIXED_FACTOR = 1.1;

    public Answer calculate(Triple... triples) {
        return calculate(asList(triples));
    }

    public Answer calculate(List<Triple> triples) {

        final Set<RelevantKeyword> keywords = new HashSet<>();
        final Map<RelevantKeyword, Double> m1Values = new HashMap<>();
        final List<Length> lengths = new ArrayList<>();

        final Metric peso_m1 = helper.getMetrics().getOrDefault("m1", null);
        final Metric peso_m2 = helper.getMetrics().getOrDefault("m2", null);
        final Metric peso_m3 = helper.getMetrics().getOrDefault("m3", null);

        triples.forEach(triple -> {
            final Map<String, Integer> lengthKeyword = new HashMap<>();
            final Map<String, Integer> lengthMatch = new HashMap<>();

            for (String resourceVarName : triple.asStringList()) {
                final String resource = helper.getVar2resource().getOrDefault(resourceVarName, null);
                final RelevantKeyword rk = helper.getResource2keyword().getOrDefault(resource, null);

                if (isNull(rk)) {
                    continue;
                }

                final String keyword_name = helper.getSynonyms().get(rk.getKeyword());

                final RelevantKeyword _rk = RelevantKeyword.builder()
                    .keyword(isNull(keyword_name) ? rk.getKeyword() : keyword_name)
                    .weight(rk.getWeight())
                    .grammarClass(rk.getGrammarClass()).build();

                keywords.add(_rk);

                calculateM1(m1Values, peso_m1, resource, _rk);

                lengthMatch.put(resource, resource.length());
                lengthKeyword.put(_rk.getKeyword(), _rk.getKeyword().length());
            }

            final Length length = Length.builder()
                .keyword(lengthKeyword)
                .match(lengthMatch)
                .build();

            lengths.add(length);

        });

        final Double properNouns = (double) keywords.stream().filter(tr -> PROP.equals(tr.getGrammarClass()))
            .count();
        final Double elements = (double) (triples.size() * 3);
        final Double m1 = m1Values.values().stream().mapToDouble(Double::doubleValue).sum();
        final Double m2 = (double) keywords.size() / elements;
        final Double m3 =
            helper.getProperNouns().size() == 0 ? 0 : properNouns / (double) helper.getProperNouns().size();

        final Double score = m1 + (m2 * peso_m2.getWeight()) + (m3 * peso_m3.getWeight());

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
            .lentghs(lengths)
            .weightClasses(wc)
            .metrics(am)
            .build();

        return Answer.builder()
            .score(score)
            .triples(triples)
            .details(details)
            .build();

    }

    private void calculateM1(final Map<RelevantKeyword, Double> m1Values, final Metric peso_m1, final String resource,
        final RelevantKeyword _rk) {
        final double currentM1 = _calculateM1(resource, _rk, peso_m1.getWeight());
        final Double existentM1 = m1Values.getOrDefault(_rk, null);
        if (isNull(existentM1)) {
            m1Values.put(_rk, currentM1);
            return;
        }

        switch (peso_m1.getPolicy()) {
            case BEST_MATCH:
                if (currentM1 < existentM1) {
                    m1Values.put(_rk, currentM1);
                }
                break;
            case WORST_MATCH:
                if (currentM1 > existentM1) {
                    m1Values.put(_rk, currentM1);
                }
                break;
            case AVG:
                double avg = (currentM1 + existentM1) / 2;
                m1Values.put(_rk, avg);
                break;
        }

    }

    /**
     * @param resource Termo que deu o match
     * @param rk TermoRelevante considerado para o metch (termo original e não o sinonimo)
     * @param peso_m1 (peso da métrica 1)
     */
    private double _calculateM1(final String resource, final RelevantKeyword rk, final double peso_m1) {
        double keywordLength = rk.getKeyword().length();
        double matchLength = resource.length();

        double dividend = rk.getWeight() * keywordLength;
        double divider = (keywordLength - 1) + Math.pow(FIXED_FACTOR, (matchLength - keywordLength));

        return (dividend / divider) * peso_m1;
    }


}
