package com.ensepro.answer.generator.answer;


import static com.ensepro.answer.generator.domain.GrammarClass.PROP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Keyword;
import com.ensepro.answer.generator.data.Metric;
import com.ensepro.answer.generator.data.ScoreDetail;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.TripleDetail;
import com.ensepro.answer.generator.domain.GrammarClass;
import com.ensepro.answer.generator.domain.Position;
import com.ensepro.answer.generator.validator.PositionValidator;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Score {

    private static final float FIXED_FACTOR = 1.1f;

    private final List<Triple> triples;
    private final Helper helper;
    private final Configuration config;
    private final PositionValidator positionValidator;

    @Builder
    public Score(final List<Triple> triples, final Helper helper, final Configuration config) {
        this.triples = triples;
        this.helper = helper;
        this.config = config;
        this.positionValidator = new PositionValidator();
    }

    public void calculate() {
        this.triples.forEach(this::calculate);
    }

    private void calculate(final Triple triple) {
        final Set<Keyword> keywords = new HashSet<>();
        final Map<Keyword, Float> m1Values = new HashMap<>();

        final Metric metricM1 = helper.getMetrics().get(Metric.M1_KEY);
        final Metric metricM2 = helper.getMetrics().get(Metric.M2_KEY);
        final Metric metricM3 = helper.getMetrics().get(Metric.M3_KEY);

        calculateFor(triple.getSubject(), Position.SUBJECT, keywords, m1Values);
        calculateFor(triple.getPredicate(), Position.PREDICATE, keywords, m1Values);
        calculateFor(triple.getObject(), Position.OBJECT, keywords, m1Values);

        calculateForAdj(triple, keywords, m1Values);

        final Float properNouns = (float) keywords.stream().filter(tr -> PROP.equals(tr.getGrammarClass())).count();

        final Float m1 = (float) m1Values.values().stream().mapToDouble(Float::doubleValue).sum();
        final Float m2 = (float) keywords.size() / 3F; //3F because at this moment we have 1 triple
        final Float m3 = helper.getProperNouns().size() == 0 ? 0 : properNouns / helper.getProperNouns().size();

        final Float score = (m1 * metricM1.getWeight()) + (m2 * metricM2.getWeight()) + (m3 * metricM3.getWeight());

        triple.setScore(score);

        ScoreDetail scoreDetail = ScoreDetail.builder()
            .m1(m1)
            .m2(m2)
            .m3(m3)
            .build();

        TripleDetail detail = TripleDetail.builder()
            .keywords(new ArrayList<>(keywords))
            .scoreDetail(scoreDetail)
            .build();

        triple.setDetail(detail);

    }

    private void calculateForAdj(final Triple triple,
        final Set<Keyword> keywords, final Map<Keyword, Float> m1Values) {
        helper.getRelevantKeywords().stream()
            .filter(keyword -> GrammarClass.ADJ.equals(keyword.getGrammarClass()))
            .forEach(adjKeyword -> {

                final String subject = helper.getVar2resource().get(triple.getSubject().toString());
                final String predicate = helper.getVar2resource().get(triple.getPredicate().toString());
                final String object = helper.getVar2resource().get(triple.getObject().toString());

                if (subject.contains(adjKeyword.getKeyword())) {
                    calculateAdjScore(m1Values, adjKeyword);
                    keywords.add(adjKeyword);
                }
                if (predicate.contains(adjKeyword.getKeyword())) {
                    calculateAdjScore(m1Values, adjKeyword);
                    keywords.add(adjKeyword);
                }
                if (object.contains(adjKeyword.getKeyword())) {
                    calculateAdjScore(m1Values, adjKeyword);
                    keywords.add(adjKeyword);
                }
            });
    }

    private void calculateAdjScore(final Map<Keyword, Float> m1Values, final Keyword adjKeyword) {
        final float currentValue = m1Values.getOrDefault(adjKeyword, 0F);
        m1Values.put(adjKeyword, currentValue + adjKeyword.getWeight());
    }

    private void calculateFor(
        final Integer resourceId,
        final Position position,
        final Set<Keyword> keywords,
        final Map<Keyword, Float> m1Values) {

        final String resource = helper.getVar2resource().get(resourceId.toString());
        final Keyword keyword = helper.getResource2keyword().get(resource);

        if (!positionValidator.validate(position, keyword.getGrammarClass())) {
            return;
        }

        final String originalTR = helper.getSynonyms().getOrDefault(keyword.getKeyword(), keyword.getKeyword());
        final Keyword m1Key = keyword.clone();
        m1Key.setKeyword(originalTR);

        final float m1Score = calculateM1score(resource, keyword, m1Values.get(m1Key));

        m1Values.put(m1Key, m1Score);
        keywords.add(m1Key);
    }

    private float calculateM1score(
        final String resource,
        final Keyword keyword,
        final Float currentScore) {

        final float newScore = m1Score(resource, keyword);

        if (currentScore == null) {
            return newScore;
        }

        switch (helper.getMetrics().get(Metric.M1_KEY).getPolicy()) {
            case BEST_MATCH:
                if (currentScore > newScore) {
                    return currentScore;
                }
                return newScore;
            case WORST_MATCH:
                if (currentScore < newScore) {
                    return currentScore;
                }
                return newScore;
            case AVG:
                return (currentScore + newScore) / 2;
            case SUM:
                return currentScore + newScore;
        }
        return 0;
    }


    private float m1Score(final String resource, final Keyword keyword) {
        float keywordLength = keyword.getKeyword().length();
        float resourceLength = resource.length();

        float dividend = keyword.getWeight() * keywordLength;
        double divider = (keywordLength - 1F) + Math.pow(FIXED_FACTOR, (resourceLength - keywordLength));

        return dividend / (float) divider;
    }
}
