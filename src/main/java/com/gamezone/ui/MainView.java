package com.gamezone.ui;

import com.gamezone.exception.DuplicateVideoGameException;
import com.gamezone.exception.InsufficientStockException;
import com.gamezone.exception.InvalidVideoGameDataException;
import com.gamezone.exception.VideoGameNotFoundException;
import com.gamezone.model.Displayable;
import com.gamezone.model.Sale;
import com.gamezone.model.VideoGame;
import com.gamezone.service.SaleService;
import com.gamezone.service.VideoGameService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainView {

    private final VideoGameService videoGameService;
    private final SaleService saleService;

    private final BorderPane root = new BorderPane();
    private final VBox contentArea = new VBox(10);
    private final Label statusLabel = new Label();

    public MainView(VideoGameService videoGameService, SaleService saleService) {
        this.videoGameService = videoGameService;
        this.saleService = saleService;

        root.setLeft(buildMenu());
        root.setCenter(buildContentWrapper());

        showWelcome();
    }

    public BorderPane getView() {
        return root;
    }

    private VBox buildMenu() {
        Label title = new Label("GAMEZONE");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitle = new Label("Sistema de Gestión");
        subtitle.setStyle("-fx-text-fill: white;");

        Button btnAgregar = menuButton("1. Agregar videojuego");
        Button btnListar = menuButton("2. Listar todos los videojuegos");
        Button btnBuscarTitulo = menuButton("3. Buscar por título");
        Button btnBuscarPlataforma = menuButton("4. Buscar por plataforma");
        Button btnVender = menuButton("5. Realizar venta");
        Button btnVentas = menuButton("6. Mostrar ventas");
        Button btnSalir = menuButton("7. Salir");

        btnAgregar.setOnAction(e -> showAddGameView());
        btnListar.setOnAction(e -> showCatalog());
        btnBuscarTitulo.setOnAction(e -> showSearchByTitle());
        btnBuscarPlataforma.setOnAction(e -> showSearchByPlatform());
        btnVender.setOnAction(e -> showSellView());
        btnVentas.setOnAction(e -> showSales());
        btnSalir.setOnAction(e -> {
            if (AlertHelper.confirm("Salir", "¿Desea cerrar el sistema GameZone?")) {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            }
        });

        VBox menu = new VBox(8, title, subtitle, new Separator(),
                btnAgregar, btnListar, btnBuscarTitulo, btnBuscarPlataforma,
                btnVender, btnVentas, new Separator(), btnSalir);
        menu.setPadding(new Insets(15));
        menu.setPrefWidth(240);
        menu.setStyle("-fx-background-color: #2b2d42;");
        return menu;
    }


    private Button menuButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: #3a3d5c; -fx-text-fill: white; -fx-background-radius: 4;");
        return button;
    }

    private VBox buildContentWrapper() {
        contentArea.setPadding(new Insets(20));
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        statusLabel.setStyle("-fx-text-fill: #555;");
        VBox wrapper = new VBox(10, contentArea);
        wrapper.setPadding(new Insets(10));
        return wrapper;
    }

    private void setContent(String headerText, javafx.scene.Node... nodes) {
        contentArea.getChildren().clear();
        Label header = new Label(headerText);
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        contentArea.getChildren().add(header);
        contentArea.getChildren().addAll(nodes);
    }

    private void showWelcome() {
        setContent("Bienvenido a GameZone",
                new Label("Seleccione una opción del menú para comenzar a gestionar el catálogo de videojuegos."));
    }


    private void showAddGameView() {
        Button btnNuevo = new Button("Crear nuevo videojuego");
        Button btnActualizar = new Button("Actualizar seleccionado");
        Button btnEliminar = new Button("Eliminar seleccionado");

        TableView<VideoGame> table = buildGameTable();
        refreshTable(table);

        btnNuevo.setOnAction(e -> {
            GameFormDialog dialog = new GameFormDialog(null);
            Optional<VideoGame> result = dialog.showAndWait();
            result.ifPresent(game -> {
                try {
                    videoGameService.agregarVideojuego(game);
                    AlertHelper.showInfo("Videojuego agregado", "El videojuego \"" + game.getTitle() + "\" fue agregado al catálogo.");
                    refreshTable(table);
                } catch (DuplicateVideoGameException ex) {
                    AlertHelper.showWarning("Videojuego duplicado", ex.getMessage());
                } catch (InvalidVideoGameDataException ex) {
                    AlertHelper.showError("Datos inválidos", ex.getMessage());
                }
            });
        });

        btnActualizar.setOnAction(e -> {
            VideoGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Sin selección", "Seleccione un videojuego de la tabla para actualizar.");
                return;
            }
            GameFormDialog dialog = new GameFormDialog(selected);
            Optional<VideoGame> result = dialog.showAndWait();
            result.ifPresent(updated -> {
                try {
                    videoGameService.actualizarVideojuego(selected.getTitle(), updated);
                    AlertHelper.showInfo("Videojuego actualizado", "El videojuego fue actualizado correctamente.");
                    refreshTable(table);
                } catch (VideoGameNotFoundException | InvalidVideoGameDataException ex) {
                    AlertHelper.showError("No se pudo actualizar", ex.getMessage());
                }
            });
        });

        btnEliminar.setOnAction(e -> {
            VideoGame selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertHelper.showWarning("Sin selección", "Seleccione un videojuego de la tabla para eliminar.");
                return;
            }
            if (AlertHelper.confirm("Eliminar videojuego", "¿Está seguro de eliminar \"" + selected.getTitle() + "\"?")) {
                try {
                    videoGameService.eliminarVideojuego(selected.getTitle());
                    AlertHelper.showInfo("Videojuego eliminado", "El videojuego fue eliminado del catálogo.");
                    refreshTable(table);
                } catch (VideoGameNotFoundException ex) {
                    AlertHelper.showError("No se pudo eliminar", ex.getMessage());
                }
            }
        });

        HBox buttons = new HBox(10, btnNuevo, btnActualizar, btnEliminar);
        setContent("Agregar / Actualizar / Eliminar videojuego", buttons, table);
    }


    private void showCatalog() {
        TableView<VideoGame> table = buildGameTable();
        refreshTable(table);
        setContent("Catálogo completo de videojuegos", table);
    }


    private void showSearchByTitle() {
        TextField titleField = new TextField();
        titleField.setPromptText("Ingrese el título a buscar");
        Button btnBuscar = new Button("Buscar");

        TableView<VideoGame> table = buildGameTable();

        btnBuscar.setOnAction(e -> {
            String titulo = titleField.getText() == null ? "" : titleField.getText().trim();
            List<VideoGame> resultados = videoGameService.buscarPorTitulo(titulo);
            if (resultados == null) {
                AlertHelper.showInfo("Sin resultados", "No se encontró ningún videojuego con el título \"" + titulo + "\".");
                table.setItems(FXCollections.observableArrayList());
            } else {
                table.setItems(FXCollections.observableArrayList(resultados));
            }
        });

        HBox searchBar = new HBox(10, titleField, btnBuscar);
        setContent("Buscar videojuego por título", searchBar, table);
    }


    private void showSearchByPlatform() {
        TextField platformField = new TextField();
        platformField.setPromptText("Ingrese la plataforma a buscar");
        Button btnBuscar = new Button("Buscar");

        TableView<VideoGame> table = buildGameTable();

        btnBuscar.setOnAction(e -> {
            String plataforma = platformField.getText() == null ? "" : platformField.getText().trim();
            List<VideoGame> resultados = videoGameService.buscarPorPlataforma(plataforma);
            if (resultados == null) {
                AlertHelper.showInfo("Sin resultados", "No se encontraron videojuegos para la plataforma \"" + plataforma + "\".");
                table.setItems(FXCollections.observableArrayList());
            } else {
                table.setItems(FXCollections.observableArrayList(resultados));
            }
        });

        HBox searchBar = new HBox(10, platformField, btnBuscar);
        setContent("Buscar videojuegos por plataforma", searchBar, table);
    }


    private void showSellView() {
        TextField titleField = new TextField();
        titleField.setPromptText("Título del videojuego");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 999, 1);
        quantitySpinner.setEditable(true);
        Button btnVender = new Button("Vender");

        btnVender.setOnAction(e -> {
            String titulo = titleField.getText() == null ? "" : titleField.getText().trim();
            int cantidad = quantitySpinner.getValue();

            if (titulo.isEmpty()) {
                AlertHelper.showWarning("Datos incompletos", "Ingrese el título del videojuego a vender.");
                return;
            }

            try {
                Sale sale = saleService.venderVideojuego(titulo, cantidad);
                AlertHelper.showInfo("Venta realizada",
                        "Venta registrada con éxito.\nTotal a pagar: $" + sale.getTotal());
                titleField.clear();
                quantitySpinner.getValueFactory().setValue(1);
            } catch (VideoGameNotFoundException ex) {
                AlertHelper.showError("Videojuego no encontrado", ex.getMessage());
            } catch (InsufficientStockException ex) {
                AlertHelper.showWarning("Stock insuficiente", ex.getMessage());
            }
        });

        GridPaneFormHelper form = new GridPaneFormHelper();
        form.addRow("Título:", titleField);
        form.addRow("Cantidad:", quantitySpinner);

        VBox box = new VBox(15, form.getGrid(), btnVender);
        setContent("Realizar venta", box);
    }


    @SuppressWarnings("unchecked")
    private void showSales() {
        TableView<Sale> table = new TableView<>();

        TableColumn<Sale, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Sale, String> titleCol = new TableColumn<>("Videojuego");
        titleCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getVideoGame().getTitle()));

        TableColumn<Sale, Integer> qtyCol = new TableColumn<>("Cantidad");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Sale, Double> unitCol = new TableColumn<>("Precio unitario");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<Sale, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<Sale, String> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getSaleDate())));

        table.getColumns().addAll(idCol, titleCol, qtyCol, unitCol, totalCol, dateCol);

        ObservableList<Sale> sales = FXCollections.observableArrayList(saleService.listarVentas());
        table.setItems(sales);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setContent("Historial de ventas (" + sales.size() + " registradas)", table);
    }


    @SuppressWarnings("unchecked")
    private TableView<VideoGame> buildGameTable() {
        TableView<VideoGame> table = new TableView<>();

        TableColumn<VideoGame, String> titleCol = column("Título", 0);
        TableColumn<VideoGame, String> typeCol = column("Tipo", 1);
        TableColumn<VideoGame, String> platformCol = column("Plataforma", 2);
        TableColumn<VideoGame, String> genreCol = column("Género", 3);
        TableColumn<VideoGame, String> priceCol = column("Precio final", 4);
        TableColumn<VideoGame, String> stockCol = column("Stock", 5);
        TableColumn<VideoGame, String> extraCol = column("Detalle", 6);

        table.getColumns().addAll(titleCol, typeCol, platformCol, genreCol, priceCol, stockCol, extraCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableColumn<VideoGame, String> column(String name, int index) {
        TableColumn<VideoGame, String> col = new TableColumn<>(name);
        col.setCellValueFactory(data -> {
            VideoGame game = data.getValue();
            Object[] row = (game instanceof Displayable displayable)
                    ? displayable.toTableRow()
                    : new Object[] { game.getTitle(), "-", game.getPlatform(), game.getGenre(), game.calculateFinalPrice(), game.getStock(), "-" };
            Object value = index < row.length ? row[index] : "";
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(value));
        });
        return col;
    }

    private void refreshTable(TableView<VideoGame> table) {
        table.setItems(FXCollections.observableArrayList(videoGameService.listarTodos()));
    }
}
