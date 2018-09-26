package com.ensepro.query.generator.main;

import com.ensepro.query.generator.classes.Query;
import com.ensepro.query.generator.classes.Triplas;
import com.ensepro.query.generator.combinacoes.DoCombinacoes;
import com.ensepro.query.generator.file.json.ranqueado.JsonFileRanqueado;
import com.ensepro.query.generator.utils.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ensepro.query.generator.classes.Triplas.fromJsonFile;
import static com.ensepro.query.generator.file.json.normalizado.JsonFileNormalizado.loadFile;
import static java.util.stream.Collectors.toList;

public class Main {

    private static String path = "C:/Users/alenc/Documents/_gitProjects/ENSEPRO/ensepro/main/";
    private static String jsonFile = "resultado_normalizado.json";
    private static String jsonSaveFile = "gerar_queries_step.json";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        jsonFile = args.length == 0 ? path + jsonFile : args[0];
        boolean shouldDo3 = false;
        int size = Integer.valueOf(args[1]);
        if(args.length > 2) {
            shouldDo3 = args[1].equalsIgnoreCase("do3");
        }

        Triplas triplas = fromJsonFile(loadFile(jsonFile));

        DoCombinacoes combinacoes = new DoCombinacoes(triplas);

        List<Query> queries = combinacoes.execute(shouldDo3);   

        queries.sort(Query::compareTo);

        List<Query> listaFinal = queries.stream().limit(size).collect(toList());

        JsonUtil.save("queries_renqueadas.json", JsonFileRanqueado.from(triplas.getHelper(), listaFinal));
    }

    private static void teste() {




    }
}
