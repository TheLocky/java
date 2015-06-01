package tk.thelocky.eazyarch;

import com.sun.istack.internal.NotNull;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import tk.thelocky.eazyarch.gui.view.CompressViewController;
import tk.thelocky.eazyarch.gui.view.MainViewController;
import tk.thelocky.eazyarch.compress.CompressThread;
import tk.thelocky.eazyarch.res.R;
import tk.thelocky.eazyarch.util.Converting;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Main extends Application {
    Stage primaryStage;

    public Main() {
        R.initialize();
        String libDir = Paths.get("lib").toAbsolutePath().toString();
        Runtime.getRuntime().load(libDir + "/NativeFS.dll");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Easy Archive");

        //pack
        /*ArchiveStream archive = ArchiveStream.newArchive(new File("tests/testArch.esa"));
        CompressThread compress = new CompressThread(archive, CompressFormat.DO_COMPRESS);
        File[] paths = new File[1];
        paths[0] = new File("src");
        compress.setPaths("", new String[0], paths);
        showCompressView(archive.getName(), compress);*/

        //unpack
        /*ArchiveStream archive = ArchiveStream.openArchive(new File("tests/testArch.esa"));
        CompressThread compress = new CompressThread(archive, CompressFormat.DO_DECOMPRESS);
        File[] paths = new File[1];
        paths[0] = new File("tests");
        InnerTree.Iterator iter = archive.getFileTree().createIterator();
        String[] archivePaths = iter.getAllFilesPaths();
        compress.setPaths(iter.getCurPathToRoot(), archivePaths, paths);
        showCompressView(archive.getName(), compress);*/

        //big pack
        /*ArchiveStream archive = ArchiveStream.newArchive(new File("testBigArch.esa"));
        CompressThread compress = new CompressThread(archive, CompressFormat.DO_COMPRESS);
        File[] paths = new File[1];
        paths[0] = new File("tests/test.jar");
        compress.setPaths("", new String[0], paths);
        showCompressView(archive.getName(), compress);*/

        //big unpack
        /*ArchiveStream archive = ArchiveStream.openArchive(new File("testBigArch.esa"));
        CompressThread compress = new CompressThread(archive, CompressFormat.DO_DECOMPRESS);
        File[] paths = new File[1];
        paths[0] = new File("tests/out");
        InnerTree.Iterator iter = archive.getFileTree().createIterator();
        String[] archivePaths = iter.getAllFilesPaths();
        compress.setPaths(iter.getCurPathToRoot(), archivePaths, paths);
        showCompressView(archive.getName(), compress);*/

        showMainView();

        //System.exit(0);
    }

    private void showCompressView(@NotNull String archName, @NotNull CompressThread thread) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("gui/view/CompressView.fxml"));
            AnchorPane pane = loader.load();

            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);

            CompressViewController controller = loader.getController();
            controller.setThread(archName, thread);
            thread.setEndCall(() -> {
                System.out.println("success");
            });
            primaryStage.show();
            controller.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("gui/view/MainView.fxml"));
            SplitPane pane = loader.load();

            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            MainViewController mvc = loader.getController();
            mvc.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}