package tk.thelocky.eazyarch.gui.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import org.controlsfx.dialog.Dialogs;
import tk.thelocky.eazyarch.compress.CompressFormat;
import tk.thelocky.eazyarch.control.SystemFileContainer;
import tk.thelocky.eazyarch.gui.model.ArchiveFileModel;
import tk.thelocky.eazyarch.res.R;
import tk.thelocky.eazyarch.stream.ArchiveStream;
import tk.thelocky.eazyarch.stream.InnerTree;
import tk.thelocky.eazyarch.util.Constants;
import tk.thelocky.eazyarch.util.FileIcon;
import tk.thelocky.eazyarch.util.IconAndName;

import java.util.NoSuchElementException;
import java.util.Optional;

public class ArchiveFileTableController {
    @FXML
    private TableView<ArchiveFileModel> fileTable;
    @FXML
    private TableColumn<ArchiveFileModel, IconAndName> fileName;
    @FXML
    private TableColumn<ArchiveFileModel, Long> realSize;
    @FXML
    private TableColumn<ArchiveFileModel, Long> compressSize;
    @FXML
    private TableColumn<ArchiveFileModel, String> compressType;
    @FXML
    private TableColumn<ArchiveFileModel, String> fileType;

    //buttons
    @FXML
    private Button up_button;
    @FXML
    private Button new_button;
    @FXML
    private Button delete_button;

    private MainViewController mainView;
    private boolean isLeft;

    private ArchiveStream archive;
    private InnerTree.Iterator currentDirectory;
    private ObservableList<ArchiveFileModel> list;

    private class DragRow extends TableRow<ArchiveFileModel> {
        public DragRow() {
            this.setOnDragDetected(event -> {

            });

            this.setOnDragDone(event -> {

            });

            this.setOnDragOver(event -> {

            });

            this.setOnDragDropped(event -> {

            });
        }
    }

    @FXML
    private void initialize() {
        archive = null;
        currentDirectory = null;
        list = FXCollections.observableArrayList();

        Image up_img = new Image(getClass().getResourceAsStream("../img/folder_up.png"));
        Image new_img = new Image(getClass().getResourceAsStream("../img/folder_new.png"));
        Image del_img = new Image(getClass().getResourceAsStream("../img/delete.png"));
        up_button.setGraphic(new ImageView(up_img));
        new_button.setGraphic(new ImageView(new_img));
        delete_button.setGraphic(new ImageView(del_img));

        fileTable.setRowFactory(row -> new DragRow());
        fileName.setCellValueFactory(cellData -> cellData.getValue().iconAndNameProperty());
        fileName.setCellFactory(cell -> new TableCell<ArchiveFileModel, IconAndName>() {
            @Override
            protected void updateItem(IconAndName item, boolean empty) {
                if (item != null) {
                    HBox hBox = new HBox();
                    Label name = new Label(item.getName());
                    ImageView icon = item.getIcon();
                    hBox.getChildren().addAll(icon, name);
                    hBox.setSpacing(4);
                    setGraphic(hBox);
                } else {
                    setGraphic(null);
                }
            }
        });
        realSize.setCellValueFactory(cellData -> cellData.getValue().realSizeProperty());
        compressSize.setCellValueFactory(cellData -> cellData.getValue().compressSizeProperty());
        compressType.setCellValueFactory(cellData -> cellData.getValue().compressTypeProperty());
        fileType.setCellValueFactory(cellData -> cellData.getValue().systemFileTypeProperty());
        fileTable.setItems(list);
    }

    public void setArchive(ArchiveStream stream) {
        archive = stream;
        if (archive != null) {
            currentDirectory = archive.getFileTree().createIterator();
            updateList();
        }
    }

    public void setMainView(MainViewController mainView, boolean isLeft) {
        this.mainView = mainView;
        this.isLeft = isLeft;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        if (archive == null || currentDirectory == null) return;
        if (event.getClickCount() > 1) { //double click
            ArchiveFileModel item = fileTable.getSelectionModel().getSelectedItem();
            if ((item != null) && (item.isFolder())) {
                currentDirectory.in(item.getIconAndName().getName());
                updateList();
            }
        }
    }

    @FXML
    private void handleUpButtonClick() {
        if (archive == null || currentDirectory == null) return;
        if (currentDirectory.isRoot()) {
            mainView.showSystemFileTable(archive.getPathToFile(), isLeft);
            archive.close();
        } else {
            currentDirectory.out();
            updateList();
        }
    }

    @FXML
    private void handleNewFolderClick() {
        if (archive == null || currentDirectory == null) return;
        try {
            TextInputDialog inputDialog = new TextInputDialog(R.get("dia:new_dir:default"));
            inputDialog.setTitle(R.get("program_name"));
            inputDialog.setHeaderText(R.get("dia:new_dir:title"));
            inputDialog.setContentText(R.get("dia:new_dir"));
            Optional<String> result = inputDialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                archive.newFolder(currentDirectory, result.get());
                updateList();
            }
        } catch (NoSuchElementException e) {
            System.out.println("cancel");
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (archive == null || currentDirectory == null) return;
        ArchiveFileModel item = fileTable.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (item.isFolder()) {
                currentDirectory.in(item.getIconAndName().getName());
                archive.deleteFolder(currentDirectory);
                updateList();
            } else {
                archive.deleteFile(currentDirectory, item.getIconAndName().getName());
                updateList();
            }
        }
    }

    private void updateList() {
        list.clear();
        if (currentDirectory == null) return;
        for (InnerTree obj : currentDirectory.getList()) {
            String type = (obj.getType() == 2) ?
                    R.get("folder_type") : FileIcon.getExtension(obj.getName());
            ArchiveFileModel object = new ArchiveFileModel(obj.getName(), type,
                    CompressFormat.formatToString(obj.getCompressType()), obj.getRealSize(), obj.getPackSize());
            list.add(object);
        }
    }

}
