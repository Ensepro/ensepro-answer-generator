package com.ensepro.answer.generator.mapper;

import static com.ensepro.answer.generator.domain.GrammarClass.PROP;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Keyword;
import com.ensepro.answer.generator.data.Metric;
import com.ensepro.answer.generator.data.ScoreDetail;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.TripleDetail;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AnswerMapper {

    private final Helper helper;

    public Answer fromTriple(final Triple triple) {
        return Answer.builder()
            .triples(singletonList(triple.getTriple()))
            .score(triple.getScore())
            .detail(triple.getDetail())
            .build();
    }

    public Answer fromTriples(final List<Triple> triples) {
        final Metric metricM1 = helper.getMetrics().get(Metric.M1_KEY);
        final Metric metricM2 = helper.getMetrics().get(Metric.M2_KEY);
        final Metric metricM3 = helper.getMetrics().get(Metric.M3_KEY);

        final Map<Keyword, Float> finalM1Values = new HashMap<>();

        List<Keyword> keywords = triples.stream()
            .map(Triple::getDetail)
            .map(TripleDetail::getKeywords)
            .flatMap(List::stream)
            .sorted()    // makes sure the highest weight are first
            .distinct()  // so here the distinct will always keep the first element
            .collect(Collectors.toList());

        triples.stream()
            .map(Triple::getDetail)
            .map(TripleDetail::getScoreDetail)
            .map(ScoreDetail::getM1Values)
            .forEach(m1Values -> merge(finalM1Values, m1Values));

        final Float properNouns = (float) keywords.stream().filter(tr -> PROP.equals(tr.getGrammarClass())).count();
        final Float m1 = (float) finalM1Values.values().stream().mapToDouble(Float::doubleValue).sum();
        final Float m2 = keywords.size() / (3F * triples.size());
        final Float m3 = helper.getProperNouns().size() == 0 ? 0 : properNouns / helper.getProperNouns().size();

        final Float score = (m1 * metricM1.getWeight()) + (m2 * metricM2.getWeight()) + (m3 * metricM3.getWeight());

        final ScoreDetail scoreDetail = ScoreDetail.builder()
            .m1(m1)
            .m2(m2)
            .m3(m3)
            .m1Values(finalM1Values)
            .build();

        final TripleDetail tripleDetail = TripleDetail.builder()
            .keywords(new ArrayList<>(keywords))
            .scoreDetail(scoreDetail)
            .properNounsCount(helper.getProperNouns().size())
            .properNounsMatchedCount(properNouns.intValue())
            .build();

        return Answer.builder()
            .triples(triples.stream().map(Triple::getTriple).collect(Collectors.toList()))
            .detail(tripleDetail)
            .score(score)
            .build();
    }

    private void merge(final Map<Keyword, Float> finalM1Values, final Map<Keyword, Float> m1Values) {
        m1Values.forEach((keyword, score) -> {
            final Float currentScore = finalM1Values.get(keyword);
            if (currentScore == null) {
                finalM1Values.put(keyword, score);
                return;
            }
            final Float newScore = getScoreBasedInPolicy(currentScore, score);
            finalM1Values.put(keyword, newScore);
        });
    }

    private Float getScoreBasedInPolicy(final Float currentScore, final Float score) {
        //TODO maybe move this logic to a Map<Policy, Function> its going to be better
        switch (helper.getMetrics().get(Metric.M1_KEY).getPolicy()) {
            case BEST_MATCH:
                if (currentScore > score) {
                    return currentScore;
                }
                return score;
            case WORST_MATCH:
                if (currentScore < score) {
                    return currentScore;
                }
                return score;
            case AVG:
                return (currentScore + score) / 2F;
            case SUM:
                return currentScore + score;
        }
        return 0F;
    }
}
