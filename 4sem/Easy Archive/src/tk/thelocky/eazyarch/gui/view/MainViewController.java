package tk.thelocky.eazyarch.gui.view;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import tk.thelocky.eazyarch.Main;
import tk.thelocky.eazyarch.control.SystemFileContainer;
import tk.thelocky.eazyarch.stream.ArchiveStream;
import tk.thelocky.eazyarch.util.Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainViewController {
    @FXML
    private AnchorPane leftTable;
    private Callback closeLeftTable = () -> {};
    @FXML
    private AnchorPane rightTable;
    private Callback closeRightTable = () -> {};

    Main mainApp;

    @FXML
    private void initialize() {

    }

    private AnchorPane getHalf(boolean left) {
        return left ? leftTable : rightTable;
    }

    void showArchiveFileTable(ArchiveStream arch, boolean toLeft) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ArchiveFileTable.fxml"));
            AnchorPane pane = loader.load();
            AnchorPane half = getHalf(toLeft);

            half.getChildren().clear();
            half.getChildren().add(pane);
            pane.prefWidthProperty().bind(half.widthProperty());
            pane.prefHeightProperty().bind(half.heightProperty());

            ArchiveFileTableController controller = loader.getController();
            controller.setMainView(this, toLeft);
            controller.setArchive(arch);

            if (toLeft) {
                closeLeftTable = arch::close;
            } else {
                closeRightTable = arch::close;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showSystemFileTable(Path p, boolean toLeft) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("SystemFileTable.fxml"));
            AnchorPane pane = loader.load();
            AnchorPane half = getHalf(toLeft);

            half.getChildren().add(pane);
            pane.prefWidthProperty().bind(half.widthProperty());
            pane.prefHeightProperty().bind(half.heightProperty());

            p = p.toFile().exists() ? p : Paths.get("").toAbsolutePath();
            SystemFileTableController controller = loader.getController();
            SystemFileContainer sfc = new SystemFileContainer(p);
            controller.setContainer(sfc);
            controller.setMainView(this, toLeft);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainApp(@NotNull Main app) {
        mainApp = app;

        mainApp.getPrimaryStage().setOnCloseRequest(event -> {
            closeLeftTable.call();
            closeRightTable.call();
        });

        showSystemFileTable(Paths.get("").toAbsolutePath(), true);
        showSystemFileTable(Paths.get("").toAbsolutePath(), false);
    }
}
