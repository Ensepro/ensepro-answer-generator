package com.ensepro.answer.generator.mapper;

import static java.util.Collections.singletonList;

import java.util.List;

import com.ensepro.answer.generator.data.Answer;
import com.ensepro.answer.generator.data.Helper;
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
        final Answer.AnswerBuilder answerBuilder = Answer.builder();
        final TripleDetail.TripleDetailBuilder tripleDetailBuilder = TripleDetail.builder();
        final ScoreDetail.ScoreDetailBuilder scoreDetailBuilder = ScoreDetail.builder();

        triples.stream().map(Triple::getTriple).forEach(answerBuilder::triple);

        triples.stream()
            .map(Triple::getDetail)
            .map(TripleDetail::getKeywords)
            .flatMap(List::stream)
            .sorted()    // makes sure the highest weight are first
            .distinct()  // so here the distinct will always keep the first element
            .forEach(tripleDetailBuilder::keyword);

        return answerBuilder.build();
    }

}
