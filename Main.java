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