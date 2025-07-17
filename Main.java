import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.sql.*;

public class Main extends Application {

    private Connection conn;  
    private Label status = new Label("Not connected");
    private Label batchTime   = new Label("Batch: N/A");
    private Label normalTime  = new Label("Non-batch: N/A");

    public static void main(String[] args) {
        launch(args);
    }
 @Override
    public void start(Stage stage) {
        // Top row: Connect button + status
        Button btnConnect = new Button("Connect…");
        btnConnect.setOnAction(e -> showConnectDialog(stage));

        HBox top = new HBox(10, btnConnect, status);
        top.setPadding(new Insets(10));

        // Middle row: two buttons to run updates
        Button btnBatch     = new Button("Batch Update");
        Button btnNonBatch  = new Button("Non-Batch Update");
        btnBatch    .setOnAction(e -> runBatch());
        btnNonBatch .setOnAction(e -> runNonBatch());

        HBox middle = new HBox(10, btnBatch, btnNonBatch);
        middle.setPadding(new Insets(10));

        // Bottom row: show elapsed times
        VBox bottom = new VBox(5, batchTime, normalTime);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(middle);
        root.setBottom(bottom);

        stage.setScene(new Scene(root));
        stage.setTitle("Exercise 35.1: Batch Update Demo");
        stage.show();
    }

     private void showConnectDialog(Window owner) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Connect to Database");

        // JDBC inputs
        TextField tfDriver = new TextField("com.mysql.cj.jdbc.Driver");
        TextField tfURL    = new TextField("jdbc:mysql://localhost:3306/yourDB");
        TextField tfUser   = new TextField("yourUser");
        PasswordField pf   = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));
        grid.addRow(0, new Label("Driver:"), tfDriver);
        grid.addRow(1, new Label("URL:"),    tfURL);
        grid.addRow(2, new Label("User:"),   tfUser);
        grid.addRow(3, new Label("Password:"), pf);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
            ButtonType.OK, ButtonType.CANCEL
        );

        dialog.setResultConverter(bt -> bt);
        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    Class.forName(tfDriver.getText().trim());
                    conn = DriverManager.getConnection(
                        tfURL.getText().trim(),
                        tfUser.getText().trim(),
                        pf.getText()
                    );
                    status.setText("Connected");
                }
                catch (Exception ex) {
                    status.setText("Error: " + ex.getMessage());
                }
            }
        });
    }
private void runBatch() {
        if (!checkConnection()) return;
        String sql = "INSERT INTO Temp(num1, num2, num3) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                ps.setDouble(1, Math.random());
                ps.setDouble(2, Math.random());
                ps.setDouble(3, Math.random());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            long elapsed = System.currentTimeMillis() - start;
            batchTime.setText("Batch: " + elapsed + " ms");
        }
        catch (Exception ex) {
            status.setText("Batch error: " + ex.getMessage());
        }
    }
    private void runNonBatch() {
        if (!checkConnection()) return;
        String sql = "INSERT INTO Temp(num1, num2, num3) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000; i++) {
                ps.setDouble(1, Math.random());
                ps.setDouble(2, Math.random());
                ps.setDouble(3, Math.random());
                ps.executeUpdate();
            }
            conn.commit();
            long elapsed = System.currentTimeMillis() - start;
            normalTime.setText("Non-batch: " + elapsed + " ms");
        }
        catch (Exception ex) {
            status.setText("Non-batch error: " + ex.getMessage());
        }
    }