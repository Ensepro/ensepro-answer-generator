package com.ensepro.answer.generator.data.normalized;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/*
 * This class represent the json loaded from the json file.
 * This is an AS IS of the json file and the structure is not that good :)
 */

@Getter
@Builder
@AllArgsConstructor
// TODO renomar classe
public class NormalizedJson {

    private final NormalizedJsonHelper helper;
    private final List<List<Integer>> values;

}
