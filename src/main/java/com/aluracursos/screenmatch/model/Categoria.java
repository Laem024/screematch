package com.aluracursos.screenmatch.model;

public enum Categoria {
    ACCION("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIMEN("Crime");

    private String categoriaOmdb;

    Categoria(String categoriaOmdb) {
        this.categoriaOmdb = categoriaOmdb;
    }

    public static Categoria fromString(String text) {
        for (Categoria cat : Categoria.values()) {
            if (cat.categoriaOmdb.equalsIgnoreCase(text)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada" + text);
    }
}
