package tk.thelocky.eazyarch.struct;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.compress.CompressFormat;
import tk.thelocky.eazyarch.util.Converting;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class InnerFileHeader implements DataHeader {
    public static final int size = Long.BYTES * 4 + Integer.BYTES + 1;
    private long realSize;
    private long packSize;
    private byte compressType;
    private long createTime;
    private long lastModifiedTime;
    private int nameSize;
    private String name;

    public InnerFileHeader() {
        realSize = 0;
        packSize = 0;
        compressType = CompressFormat.NO_COMPRESS;
        createTime = Converting.getUnixTime();
        lastModifiedTime = createTime;
        setName("");
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(fullSize());
        buffer.putLong(realSize);
        buffer.putLong(packSize);
        buffer.put(compressType);
        buffer.putLong(createTime);
        buffer.putLong(lastModifiedTime);
        buffer.putInt(nameSize);
        buffer.put(name.getBytes(Charset.forName("UTF-8")));
        return buffer.array();
    }

    @Override
    public void setData(@NotNull byte[] data) {
        if (data.length >= size) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            realSize = buffer.getLong();
            packSize = buffer.getLong();
            compressType = buffer.get();
            createTime = buffer.getLong();
            lastModifiedTime = buffer.getLong();
            nameSize = buffer.getInt();
        }
    }

    public static InnerFileHeader fromData(@NotNull byte[] data) {
        InnerFileHeader header = new InnerFileHeader();
        header.setData(data);
        return header;
    }

    public int fullSize() {
        return size + nameSize;
    }

    public long getRealSize() {
        return realSize;
    }

    public void setRealSize(long realSize) {
        this.realSize = realSize;
    }

    public long getPackSize() {
        return packSize;
    }

    public void setPackSize(long packSize) {
        this.packSize = packSize;
    }

    public byte getCompressType() {
        return compressType;
    }

    public void setCompressType(byte compressType) {
        this.compressType = compressType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
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
}
