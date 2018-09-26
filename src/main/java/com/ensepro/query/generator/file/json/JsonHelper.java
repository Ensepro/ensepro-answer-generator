package com.ensepro.query.generator.file.json;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonHelper {

    Map<String, String> map_resource_to_var;
    Map<String, String> map_var_to_resource;
    Map<String, Integer> map_distancias_edicao;
    Map<String, List<Object>> map_resource_to_tr;
    @Singular
    List<List<Object>> termos_relevantes;
    Map<String, List<String>> termos_relacionados;
    Map<String, String> sinonimos;
}
