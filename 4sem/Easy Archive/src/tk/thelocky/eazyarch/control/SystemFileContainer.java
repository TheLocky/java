package tk.thelocky.eazyarch.control;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tk.thelocky.eazyarch.gui.model.SystemFileModel;

import java.io.File;
import java.nio.file.Path;
import java.util.Comparator;

public class SystemFileContainer {
    private Path homeDir;
    private Path curDir;

    private final ObservableList<SystemFileModel> files;

    public SystemFileContainer(@NotNull Path homeDirectory) {
        files = FXCollections.observableArrayList();
        if (!homeDirectory.toFile().isDirectory())
            homeDirectory = homeDirectory.getParent();
        homeDir = homeDirectory;
        toHome();
        updateList();
    }

    public void in(String dirName) {
        for (File f : curDir.toFile().listFiles()) {
            if (f != null) {
                if (f.isDirectory() && f.getName().compareToIgnoreCase(dirName) == 0) {
                    curDir = f.toPath();
                    updateList();
                    break;
                }
            }
        }
    }

    public void out() {
        if (curDir.getParent() != null) {
            curDir = curDir.getParent();
            updateList();
        }
    }

    public void toHome() {
        curDir = homeDir;
        updateList();
    }

    public void updateList() {
        files.clear();
        for (File f : curDir.toFile().listFiles()) {
            if (f != null) {
                files.add(new SystemFileModel(f));
            }
        }
        sort();
    }

    private void sort() {
        FXCollections.sort(files, new Comparator<SystemFileModel>() {
            @Override
            public int compare(SystemFileModel o1, SystemFileModel o2) {
                if (o1.isFolder() && !o2.isFolder()) {
                    return -1;
                } else if (o2.isFolder() && !o1.isFolder()) {
                    return 1;
                } else {
                    return o1.getIconAndName().getName().compareTo(o2.getIconAndName().getName());
                }
            }
        });
    }

    public ObservableList<SystemFileModel> getFiles() {
        return files;
    }

    public String getCurDirAbsolutePath() {
        return curDir.toAbsolutePath().toString();
    }
}
