package com.ensepro.answer.generator.data.result;

import com.ensepro.answer.generator.data.answer.Answer;
import com.ensepro.answer.generator.data.normalized.NormalizedJsonHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class JsonAnswers {

    private final NormalizedJsonHelper helper;
    private final List<Answer> answers;

}
