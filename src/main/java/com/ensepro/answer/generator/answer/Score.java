package com.ensepro.answer.generator.answer;


import com.ensepro.answer.generator.configuration.Configuration;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Keyword;
import com.ensepro.answer.generator.data.Match;
import com.ensepro.answer.generator.data.Metric;
import com.ensepro.answer.generator.data.ScoreDetail;
import com.ensepro.answer.generator.data.Triple;
import com.ensepro.answer.generator.data.TripleDetail;
import com.ensepro.answer.generator.domain.GrammarClass;
import com.ensepro.answer.generator.domain.Position;
import com.ensepro.answer.generator.validator.PositionValidator;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        this.triples.parallelStream().forEach(this::calculate);
    }

    private void calculate(final Triple triple) {
        final Set<Keyword> keywords = new HashSet<>();
        final Map<Keyword, Match> matches = new HashMap<>();

        final Float weightM1 = helper.getMetrics().get(Metric.M1_KEY).getWeight();
        final Float weightM2 = helper.getMetrics().get(Metric.M2_KEY).getWeight();
        final Float weightM3 = helper.getMetrics().get(Metric.M3_KEY).getWeight();

        calculateFor(triple.getSubject(), Position.SUBJECT, keywords, matches);
        calculateFor(triple.getPredicate(), Position.PREDICATE, keywords, matches);
        calculateFor(triple.getObject(), Position.OBJECT, keywords, matches);

        calculateForAdj(triple, keywords, matches);

        final Float properNouns = (float) keywords.stream()
            .map(Keyword::getGrammarClass)
            .filter(GrammarClass::isProp)
            .count();

        final Float m1 =
            (float) matches.values().stream().map(Match::getScore).mapToDouble(Float::doubleValue).sum() * weightM1;
        final Float m2 = (keywords.size() / 3F) * weightM2;
        final Float m3 = helper.getProperNouns().size() == 0
            ? 0
            : (properNouns / helper.getProperNouns().size()) * weightM3;

        final Float score = m1 + m2 + m3;

        triple.setScore(score);

        final ScoreDetail scoreDetail = ScoreDetail.builder()
            .m1(m1)
            .m2(m2)
            .m3(m3)
            .matches(matches)
            .build();

        final TripleDetail detail = TripleDetail.builder()
            .listKeywords(new ArrayList<>(keywords))
            .keywords(keywords.stream().map(Keyword::getKeyword).collect(Collectors.toList()))
            .scoreDetail(scoreDetail)
            .properNounsCount(helper.getProperNouns().size())
            .properNounsMatchedCount(properNouns.intValue())
            .build();

        triple.setDetail(detail);

    }

    private void calculateForAdj(final Triple triple, final Set<Keyword> keywords, final Map<Keyword, Match> matches) {
        helper.getRelevantKeywords().stream()
            .filter(keyword -> keyword.getGrammarClass().isAdj())
            .forEach(adjKeyword -> {

                final String subject = helper.getVar2resource().get(triple.getSubject().toString());
                final String predicate = helper.getVar2resource().get(triple.getPredicate().toString());
                final String object = helper.getVar2resource().get(triple.getObject().toString());

                if (subject.contains(adjKeyword.getKeyword())) {
                    calculateAdjScore(matches, adjKeyword, subject);
                    keywords.add(adjKeyword);
                }
                if (predicate.contains(adjKeyword.getKeyword())) {
                    calculateAdjScore(matches, adjKeyword, predicate);
                    keywords.add(adjKeyword);
                }
                if (object.contains(adjKeyword.getKeyword())) {
                    calculateAdjScore(matches, adjKeyword, object);
                    keywords.add(adjKeyword);
                }
            });
    }

    private void calculateAdjScore(final Map<Keyword, Match> matches, final Keyword adjKeyword, final String resource) {
        final Match currentValue = matches.get(adjKeyword);
        if (currentValue == null) {
            matches.put(adjKeyword, Match.builder()
                .keyword(adjKeyword)
                .resource(resource)
                .score(adjKeyword.getWeight())
                .build());
            return;
        }

        if (currentValue.getResource().equals(resource)) {
            return;
        }

        matches.put(adjKeyword, Match.builder()
            .keyword(adjKeyword)
            .resource(currentValue.getResource() + "|adj-sum|" + resource)
            .score(currentValue.getScore() + adjKeyword.getWeight())
            .build());
    }

    private void calculateFor(
        final Integer resourceId,
        final Position position,
        final Set<Keyword> keywords,
        final Map<Keyword, Match> matches) {

        final String resource = helper.getVar2resource().get(resourceId.toString());
        final Keyword keyword = helper.getResource2keyword().get(resource);

        if (keyword == null || !positionValidator.validate(position, keyword.getGrammarClass())) {
            return;
        }

        final String originalTR = helper.getSynonyms().getOrDefault(keyword.getKeyword(), keyword.getKeyword());
        final Keyword m1Key = keyword.clone();
        m1Key.setKeyword(originalTR);

        final Match match = calculateM1score(resource, keyword, matches.get(m1Key));

        keywords.add(m1Key);
        matches.put(m1Key, match);
    }

    private Match calculateM1score(
        final String resource,
        final Keyword keyword,
        final Match currentScore) {

        final float score = m1Score(resource, keyword);
        final Match newMatch = Match.builder().keyword(keyword).resource(resource).score(score).build();

        if (currentScore == null) {
            return newMatch;
        }

        switch (helper.getMetrics().get(Metric.M1_KEY).getPolicy()) {
            case BEST_MATCH:
                if (currentScore.getScore() > score) {
                    return currentScore;
                }
                return newMatch;
            case WORST_MATCH:
                if (currentScore.getScore() < score) {
                    return currentScore;
                }
                return newMatch;
            case AVG:
                return Match.builder()
                    .keyword(keyword)
                    .resource(currentScore.getResource() + "|avg|" + newMatch.getResource())
                    .score((currentScore.getScore() + score) / 2)
                    .build();
            case SUM:
                return Match.builder()
                    .keyword(keyword)
                    .resource(currentScore.getResource() + "|sum|" + newMatch.getResource())
                    .score(currentScore.getScore() + score)
                    .build();
            default:
                //if no metrics exists, use the first one
                return currentScore;
        }
    }

    private float m1Score(final String resource, final Keyword keyword) {
        float keywordLength = keyword.getKeyword().length();
        float resourceLength = resource.length();

        float dividend = keyword.getWeight() * keywordLength;
        double divider = (keywordLength - 1F) + Math.pow(FIXED_FACTOR, (resourceLength - keywordLength));

        return dividend / (float) divider;
    }
}
