package com.phuc.ftpclient;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.phuc.ftpclient.commands.CommandHandler;
import com.phuc.ftpclient.commands.LoginCmd;
import com.phuc.ftpclient.commands.MLSDCmd;
import com.phuc.ftpclient.commands.PutCmd;
import com.phuc.ftpclient.exception.ClientIOException;
import com.phuc.ftpclient.exception.InvalidArgumentsException;
import com.phuc.ftpclient.exception.ServerException;
import com.phuc.ftpclient.gui.FTPTreeItem;
import com.phuc.ftpclient.gui.FilePathTreeItem;
import com.phuc.ftpclient.state.State;
import com.phuc.ftpclient.state.StateMachine;
import com.phuc.ftpclient.threads.PassiveSocketThread;
import com.phuc.ftpclient.threads.ReceiveMessageThread;
import com.phuc.ftpclient.util.Console;
import com.phuc.ftpclient.util.Constants;
import com.phuc.ftpclient.util.MLSDEntry;
import com.phuc.ftpclient.util.ServerResponse;

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

    private TextField hostNameTF;
    private TextField portTF;
    private HBox connectionBox;
    private TextField usernameTF;
    private TextField passwordTF;
    private HBox loginBox;
    private TreeView<String> treeViewLocal;
    private FilePathTreeItem rootNodeLocal;
    private final ImageView FOLDER_IMAGE = new ImageView("img/folder.png");
    private VBox localFileBox;
    private TreeView<String> treeViewServer;
    private FTPTreeItem rootNodeServer;
    private VBox serverFileBox;
    private Button connectBtn;
    private Button uploadBtn;
    private Button downloadBtn;
    private VBox controlButtonsBox;
    private HBox fileTransferBox;
    private TextArea responseTA;
    private TextField commandTF;
    private Button sendCommandBtn;
    private HBox commandBox;
    private VBox serverResponseBox;
    private VBox root;
    private Scene mainScene;

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

                }
            }
        });

        StateMachine.getInstance().switchState(State.INIT);

        // new Thread(() -> {
        // try {
        // while (!App.getIsRunning()) {

        // }
        // Thread.sleep(500);
        // List<String> workingDir = getCommandOutput(new
        // PrintWorkingDirCmd().getName());
        // for (String s : workingDir) {
        // Console.debug(s);
        // }
        // } catch (InterruptedException ex) {
        // System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR,
        // (String) null, ex);
        // }
        // }).start();
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

    }

    // private static List<String> getCommandOutput(String command) {
    // List<String> messages = new ArrayList<>();
    // try {
    // Console.addListener(msg -> {
    // messages.add(msg);
    // });
    // CommandHandler.getInstance().executeCommand(App.getClient(), command);

    // Thread.sleep(200);
    // } catch (ClientIOException ex) {
    // System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR,
    // (String) null, ex);
    // } catch (InvalidArgumentsException ex) {
    // System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR,
    // (String) null, ex);
    // } catch (InterruptedException ex) {
    // System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR,
    // (String) null, ex);
    // }
    // return messages;
    // }

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
                            App.getClient().sendMessage("PWD");
                            ServerResponse tempResponse = ReceiveMessageThread.receiveMessages();

                            Console.debug("APoKSDPIEOusfALUE" + tempResponse.getMessage());

                            App.getClient().sendMessage("CWD " + folder.getFilePath());
                            ReceiveMessageThread.receiveMessages();

                            new MLSDCmd().execute(new ArrayList<>());
                            PassiveSocketThread t = CommandHandler.getInstance().getPasvSocketThread();
                            String mlsdResponses = t.getMlsdResponse();
                            // new PASVCmd().execute(new ArrayList<>());
                            // ServerResponse pasvResponse = ReceiveMessageThread.receiveMessages();
                            // PassiveSocketThread t = new PassiveSocketThread(pasvResponse, Purpose.MLSD);
                            // t.start();
                            // App.getClient().sendMessage("MLSD");
                            // ReceiveMessageThread.receiveMessages();
                            // String response = t.getMlsdResponse();

                            List<MLSDEntry> entries = new ArrayList<>();

                            for (String line : mlsdResponses.split("\n")) {
                                if (!line.isBlank()) {
                                    entries.add(new MLSDEntry(line));
                                }
                            }

                            while (!getCurrentDir().equals(currentDirectory)) {
                                Console.debug("????????? CWD ../");
                                App.getClient().sendMessage("CWD ../");
                                ReceiveMessageThread.receiveMessages();
                            }

                            return entries;
                        } catch (Exception ex) {
                            return Collections.emptyList();
                        }
                    });

            treeViewServer.setRoot(rootNodeServer);
            Console.debug("DONE!");
        } catch (ClientIOException | ServerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

        Path localPath = Paths.get(Constants.LOCAL_DIR);
        rootNodeLocal = new FilePathTreeItem(localPath);
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(localPath)) {
            for (Path fileName : dir) {
                FilePathTreeItem treeNode = new FilePathTreeItem(fileName);
                rootNodeLocal.getChildren().add(treeNode);
            }
        } catch (IOException ex) {
            System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        treeViewLocal = new TreeView<>(rootNodeLocal);

        localFileBox = new VBox(treeViewLocal);
        localFileBox.setMinWidth(300);

        // TODO: TreeViewServer

        treeViewServer = new TreeView<>();

        serverFileBox = new VBox(treeViewServer);
        serverFileBox.setMinWidth(300);

        // INFO: Only connect to server if INIT has completed.
        connectBtn = new Button("Connect");
        connectBtn.setOnAction(event -> {
            if (StateMachine.getInstance().getCurrentState() != State.CONN) {
                return;
            }

            try {
                Console.announce("Connecting...");
                App.connect(hostNameTF.getText(), Integer.parseInt(portTF.getText()));
                ReceiveMessageThread.receiveMessages();
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
            } catch (IOException e) {
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
            } catch (ClientIOException | InvalidArgumentsException e) {
                e.announceError();
            } catch (ServerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        downloadBtn = new Button("< Download");

        controlButtonsBox = new VBox(connectBtn, uploadBtn, downloadBtn);

        fileTransferBox = new HBox(localFileBox, controlButtonsBox, serverFileBox);

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
            } catch (ClientIOException | InvalidArgumentsException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ServerException ex) {
                System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } catch (IOException ex) {
                System.getLogger(FTPApplication.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
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
            App.shutdown();
        });
        primaryStage.show();
    }

    private void setDisableAllButtons(boolean toSet) {
        uploadBtn.setDisable(toSet);
        downloadBtn.setDisable(toSet);
        sendCommandBtn.setDisable(toSet);
    }

    private String getCurrentDir() throws ClientIOException, ServerException, IOException {
        App.getClient().sendMessage("PWD");
        ServerResponse serverResponse = ReceiveMessageThread.receiveMessages();

        return serverResponse.getMessage().split("\"")[1];
    }

}
