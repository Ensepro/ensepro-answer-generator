package com.ensepro.query.generator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class JsonUtil {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T read(String file, Class<T> clazz) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        return gson.fromJson(br, clazz);
    }

    public static void save(String file, Object obj) throws IOException {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(file), obj);
    }


}
