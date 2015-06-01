package tk.thelocky.eazyarch.gui.view;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import tk.thelocky.eazyarch.compress.CompressThread;

public class CompressViewController {
    private CompressThread thread;

    @FXML
    private Label archiveName;
    @FXML
    private Label innerFile;
    @FXML
    private Label sysFile;
    @FXML
    private ProgressBar progress;

    @FXML
    private void initialize() {
        thread = null;
        archiveName.setText("");
        innerFile.setText("");
        sysFile.setText("");
        progress.setProgress(0);
    }

    public void setThread(@NotNull String archName, @NotNull CompressThread thread) {
        archiveName.setText(archName);
        this.thread = thread;
        this.thread.setProperties(innerFile, sysFile, progress);
    }

    public void start() {
        if (true) {
            thread.start();
        } else {
            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
