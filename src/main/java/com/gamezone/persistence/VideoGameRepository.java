package com.gamezone.persistence;

import com.gamezone.model.VideoGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VideoGameRepository {

    private static final String FILE_PATH = "data/videogames.json";
    private static final Type LIST_TYPE = new TypeToken<List<VideoGame>>() {}.getType();

    private final Gson gson;

    public VideoGameRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(VideoGame.class, new VideoGameTypeAdapter())
                .create();
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            Path path = Path.of(FILE_PATH);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.writeString(path, "[]");
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el archivo de datos: " + e.getMessage(), e);
        }
    }

    public List<VideoGame> findAll() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            List<VideoGame> games = gson.fromJson(reader, LIST_TYPE);
            return games != null ? games : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el catálogo: " + e.getMessage(), e);
        }
    }

    public Optional<VideoGame> findByTitle(String title) {
        if (title == null) {
            return Optional.empty();
        }
        return findAll().stream()
                .filter(game -> game.getTitle() != null && game.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    public void save(VideoGame game) {
        List<VideoGame> games = findAll();
        games.add(game);
        writeAll(games);
    }

    public boolean update(String title, VideoGame newGame) {
        List<VideoGame> games = findAll();
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getTitle().equalsIgnoreCase(title)) {
                games.set(i, newGame);
                writeAll(games);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String title) {
        List<VideoGame> games = findAll();
        boolean removed = games.removeIf(game -> game.getTitle().equalsIgnoreCase(title));
        if (removed) {
            writeAll(games);
        }
        return removed;
    }

    private void writeAll(List<VideoGame> games) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(games, LIST_TYPE, writer);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir el catálogo: " + e.getMessage(), e);
        }
    }
}
