package com.ensepro.answer.generator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ensepro.answer.generator.answer.AnswerGenerator;
import com.ensepro.answer.generator.answer.ScoreCalculation;
import com.ensepro.answer.generator.config.Configuration;
import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Triples;
import com.ensepro.answer.generator.data.answer.Answer;
import com.ensepro.answer.generator.data.normalized.NormalizedJson;
import com.ensepro.answer.generator.data.normalized.NormalizedJsonHelper;
import com.ensepro.answer.generator.data.result.JsonAnswers;
import com.ensepro.answer.generator.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration config = Configuration.fromArgs(args);
        log.info("####################################################################################");
        log.info("#### STARTING PROCESS: {}", config);
        log.info("####################################################################################");
        NormalizedJson json = JsonUtil.read(config.getLoadFile(), NormalizedJson.class);

        Helper helper = Helper.fromNormalizedHelper(json.getHelper());
        Triples triples = Triples.fromNormalizedValues(json.getValues());

        AnswerGenerator generator = AnswerGenerator.builder()
            .helper(helper)
            .triples(triples)
            .level(config.getLevel())
            .threads(config.getThreads())
            .scoreCalculator(ScoreCalculation.builder()
                .helper(helper)
                .build())
            .build();

        List<Answer> answers = generator.execute();

        Collections.sort(answers);

        answers = answers.stream()
            .filter(t -> t.getDetails().getMetrics().getScoreMetrics().get(2) == 1)
            .limit(config.getResultSize())
            .collect(Collectors.toList());

        JsonUtil.save(config.getSaveFile(),
            JsonAnswers.builder()
                .answers(answers)
                .helper(NormalizedJsonHelper.fromHelper(helper))
                .build()
        );

    }
}
