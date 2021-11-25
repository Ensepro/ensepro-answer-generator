package com.ensepro.answer.generator.data;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class JavaResult {

//    private final Helper helper;
    private final List<Answer> answers;
    @Singular
    private final List<Integer> l_sizes;
    @Singular
    private final List<Integer> answer_sizes;
    private final long nanoSeconds;
    private final Map<String, Object> stats;

}
