package com.ensepro.answer.generator.data.normalized;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ensepro.answer.generator.data.Helper;
import com.ensepro.answer.generator.data.Metrica;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

/*
 * This class represent the json loaded from the json file.
 * This is an AS IS of the json file and the structure is not that good :)
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO renomar classe
public class NormalizedJsonHelper {

    Map<String, String> map_resource_to_var;
    Map<String, String> map_var_to_resource;
    Map<String, List<Object>> map_resource_to_tr;
    @Singular
//    List<List<Object>> termos_relevantes;
    Map<String, String> sinonimos;
    List<String> substantivos_proprios_frase;
    Map<String, Metrica> metricas;


    public static NormalizedJsonHelper fromHelper(Helper helper) {
        NormalizedJsonHelperBuilder normalizedJsonHelper = NormalizedJsonHelper.builder();

        final Map<String, List<Object>> map_resource_to_tr = new HashMap<>();
        final Map<String, Metrica> metricas = new HashMap<>();
        helper.getResource2keyword().forEach((key, value) ->
            map_resource_to_tr.put(key, asList(value.getKeyword(), value.getWeight(), value.getGrammarClass()))
        );

//        helper.getKeywords().forEach(termo ->
//            normalizedJsonHelper
//                .termos_relevante(asList(termo.getKeyword(), termo.getWeight(), termo.getGrammarClass()))
//        );

        helper.getMetrics().forEach((key, value) ->
            metricas.put(key, Metrica.builder()
                .peso(value.getWeight())
                .policy(value.getPolicy())
                .build())
        );

        normalizedJsonHelper.map_resource_to_var(helper.getResource2var());
        normalizedJsonHelper.map_var_to_resource(helper.getVar2resource());
        normalizedJsonHelper.map_resource_to_tr(map_resource_to_tr);
        normalizedJsonHelper.sinonimos(helper.getSynonyms());
        normalizedJsonHelper.metricas(metricas);
        normalizedJsonHelper.substantivos_proprios_frase(helper.getProperNouns());

        return normalizedJsonHelper.build();
    }

}
