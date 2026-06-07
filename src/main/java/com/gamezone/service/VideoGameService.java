package com.gamezone.service;

import com.gamezone.exception.DuplicateVideoGameException;
import com.gamezone.exception.InvalidVideoGameDataException;
import com.gamezone.exception.VideoGameNotFoundException;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.VideoGameRepository;

import java.util.List;
import java.util.stream.Collectors;

public class VideoGameService {

    private final VideoGameRepository repository;

    public VideoGameService(VideoGameRepository repository) {
        this.repository = repository;
    }

    public void agregarVideojuego(VideoGame game) {
        validarVideojuego(game);

        if (repository.findByTitle(game.getTitle()).isPresent()) {
            throw new DuplicateVideoGameException("El videojuego ya existe en el catálogo");
        }
        repository.save(game);
    }

    private void validarVideojuego(VideoGame game) {
        if (game.getTitle() == null || game.getTitle().trim().isEmpty()) {
            throw new InvalidVideoGameDataException("El título no puede estar vacío");
        }
        if (game.getPrice() <= 0) {
            throw new InvalidVideoGameDataException("El precio debe ser mayor a 0");
        }
        if (game.getStock() < 0) {
            throw new InvalidVideoGameDataException("El stock debe ser mayor o igual a 0");
        }
    }

    public List<VideoGame> listarTodos() {
        return repository.findAll();
    }

    public List<VideoGame> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return null;
        }
        String textoBusqueda = titulo.trim().toLowerCase();
        List<VideoGame> resultado = repository.findAll().stream()
                .filter(game -> game.getTitle() != null && game.getTitle().toLowerCase().contains(textoBusqueda))
                .collect(Collectors.toList());
        return resultado.isEmpty() ? null : resultado;
    }

    public List<VideoGame> buscarPorPlataforma(String plataforma) {
        if (plataforma == null || plataforma.trim().isEmpty()) {
            return null;
        }
        String textoBusqueda = plataforma.trim().toLowerCase();
        List<VideoGame> resultado = repository.findAll().stream()
                .filter(game -> game.getPlatform() != null && game.getPlatform().toLowerCase().contains(textoBusqueda))
                .collect(Collectors.toList());
        return resultado.isEmpty() ? null : resultado;
    }

    public void actualizarVideojuego(String tituloOriginal, VideoGame nuevoVideojuego) {
        validarVideojuego(nuevoVideojuego);
        boolean actualizado = repository.update(tituloOriginal, nuevoVideojuego);
        if (!actualizado) {
            throw new VideoGameNotFoundException("No se encontró un videojuego con el título: " + tituloOriginal);
        }
    }

    public void eliminarVideojuego(String titulo) {
        boolean eliminado = repository.delete(titulo);
        if (!eliminado) {
            throw new VideoGameNotFoundException("No se encontró un videojuego con el título: " + titulo);
        }
    }
}
