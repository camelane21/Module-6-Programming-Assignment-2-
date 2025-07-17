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
