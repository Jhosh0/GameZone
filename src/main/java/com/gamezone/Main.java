package com.gamezone;

import com.gamezone.exception.DuplicateVideoGameException;
import com.gamezone.model.DigitalVideoGame;
import com.gamezone.model.PhysicalVideoGame;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.VideoGameRepository;
import com.gamezone.service.SaleService;
import com.gamezone.service.VideoGameService;
import com.gamezone.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        VideoGameRepository videoGameRepository = new VideoGameRepository();
        SaleRepository saleRepository = new SaleRepository();

        VideoGameService videoGameService = new VideoGameService(videoGameRepository);
        SaleService saleService = new SaleService(videoGameRepository, saleRepository);

        seedCatalog(videoGameService);

        MainView mainView = new MainView(videoGameService, saleService);

        Scene scene = new Scene(mainView.getView(), 1100, 650);
        primaryStage.setTitle("GameZone - Sistema de Gestión de Videojuegos");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void seedCatalog(VideoGameService service) {
        if (!service.listarTodos().isEmpty()) {
            return;
        }
        try {
            service.agregarVideojuego(new DigitalVideoGame(
                    "Hollow Knight", 35000, "PC", 50, "Metroidvania", 9.5, "Steam"));
            service.agregarVideojuego(new DigitalVideoGame(
                    "Cyberpunk 2077", 120000, "PC", 30, "RPG", 70.0, "GOG"));
            service.agregarVideojuego(new PhysicalVideoGame(
                    "The Legend of Zelda: TOTK", 250000, "Nintendo Switch", 20, "Aventura", "Nuevo", "Nintendo"));
            service.agregarVideojuego(new PhysicalVideoGame(
                    "God of War Ragnarok", 180000, "PlayStation 5", 15, "Accion", "Usado", "Sony"));
        } catch (DuplicateVideoGameException ignored) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
