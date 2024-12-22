package com.aluracursos.screematch.principal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EjemploStreams {
    public void muestraEjemplo() {
        List<String> nombres = Arrays.asList("Luis","Jose", "Miguel", "Marco");

        nombres.stream()
                .sorted()
                .limit(2)
                .filter(name -> name.startsWith("J"))
                .map(String::toUpperCase)
                .forEach(System.out::println);
    }
}
