package com.gamezone.ui;

import com.gamezone.model.DigitalVideoGame;
import com.gamezone.model.PhysicalVideoGame;
import com.gamezone.model.VideoGame;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class GameFormDialog {

    private final Dialog<VideoGame> dialog;
    private final ComboBox<String> typeCombo;

    private final TextField titleField = new TextField();
    private final TextField priceField = new TextField();
    private final TextField platformField = new TextField();
    private final TextField stockField = new TextField();
    private final TextField genreField = new TextField();

    private final TextField sizeGBField = new TextField();
    private final TextField downloadPlatformField = new TextField();

    private final ComboBox<String> conditionCombo = new ComboBox<>();
    private final TextField distributorField = new TextField();

    private final GridPane digitalGrid = new GridPane();
    private final GridPane physicalGrid = new GridPane();

    public GameFormDialog(VideoGame existingGame) {
        dialog = new Dialog<>();
        dialog.setTitle(existingGame == null ? "Agregar videojuego" : "Actualizar videojuego");
        dialog.setHeaderText(existingGame == null
                ? "Ingrese los datos del nuevo videojuego"
                : "Modifique los datos del videojuego");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Digital", "Fisico");
        typeCombo.setValue("Digital");
        typeCombo.setDisable(existingGame != null);

        conditionCombo.getItems().addAll("Nuevo", "Usado");
        conditionCombo.setValue("Nuevo");

        GridPane commonGrid = new GridPane();
        commonGrid.setHgap(10);
        commonGrid.setVgap(8);
        commonGrid.add(new Label("Tipo:"), 0, 0);
        commonGrid.add(typeCombo, 1, 0);
        commonGrid.add(new Label("Título:"), 0, 1);
        commonGrid.add(titleField, 1, 1);
        commonGrid.add(new Label("Precio base:"), 0, 2);
        commonGrid.add(priceField, 1, 2);
        commonGrid.add(new Label("Plataforma:"), 0, 3);
        commonGrid.add(platformField, 1, 3);
        commonGrid.add(new Label("Stock:"), 0, 4);
        commonGrid.add(stockField, 1, 4);
        commonGrid.add(new Label("Género:"), 0, 5);
        commonGrid.add(genreField, 1, 5);

        digitalGrid.setHgap(10);
        digitalGrid.setVgap(8);
        digitalGrid.setPadding(new Insets(10, 0, 0, 0));
        digitalGrid.add(new Label("Tamaño (GB):"), 0, 0);
        digitalGrid.add(sizeGBField, 1, 0);
        digitalGrid.add(new Label("Plataforma de descarga:"), 0, 1);
        digitalGrid.add(downloadPlatformField, 1, 1);

        physicalGrid.setHgap(10);
        physicalGrid.setVgap(8);
        physicalGrid.setPadding(new Insets(10, 0, 0, 0));
        physicalGrid.add(new Label("Condición:"), 0, 0);
        physicalGrid.add(conditionCombo, 1, 0);
        physicalGrid.add(new Label("Distribuidor:"), 0, 1);
        physicalGrid.add(distributorField, 1, 1);

        VBox content = new VBox(10, commonGrid, digitalGrid, physicalGrid);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateVisibleFields());
        updateVisibleFields();

        if (existingGame != null) {
            prefill(existingGame);
        }

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return buildGameFromInput();
            }
            return null;
        });
    }

    private void updateVisibleFields() {
        boolean isDigital = "Digital".equals(typeCombo.getValue());
        digitalGrid.setVisible(isDigital);
        digitalGrid.setManaged(isDigital);
        physicalGrid.setVisible(!isDigital);
        physicalGrid.setManaged(!isDigital);
    }

    private void prefill(VideoGame game) {
        titleField.setText(game.getTitle());
        priceField.setText(String.valueOf(game.getPrice()));
        platformField.setText(game.getPlatform());
        stockField.setText(String.valueOf(game.getStock()));
        genreField.setText(game.getGenre());

        if (game instanceof DigitalVideoGame digital) {
            typeCombo.setValue("Digital");
            sizeGBField.setText(String.valueOf(digital.getSizeGB()));
            downloadPlatformField.setText(digital.getDownloadPlatform());
        } else if (game instanceof PhysicalVideoGame physical) {
            typeCombo.setValue("Fisico");
            conditionCombo.setValue(physical.getCondition());
            distributorField.setText(physical.getDistributor());
        }
        updateVisibleFields();
    }

    private VideoGame buildGameFromInput() {
        try {
            String title = titleField.getText() == null ? "" : titleField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String platform = platformField.getText().trim();
            int stock = Integer.parseInt(stockField.getText().trim());
            String genre = genreField.getText().trim();

            if ("Digital".equals(typeCombo.getValue())) {
                double sizeGB = Double.parseDouble(sizeGBField.getText().trim());
                String downloadPlatform = downloadPlatformField.getText().trim();
                return new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, downloadPlatform);
            } else {
                String condition = conditionCombo.getValue();
                String distributor = distributorField.getText().trim();
                return new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor);
            }
        } catch (NumberFormatException e) {
            AlertHelper.showError("Datos inválidos", "Verifique que el precio, el stock y el tamaño sean numéricos.");
            return null;
        }
    }

    public Optional<VideoGame> showAndWait() {
        return dialog.showAndWait();
    }
}
