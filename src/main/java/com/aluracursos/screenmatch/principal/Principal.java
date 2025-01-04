package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=3ffd9a0a";
    private ConvierteDatos conversor = new ConvierteDatos();
    private  List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar serie por titulo  
                    5 - Buscar top 5 mejores series   
                    6 - Buscar series por categoria     
                    7 - Filtrar series por cantidad de temporadas y evaluacion    
                    8 - Buscar episodios por titulo
                    9 - Top 5 episodios por serie
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5MejoresSeries();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesCantTempEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        //DatosSerie datosSerie = getDatosSerie();
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie que deseas ver los episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();

            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .toList();

            serieEncontrada.setEpisodios(episodios);

            repository.save(serieEncontrada);
        }
    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repository.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
//        List<Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//                .map(Serie::new)
//                .toList();

        series = repository.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    private void buscarSeriePorTitulo() {
        System.out.println("Escribe el titulo de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();

        serieBuscada = repository.findByTituloContainingIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        }else {
            System.out.println("La serie no existe");
        }
    }

    private void buscarTop5MejoresSeries() {
        List<Serie> topSeries = repository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + " Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escribe el genero/categoria de la serie que deseas buscar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromSpanish(genero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);

        System.out.println("Las series de la categoria: " + genero);

        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesCantTempEvaluacion(){
        System.out.println("Series cantidad de temporadas menores a:");
        var temporadas = teclado.nextInt();

        System.out.println("Series con evaluación mayor a:");
        var evaluacion = teclado.nextDouble();

        //utilizando consultas derivadas
        List<Serie> serieFiltradas = repository.findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(temporadas, evaluacion);
        System.out.println("*** Series filtradas ***");
        serieFiltradas.forEach(s -> System.out.println(s.getTitulo() + " - Evaluacion: " + s.getEvaluacion()));

        //utilzando consultas manuales (native Querys)
        serieFiltradas = repository.seriesPorTemporadasYEvaluacion(temporadas, evaluacion);
        System.out.println("*** Series filtradas ***");
        serieFiltradas.forEach(s -> System.out.println(s.getTitulo() + " - Evaluacion: " + s.getEvaluacion()));
    }

    private void  buscarEpisodiosPorTitulo(){
        System.out.println("Escribe el titulo del episodio que deseas buscar:");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s, Temporada: %s, Episodio: %s, Titulo: %s, Evaluación: %s \n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion()));
    }

    private void buscarTop5EpisodiosPorSerie(){
        buscarSeriePorTitulo();

        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodio = repository.top5Episodios(serie);

            topEpisodio.forEach(e ->
                    System.out.printf("Serie: %s, Temporada: %s, Episodio: %s, Titulo: %s, Evaluación: %s \n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion()));
        }
    }
}

