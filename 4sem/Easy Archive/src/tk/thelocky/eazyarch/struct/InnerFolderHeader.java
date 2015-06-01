package tk.thelocky.eazyarch.struct;

import com.sun.istack.internal.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class InnerFolderHeader implements DataHeader {
    public static final int size = Integer.BYTES * 2;
    private int count;
    private int nameSize;
    private String name;

    public InnerFolderHeader() {
        setName("");
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(fullSize());
        buffer.putInt(count);
        buffer.putInt(nameSize);
        buffer.put(name.getBytes(Charset.forName("UTF-8")));
        return buffer.array();
    }

    @Override
    public void setData(@NotNull byte[] data) {
        if (data.length >= size) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            count = buffer.getInt();
            nameSize = buffer.getInt();
        }
    }

    public static InnerFolderHeader fromData(@NotNull byte[] data) {
        InnerFolderHeader header = new InnerFolderHeader();
        header.setData(data);
        return header;
    }

    public int fullSize() {
        return size + nameSize;
    }

    public int getNameSize() {
        return nameSize;
    }

    public void setName(String name) {
        this.name = name;
        nameSize = this.name.getBytes(Charset.forName("UTF-8")).length;
    }

    public void setName(@NotNull byte[] buf) {
        setName(new String(buf, Charset.forName("UTF-8")));
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
