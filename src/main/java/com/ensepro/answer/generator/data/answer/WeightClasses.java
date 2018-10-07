package com.ensepro.answer.generator.data.answer;

import com.ensepro.answer.generator.data.RelevantKeyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WeightClasses implements Serializable {

    private List<RelevantKeyword> keyword;

}
