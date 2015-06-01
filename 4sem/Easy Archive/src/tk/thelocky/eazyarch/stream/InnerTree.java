package tk.thelocky.eazyarch.stream;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.struct.InnerFileHeader;
import tk.thelocky.eazyarch.struct.InnerFolderHeader;
import tk.thelocky.eazyarch.util.Constants;
import tk.thelocky.eazyarch.util.Converting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InnerTree {
    ArchiveStream stream;
    private InnerTree root;
    private InnerTree parent;
    private List<InnerTree> child;
    private String name;
    private int type;
    long posInArchive;
    long realSize;
    long packSize;
    long createTime;
    long updateTime;
    int compressType;

    public static class Iterator {
        private InnerTree par;
        private Iterator(InnerTree parent) {
            this.par = parent;
        }

        boolean add(@NotNull InnerTree file) {
            InnerTree tmp = getObject(file.getName(), file.getType());
            if (tmp == null) {
                par.addObj(file);
                return true;
            } else
                return false;
        }

        void createPath(@NotNull String path) throws IOException {
            String[] p = path.replaceAll("\\\\", "/").split("\\/");
            Iterator iter = par.root.createIterator();
            for (String f : p) {
                InnerTree obj = getObject(f, 2);
                if (obj == null) {
                    iter.add(new InnerTree(f, 2, 0, 0, 0, 0, 0, 0));
                }
                iter.in(f);
            }
        }

        void delFile(@NotNull String fileName) {
            InnerTree f = getFile(fileName);
            if (f != null)
                par.child.remove(f);
        }

        void delFolder(@NotNull String folderName) {
            InnerTree f = getObject(folderName, 2);
            if (f != null) {
                par.child.remove(f);
            }
        }

        public Iterator in(@NotNull String folderName) {
            InnerTree tmp = getObject(folderName, 2);
            if (tmp != null) {
                par = tmp;
                par.stream.loadFileTree(this);
            }
            return this;
        }

        public Iterator inPath(@NotNull String path) throws IOException {
            path = Converting.getCorrectPath(path);
            String[] p = path.split("\\/");
            Iterator iter = par.root.createIterator();
            for (String f : p) {
                InnerTree obj = iter.getObject(f, 2);
                if (obj == null) {
                    break;
                }
                iter.in(f);
            }
            par = iter.par;
            return this;
        }

        public Iterator out() {
            if (par.parent != null) {
                par = par.parent;
            }
            return this;
        }

        public InnerTree getFile(@NotNull String fileName) {
            InnerTree file = getObject(fileName, 1);
            if (file != null)
                return file;
            return null;
        }

        public InnerTree getFileFromPath(@NotNull String filePath) throws IOException {
            filePath = Converting.getCorrectPath(filePath);
            String[] p = filePath.split("\\/");
            String fileName = p[p.length - 1];
            Iterator iter = par.createIterator();
            iter.inPath(filePath.substring(0, filePath.length() - fileName.length()));
            return iter.getFile(fileName);
        }

        public String getCurPathToRoot() {
            Iterator iter = this.par.createIterator();
            String result = "/";
            if (iter.isRoot()) {
                return "";
            }
            do {
                result = "/" + iter.par.name + result;
                iter.out();
            } while ((iter.par.parent != iter.par.root));
            result = Converting.getCorrectPath(result);
            return result;
        }

        public String[] getAllFilesPaths() throws IOException {
            List<String> list = getAllFilesPaths("", this.par.createIterator());
            return list.toArray(new String[list.size()]);
        }

        private List<String> getAllFilesPaths(String prefix, Iterator dst) throws IOException {
            List<String> res = new ArrayList<>();
            for (InnerTree f : dst.getList()) {
                if (f.getType() == 2) {
                    dst.in(f.getName());
                    List<String> list = getAllFilesPaths(prefix + f.getName() + "/", dst);
                    res.addAll(list);
                    dst.out();
                } else {
                    String path = prefix + f.getName();
                    res.add(path);
                }
            }
            return res;
        }

        private InnerTree getObject(@NotNull String name, int type) {
            for (InnerTree file : par.child) {
                if (file.getName().compareTo(name) == 0 && file.getType() == type) {
                    return file;
                }
            }
            return null;
        }

        public List<InnerTree> getList() {
            return new ArrayList<>(par.child);
        }

        public InnerTree getParent() {
            return par;
        }

        public boolean isRoot() {
            return par.parent == null;
        }

        void clear() {
            par.child.clear();
        }
    }

    public InnerTree(@NotNull String name, int type, long posInArchive, long realSize,
                     long packSize, int compressType, long createTime, long updateTime) {
        this.parent = null;
        this.child = new ArrayList<>();
        this.name = name;
        this.type = type;
        this.posInArchive = posInArchive;
        this.realSize = realSize;
        this.packSize = packSize;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.compressType = compressType;
        this.root = this;
    }

    private void addObj(@NotNull InnerTree obj) {
        child.add(obj);
        obj.parent = this;
        obj.root = root;
        obj.stream = stream;
    }

    private void delObj(@NotNull InnerTree obj) {
        if (child.contains(obj)) {
            child.remove(obj);
            obj.parent = null;
        }
    }

    public String getName() { return name; }

    public int getType() {
        return type;
    }

    public long getPosInArchive() {
        return posInArchive;
    }

    public long getRealSize() {
        return realSize;
    }

    public long getPackSize() {
        return packSize;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public int getCompressType() {
        return compressType;
    }

    public Iterator createIterator() {
        if ((type == 1) && (parent != null)) {
            return new Iterator(parent);
        } else if (type == 2)
            return new Iterator(this);
        else
            return null;
    }

    public InnerTree getRoot() {
        return root;
    }

    void setPosInArchive(long posInArchive) {
        this.posInArchive = posInArchive;
    }

    static InnerTree getObjFromHeader(@NotNull InnerFileHeader f, long pos) {
        return new InnerTree(f.getName(), 1, pos, f.getRealSize(), f.getPackSize(),
                f.getCompressType(), f.getCreateTime(), f.getLastModifiedTime());
    }

    static InnerTree getObjFromHeader(@NotNull InnerFolderHeader f, long pos) {
        return new InnerTree(f.getName(), 2, pos, 0, 0, 0, 0, 0);
    }
}
