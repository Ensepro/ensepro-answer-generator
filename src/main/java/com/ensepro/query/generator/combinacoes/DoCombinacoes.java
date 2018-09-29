package com.ensepro.query.generator.combinacoes;

import com.ensepro.query.generator.classes.Query;
import com.ensepro.query.generator.classes.Tripla;
import com.ensepro.query.generator.classes.Triplas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DoCombinacoes {

    private static final int THREADS = 20;

    private final List<Callable<List<Query>>> callables;
    private final CalcularMetricas metricas;
    private final List<Tripla> triplas;
    private final ExecutorService executor;

    public DoCombinacoes(Triplas triplas) {
        this.metricas = CalcularMetricas.builder().helper(triplas.getHelper()).build();
        this.triplas = triplas.getTriplas();
        this.callables = Collections.synchronizedList(new ArrayList<>());
        this.executor = Executors.newFixedThreadPool(THREADS);
    }

    public List<Query> execute(boolean shouldDo3) throws InterruptedException {
        return doCombinacoes(triplas, shouldDo3);
    }

    private List<Query> doCombinacoes(List<Tripla> triplas, boolean shouldDo3) throws InterruptedException {
        final Set<Query> combinacoes = new HashSet<>();

        triplas.forEach(tripla1 -> {

            combinacoes.add(metricas.calcular(tripla1));

            callables.add(() -> {
                return doCombinacoes(triplas, tripla1);
            });
            if (shouldDo3) {
                triplas.forEach(tripla2 -> {
                    callables.add(() -> {
                        return doCombinacoes(triplas, tripla1, tripla2);
                    });
                });
            }
        });

        executor.invokeAll(callables)
                .stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return Collections.emptyList();
                }).collect(Collectors.toList())
                .forEach(listQuery -> {
                    combinacoes.addAll((Collection<? extends Query>) listQuery);
                });

        executor.shutdown();

        return new ArrayList<>(combinacoes);
    }

    private List<Query> doCombinacoes(List<Tripla> triplas, Tripla tripla1) {
        final List<Query> combinacoes = new ArrayList<>();
        triplas.forEach(tripla2 -> {
            if (shouldAdd(tripla1, tripla2)) {
                combinacoes.add(metricas.calcular(tripla1, tripla2));
            }
        });

        return combinacoes;
    }


    private List<Query> doCombinacoes(List<Tripla> triplas, Tripla tripla1, Tripla tripla2) {
        final List<Query> combinacoes = new ArrayList<>();
        triplas.forEach(tripla3 -> {
            if (shouldAdd(tripla1, tripla2, tripla3)) {
                combinacoes.add(metricas.calcular(tripla1, tripla2, tripla3));
            }
        });

        return combinacoes;
    }

    private boolean shouldAdd(Tripla tripla1, Tripla tripla2) {
        boolean sujeito_igual, predicado_igual, objeto_igual;
        boolean sujeito_diferente, predicado_diferente, objeto_diferente;
        boolean sujeito_igual_objeto;

        sujeito_igual = tripla1.getSujeito().equals(tripla2.getSujeito());
        predicado_igual = tripla1.getPredicado().equals(tripla2.getPredicado());
        objeto_igual = tripla1.getObjeto().equals(tripla2.getObjeto());

        sujeito_diferente = !sujeito_igual;
        predicado_diferente = !predicado_igual;
        objeto_diferente = !objeto_igual;

        sujeito_igual_objeto = tripla1.getSujeito().equals(tripla2.getObjeto());


        if (sujeito_igual && predicado_diferente && objeto_diferente) {
            return true;
        }

        if (sujeito_diferente && predicado_diferente && objeto_diferente && sujeito_igual_objeto) {
            return true;
        }

        if (sujeito_igual && predicado_igual && objeto_diferente) {
            return true;
        }

        if (sujeito_diferente && predicado_igual && objeto_igual) {
            return true;
        }

        return false;
    }


    private boolean shouldAdd(Tripla tripla1, Tripla tripla2, Tripla tripla3) {
        boolean sujeito1_igual_sujeito2, sujeito2_igual_sujeito3;
        boolean predicado1_igual_predicado2, predicado2_igual_predicado3, predicado1_igual_predicado3;
        boolean objeto1_igual_objeto2, objeto2_igual_objeto3, objeto1_igual_objeto3;
        boolean objeto1_igual_sujeito2, objeto2_igual_sujeito3;
        boolean sujeitos_iguais, predicados_iguais, objetos_iguais;
        boolean predicados_todos_diferentes, objetos_todos_diferentes;

        sujeito1_igual_sujeito2 = tripla1.getSujeito().equals(tripla2.getSujeito());
        sujeito2_igual_sujeito3 = tripla2.getSujeito().equals(tripla3.getSujeito());
//            boolean sujeito1_igual_sujeito3 = tripla1.get(0).equals(tripla3.get(0));

        predicado1_igual_predicado2 = tripla1.getPredicado().equals(tripla2.getPredicado());
        predicado2_igual_predicado3 = tripla2.getPredicado().equals(tripla3.getPredicado());
        predicado1_igual_predicado3 = tripla1.getPredicado().equals(tripla3.getPredicado());

        objeto1_igual_objeto2 = tripla1.getObjeto().equals(tripla2.getObjeto());
        objeto2_igual_objeto3 = tripla2.getObjeto().equals(tripla3.getObjeto());
        objeto1_igual_objeto3 = tripla1.getObjeto().equals(tripla3.getObjeto());

        objeto1_igual_sujeito2 = tripla1.getObjeto().equals(tripla2.getSujeito());
        objeto2_igual_sujeito3 = tripla2.getObjeto().equals(tripla3.getSujeito());

        sujeitos_iguais = sujeito1_igual_sujeito2 && sujeito2_igual_sujeito3;
        predicados_iguais = predicado1_igual_predicado2 && predicado2_igual_predicado3;
        objetos_iguais = objeto1_igual_objeto2 && objeto2_igual_objeto3;

//            boolean sujeitos_todos_diferentes = !sujeito1_igual_sujeito2 && !sujeito2_igual_sujeito3 && !sujeito1_igual_sujeito3;
        predicados_todos_diferentes = !predicado1_igual_predicado2 && !predicado2_igual_predicado3 && !predicado1_igual_predicado3;
        objetos_todos_diferentes = !objeto1_igual_objeto2 && !objeto2_igual_objeto3 && !objeto1_igual_objeto3;

        if (sujeitos_iguais && predicados_iguais && objetos_iguais) {
            return true;
        }

        if (sujeito1_igual_sujeito2 && objeto2_igual_objeto3 && predicados_todos_diferentes && !objeto1_igual_objeto2) {
            return true;
        }

        if (sujeito1_igual_sujeito2 && objeto2_igual_sujeito3 && predicados_todos_diferentes && objetos_todos_diferentes &&
                !sujeito2_igual_sujeito3) {
            return true;
        }

        if (objeto1_igual_sujeito2 &&
                sujeito2_igual_sujeito3 &&
                predicado2_igual_predicado3 &&
                objetos_todos_diferentes &&
                !predicado1_igual_predicado2 &&
                !sujeitos_iguais) {
            return true;
        }

        return false;
    }
}
