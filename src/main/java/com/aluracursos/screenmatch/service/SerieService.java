package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    /*
    autowired no recomendado por java
    @Autowired
    private SerieRepository repository;
    */

    private final SerieRepository repository;

    public SerieService(SerieRepository repository) {
        this.repository = repository;
    }

    public List<SerieDTO> obtenerTodasLasSeries(){
        return convierteDatos(repository.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> obtenerLanzamientosMasRecientes() {
        return convierteDatos(repository.lanzamientosMasRecientes());
    }

    public List<SerieDTO> convierteDatos(List<Serie> serie){
        return serie.stream()
                .map(s -> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getEvaluacion(),
                        s.getPoster(),
                        s.getGenero(),
                        s.getActores(),
                        s.getSinopsis()
                )).collect(Collectors.toList());
    }

    public SerieDTO obtenerPorID(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()) {
            Serie s = serie.get();

            return new SerieDTO(
                    s.getId(),
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getEvaluacion(),
                    s.getPoster(),
                    s.getGenero(),
                    s.getActores(),
                    s.getSinopsis()
            );
        }else {
            return null;
        }
    }

    public List<EpisodioDTO> obtenerTodasLastemporadas(Long id) {
        Optional<Serie> serie = repository.findById(id);
        if(serie.isPresent()) {
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(
                        e.getTemporada(),
                        e.getTitulo(),
                        e.getNumeroEpisodio()
                    )).collect(Collectors.toList());
        }else{
            return null;
        }
    }

    public List<EpisodioDTO> obtenerTemporadasPorNumero(Long id, Integer numeroTemporada) {
        return repository.obtenerTemporadasPorNumero(id, numeroTemporada).stream()
                .map(e -> new EpisodioDTO(
                        e.getTemporada(),
                        e.getTitulo(),
                        e.getNumeroEpisodio()
                )).collect(Collectors.toList());
    }

    public List<SerieDTO> ObtenerSeriePorCategoria(String nombreGenero) {
        Categoria categoria = Categoria.fromSpanish(nombreGenero);

        return convierteDatos(repository.findByGenero(categoria));
    }
}
