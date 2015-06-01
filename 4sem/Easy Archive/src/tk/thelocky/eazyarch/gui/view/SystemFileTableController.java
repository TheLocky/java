package tk.thelocky.eazyarch.gui.view;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import tk.thelocky.eazyarch.control.SystemFileContainer;
import tk.thelocky.eazyarch.gui.model.SystemFileModel;
import tk.thelocky.eazyarch.res.R;
import tk.thelocky.eazyarch.stream.ArchiveStream;
import tk.thelocky.eazyarch.util.FileIcon;
import tk.thelocky.eazyarch.util.IconAndName;
import tk.thelocky.eazyarch.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class SystemFileTableController {
    @FXML
    private TableView<SystemFileModel> fileTable;
    @FXML
    private TableColumn<SystemFileModel, IconAndName> fileName;
    @FXML
    private TableColumn<SystemFileModel, String> fileType;
    @FXML
    private TableColumn<SystemFileModel, Long> fileSize;

    //buttons
    @FXML
    private Button up_button;
    @FXML
    private Button new_arch_button;

    private SystemFileContainer sfc;
    private boolean isLeft;
    private MainViewController mainView;

    private class DragRow extends TableRow<SystemFileModel> {
        public DragRow() {
            this.setOnDragDetected(event -> {
                SystemFileModel item = fileTable.getSelectionModel().getSelectedItem();
                if ((item != null)) {

                }
            });

            this.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.getContent(Constants.dragFormat).toString().compareTo("ACCEPT") == 0) {

                }
            });
        }
    }

    @FXML
    private void initialize() {
        mainView = null;

        Image up_img = new Image(getClass().getResourceAsStream("../img/folder_up.png"));
        Image new_arch_img = new Image(getClass().getResourceAsStream("../img/archive_new.png"));
        up_button.setGraphic(new ImageView(up_img));
        new_arch_button.setGraphic(new ImageView(new_arch_img));

        fileTable.setRowFactory(param -> new DragRow());
        fileName.setCellValueFactory(cellData -> cellData.getValue().iconAndNameProperty());
        fileName.setCellFactory(cell -> new TableCell<SystemFileModel, IconAndName>() {
            @Override
            protected void updateItem(IconAndName item, boolean empty) {
                if (item != null) {
                    HBox hBox = new HBox();
                    hBox.setSpacing(4);
                    Label name = new Label(item.getName());
                    ImageView ico = item.getIcon();
                    hBox.getChildren().addAll(ico, name);
                    setGraphic(hBox);
                } else
                    setGraphic(null);
            }
        });
        fileType.setCellValueFactory(cellData -> cellData.getValue().fileTypeProperty());
        fileSize.setCellValueFactory(cellData -> cellData.getValue().fileSizeProperty());
    }

    public void setContainer(@NotNull SystemFileContainer container) {
        sfc = container;
        fileTable.setItems(sfc.getFiles());
    }

    public void setMainView(MainViewController mainView, boolean isLeft) {
        this.mainView = mainView;
        this.isLeft = isLeft;
    }

    @FXML
    private void handleLineDoubleClick(MouseEvent e) {
        if (e.getClickCount() == 2) {
            SystemFileModel item = fileTable.getSelectionModel().getSelectedItem();
            if ((item != null)) {
                if (item.isFolder()) {
                    sfc.in(item.getIconAndName().getName());
                } else {
                    String ext = FileIcon.getExtension(item.getIconAndName().getName());
                    if (ext != null && ext.compareTo("esa") == 0) {
                        try {
                            File f = new File(sfc.getCurDirAbsolutePath() + "\\" + item.getIconAndName().getName());
                            ArchiveStream arch = ArchiveStream.openArchive(f);
                            mainView.showArchiveFileTable(arch, isLeft);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void handleUpButtonClick() {
        sfc.out();
    }

    @FXML
    private void handleNewArchiveButtonClick() {
        TextInputDialog getName = new TextInputDialog(R.get("dia:new_arch:default"));
        getName.setTitle(R.get("program_name"));
        getName.setHeaderText(R.get("dia:new_arch:title"));
        getName.setContentText(R.get("dia:new_arch"));
        Optional<String> res = getName.showAndWait();
        if (res.isPresent() && !res.get().isEmpty()) {
            File f = new File(sfc.getCurDirAbsolutePath() + "\\" + res.get() + ".esa");
            Alert replaceQuestion = new Alert(Alert.AlertType.CONFIRMATION);
            replaceQuestion.setTitle(R.get("program_name"));
            replaceQuestion.setHeaderText(R.get("dia:replace:title"));
            replaceQuestion.setContentText(R.get("dia:replace"));
            if (!f.exists() || replaceQuestion.showAndWait().get() == ButtonType.OK) {
                try {
                    ArchiveStream newArch = ArchiveStream.newArchive(f);
                    newArch.close();
                    sfc.updateList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
