package com.ensepro.answer.generator.data.answer;

import java.io.Serializable;
import java.util.List;

import com.ensepro.answer.generator.data.RelevantKeyword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WeightClasses implements Serializable {

    private List<RelevantKeyword> keyword;

}
