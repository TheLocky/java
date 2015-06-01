package tk.thelocky.eazyarch.gui.model;

import javafx.beans.property.*;
import javafx.scene.image.ImageView;
import tk.thelocky.eazyarch.res.R;
import tk.thelocky.eazyarch.util.FileIcon;
import tk.thelocky.eazyarch.util.IconAndName;
import tk.thelocky.eazyarch.util.Constants;

public class ArchiveFileModel {
    private final ObjectProperty<IconAndName> iconAndName;
    private final StringProperty compressType;
    private final StringProperty systemFileType;
    private final ObjectProperty<Long> realSize;
    private final ObjectProperty<Long> compressSize;
    private final String fileType;

    public ArchiveFileModel(String fileName, String fileType, String compressType,
                            Long realSize, Long compressSize) {
        this.compressType = new SimpleStringProperty(compressType);
        this.realSize = new SimpleObjectProperty<>(realSize);
        this.fileType = fileType == null ? "" : fileType;
        this.compressSize = new SimpleObjectProperty<>(compressSize);
        ImageView icon;
        if (isFolder()) {
            icon = FileIcon.getFolderIcon();
            this.systemFileType = new SimpleStringProperty(R.get("folder_type"));
        }
        else {
            String[] buf = new String[1];
            icon = FileIcon.getIconForType(fileType, buf);
            this.systemFileType = new SimpleStringProperty(buf[0]);
        }
        this.iconAndName = new SimpleObjectProperty<>(new IconAndName(fileName, icon));
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

    public String getCompressType() {
        return compressType.get();
    }

    public StringProperty compressTypeProperty() {
        return compressType;
    }

    public void setCompressType(String compressType) {
        this.compressType.set(compressType);
    }

    public String getSystemFileType() {
        return systemFileType.get();
    }

    public StringProperty systemFileTypeProperty() {
        return systemFileType;
    }

    public void setSystemFileType(String systemFileType) {
        this.systemFileType.set(systemFileType);
    }

    public Long getRealSize() {
        return realSize.get();
    }

    public ObjectProperty<Long> realSizeProperty() {
        return realSize;
    }

    public void setRealSize(Long realSize) {
        this.realSize.set(realSize);
    }

    public Long getCompressSize() {
        return compressSize.get();
    }

    public ObjectProperty<Long> compressSizeProperty() {
        return compressSize;
    }

    public void setCompressSize(Long compressSize) {
        this.compressSize.set(compressSize);
    }

    public boolean isFolder() {
        return fileType.compareToIgnoreCase(R.get("folder_type")) == 0;
    }
}
