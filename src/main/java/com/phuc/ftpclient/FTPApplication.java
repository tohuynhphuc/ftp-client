package com.phuc.ftpclient;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.gui.FilePathTreeItem;
import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;

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
        ImageView image = new ImageView("img/file.png");
        image.setFitWidth(16);
        image.setFitHeight(16);
        Path localPath = Paths.get(Constants.LOCAL_DIR);
        FilePathTreeItem rootNodeLocal = new FilePathTreeItem(localPath);

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
        Path serverPath = Paths.get(Constants.SERVER_DIR);
        FilePathTreeItem rootNodeServer = new FilePathTreeItem(serverPath);

        try (DirectoryStream<Path> dir = Files.newDirectoryStream(serverPath)) {
            for (Path fileName : dir) {
                FilePathTreeItem treeNode = new FilePathTreeItem(fileName);
                rootNodeServer.getChildren().add(treeNode);
            }
        }

        // rootNodeServer.setExpanded(true);

        treeViewServer = new TreeView<>(rootNodeServer);
        VBox serverFileBox = new VBox(treeViewServer);
        serverFileBox.setMinWidth(300);

        Button connectBtn = new Button("Connect");
        connectBtn.setOnAction(event -> {
            Console.announce("Connecting...");
            try {
                App.connect();
                App.startThreads();
            } catch (ClientIOException ex) {
                System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            Console.announce("Connected.");
        });

        VBox controlButtonsBox = new VBox();
        controlButtonsBox.getChildren().addAll(connectBtn);

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

        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
