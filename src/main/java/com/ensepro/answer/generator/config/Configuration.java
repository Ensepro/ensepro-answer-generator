package com.ensepro.answer.generator.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Configuration {

    private static final String DEFAULT_LOAD_FILE = "resultado_normalizado.json";
    private static final String DEFAULT_SAVE_FILE = "queries_renqueadas.json";
    private static final Integer DEFAULT_LEVEL = 2;
    private static final Integer DEFAULT_RESULT_SIZE = 10;

    private final String loadFile;
    private final String saveFile;
    private final Integer level; //2 - combination of 2 triples. 3 - combination of 3 triples.
    private final Integer resultSize;

    private static ConfigurationBuilder getDefaultBuilder() {
        return Configuration.builder()
            .loadFile(DEFAULT_LOAD_FILE)
            .saveFile(DEFAULT_SAVE_FILE)
            .level(DEFAULT_LEVEL)
            .resultSize(DEFAULT_RESULT_SIZE);

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
            config.level(Integer.valueOf(args[2]));
        }

        return config.build();
    }

}
