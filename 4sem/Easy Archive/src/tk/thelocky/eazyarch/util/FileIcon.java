package tk.thelocky.eazyarch.util;


import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileIcon {
    public static ImageView getFolderIcon() {
        try {
            Path p = Files.createTempDirectory(null);
            File file = p.toFile();
            FileSystemView fsv = new JFileChooser().getFileSystemView();
            Icon ico = fsv.getSystemIcon(file);
            BufferedImage tmp = new BufferedImage(ico.getIconWidth(),
                    ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            ico.paintIcon(null, tmp.createGraphics(), 0, 0);
            return toImageView(tmp);
        } catch (IOException e) {
            return null;
        }
    }

    public static ImageView getIconForType(@NotNull String type, @Nullable String[] systemTypeName) {
        try {
            if (!type.startsWith("."))
                type = "." + type;
            Path p = Files.createTempFile(null, type);
            File file = p.toFile();
            FileSystemView fsv = new JFileChooser().getFileSystemView();
            Icon ico = fsv.getSystemIcon(file);
            if ((systemTypeName != null) && (systemTypeName.length > 0)) {
                systemTypeName[0] = ico.toString();
            }
            BufferedImage tmp = new BufferedImage(ico.getIconWidth(),
                    ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            ico.paintIcon(null, tmp.createGraphics(), 0, 0);
            return toImageView(tmp);
        } catch (IOException e) {
            return null;
        }
    }

    public static ImageView getIconForName(@NotNull String fileName, @Nullable String[] systemTypeName) {
        String ext = getExtension(fileName);
        if (ext != null) {
            return getIconForType(ext, systemTypeName);
        }
        return null;
    }

    public static String getExtension(@NotNull String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx >= 0) {
            String ext = fileName.substring(idx + 1);
            return ext;
        }
        return null;
    }

    private static ImageView toImageView(BufferedImage img) {
        WritableImage wr = SwingFXUtils.toFXImage(img, null);
        return new ImageView(wr);
    }
}
