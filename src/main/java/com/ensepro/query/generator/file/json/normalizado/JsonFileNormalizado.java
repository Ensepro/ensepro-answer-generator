package com.ensepro.query.generator.file.json.normalizado;

import com.ensepro.query.generator.file.json.JsonHelper;
import com.ensepro.query.generator.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JsonFileNormalizado {

    private JsonHelper helper;
    private List<List<String>> values;

    public void saveFile(String file) throws IOException {
        JsonUtil.save(file, this);
    }

    public static JsonFileNormalizado loadFile(String file) throws FileNotFoundException {
        return JsonUtil.read(file, JsonFileNormalizado.class);
    }

}
