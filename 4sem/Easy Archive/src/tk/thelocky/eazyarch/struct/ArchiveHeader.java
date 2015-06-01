package tk.thelocky.eazyarch.struct;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.util.Constants;
import tk.thelocky.eazyarch.util.Converting;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.zip.CRC32;

public class ArchiveHeader implements DataHeader {
    public static final int size = Long.BYTES * 3 + Constants.HEADER_MAP_SIZE;
    private long signature; //8B
    private long blockSize; //8B
    private BitSet clusterMap; // 2048B
    private long checkSum; //8B

    public ArchiveHeader() {
        signature = Constants.ARCHIVE_SIGNATURE;
        blockSize = 512;
        clusterMap = new BitSet(Constants.HEADER_MAP_SIZE * 8);
        recountCRC();
    }

    public byte[] getData(boolean recount) {
        if (recount)
            recountCRC();
        byte[] mapData = clusterMap.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.putLong(signature);
        byteBuffer.putLong(blockSize);
        byteBuffer.putLong(checkSum);
        byteBuffer.put(mapData);
        return byteBuffer.array();
    }

    @Override
    public byte[] getData() {
        return getData(true);
    }

    @Override
    public void setData(@NotNull byte[] data) {
        if (data.length >= Long.BYTES * 3) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);
            signature = byteBuffer.getLong();
            blockSize = byteBuffer.getLong();
            checkSum = byteBuffer.getLong();
            clusterMap.clear();
            byte[] mapData = new byte[data.length - Long.BYTES * 3];
            byteBuffer.get(mapData, 0, data.length - Long.BYTES * 3);
            clusterMap.or(Converting.byteArrayToBitSet(mapData));
        }
    }

    public static ArchiveHeader fromData(@NotNull byte[] data) {
        ArchiveHeader tmp = new ArchiveHeader();
        tmp.setData(data);
        return tmp;
    }

    public void recountCRC() {
        byte[] mapData = clusterMap.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 + mapData.length);
        byteBuffer.putLong(signature);
        byteBuffer.putLong(blockSize);
        byteBuffer.put(mapData);
        CRC32 crc32 = new CRC32();
        crc32.update(byteBuffer.array());
        checkSum = crc32.getValue();
    }

    public boolean isValidHeader() {
        CRC32 crc32 = new CRC32();
        byte[] mapData = clusterMap.toByteArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES * 2 + mapData.length);
        byteBuffer.putLong(signature);
        byteBuffer.putLong(blockSize);
        byteBuffer.put(mapData);
        crc32.update(byteBuffer.array());
        return (signature == Constants.ARCHIVE_SIGNATURE) && (checkSum == crc32.getValue());
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public BitSet getClusterMap() {
        return clusterMap;
    }
}
