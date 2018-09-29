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
    private static final double FIXED_FACTOR = 1.1;



    public Query calcular(Tripla... triplas) {
        final List<Integer> vars = new ArrayList<>();
        final List<Integer> soma_distancias = new ArrayList<>();
        final Set<TermoRelevante> trs = new HashSet<>();
        final List<Double> soma_m1 = new ArrayList<>();

        float peso_m1 = helper.getPesos().getOrDefault("peso_m1", 1F);
        float peso_m2 = helper.getPesos().getOrDefault("peso_m2", 1F);
        float peso_m3 = helper.getPesos().getOrDefault("peso_m3", 1F);

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

                    soma_m1.add(calcularM1(resource, tr, peso_m1));

                }
            });
        });



        int tr_count = trs.stream().mapToInt(TermoRelevante::getPeso).sum();
        int edit_count = soma_distancias.stream().mapToInt(Integer::intValue).sum();
        int var_count = vars.stream().mapToInt(Integer::intValue).sum();
        float m1 = (float) soma_m1.stream().mapToDouble(Double::doubleValue).sum();
        float m2 = (float) calcularM2(vars, trs);
        float m3 = 0;

        m1 = m1 * peso_m1;
        m2 = m2 * peso_m2;
        m3 = m3 * peso_m3;

        return Query.builder()
                .triplas(asList(triplas))
                .editDistance(edit_count)
                .trCount(tr_count)
                .varCount(var_count)
                .m1(m1)
                .m2(m2)
                .m3(m3)
                .score(m1 + m2 + m3)
                .build();
    }

    private double calcularM2(List<Integer> vars, Set<TermoRelevante> trs) {
        double qtde_match_tripla = (double) trs.size();
        double qtde_elem_tripla = qtde_match_tripla + vars.size();
        return qtde_match_tripla / qtde_elem_tripla;
    }

    private double calcularM1(String resource, TermoRelevante tr, float peso_m1) {
        double lenTR = tr.getTermo().length();
        double lenMatch = resource.length();
        double peso_classe_tr = tr.getPeso().doubleValue();
        double dividendo = peso_classe_tr * lenTR;
        double divisor = Math.pow((lenTR - 1) + FIXED_FACTOR, (lenMatch - lenTR));
        return (dividendo / divisor) * peso_m1;
    }


}
