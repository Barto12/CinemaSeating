package com.example.cinemaseating;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CinemaSeating extends Application {

    private static final int ROWS = 5;
    private static final int COLUMNS = 5;
    private Label statusLabel;
    private ComboBox<String> movieComboBox;
    private ComboBox<String> timeComboBox;
    private TextField priceField;
    private GridPane seatGrid;
    private Button payButton;
    private ImageView movieImageView;

    private List<Movie> movies;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Taquilla de Cine");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Initialize movies
        initializeMovies();

        // Create top menu bar
        MenuBar menuBar = createMenuBar();

        // Create movie selection pane
        VBox movieSelectionPane = createMovieSelectionPane();

        // Create seat selection grid
        seatGrid = createSeatGrid();

        // Create payment button
        payButton = new Button("Pagar");
        payButton.setDisable(true);
        payButton.setOnAction(e -> handlePayment());

        // Status label
        statusLabel = new Label("Seleccione una película y un horario");

        statusLabel.setFont(new Font("Arial", 16));
        statusLabel.setTextFill(Color.BLUE);
        statusLabel.setPadding(new Insets(10));

        root.setTop(menuBar);
        root.setLeft(movieSelectionPane);
        root.setCenter(seatGrid);
        root.setBottom(new VBox(statusLabel, payButton));

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeMovies() {
        movies = new ArrayList<>();
        movies.add(new Movie("Película 1", new String[]{"10:00", "13:00", "16:00"}, 8.0, "file:src/main/resources/movie1.jpg"));
        movies.add(new Movie("Película 2", new String[]{"11:00", "14:00", "17:00"}, 10.0, "file:src/main/resources/movie2.jpg"));
        movies.add(new Movie("Película 3", new String[]{"12:00", "15:00", "18:00"}, 12.0, "file:src/main/resources/movie3.jpg"));
    }

    private VBox createMovieSelectionPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        movieComboBox = new ComboBox<>();
        movies.forEach(movie -> movieComboBox.getItems().add(movie.getTitle()));
        movieComboBox.setOnAction(e -> updateMovieDetails());

        timeComboBox = new ComboBox<>();
        timeComboBox.setDisable(true);

        priceField = new TextField();
        priceField.setEditable(false);

        movieImageView = new ImageView();
        movieImageView.setFitWidth(200);
        movieImageView.setFitHeight(300);

        vbox.getChildren().addAll(new Label("Película:"), movieComboBox, movieImageView, new Label("Horario:"), timeComboBox, new Label("Precio:"), priceField);

        return vbox;
    }

    private void updateMovieDetails() {
        String selectedMovie = movieComboBox.getValue();
        if (selectedMovie != null) {
            Movie movie = movies.stream().filter(m -> m.getTitle().equals(selectedMovie)).findFirst().orElse(null);
            if (movie != null) {
                timeComboBox.getItems().clear();
                timeComboBox.getItems().addAll(movie.getTimes());
                timeComboBox.setDisable(false);
                timeComboBox.setOnAction(e -> {
                    priceField.setText("$" + movie.getPrice());
                    movieImageView.setImage(new Image(movie.getImagePath()));
                    payButton.setDisable(false);
                });
            }
        }
    }

    private GridPane createSeatGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Button seatButton = new Button("Asiento " + (row * COLUMNS + col + 1));
                seatButton.setId("seatButton");
                seatButton.setPrefSize(80, 80);
                seatButton.setOnAction(e -> {
                    Button btn = (Button) e.getSource();
                    btn.setStyle("-fx-background-color: green");
                    statusLabel.setText(btn.getText() + " seleccionado");
                });
                grid.add(seatButton, col, row);
            }
        }
        return grid;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("Archivo");
        MenuItem exitMenuItem = new MenuItem("Salir");
        exitMenuItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitMenuItem);

        Menu helpMenu = new Menu("Ayuda");
        MenuItem aboutMenuItem = new MenuItem("Acerca de");
        aboutMenuItem.setOnAction(e -> showAlert("Acerca de", "Aplicación de Taquilla de Cine"));
        helpMenu.getItems().add(aboutMenuItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handlePayment() {
        ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("Efectivo", "Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito");
        paymentDialog.setTitle("Método de Pago");
        paymentDialog.setHeaderText("Seleccione el método de pago");
        paymentDialog.setContentText("Método de Pago:");

        Optional<String> result = paymentDialog.showAndWait();
        result.ifPresent(paymentMethod -> {
            if (!paymentMethod.isEmpty()) {
                showAlert("Pago", "Pago realizado con éxito usando " + paymentMethod);
                printTicket(paymentMethod);
            }
        });
    }

    private void printTicket(String paymentMethod) {
        String selectedMovie = movieComboBox.getValue();
        String selectedTime = timeComboBox.getValue();
        String price = priceField.getText();
        showAlert("Ticket", "Película: " + selectedMovie + "\nHorario: " + selectedTime + "\nPrecio: " + price + "\nMétodo de Pago: " + paymentMethod);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Movie {
    private String title;
    private String[] times;
    private double price;
    private String imagePath;

    public Movie(String title, String[] times, double price, String imagePath) {
        this.title = title;
        this.times = times;
        this.price = price;
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String[] getTimes() {
        return times;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }
}
