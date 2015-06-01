package tk.thelocky.eazyarch.stream;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.struct.InnerFolderHeader;
import tk.thelocky.eazyarch.util.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;

public class LinkStream {
    private ArchiveStream stream;
    private InnerTree.Iterator dst;
    private InnerStream folderStream;
    private InnerFolderHeader fHead;

    LinkStream(@NotNull ArchiveStream stream, @NotNull InnerTree.Iterator dst) {
        this.stream = stream;
        this.dst = dst;
        readHeader();
        folderStream = new InnerStream(stream, dst.getParent().getPosInArchive() + fHead.fullSize(), -1, (byte) 2);
    }

    private void readHeader() {
        stream.accessFile.seek(dst.getParent().getPosInArchive());
        byte[] fData = new byte[InnerFolderHeader.size];
        stream.accessFile.read(fData);
        fHead = InnerFolderHeader.fromData(fData);
        byte[] fNameData = new byte[fHead.getNameSize()];
        stream.accessFile.read(fNameData);
        fHead.setName(fNameData);
    }

    private void writeHeader() {
        stream.accessFile.seek(dst.getParent().getPosInArchive());
        stream.accessFile.write(fHead.getData());
    }

    void addLink(long link) {
        if (link == 0) {
            if (Constants.DEBUG_MODE)
                System.out.println("Warning! An attempt to adding null link in folder " + dst.getCurPathToRoot());
            return;
        }
        int off = fHead.getCount();
        fHead.setCount(fHead.getCount() + 1);
        writeHeader();
        folderStream.seek(off * Long.BYTES);
        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
        buf.putLong(link);
        folderStream.write(buf.array());
        folderStream.seek(0);
    }

    int getLinkIndex(long pos) {
        for (int i = 0; i < getCount(); i++) {
            if (getLink(i) == pos)
                return i;
        }
        return -1;
    }

    long getLink(int idx) {
        if ((idx >= 0) && (idx < fHead.getCount())) {
            folderStream.seek(idx * Long.BYTES);
            byte[] link = new byte[Long.BYTES];
            folderStream.read(link);
            ByteBuffer buf = ByteBuffer.wrap(link);
            return buf.getLong();
        } else
            return 0;
    }

    void delLink(int idx) {
        if ((idx >= 0) && (idx < fHead.getCount())) {
            folderStream.seek((fHead.getCount() - 1) * Long.BYTES);
            byte[] link = new byte[Long.BYTES];
            folderStream.read(link);
            folderStream.seek(idx * Long.BYTES);
            folderStream.write(link);
        }
    }

    int getCount() {
        return fHead.getCount();
    }

    void close() { folderStream.close(); }
}
