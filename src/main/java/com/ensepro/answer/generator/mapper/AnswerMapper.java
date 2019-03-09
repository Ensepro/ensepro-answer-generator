package com.ensepro.answer.generator.mapper;

import static com.ensepro.answer.generator.domain.GrammarClass.ADJ;
import static com.ensepro.answer.generator.domain.GrammarClass.PROP;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Keyword;
import com.ensepro.answer.generator.data.Match;
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
        final Float weightM1 = helper.getMetrics().get(Metric.M1_KEY).getWeight();
        final Float weightM2 = helper.getMetrics().get(Metric.M2_KEY).getWeight();
        final Float weightM3 = helper.getMetrics().get(Metric.M3_KEY).getWeight();

        final Map<Keyword, Match> finalMatches = new HashMap<>();

        final List<Keyword> keywords = triples.stream()
            .map(Triple::getDetail)
            .map(TripleDetail::getListKeywords)
            .flatMap(List::stream)
            .sorted()    // makes sure the highest weight are first
            .distinct()// so here the distinct will always keep the first element
            .collect(Collectors.toList());

        triples.stream()
            .map(Triple::getDetail)
            .map(TripleDetail::getScoreDetail)
            .map(ScoreDetail::getMatches)
            .forEach(matches -> mergeMatches(finalMatches, matches));

        final Float properNouns = (float) keywords.stream().filter(tr -> PROP.equals(tr.getGrammarClass())).count();
        final Float m1 =
            (float) finalMatches.values().stream().map(Match::getScore).mapToDouble(Float::doubleValue).sum()
                * weightM1;
        final Float m2 = (keywords.size() / (3F * triples.size())) * weightM2;
        final Float m3 = helper.getProperNouns().size() == 0
            ? 0
            : (properNouns / helper.getProperNouns().size()) * weightM3;

        final Float score = m1 + m2 + m3;
        final ScoreDetail scoreDetail = ScoreDetail.builder()
            .m1(m1)
            .m2(m2)
            .m3(m3)
            .matches(finalMatches)
            .build();

        final TripleDetail tripleDetail = TripleDetail.builder()
            .listKeywords(new ArrayList<>(keywords))
            .keywords(keywords.stream().map(Keyword::getKeyword).collect(Collectors.toList()))
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

    private void mergeMatches(final Map<Keyword, Match> finalMatches, final Map<Keyword, Match> matches) {
        matches.forEach((keyword, match) -> {
            final Match currentScore = finalMatches.get(keyword);
            if (currentScore == null) {
                finalMatches.put(keyword, match);
                return;
            }
            final Match newMatch = getScoreBasedInPolicy(currentScore, match);
            finalMatches.put(keyword, newMatch);
        });
    }

    private Match getScoreBasedInPolicy(final Match currentScore, final Match match) {
        if (currentScore.getKeyword().getGrammarClass().isAdj()) {
            return currentScore;
        }

        //TODO maybe move this logic to a Map<Policy, Function> its going to be better
        switch (helper.getMetrics().get(Metric.M1_KEY).getPolicy()) {
            case BEST_MATCH:
                if (currentScore.getScore() > match.getScore()) {
                    return currentScore;
                }
                return match;
            case WORST_MATCH:
                if (currentScore.getScore() < match.getScore()) {
                    return currentScore;
                }
                return match;
            case AVG:
                return Match.builder()
                    .keyword(currentScore.getKeyword())
                    .resource(currentScore.getResource() + "|avg|" + match.getResource())
                    .score((currentScore.getScore() + match.getScore()) / 2)
                    .build();
            case SUM:
                return Match.builder()
                    .keyword(currentScore.getKeyword())
                    .resource(currentScore.getResource() + "|sum|" + match.getResource())
                    .score(currentScore.getScore() + match.getScore())
                    .build();
            default:
                //if no metrics exists, use the first one
                return currentScore;

        }
    }
}
