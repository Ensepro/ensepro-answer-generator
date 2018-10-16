package com.ensepro.answer.generator.data.result;

import java.util.List;

import com.ensepro.answer.generator.data.answer.Answer;
import com.ensepro.answer.generator.data.normalized.NormalizedJsonHelper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JsonAnswers {

    private final NormalizedJsonHelper helper;
    private final List<Answer> answers;

}
