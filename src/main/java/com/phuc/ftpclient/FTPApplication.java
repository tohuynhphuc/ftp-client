package com.phuc.ftpclient;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.commands.DeleteCmd;
import com.phuc.ftpclient.commands.GetCmd;
import com.phuc.ftpclient.commands.LoginCmd;
import com.phuc.ftpclient.commands.MLSDCmd;
import com.phuc.ftpclient.commands.MakeDirCmd;
import com.phuc.ftpclient.commands.PutCmd;
import com.phuc.ftpclient.commands.RemoveDirCmd;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.gui.FTPTreeItem;
import com.phuc.ftpclient.gui.FilePathTreeItem;
import com.phuc.ftpclient.state.State;
import com.phuc.ftpclient.state.StateMachine;
import com.phuc.ftpclient.threads.PassiveSocketThread;
import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;
import com.phuc.ftpclient.util.MLSDEntry;
import com.phuc.ftpclient.util.ReceiveMessage;
import com.phuc.ftpclient.util.ServerResponse;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class FTPApplication extends Application {

    private TextField hostNameTF;
    private TextField portTF;
    private HBox connectionBox;
    private TextField usernameTF;
    private TextField passwordTF;
    private HBox loginBox;
    private TreeView<String> treeViewLocal;
    private final ImageView FOLDER_IMAGE = new ImageView("img/folder.png");
    private VBox localFileBox;
    private TreeView<String> treeViewServer;
    private FTPTreeItem rootNodeServer;
    private VBox serverFileBox;
    private Button connectBtn;
    private Button uploadBtn;
    private Button downloadBtn;
    private Button createFolderBtn;
    private Button deleteBtn;
    private VBox controlButtonsBox;
    private HBox fileTransferBox;
    private TextArea responseTA;
    private TextField commandTF;
    private Button sendCommandBtn;
    private HBox commandBox;
    private VBox serverResponseBox;
    private VBox root;
    private Scene mainScene;

    private static Client client;

    public static void connect(String hostName, int port) throws ClientIOException {
        client = new Client();
        client.connect(hostName, port);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StateMachine.getInstance().addListener(state -> {
            switch (state) {
                case INIT -> {
                    setUpStage(primaryStage);
                    StateMachine.getInstance().switchState(State.CONN);
                }
                case CONN -> {

                }
                case COMD -> {
                    setUpTreeViewServer();
                }
                case SHUT -> {
                    primaryStage.close();
                }
            }
        });

        StateMachine.getInstance().switchState(State.INIT);
    }

    public static void main(String[] args) {
        launch();
    }

    @SuppressWarnings("UseSpecificCatch")
    private void setUpTreeViewServer() {
        try {
            String currentDirectory = getCurrentDir();

            rootNodeServer = new FTPTreeItem(new MLSDEntry("dir", currentDirectory),
                    folder -> {
                        try {
                            FTPApplication.getClient().sendMessage("CWD " + folder.getFilePath());
                            ReceiveMessage.receiveMessages();

                            new MLSDCmd().execute(new ArrayList<>());
                            PassiveSocketThread t = CommandHandler.getInstance().getPasvSocketThread();
                            String mlsdResponses = t.getMlsdResponse();

                            List<MLSDEntry> entries = new ArrayList<>();

                            for (String line : mlsdResponses.split("\n")) {
                                if (!line.isBlank()) {
                                    entries.add(new MLSDEntry(line));
                                }
                            }

                            while (!getCurrentDir().equals(currentDirectory)) {
                                FTPApplication.getClient().sendMessage("CWD ../");
                                ReceiveMessage.receiveMessages();
                            }

                            return entries;
                        } catch (Exception ex) {
                            return Collections.emptyList();
                        }
                    });

            treeViewServer.setRoot(rootNodeServer);
        } catch (ClientIOException | ServerException e) {
            e.announceError();
        }
    }

    private void setUpStage(Stage primaryStage) {
        hostNameTF = new TextField(Constants.HOST_NAME);
        portTF = new TextField(Constants.PORT + "");
        connectionBox = new HBox(hostNameTF, portTF);
        usernameTF = new TextField();
        passwordTF = new TextField();
        loginBox = new HBox(usernameTF, passwordTF);

        FOLDER_IMAGE.setFitWidth(16);
        FOLDER_IMAGE.setFitHeight(16);

        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            System.out.println(p);
        }

        TreeItem<String> fakeRoot = new TreeItem<>("Computer");
        Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
        for (Path name : rootDirectories) {
            FilePathTreeItem treeNode = new FilePathTreeItem(name);
            fakeRoot.getChildren().add(treeNode);
        }

        treeViewLocal = new TreeView<>(fakeRoot);
        treeViewLocal.setShowRoot(false);

        localFileBox = new VBox(treeViewLocal);
        localFileBox.setMinWidth(300);

        treeViewServer = new TreeView<>();

        serverFileBox = new VBox(treeViewServer);
        serverFileBox.setMinWidth(300);

        // INFO: Only connect to server if INIT has completed.
        connectBtn = new Button("Connect");
        connectBtn.setOnAction(event ->

        {
            if (StateMachine.getInstance().getCurrentState() != State.CONN) {
                return;
            }

            try {
                Console.announce("Connecting...");
                FTPApplication.connect(hostNameTF.getText(), Integer.parseInt(portTF.getText()));
                ReceiveMessage.receiveMessages();
                boolean isLoginSuccess = new LoginCmd()
                        .execute(new ArrayList<>(List.of(usernameTF.getText(), passwordTF.getText())));
                if (isLoginSuccess) {
                    Console.announce("Connected");
                } else {
                    throw new ServerException("Unknown error while loggin in. Please check details carefully.");
                }
            } catch (ClientIOException | ServerException | InvalidArgumentsException ex) {
                ex.announceError();
                return;
            }

            setDisableAllButtons(false);

            StateMachine.getInstance().switchState(State.COMD);
        });

        uploadBtn = new Button("Upload >");
        uploadBtn.setOnAction(event -> {
            if (StateMachine.getInstance().getCurrentState() != State.COMD) {
                return;
            }

            FilePathTreeItem localItem = (FilePathTreeItem) treeViewLocal.getSelectionModel().getSelectedItem();
            FTPTreeItem serverItem = (FTPTreeItem) treeViewServer.getSelectionModel().getSelectedItem();

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
                CommandHandler.getInstance().executeCommand(command);
            } catch (ClientIOException | InvalidArgumentsException | ServerException e) {
                e.announceError();
            }
        });

        downloadBtn = new Button("< Download");
        downloadBtn.setOnAction(event -> {
            if (StateMachine.getInstance().getCurrentState() != State.COMD) {
                return;
            }

            FilePathTreeItem localItem = (FilePathTreeItem) treeViewLocal.getSelectionModel().getSelectedItem();
            FTPTreeItem serverItem = (FTPTreeItem) treeViewServer.getSelectionModel().getSelectedItem();

            if (localItem == null || !localItem.getIsDirectory()) {
                Console.error("Please select a local folder (not a file).");
                return;
            }

            if (serverItem == null || serverItem.getIsDirectory()) {
                Console.error("Please select a server file (not a folder).");
                return;
            }

            String folderPath = localItem.getFilePath();
            String filePath = serverItem.getFilePath();
            String fileName = serverItem.getValue();

            String command = (new GetCmd().getName()) + " " + filePath + " " + folderPath + "/" + fileName;
            Console.debug(command);
            try {
                CommandHandler.getInstance().executeCommand(command);
            } catch (ClientIOException | InvalidArgumentsException | ServerException e) {
                e.announceError();
            }
        });

        createFolderBtn = new Button("Create Folder");
        createFolderBtn.setOnAction(event -> {
            if (StateMachine.getInstance().getCurrentState() != State.COMD) {
                return;
            }

            FTPTreeItem serverItem = (FTPTreeItem) treeViewServer.getSelectionModel().getSelectedItem();

            if (serverItem == null || !serverItem.getIsDirectory()) {
                Console.error("Please select a server folder (not a file).");
                return;
            }

            String folderPath = serverItem.getFilePath();

            if (commandTF.getText().isBlank()) {
                Console.error("Please input the new folder name in the Command box.");
                return;
            }

            String command = (new MakeDirCmd().getName()) + " " + folderPath + "/" + commandTF.getText();
            Console.debug(command);
            try {
                CommandHandler.getInstance().executeCommand(command);
            } catch (ClientIOException | InvalidArgumentsException | ServerException e) {
                e.announceError();
            }
        });

        deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(event -> {
            if (StateMachine.getInstance().getCurrentState() != State.COMD) {
                return;
            }

            FTPTreeItem serverItem = (FTPTreeItem) treeViewServer.getSelectionModel().getSelectedItem();

            if (serverItem == null) {
                Console.error("Please select a server file or folder.");
                return;
            }

            String filePath = serverItem.getFilePath();

            String command;
            if (serverItem.getIsDirectory()) {
                command = (new RemoveDirCmd().getName()) + " " + filePath;
            } else {
                command = (new DeleteCmd().getName()) + " " + filePath;
            }

            Console.debug(command);
            try {
                CommandHandler.getInstance().executeCommand(command);
            } catch (ClientIOException | InvalidArgumentsException | ServerException e) {
                e.announceError();
            }
        });

        controlButtonsBox = new VBox(connectBtn, uploadBtn, downloadBtn, createFolderBtn, deleteBtn);

        fileTransferBox = new HBox(localFileBox, controlButtonsBox, serverFileBox);

        controlButtonsBox.setAlignment(Pos.TOP_CENTER);

        responseTA = new TextArea();
        responseTA.setEditable(false);
        responseTA.setWrapText(true);
        responseTA.setFont(new Font("Courier New Bold", 12));
        Console.setResponseTA(responseTA);

        commandTF = new TextField();

        sendCommandBtn = new Button("Send");
        sendCommandBtn.setOnAction(event -> {
            try {
                CommandHandler.getInstance().executeCommand(commandTF.getText());
            } catch (ClientIOException | InvalidArgumentsException | ServerException e) {
                e.announceError();
            }
            commandTF.clear();
        });

        commandBox = new HBox(commandTF, sendCommandBtn);

        // * Server Response Box (Bottom) */
        serverResponseBox = new VBox(responseTA, commandBox);

        setDisableAllButtons(true);

        root = new VBox(connectionBox, loginBox, fileTransferBox, serverResponseBox);

        mainScene = new Scene(root);
        mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("FTP Client");
        primaryStage.setOnCloseRequest(event -> {
            StateMachine.getInstance().switchState(State.SHUT);
            try {
                if (client != null)
                    client.closeConnection();
            } catch (ClientIOException e) {
                e.announceError();
            }
            System.exit(0);
        });
        primaryStage.show();
    }

    private void setDisableAllButtons(boolean toSet) {
        uploadBtn.setDisable(toSet);
        downloadBtn.setDisable(toSet);
        sendCommandBtn.setDisable(toSet);
    }

    private String getCurrentDir() throws ClientIOException, ServerException {
        FTPApplication.getClient().sendMessage("PWD");
        ServerResponse serverResponse = ReceiveMessage.receiveMessages();

        return serverResponse.getMessage().split("\"")[1];
    }

    public static Client getClient() {
        return client;
    }

}
