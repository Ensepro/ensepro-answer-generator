package com.ensepro.answer.generator.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ensepro.answer.generator.data.normalized.NormalizedJsonHelper;
import com.ensepro.answer.generator.domain.GrammarClass;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder
public class Helper {

    private final Map<String, Integer> resource2var;
    private final Map<String, String> var2resource;
    private final Map<String, RelevantKeyword> resource2keyword;

    @Singular
//    private final List<RelevantKeyword> keywords;
    private final Map<String, String> synonyms;
    private final List<String> properNouns;
    private final Map<String, Metric> metrics;


    public static Helper fromNormalizedHelper(NormalizedJsonHelper normalizedJsonHelper) {
        HelperBuilder helper = Helper.builder();

        final Map<String, RelevantKeyword> resource2keyword = new HashMap<>();
        final Map<String, Metric> metrics = new HashMap<>();

        normalizedJsonHelper.getMap_resource_to_tr().forEach((key, value) -> {
            resource2keyword.put(key, RelevantKeyword.builder()
                .keyword(value.get(0).toString())
                .weight(Float.valueOf(value.get(1).toString()))
                .grammarClass(GrammarClass.valueOf(value.get(2).toString()))
                .build());
        });

//        normalizedJsonHelper.getTermos_relevantes().forEach(termo -> {
//            helper.keyword(RelevantKeyword.builder()
//                .keyword(termo.get(0).toString())
//                .weight(Float.valueOf(termo.get(1).toString()))
//                .grammarClass(GrammarClass.valueOf(termo.get(2).toString()))
//                .build());
//        });

        normalizedJsonHelper.getMetricas().forEach((key, value) ->
            metrics.put(key, Metric.builder()
                .weight(value.getPeso())
                .policy(value.getPolicy())
                .build())
        );

        helper.resource2var(normalizedJsonHelper.getMap_resource_to_var());
        helper.var2resource(normalizedJsonHelper.getMap_var_to_resource());
        helper.resource2keyword(resource2keyword);
        helper.synonyms(normalizedJsonHelper.getSinonimos());
        helper.properNouns(normalizedJsonHelper.getSubstantivos_proprios_frase());
        helper.metrics(metrics);

        return helper.build();
    }


}
