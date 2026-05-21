package com.phuc.ftpclient.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FilePathTreeItem extends TreeItem<String> {

    public static final Image FOLDER_COLLAPSE_IMG = new Image("img/folder.png");
    public static final Image FOLDER_EXPAND_IMG = new Image("img/open-folder.png");
    public static final Image FILE_IMG = new Image("img/file.png");

    private static final WatchService WATCH_SERVICE = createWatchService();
    private static final Map<WatchKey, FilePathTreeItem> KEY_TO_ITEM = new ConcurrentHashMap<>();
    private static volatile boolean watcherThreadStarted = false;

    private Path path;
    private boolean isDirectory;
    private WatchKey watchKey;

    public FilePathTreeItem(Path file) {
        super(file.toString());

        path = file;

        ImageView imageView;
        if (Files.isDirectory(file)) {
            isDirectory = true;
            imageView = new ImageView(FOLDER_COLLAPSE_IMG);
        } else {
            isDirectory = false;
            imageView = new ImageView(FILE_IMG);
        }
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);

        // Separator is a /
        if (!file.toString().endsWith(File.separator)) {
            String value = file.toString();
            int indexOf = value.lastIndexOf(File.separator);
            if (indexOf > 0) {
                setValue(value.substring(indexOf + 1));
            } else {
                setValue(value);
            }
        }

        addEventHandler(TreeItem.branchExpandedEvent(), (TreeModificationEvent<String> e) -> {
            FilePathTreeItem source = (FilePathTreeItem) e.getSource();

            if (!source.getIsDirectory()) {
                return;
            }

            if (source.isExpanded()) {
                ImageView iv = (ImageView) source.getGraphic();
                iv.setImage(FOLDER_EXPAND_IMG);
                iv.setFitWidth(16);
                iv.setFitHeight(16);
            }

            source.refresh();
            source.registerWatcher();
        });

        addEventHandler(TreeItem.branchCollapsedEvent(), (TreeModificationEvent<String> e) -> {
            FilePathTreeItem source = (FilePathTreeItem) e.getSource();

            if (!source.getIsDirectory()) {
                return;
            }

            if (!source.isExpanded()) {
                ImageView iv = (ImageView) source.getGraphic();
                iv.setImage(FOLDER_COLLAPSE_IMG);
                source.unregisterWatcher();
            }
        });
    }

    public synchronized void refresh() {
        try {
            for (TreeItem<String> child : new ArrayList<>(getChildren())) {
                if (child instanceof FilePathTreeItem fileChild) {
                    fileChild.unregisterWatcher();
                }
            }
            getChildren().clear();

            try (DirectoryStream<Path> dir = Files.newDirectoryStream(path)) {
                for (Path _file : dir) {
                    FilePathTreeItem treeNode = new FilePathTreeItem(_file);
                    getChildren().add(treeNode);
                }
            }
        } catch (IOException e) {

        }
    }

    private synchronized void registerWatcher() {
        if (watchKey != null) {
            return;
        }

        try {
            watchKey = path.register(WATCH_SERVICE, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            KEY_TO_ITEM.put(watchKey, this);
            startWatcherThread();
        } catch (IOException ex) {
            System.getLogger(FilePathTreeItem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private synchronized void unregisterWatcher() {
        if (watchKey != null) {
            KEY_TO_ITEM.remove(watchKey);
            watchKey.cancel();
            watchKey = null;
        }
    }

    private static WatchService createWatchService() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void startWatcherThread() {
        if (watcherThreadStarted) {
            return;
        }

        watcherThreadStarted = true;

        Thread watcherThread = new Thread(() -> {
            while (true) {
                WatchKey key;

                try {
                    key = WATCH_SERVICE.take();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }

                FilePathTreeItem item = KEY_TO_ITEM.get(key);

                if (item != null) {
                    if (!key.pollEvents().isEmpty()) {
                        Platform.runLater(item::refresh);
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    KEY_TO_ITEM.remove(key);
                }
            }
        });

        watcherThread.start();
    }

    public boolean getIsDirectory() {
        return isDirectory;
    }

    public String getFilePath() {
        return path.toString();
    }

    @Override
    public boolean isLeaf() {
        return !getIsDirectory();
    }

}