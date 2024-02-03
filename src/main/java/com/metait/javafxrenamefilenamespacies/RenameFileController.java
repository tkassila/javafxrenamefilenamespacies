package com.metait.javafxrenamefilenamespacies;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ListView;

import java.io.File;

public class RenameFileController {
    @FXML
    private Label welcomeText;
    @FXML
    private Label oldFileText;
    @FXML
    private Label newFileText;
    @FXML
    private CheckBox checkBoxAllFiles;
    @FXML
    private Button buttonOpen;
    @FXML
    private Button buttonOpenResult;
    @FXML
    private Label labelMsg;
    @FXML
    FileChooser fileChooser = new FileChooser();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    ObservableList<String> dirResult = FXCollections.observableArrayList();
    ListView<String> listView = new ListView<String>(dirResult);
    ButtonType loginButtonType = new ButtonType("Ok", ButtonType.OK.getButtonData());
    Dialog<String> dialog = new Dialog<>();
    static Stage primaryStage = null;
    static String staticMsg = "";
    static String staticMsg2 = "";

    @FXML
    protected void checkBoxAllFilesPressed()
    {
        if (checkBoxAllFiles.isSelected())
            buttonOpen.setText("Open directory");
        else
            buttonOpen.setText("Open file");
    }

    public void initialize()
    {
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHgrow(Priority.ALWAYS);

        listView.setMaxWidth(600);
        listView.setMinWidth(600);
        listView.setMaxHeight(400);
        listView.setMinHeight(400);
        dialog.setResizable(true);
        dialog.setTitle("Directory changes");
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        boolean disabled = false; // computed based on content of text fields, for example
        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().lookupButton(loginButtonType).setDisable(disabled);

        buttonOpen.setDefaultButton(true);
        buttonOpenResult.setDisable(true);
        dialog.setWidth(500);
    }

    @FXML
    private void buttonOpenResultPressed(){
      //  dialog.getDialogPane()
        Platform.runLater(new Runnable() {
            public void run() {

                dialog.getDialogPane().setContent(listView);
                dialog.show();
            }
        });
    }

    private void renameAllSpaceFilesWithUnderlineChars(File dir)
    {
        oldFileText.setText("");
        newFileText.setText("");
        dirResult.clear();
        buttonOpenResult.setDisable(true);
        labelMsg.setText("Started...");

        if (dir == null || !dir.exists() || !dir.canRead())
            return;
        File [] files = dir.listFiles();
        if (files == null || files.length == 0)
            return;
        File file, newFile;
        String fileName;
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            if (file == null)
                continue;
            if (!file.exists() || file.isDirectory())
                continue;
            fileName = file.getName();
            if (fileName == null || fileName.trim().length() == 0  || !fileName.contains(" "))
                continue;
            newFile = new File(file.getParent() +File.separator
                    +fileName.replaceAll("\\s", "_"));
            if (newFile.exists()) {
                dirResult.add("Cannot rename: " +fileName +" because " +newFile.getName() +" exists!");
                continue;
            }
            if (!file.renameTo(newFile))
                dirResult.add("Error! Cannot rename: " +fileName);
            else
                dirResult.add("Renamed: " +fileName);
        }
        if (!dirResult.isEmpty())
            buttonOpenResult.setDisable(false);
        labelMsg.setText("All done. See result dialog by pressing the button.");
    };

    @FXML
    protected void onOpenFileButtonClick() {

        dirResult.clear();
        if (checkBoxAllFiles.isSelected())
        {
            directoryChooser.setTitle("Rename all space files after selecting dir");
            File file = directoryChooser.showDialog(primaryStage);
            if (file == null) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        oldFileText.setText("");
                        newFileText.setText("");
                        labelMsg.setText("");
                    }
                });
                return;
            }

            if (file != null && file.isDirectory()) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        oldFileText.setText("");
                        newFileText.setText("");
                        labelMsg.setText("");
                    }
                });
                renameAllSpaceFilesWithUnderlineChars(file);
            }
        }
        else {
            fileChooser.setTitle("Rename File After Open");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file == null) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        oldFileText.setText("");
                        newFileText.setText("");
                        labelMsg.setText("");
                    }
                });
                return;
            }

                Platform.runLater(new Runnable() {
                    public void run() {
                        oldFileText.setText("");
                        newFileText.setText("");
                        labelMsg.setText("");
                    }
                });
                String strFileName = file.getName();
                if (strFileName != null && strFileName.contains(" ")) {
                    staticMsg = new String(strFileName);
                    Platform.runLater(new Runnable() {
                        public void run() {
                            oldFileText.setText(staticMsg);
                        }
                    });

                    String newFileName = new String(strFileName).replaceAll("\\s", "_");
                    File newFile = new File(file.getParent() + File.separator + newFileName);
                    if (newFile.exists())
                    {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                oldFileText.setText(staticMsg);
                                newFileText.setText(newFile.getName());
                                labelMsg.setText("Cannot rename into a new file name, because there is a file with the same name all ready!");
                            }
                        });
                        return;
                    }
                    if (!file.renameTo(newFile)) {
                        String strError = "For some reason: cannot rename into a new file name!";
                        if (newFile.exists())
                            strError = "Cannot rename into a new file name, because there is a file with the same name all ready!";
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Warning");
                        alert.setHeaderText("File renaming");
                        alert.setContentText(strError);
                        alert.showAndWait();
                    } else {
                        staticMsg2 = new String(newFileName);
                        Platform.runLater(new Runnable() {
                            public void run() {
                                newFileText.setText(staticMsg2);
                            }
                        });

                    }
                }
            }
    }

    static public void setPrimaryStage(Stage stage)
    {
        primaryStage = stage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}