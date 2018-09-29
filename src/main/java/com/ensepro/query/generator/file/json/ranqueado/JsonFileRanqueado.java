package com.ensepro.query.generator.file.json.ranqueado;

import com.ensepro.query.generator.classes.Helper;
import com.ensepro.query.generator.classes.Query;
import com.ensepro.query.generator.file.json.JsonHelper;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Builder
@Getter
public class JsonFileRanqueado {

    private final JsonHelper helper;
    @Singular
    private final List<Object> values;


    public static JsonFileRanqueado from(Helper helper, List<Query> queries) {
        JsonFileRanqueado.JsonFileRanqueadoBuilder jsonFile = JsonFileRanqueado.builder();
        jsonFile.helper(helper.toJsonHelper());

        queries.forEach(query -> {
            List<Object> value = new ArrayList<>();
            query.getTriplas().forEach(tripla -> {
                value.add(asList(tripla.getSujeito(), tripla.getPredicado(), tripla.getObjeto()));
            });
            value.add(query.getVarCount());
            value.add(query.getTrCount());
            value.add(query.getEditDistance());
            value.add(query.getM1());
            value.add(query.getM2());
            value.add(query.getM3());
            value.add(query.getScore());

            jsonFile.value(value);
        });

        return jsonFile.build();

    }

}
