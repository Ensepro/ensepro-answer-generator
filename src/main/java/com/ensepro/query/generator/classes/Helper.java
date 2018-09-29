package com.ensepro.query.generator.classes;


import com.ensepro.query.generator.file.json.JsonHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Getter
@Builder
public class Helper {


    private final Map<String, String> map_resource_to_var;
    private final Map<String, String> map_var_to_resource;
    private final Map<String, Integer> map_distancias_edicao;
    private final Map<String, TermoRelevante> map_resource_to_tr;
    @Singular
    private final List<TermoRelevante> termos_relevantes;
    Map<String, List<String>> termos_relacionados;
    Map<String, String> sinonimos;
    List<String> substantivos_proprios_frase;
    Map<String, Float> pesos;

    public static Helper fromJsonHelper(JsonHelper jsonHelper) {
        final HelperBuilder helper = Helper.builder();

        final Map<String, TermoRelevante> map_resource_to_tr = new HashMap<>();

        jsonHelper.getMap_resource_to_tr().forEach((key, value) -> {
            map_resource_to_tr.put(key, TermoRelevante.builder()
                    .termo(value.get(0).toString())
                    .peso(Double.valueOf(value.get(1).toString()).intValue())
                    .build());
        });

        jsonHelper.getTermos_relevantes().forEach(termo -> {
            helper.termos_relevante(TermoRelevante.builder()
                    .termo(termo.get(0).toString())
                    .peso(Double.valueOf(termo.get(1).toString()).intValue())
                    .build());
        });

        helper.map_resource_to_var(jsonHelper.getMap_resource_to_var());
        helper.map_var_to_resource(jsonHelper.getMap_var_to_resource());
        helper.map_distancias_edicao(jsonHelper.getMap_distancias_edicao());
        helper.map_resource_to_tr(map_resource_to_tr);
        helper.termos_relacionados(jsonHelper.getTermos_relacionados());
        helper.sinonimos(jsonHelper.getSinonimos());
        helper.substantivos_proprios_frase(jsonHelper.getSubstantivos_proprios_frase());
        helper.pesos(jsonHelper.getPesos());

        return helper.build();
    }

    public JsonHelper toJsonHelper() {
        JsonHelper.JsonHelperBuilder helper = JsonHelper.builder();

        final Map<String, List<Object>> map_resource_to_tr = new HashMap<>();

        getMap_resource_to_tr().forEach((key, value) -> {
            map_resource_to_tr.put(key, asList(value.getTermo(), value.getPeso()));
        });

        getTermos_relevantes().forEach(termo -> {
            helper.termos_relevante(asList(termo.getTermo(), termo.getPeso()));
        });

        helper.map_resource_to_var(getMap_resource_to_var());
        helper.map_var_to_resource(getMap_var_to_resource());
        helper.map_distancias_edicao(getMap_distancias_edicao());
        helper.map_resource_to_tr(map_resource_to_tr);
        helper.termos_relacionados(getTermos_relacionados());
        helper.sinonimos(getSinonimos());
        helper.pesos(getPesos());
        helper.substantivos_proprios_frase(getSubstantivos_proprios_frase());

        return helper.build();
    }

}
