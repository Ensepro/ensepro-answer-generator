package com.ensepro.query.generator.combinacoes;

import com.ensepro.query.generator.classes.Helper;
import com.ensepro.query.generator.classes.Query;
import com.ensepro.query.generator.classes.TermoRelevante;
import com.ensepro.query.generator.classes.Tripla;
import lombok.Builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

@Builder
public class CalcularMetricas {

    private final Helper helper;

    public Query calcular(Tripla... triplas) {
        final List<Integer> vars = new ArrayList<>();
        final List<Integer> soma_distancias = new ArrayList<>();
        final Set<TermoRelevante> trs = new HashSet<>();

        asList(triplas).forEach(tripla -> {
            final List<String> temp = asList(tripla.getSujeito(), tripla.getPredicado(), tripla.getObjeto());

            temp.forEach(resource_var_name -> {

                String resource = helper.getMap_var_to_resource().getOrDefault(resource_var_name, null);
                TermoRelevante tr = helper.getMap_resource_to_tr().getOrDefault(resource, null);

                if (isNull(tr)) {

                    vars.add(1);

                } else {

                    soma_distancias.add(helper.getMap_distancias_edicao().getOrDefault(tr.getTermo() + "-" + resource_var_name, 0));

                    String tr_name = helper.getSinonimos().get(tr.getTermo());

                    TermoRelevante _tr = TermoRelevante.builder().termo(tr_name).peso(tr.getPeso()).build();

                    trs.add(_tr);

                }
            });
        });

        int tr_count = trs.stream().mapToInt(TermoRelevante::getPeso).sum();
        int edit_count = soma_distancias.stream().mapToInt(Integer::intValue).sum();
        int var_count = vars.stream().mapToInt(Integer::intValue).sum();

        return Query.builder()
                .triplas(asList(triplas))
                .editDistance(edit_count)
                .trCount(tr_count)
                .varCount(var_count)
                .build();
    }


}
