package com.ensepro.query.generator.combinacoes;

import com.ensepro.query.generator.classes.ClasseGramatical;
import com.ensepro.query.generator.classes.Helper;
import com.ensepro.query.generator.classes.Metrica;
import com.ensepro.query.generator.classes.Query;
import com.ensepro.query.generator.classes.TermoRelevante;
import com.ensepro.query.generator.classes.Tripla;
import lombok.Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ensepro.query.generator.classes.ClasseGramatical.PROP;
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
        final Map<TermoRelevante, Double> mapM1 = new HashMap<>();


        Metrica peso_m1 = helper.getMetricas().getOrDefault("m1", null);
        Metrica peso_m2 = helper.getMetricas().getOrDefault("m2", null);
        Metrica peso_m3 = helper.getMetricas().getOrDefault("m3", null);


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
                    TermoRelevante _tr = TermoRelevante.builder().termo(tr_name).peso(tr.getPeso()).classeGramatical(tr.getClasseGramatical()).build();

                    trs.add(_tr);

                    calcularM1(mapM1, peso_m1, resource, tr, _tr);


                }
            });
        });


        int tr_count = trs.stream().mapToInt(TermoRelevante::getPeso).sum();
        int edit_count = soma_distancias.stream().mapToInt(Integer::intValue).sum();
        int var_count = vars.stream().mapToInt(Integer::intValue).sum();
        double m1 = mapM1.values().stream().mapToDouble(Double::doubleValue).sum();
        double m2 = (double) trs.size() / (double) (triplas.length * 3);
        double m3 = (double) trs.stream().filter(tr -> PROP.equals(tr.getClasseGramatical())).count() / (double) helper.getSubstantivos_proprios_frase().size();

        if(m3> 0){
            System.out.println();
        }
        m2 = m2 * peso_m2.getPeso();
        m3 = m3 * peso_m3.getPeso();

        return Query.builder()
                .triplas(asList(triplas))
                .editDistance(edit_count)
                .trCount(tr_count)
                .varCount(var_count)
                .m1((float) m1)
                .m2((float) m2)
                .m3((float) m3)
                .score((float) (m1 + m2 + m3))
                .build();
    }

    private void calcularM1(Map<TermoRelevante, Double> mapM1, Metrica peso_m1, String resource, TermoRelevante tr, TermoRelevante _tr) {
        double currentM1 = _calcularM1(resource, tr, peso_m1.getPeso());
        Double existentM1 = mapM1.getOrDefault(_tr, null);
        if (isNull(existentM1)) {
            mapM1.put(_tr, currentM1);
        } else {
            switch (peso_m1.getPolicy()) {
                case BEST_MATCH:
                    if (currentM1 < existentM1) {
                        mapM1.put(_tr, currentM1);
                    }
                    break;
                case WORST_MATCH:
                    if (currentM1 > existentM1) {
                        mapM1.put(_tr, currentM1);
                    }
                    break;
                case AVG:
                    double avg = (currentM1 + existentM1) / 2;
                    mapM1.put(_tr, avg);
                    break;
            }
        }
    }

    /**
     * @param resource Termo que deu o match
     * @param tr       TermoRelevante considerado para o metch (termo original e não o sinonimo)
     * @param peso_m1  (peso da métrica 1)
     * @return
     */
    private double _calcularM1(String resource, TermoRelevante tr, double peso_m1) {
        double lenTR = tr.getTermo().length();
        double lenMatch = resource.length();

        double peso_classe_tr = tr.getPeso().doubleValue();

        double dividendo = peso_classe_tr * lenTR;
        double divisor = (lenTR - 1) + Math.pow(FIXED_FACTOR, (lenMatch - lenTR));

        return (dividendo / divisor) * peso_m1;
    }


}
