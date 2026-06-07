package com.gamezone.persistence;

import com.gamezone.model.Sale;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {

    private static final String FILE_PATH = "data/sales.json";
    private static final Type LIST_TYPE = new TypeToken<List<Sale>>() {}.getType();

    private final Gson gson;

    public SaleRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(com.gamezone.model.VideoGame.class, new VideoGameTypeAdapter())
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
            throw new RuntimeException("No se pudo inicializar el archivo de ventas: " + e.getMessage(), e);
        }
    }

    public List<Sale> findAll() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            List<Sale> sales = gson.fromJson(reader, LIST_TYPE);
            return sales != null ? sales : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el historial de ventas: " + e.getMessage(), e);
        }
    }

    public void save(Sale sale) {
        List<Sale> sales = findAll();
        sales.add(sale);
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(sales, LIST_TYPE, writer);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la venta: " + e.getMessage(), e);
        }
    }
}
