package com.aluracursos.screematch.service;

public interface IConvierteDatos {
    <T>T obtenerDatos(String json, Class<T> clase);
}
