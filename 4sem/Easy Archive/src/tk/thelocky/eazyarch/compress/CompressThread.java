package tk.thelocky.eazyarch.compress;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import tk.thelocky.eazyarch.stream.ArchiveStream;
import tk.thelocky.eazyarch.stream.InnerStream;
import tk.thelocky.eazyarch.stream.InnerTree;
import tk.thelocky.eazyarch.util.Callback;
import tk.thelocky.eazyarch.util.Converting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class CompressThread extends Thread {
    private ArchiveStream stream;
    private InnerTree.Iterator curRoot;
    private String[] archiveFilesPaths;
    private File[] systemPaths;
    private int operation;
    private Callback end;
    private Label innerFile;
    private Label sysFile;
    private ProgressBar progress;

    public CompressThread(@NotNull ArchiveStream stream, int operation) {
        this.stream = stream;
        this.operation = operation;
        systemPaths = new File[0];
        archiveFilesPaths = new String[0];
    }

    public void setPaths(@NotNull String archivePath, @NotNull String[] archiveFilesPaths,
                         @NotNull File[] systemPaths) throws IOException {
        this.curRoot = stream.getFileTree().createIterator().inPath(archivePath);
        this.archiveFilesPaths = archiveFilesPaths;
        this.systemPaths = systemPaths;
    }

    public void setProperties(Label iF, Label sF, ProgressBar p) {
        innerFile = iF;
        sysFile = sF;
        progress = p;
    }

    public void setEndCall(Callback end) {
        this.end = end;
    }

    private void updateLabels(String iF, String sF) {
        Platform.runLater(() -> {
            innerFile.setText(Converting.getShortPath(iF, 50));
            sysFile.setText(Converting.getShortPath(sF, 50));
        });
    }

    @Override
    public void run() {
        long mils = System.currentTimeMillis();
        try {
            switch (operation) {
                case 1: //compress
                    doCompress(systemPaths);
                    break;
                case 2: //decompress
                    doDecompress();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            long runTime = (System.currentTimeMillis() - mils);
            System.out.println((double)runTime / 1000L);
            end.call();
        }
    }

    private void doCompress(File[] paths) throws IOException {
        for (File p : paths) {
            if (p.exists()) {
                if (p.isDirectory()) {
                    stream.newFolder(curRoot, p.getName());
                    curRoot.in(p.getName());
                    doCompress(p.listFiles());
                    curRoot.out();
                } else {
                    BasicFileAttributes attr = Files.readAttributes(p.toPath(), BasicFileAttributes.class);
                    stream.newFile(curRoot, p.getName());
                    InnerStream fStream = stream.getFileStream(curRoot, p.getName());
                    updateLabels(curRoot.getCurPathToRoot() + p.getName(), p.getAbsolutePath());
                    //TODO выбор компрессора
                    Compressor cmp = CompressFormat.getCompressor(0);
                    cmp.getProgressProperty().addListener((observable, oldValue, newValue) -> {
                        if (oldValue.intValue() != newValue.intValue()) {
                            progress.setProgress((double) (newValue.intValue()) / 100);
                        }
                    });
                    long result = cmp.compress(p, fStream);
                    stream.updateFileInfo(curRoot, p.getName(), attr.size(), result,
                            attr.creationTime().toMillis(), attr.lastModifiedTime().toMillis(), 0);
                    fStream.close();
                }
            }
        }
    }

    private void doDecompress() throws IOException {
        if (systemPaths.length > 0) {
            File dst = systemPaths[0];
            if (dst.exists() && dst.isDirectory()) {
                for (String aPath : archiveFilesPaths) {
                    aPath = Converting.getCorrectPath(aPath);
                    InnerTree fInfo = curRoot.getFileFromPath(aPath);
                    if (fInfo != null) {
                        InnerTree.Iterator iter = fInfo.createIterator();
                        InnerStream fStream = stream.getFileStream(iter, fInfo.getName());
                        if (fStream != null) {
                            Compressor cmp = CompressFormat.getCompressor(fInfo.getCompressType());
                            File outputFile = new File(dst.getAbsolutePath()+ "/" + aPath);
                            Files.createDirectories(outputFile.getParentFile().toPath());
                            updateLabels(iter.getCurPathToRoot() + fInfo.getName(), outputFile.getAbsolutePath());
                            cmp.getProgressProperty().addListener((observable, oldValue, newValue) -> {
                                if (oldValue.intValue() != newValue.intValue()) {
                                    progress.setProgress((double) (newValue.intValue()) / 100);
                                }
                            });
                            if (cmp.decompress(fStream, outputFile)) {
                                FileTime creationTime = FileTime.fromMillis(fInfo.getCreateTime());
                                FileTime updateTime = FileTime.fromMillis(fInfo.getUpdateTime());
                                BasicFileAttributeView attrs = Files.getFileAttributeView(outputFile.toPath(),
                                        BasicFileAttributeView.class);
                                attrs.setTimes(updateTime, updateTime, creationTime);
                            }
                            fStream.close();
                        }
                    }
                }
            }
        }
    }
}
