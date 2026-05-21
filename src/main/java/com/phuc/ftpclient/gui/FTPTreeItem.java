package com.phuc.ftpclient.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.phuc.ftpclient.util.MLSDEntry;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FTPTreeItem extends TreeItem<String> {

    public static final Image FOLDER_COLLAPSE_IMG = new Image("img/folder.png");
    public static final Image FOLDER_EXPAND_IMG = new Image("img/open-folder.png");
    public static final Image FILE_IMG = new Image("img/file.png");

    private final MLSDEntry node;
    private final Function<FTPTreeItem, List<MLSDEntry>> childLoader;
    private boolean isChildrenLoaded = false;

    public FTPTreeItem(MLSDEntry node, Function<FTPTreeItem, List<MLSDEntry>> childLoader) {
        super(node.getName());

        this.node = node;
        this.childLoader = childLoader;

        ImageView imageView = new ImageView(node.isDirectory() ? FOLDER_COLLAPSE_IMG : FILE_IMG);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        setGraphic(imageView);

        addEventHandler(TreeItem.branchExpandedEvent(), (TreeModificationEvent<String> e) -> {
            FTPTreeItem source = (FTPTreeItem) e.getSource();

            if (!source.getIsDirectory()) {
                return;
            }

            if (source.isExpanded()) {
                ImageView iv = (ImageView) source.getGraphic();
                iv.setImage(FOLDER_EXPAND_IMG);
                iv.setFitWidth(16);
                iv.setFitHeight(16);
            }

            source.loadChildren();
        });

        addEventHandler(TreeItem.branchCollapsedEvent(), (TreeModificationEvent<String> e) -> {
            FTPTreeItem source = (FTPTreeItem) e.getSource();

            if (!source.getIsDirectory()) {
                return;
            }

            if (!source.isExpanded()) {
                ImageView iv = (ImageView) source.getGraphic();
                iv.setImage(FOLDER_COLLAPSE_IMG);
            }
        });
    }

    private synchronized void loadChildren() {
        if (isChildrenLoaded || !node.isDirectory()) {
            return;
        }

        isChildrenLoaded = true;

        List<MLSDEntry> children = childLoader.apply(this);

        Platform.runLater(() -> {
            getChildren().clear();

            if (children == null) {
                return;
            }

            for (MLSDEntry child : children) {
                getChildren().add(new FTPTreeItem(child, childLoader));
            }
        });
    }

    public String getFilePath() {
        LinkedList<String> parts = new LinkedList<>();

        TreeItem<String> current = this;

        while (current != null) {
            String value = current.getValue();

            if (value != null && !value.isBlank() && !value.equals("/")) {
                parts.addFirst(value);
            }

            current = current.getParent();
        }

        return "/" + String.join("/", parts);
    }

    public boolean getIsDirectory() {
        return node.isDirectory();
    }

    @Override
    public boolean isLeaf() {
        return !getIsDirectory();
    }

}