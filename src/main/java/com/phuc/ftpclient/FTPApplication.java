package com.phuc.ftpclient;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.commands.PrintWorkingDirCmd;
import com.phuc.ftpclient.commands.PutCmd;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.gui.FTPTreeItem;
import com.phuc.ftpclient.gui.FilePathTreeItem;
import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;
import com.phuc.ftpclient.util.MLSDEntry;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FTPApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        TreeView<String> treeViewLocal;
        FilePathTreeItem rootNodeLocal;
        ImageView image = new ImageView("img/folder.png");
        image.setFitWidth(16);
        image.setFitHeight(16);
        Path localPath = Paths.get(Constants.LOCAL_DIR);
        rootNodeLocal = new FilePathTreeItem(localPath);

        try (DirectoryStream<Path> dir = Files.newDirectoryStream(localPath)) {
            for (Path fileName : dir) {
                FilePathTreeItem treeNode = new FilePathTreeItem(fileName);
                rootNodeLocal.getChildren().add(treeNode);
            }
        }

        // rootNodeLocal.setExpanded(true);
        treeViewLocal = new TreeView<>(rootNodeLocal);

        VBox localFileBox = new VBox(treeViewLocal);
        localFileBox.setMinWidth(300);

        TreeView<String> treeViewServer;

        new Thread(() -> {
            try {
                while (!App.getIsRunning()) {
                    
                }
                Thread.sleep(500);
                List<String> workingDir = getCommandOutput(new PrintWorkingDirCmd().getName());
                for (String s : workingDir) {
                    Console.debug(s);
                }
            } catch (InterruptedException ex) {
                System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }).start();
        // TODO: get working directory
        // FTPTreeItem rootNodeServer = new FTPTreeItem(new MLSDEntry("dir", "/"),
        // folder -> App.getClient().listDir(folder));

        // try (DirectoryStream<Path> dir = Files.newDirectoryStream(serverPath)) {
        // for (Path fileName : dir) {
        // FilePathTreeItem treeNode = new FilePathTreeItem(fileName);
        // rootNodeServer.getChildren().add(treeNode);
        // }
        // }

        // rootNodeServer.setExpanded(true);

        treeViewServer = new TreeView<>();
        VBox serverFileBox = new VBox(treeViewServer);
        serverFileBox.setMinWidth(300);

        Button connectBtn = new Button("Connect");
        connectBtn.setOnAction(event -> {
            Console.announce("Connecting...");
            try {
                App.connect();
                Console.debug("Connected");
                App.startThreads();
            } catch (ClientIOException ex) {
                System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            Console.announce("Connected.");
        });

        Button uploadBtn = new Button("Upload >");
        uploadBtn.setOnAction(event -> {
            FilePathTreeItem localItem = (FilePathTreeItem) treeViewLocal.getSelectionModel().getSelectedItem();
            FilePathTreeItem serverItem = (FilePathTreeItem) treeViewServer.getSelectionModel().getSelectedItem();

            if (localItem == null || localItem.getIsDirectory()) {
                Console.error("Please select a local file (not a folder).");
                return;
            }

            if (serverItem == null || !serverItem.getIsDirectory()) {
                Console.error("Please select a server folder (not a file).");
                return;
            }

            String filePath = localItem.getFilePath();
            String fileName = Paths.get(filePath).getFileName().toString();
            String folderPath = serverItem.getFilePath();

            String command = (new PutCmd().getName()) + " " + filePath + " " + folderPath + "/" + fileName;
            Console.debug(command);
            try {
                CommandHandler.getInstance().executeCommand(App.getClient(), command);
            } catch (ClientIOException | InvalidArgumentsException e) {
                e.announceError();
            }
        });

        VBox controlButtonsBox = new VBox();
        controlButtonsBox.getChildren().addAll(connectBtn, uploadBtn);

        // * File Transfer Box (Top) */
        HBox fileTransferBox = new HBox(localFileBox, controlButtonsBox, serverFileBox);

        TextArea responseTA = new TextArea();
        responseTA.setEditable(false);
        responseTA.setWrapText(true);
        responseTA.setFont(new Font("Courier New Bold", 12));
        Console.setResponseTA(responseTA);

        TextField commandTF = new TextField();

        Button sendCommandBtn = new Button("Send");
        sendCommandBtn.setOnAction(event -> {
            try {
                CommandHandler.getInstance().executeCommand(App.getClient(), commandTF.getText());
            } catch (ClientIOException | InvalidArgumentsException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            commandTF.clear();
        });

        HBox commandBox = new HBox(commandTF, sendCommandBtn);

        // * Server Response Box (Bottom) */
        VBox serverResponseBox = new VBox(responseTA, commandBox);

        VBox root = new VBox(fileTransferBox, serverResponseBox);

        Scene mainScene = new Scene(root);
        mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("FTP Client");
        primaryStage.setOnCloseRequest(event -> {
            App.shutdown();
        });
        primaryStage.show();
    }

    private static List<String> getCommandOutput(String command) {
        List<String> messages = new ArrayList<>();
        try {
            Console.addListener(msg -> {
                messages.add(msg);
            });
            CommandHandler.getInstance().executeCommand(App.getClient(), command);

            Thread.sleep(200);
        } catch (ClientIOException ex) {
            System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (InvalidArgumentsException ex) {
            System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (InterruptedException ex) {
            System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return messages;
    }

    public static void main(String[] args) {
        launch();
    }

}
