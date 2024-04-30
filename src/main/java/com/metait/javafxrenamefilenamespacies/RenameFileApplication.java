package com.metait.javafxrenamefilenamespacies;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RenameFileApplication extends Application {
    private RenameFileController controller = new RenameFileController();
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RenameFileApplication.class.getResource("renamefile-view.fxml"));
        controller.setPrimaryStage(stage);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 720, 440);
        stage.setTitle("Rename file with space characters");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}