package com.ensepro.query.generator.classes;

import com.ensepro.query.generator.file.json.normalizado.JsonFileNormalizado;
import com.ensepro.query.generator.file.json.ranqueado.JsonFileRanqueado;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class Triplas {

    private final Helper helper;
    @Singular
    private final List<Tripla> triplas;


    public static Triplas fromJsonFile(JsonFileNormalizado json) {
        final TriplasBuilder builder = Triplas.builder();

        json.getValues().forEach(listTripla -> {
            builder.tripla(Tripla.builder()
                                 .sujeito(listTripla.get(0))
                                 .predicado(listTripla.get(1))
                                 .objeto(listTripla.get(2))
                                 .build()
                          );
        });


        builder.helper(Helper.fromJsonHelper(json.getHelper()));


        return builder.build();
    }

}
