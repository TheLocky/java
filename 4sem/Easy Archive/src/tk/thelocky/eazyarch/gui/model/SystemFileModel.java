package tk.thelocky.eazyarch.gui.model;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.*;
import tk.thelocky.eazyarch.res.R;
import tk.thelocky.eazyarch.util.FileIcon;
import tk.thelocky.eazyarch.util.IconAndName;
import tk.thelocky.eazyarch.util.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class SystemFileModel {
    private final ObjectProperty<IconAndName> iconAndName;
    private final StringProperty fileType;
    private final ObjectProperty<Long> fileSize;

    public SystemFileModel(@NotNull File file) {
        iconAndName = new SimpleObjectProperty<>(null);
        fileType = new SimpleStringProperty(null);
        fileSize = new SimpleObjectProperty<>(null);
        if (file.exists()) {
            if (!file.isDirectory()) {
                String[] ft = new String[1];
                IconAndName ian = new IconAndName(file.getName(),
                        FileIcon.getIconForName(file.getName(), ft));
                iconAndName.set(ian);
                fileType.set(ft[0]);
                try {
                    BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    fileSize.set(attr.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                IconAndName ian = new IconAndName(file.getName(), FileIcon.getFolderIcon());
                iconAndName.set(ian);
                fileType.set(R.get("folder_type"));
            }
        }
    }

    public IconAndName getIconAndName() {
        return iconAndName.get();
    }

    public ObjectProperty<IconAndName> iconAndNameProperty() {
        return iconAndName;
    }

    public void setIconAndName(IconAndName iconAndName) {
        this.iconAndName.set(iconAndName);
    }

    public String getFileType() {
        return fileType.get();
    }

    public StringProperty fileTypeProperty() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType.set(fileType);
    }

    public Long getFileSize() {
        return fileSize.get();
    }

    public ObjectProperty<Long> fileSizeProperty() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize.set(fileSize);
    }

    public boolean isFolder() {
        return fileType.get().compareToIgnoreCase(R.get("folder_type")) == 0;
    }
}
