package com.aluracursos.screenmatch.model;

public enum Categoria {
    ACCION("Action", "Acción"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comedia"),
    DRAMA("Drama", "Drama"),
    CRIMEN("Crime", "Crimen"),
    ANIMACION("Animation", "Animación"),
    MUSICAL("Musical", "Musical");

    private final String categoriaOmdb;
    private final String categoriaSpanish;

    Categoria(String categoriaOmdb, String categoriaSpanish) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaSpanish = categoriaSpanish;
    }

    public static Categoria fromString(String text) {
        for (Categoria cat : Categoria.values()) {
            if (cat.categoriaOmdb.equalsIgnoreCase(text)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada" + text);
    }

    public static Categoria fromSpanish(String text) {
        for (Categoria cat : Categoria.values()) {
            if (cat.categoriaSpanish.equalsIgnoreCase(text)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada" + text);
    }
}
