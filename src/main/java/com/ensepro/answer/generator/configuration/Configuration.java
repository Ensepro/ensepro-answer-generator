package com.ensepro.answer.generator.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Configuration {

    private static final String DEFAULT_LOAD_FILE = "C:\\_ensepro\\ensepro-core\\main\\resultado_normalizado.json";
//    private static final String DEFAULT_SAVE_FILE = "C:\\_ensepro\\ensepro-core\\main\\queries_renqueadas.json";
    private static final String DEFAULT_SAVE_FILE = "queries_renqueadas.json";
    private static final Integer DEFAULT_LEVEL = 2;
    private static final Integer DEFAULT_RESULT_SIZE = 10;
    private static final Integer DEFAULT_SLM1_FACTOR = 500;
    private static final boolean  DEFAULT_SLM1_ONLY_L1 = true;



    private final String loadFile;
    private final String saveFile;
    private final Integer threads;
    private final Integer level; //2 - combination of 2 triples. 3 - combination of 3 triples.
    private final Integer resultSize;
    private final Integer slm1Factor;
    private final Boolean slm1OnlyL1;

    private static ConfigurationBuilder getDefaultBuilder() {
        return Configuration.builder()
            .threads(8)
            .loadFile(DEFAULT_LOAD_FILE)
            .saveFile(DEFAULT_SAVE_FILE)
            .level(DEFAULT_LEVEL)
            .resultSize(DEFAULT_RESULT_SIZE)
            .slm1Factor(DEFAULT_SLM1_FACTOR)
            .slm1OnlyL1(DEFAULT_SLM1_ONLY_L1);

    }

    public static Configuration getDefault() {
        return getDefaultBuilder().build();
    }

    public static Configuration fromArgs(String[] args) {
        ConfigurationBuilder config = getDefaultBuilder();

        if (args.length > 0) {
            config.loadFile(args[0]);
        }

        if (args.length > 1) {
            config.resultSize(Integer.valueOf(args[1]));
        }

        if (args.length > 2) {
            config.threads(Integer.valueOf(args[2]));
        }

        if (args.length > 3) {
            config.level(Integer.valueOf(args[3]));
        }

        if(args.length > 4){
            config.slm1Factor(Integer.valueOf(args[4]));
        }

        if(args.length > 5){
            config.slm1OnlyL1(args[4].equals("true"));
        }

        return config.build();
    }

}