package tk.thelocky.eazyarch.stream;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.struct.*;
import tk.thelocky.eazyarch.util.BlockManager;
import tk.thelocky.eazyarch.util.Constants;
import tk.thelocky.eazyarch.util.PosCalculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArchiveStream {
    ArchiveHeader header;
    NativeFileIOStream accessFile;
    PosCalculator calc;
    BlockManager blockManager;
    InnerStream locker;
    private InnerTree fileTree;
    private String name;
    private Path pathToFile;


    private ArchiveStream() {
        fileTree = new InnerTree(".", 2, 0, 0, 0, 0, 0, 0);
        fileTree.stream = this;
        locker = null;
    }

    public static ArchiveStream openArchive(@NotNull File file) throws IOException {
        if (file.exists() && !file.isDirectory()) {
            ArchiveStream archStream = new ArchiveStream();
            archStream.accessFile = new NativeFileIOStream(file.getAbsolutePath());
            archStream.accessFile.open("rb+");
            archStream.readHeader();
            if (!archStream.isValidArchive())
                throw new IOException("not valid archive");
            archStream.calc = new PosCalculator(archStream.header);
            archStream.blockManager = new BlockManager(archStream.header, archStream.accessFile);
            archStream.fileTree.setPosInArchive(archStream.calc.getBlockPos(0, 0) + BlockHeader.size);
            archStream.loadFileTree(archStream.fileTree.createIterator());
            archStream.name = file.getName();
            archStream.pathToFile = file.getParentFile().toPath();
            return archStream;
        } else
            throw new IOException("file " + file.getName() +" not found");
    }

    public static ArchiveStream newArchive(@NotNull File file) throws IOException {
        return newArchive(file, 512);
    }

    public static ArchiveStream newArchive(@NotNull File file, long blockSize) throws IOException {
        if (file.exists() && !file.isDirectory())
            if (!file.delete())
                throw new IOException("Can't delete exists file");
        if (!file.createNewFile())
            throw new IOException("Can't create new file");
        ArchiveStream archStream = new ArchiveStream();
        archStream.accessFile = new NativeFileIOStream(file.getAbsolutePath());
        archStream.accessFile.open("wb+");
        archStream.header = new ArchiveHeader();
        archStream.header.setBlockSize(blockSize);
        archStream.calc = new PosCalculator(archStream.header);
        archStream.blockManager = new BlockManager(archStream.header, archStream.accessFile);
        long free = archStream.blockManager.getFreeBlock(0);
        archStream._newFolder(free, ".");
        archStream.fileTree.setPosInArchive(free + BlockHeader.size);
        archStream.name = file.getName();
        return archStream;
    }

    public InnerTree newFile(@NotNull InnerTree.Iterator dst, @NotNull String fileName) {
        if ((dst.getParent().getRoot() == fileTree) && (fileName.length() > 0)) {
            long freeSpace = blockManager.getFreeBlock(0);
            if (freeSpace != -1) {
                InnerTree file = new InnerTree(fileName, 1, 0, 0, -1, 0, 0, 0);
                if (dst.add(file)) {
                    //write file info
                    BlockHeader bHead = blockManager.getBlockHeader(calc.getClusterNum(freeSpace),
                            calc.getBlockNum(freeSpace));
                    bHead.setBlockType((byte) 1);
                    bHead.setNextBlock(0);
                    InnerFileHeader fHead = new InnerFileHeader();
                    fHead.setName(file.getName());
                    accessFile.seek(freeSpace + BlockHeader.size);
                    accessFile.write(fHead.getData());
                    file.setPosInArchive(freeSpace + BlockHeader.size);
                    //add link in folder
                    LinkStream linker = new LinkStream(this, dst);
                    linker.addLink(file.getPosInArchive());
                    return file;
                }
            }
        }
        return null;
    }

    public void updateFileInfo(@NotNull InnerTree.Iterator dst, @NotNull String fileName,
                               long realSize, long packSize, long createTime, long updateTime,
                               int compressType) {
        if (dst.getParent().getRoot() == fileTree) {
            InnerTree info = dst.getFile(fileName);
            if (info != null) {
                accessFile.seek(info.getPosInArchive());
                byte[] fData = new byte[InnerFileHeader.size];
                accessFile.read(fData);
                InnerFileHeader fHead = InnerFileHeader.fromData(fData);
                byte[] fName = new byte[fHead.getNameSize()];
                accessFile.read(fName);
                fHead.setName(fName);
                fHead.setRealSize(info.realSize = realSize);
                fHead.setPackSize(info.packSize = packSize);
                fHead.setCreateTime(info.createTime = createTime);
                fHead.setLastModifiedTime(info.updateTime = updateTime);
                fHead.setCompressType((byte) (info.compressType = compressType));
                accessFile.seek(info.getPosInArchive());
                accessFile.write(fHead.getData());
            }
        }
    }

    public void newFolder(@NotNull InnerTree.Iterator dst, @NotNull String folderName) {
        long freeSpace = blockManager.getFreeBlock(0);
        if ((freeSpace != -1) && (dst.getParent().getRoot() == fileTree)) {
            InnerTree folder = new InnerTree(folderName, 2, 0, 0, 0, 0, 0, 0);
            if (dst.add(folder)) {
                folder.setPosInArchive(freeSpace + BlockHeader.size);
                LinkStream linker = new LinkStream(this, dst);
                linker.addLink(freeSpace + BlockHeader.size);
                _newFolder(freeSpace, folderName);
            }
        }
    }

    private void _newFolder(long pos, @NotNull String folderName) {
        int clIdx = calc.getClusterNum(pos);
        int idx = calc.getBlockNum(pos);
        BlockHeader bHead = blockManager.getBlockHeader(clIdx, idx);
        bHead.setNextBlock(0);
        bHead.setBlockType((byte) 2);
        InnerFolderHeader folderHeader = new InnerFolderHeader();
        folderHeader.setCount(0);
        folderHeader.setName(folderName);
        accessFile.seek(pos + BlockHeader.size);
        accessFile.write(folderHeader.getData());
    }

    public InnerStream getFileStream(@NotNull InnerTree.Iterator dst, @NotNull String fileName) {
        if (locker != null) {
            if (Constants.DEBUG_MODE) {
                System.out.println("Warning! An attempt to get stream while previous stream not closed:");
                System.out.println("Stream path: " + dst.getCurPathToRoot() + fileName);
                System.out.println("Previous stream: " + locker.toString());
            }
            return null;
        }
        if (dst.getParent().getRoot() == fileTree) {
            InnerTree file = dst.getFile(fileName);
            if (file != null) {
                byte[] fData = new byte[InnerFileHeader.size];
                accessFile.seek(file.getPosInArchive());
                accessFile.read(fData);
                InnerFileHeader fHead = InnerFileHeader.fromData(fData);
                locker = new InnerStream(this, file.getPosInArchive() + fHead.fullSize(),
                        file.getPackSize(), (byte) 1);
                if (Constants.DEBUG_MODE) {
                    System.out.println("Opened new file stream: " + locker.toString() +
                            " from path: " + dst.getCurPathToRoot() + fileName);
                }
                return locker;
            }
        }
        return null;
    }

    public void deleteFile(@NotNull InnerTree.Iterator dst, @NotNull String fileName) {
        InnerStream fStream = getFileStream(dst, fileName);
        if (fStream != null) {
            fStream.freeAllContent();
            fStream.close();
            InnerTree fInfo = dst.getFile(fileName);
            LinkStream linker = new LinkStream(this, dst);
            int idx = linker.getLinkIndex(fInfo.getPosInArchive());
            if (idx > -1) {
                linker.delLink(idx);
            } else if (Constants.DEBUG_MODE) {
                System.out.println("Warning! File found in tree but not found in folder:");
                System.out.println("Tree path: " + dst.getCurPathToRoot());
            }
            dst.delFile(fileName);
        }
    }

    public void deleteFolder(@NotNull InnerTree.Iterator dst) {
        if (dst.getParent().getRoot() == fileTree) {
            if (dst.getParent() == fileTree) return;
            for (InnerTree f : dst.getList()) {
                if (f.getType() == 2) {
                    dst.in(f.getName());
                    deleteFolder(dst);
                    long folderPos = dst.getParent().getPosInArchive();
                    InnerStream stream = new InnerStream(this, folderPos, -1, (byte) 2);
                    stream.freeAllContent();
                    stream.close();
                    dst.out();
                    dst.delFolder(f.getName());
                } else {
                    deleteFile(dst, f.getName());
                }
            }
        }
    }

    private boolean readHeader() {
        if (accessFile != null) {
            accessFile.seek(0);
            byte[] headerData = new byte[ArchiveHeader.size];
            accessFile.read(headerData);
            header = ArchiveHeader.fromData(headerData);
            return true;
        } else
            return false;
    }

    private boolean isValidArchive() {
        return (header != null) && header.isValidHeader();
    }

    void loadFileTree(@NotNull InnerTree.Iterator src) {
        if (src.getParent().getRoot() == fileTree) {
            LinkStream linker = new LinkStream(this, src);
            src.clear();
            for (int i = 0; i < linker.getCount(); i++) {
                long link = linker.getLink(i);
                if (link == 0) {
                    linker.delLink(i);
                    continue;
                }
                BlockHeader bHead = blockManager.getBlockHeader(calc.getClusterNum(link),
                        calc.getBlockNum(link));
                accessFile.seek(link);
                switch (bHead.getBlockType()) {
                    case 0:
                        linker.delLink(i);
                        break;
                    case 1:
                        byte[] fileData = new byte[InnerFileHeader.size];
                        accessFile.read(fileData);
                        InnerFileHeader fileHeader = InnerFileHeader.fromData(fileData);
                        byte[] fileName = new byte[fileHeader.getNameSize()];
                        accessFile.read(fileName);
                        fileHeader.setName(fileName);
                        src.add(InnerTree.getObjFromHeader(fileHeader, link));
                        break;
                    case 2:
                        byte[] folderData = new byte[InnerFolderHeader.size];
                        accessFile.read(folderData);
                        InnerFolderHeader folderHeader = InnerFolderHeader.fromData(folderData);
                        byte[] folderName = new byte[folderHeader.getNameSize()];
                        accessFile.read(folderName);
                        folderHeader.setName(folderName);
                        src.add(InnerTree.getObjFromHeader(folderHeader, link));
                        break;
                }
            }
        }
    }

    public void close() {
        blockManager.saveAllChanges();
        accessFile.close();
    }

    public InnerTree getFileTree() {
        return fileTree;
    }

    public String getName() {
        return name;
    }

    public Path getPathToFile() {
        return Paths.get(pathToFile.toUri());
    }
}
