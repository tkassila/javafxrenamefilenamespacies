package com.metait.javafxrenamefilenamespacies;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.stage.*;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;

public class RenameFileController  {
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
    public Label labelMsg;
    @FXML
    FileChooser fileChooser = new FileChooser();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    ObservableList<String> dirResult = FXCollections.observableArrayList();
    ListView<String> listView = new ListView<String>(dirResult);
    ButtonType loginButtonType = new ButtonType("Ok", ButtonType.OK.getButtonData());
    Dialog<String> dialog = new Dialog<>();
    Stage primaryStage = null;
    static String staticMsg = "";
    static String staticMsg2 = "";
    private File selectedDir = null;

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

        buttonOpenResult.setDisable(true);
        setLabelMsg("Application started.");

        listView.setMaxWidth(600);
        listView.setMinWidth(600);
        listView.setMaxHeight(400);
        listView.setMinHeight(400);
        dialog.setResizable(true);

        // Lisätään tuplaklikkauksen kuuntelija (Listener / EventHandler)
        listView.setOnMouseClicked((MouseEvent event) -> {
            // Tarkistetaan onko kyseessä tuplaklikkaus ja hiiren vasen painike
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                String valittu = listView.getSelectionModel().getSelectedItem();
                if (valittu != null) {
                    String fileName = getFileNameFromCannotRenameDialogSelection(valittu);
                    // Kutsutaan pääohjelman metodia suoraan!
                    openFileInNautilus(fileName);
                    // Suljetaan dialogi valinnan jälkeen
                   // dialogi.setResult(null);
                   // dialogi.close();
                }
            }
        });

        dialog.setTitle("Directory changes");
        dialog.getDialogPane().getButtonTypes().add(loginButtonType);
        boolean disabled = false; // computed based on content of text fields, for example
        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().lookupButton(loginButtonType).setDisable(disabled);

        buttonOpen.setDefaultButton(true);
        buttonOpenResult.setDisable(true);
        dialog.setWidth(500);
    }

    protected String getFileNameFromCannotRenameDialogSelection(String valittu)
    {
        if (valittu == null || valittu.isEmpty() || valittu.trim().length()==0)
            return null;
        String tmp = getTwoWordsOf(valittu);
        if (tmp == null || tmp.isEmpty())
            return null;
        if (selectedDir == null)
            return null;
        boolean bContainsSpace = tmp.contains(" ");
        tmp = selectedDir.getAbsolutePath() +File.separatorChar +tmp;
        /*
        if (bContainsSpace)
            tmp = "'" +tmp +"'";
         */
        return tmp;
    }

    private String getTwoWordsOf(String str)
    {
        if (str == null || str.isEmpty())
            return str;
        StringBuffer sb = new StringBuffer();
        int i = 0;
        int len = str.length();
        char[] arrStr = str.toCharArray();
        while (arrStr[i] != ' ') {
            i++;
        }
        i++;
        while (arrStr[i] != ' ') {
            i++;
        }
        i++;
        for (; i < len; i++)
        {
            sb.append(arrStr[i]);
        }
        String tmp = sb.toString();
        int index = tmp.indexOf(".");
        if (index > -1)
        {
            char[] arrStr2 = tmp.toCharArray();
            int j = index;
            while (arrStr2[j] != ' ') {
                j++;
            }
            tmp = tmp.substring(0, j);
            return tmp;
        }
        return sb.toString();
    }

    protected void openFileInNautilus(String selectedFile)
    {
        File selected = new File(selectedFile);
        if (selected.isDirectory())
            return;
        if (!selected.exists())
            return;

        boolean bContainsSpace = selected.getAbsolutePath().contains(" ");
        String strFile = selected.getAbsolutePath();
        /*
        if (bContainsSpace)
            strFile = "'" +strFile +"'";
         */

        try {
            // Välitetään polku argumenttina nautilus-komennolle
            ProcessBuilder pb = new ProcessBuilder("nautilus", strFile);
            Process prosessi = pb.start();
            // waitFor() odottaa prosessin loppumista ja palauttaa exit-arvon
            int exitValue = prosessi.waitFor();

            System.out.println("Prosessin exit-arvo: " + exitValue);
            if (exitValue == 0) {
                String strMsg = "Nautilus avattu polussa: " + selected.getAbsolutePath();
                System.out.println(strMsg);
                setLabelMsg(strMsg);
            }
        } catch (java.lang.InterruptedException e1) {
            String strMsg = "Virhe tiedoston nautilus avaamisessa: " + e1.getMessage();
            System.err.println(strMsg);
            setLabelMsg(strMsg);
    } catch (IOException e) {
            String strMsg = "Virhe tiedoston nautilus avaamisessa: " + e.getMessage();
            System.err.println(strMsg);
            setLabelMsg(strMsg);
        }
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
        setLabelMsg("File conversion started...");

        if (dir == null || !dir.exists() || !dir.canRead()) {
            setLabelMsg("Cannot read or not exists chosen directory.");
            return;
        }
        selectedDir = dir;
        File [] files = dir.listFiles();
        if (files == null || files.length == 0) {
            setLabelMsg("No files exists in chosen directory.");
            return;
        }
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
                dirResult.add("Cannot rename: " +fileName +" because " +newFile.getName() +" all ready exists!");
                continue;
            }
            if (!file.renameTo(newFile))
                dirResult.add("Error! Cannot rename: " +fileName);
            else {
                dirResult.add("Renamed: " + fileName);
                if (!newFile.exists()) {
                    dirResult.add("Cannot rename: " + fileName + " because " + newFile.getName() + " does not exists!");
                } else {
                    if (file.exists() && !file.delete())
                        dirResult.add("Cannot delete file: " + fileName + " after renamed new file!");
                }
            }
        }
        if (dirResult.isEmpty()) {
            buttonOpenResult.setDisable(false);
            setLabelMsg("No renamed files.");
            buttonOpenResult.setDisable(true);
        }
        else {
            setLabelMsg("All done. See result dialog by pressing the button.");
            buttonOpenResult.setDisable(false);
        }
    };

    @FXML
    protected void onOpenFileButtonClick() {

        dirResult.clear();
        buttonOpenResult.setDisable(true);
        if (checkBoxAllFiles.isSelected())
        {
            directoryChooser.setTitle("Rename all space files after selecting dir");
            File file = directoryChooser.showDialog(primaryStage);
            if (file == null) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        oldFileText.setText("");
                        newFileText.setText("");
                        setLabelMsg("");
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
                        setLabelMsg("");
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
                        alert.initModality(Modality.APPLICATION_MODAL); /* *** */
                        alert.initOwner(primaryStage);
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
                else
                {
                    String strError = "The selected file name does not contains space character!";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Warning");
                    alert.setHeaderText("File renaming");
                    alert.initModality(Modality.APPLICATION_MODAL); /* *** */
                    alert.initOwner(primaryStage);
                    alert.setContentText(strError);
                    alert.showAndWait();
                }
            }
    }

    public void setPrimaryStage(Stage stage)
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
    
    private void setLabelMsg(String msg)
    {
        Platform.runLater(new Runnable() {
            public void run() {
                labelMsg.setText(msg);
            }
        });
    }
}