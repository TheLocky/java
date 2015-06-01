package tk.thelocky.eazyarch.struct;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.util.Constants;
import tk.thelocky.eazyarch.util.Converting;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class ClusterHeader implements DataHeader {
    public static final int size = Constants.CLUSTER_MAP_SIZE + Long.BYTES + Integer.BYTES;
    private long firstFreeBlock;
    private int blocksCount;
    private BitSet blockMap;

    public ClusterHeader() {
        firstFreeBlock = Constants.NO_HAVE_BLOCK;
        blocksCount = 0;
        blockMap = new BitSet(Constants.CLUSTER_MAP_SIZE * 8);
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.putLong(firstFreeBlock);
        buffer.putInt(blocksCount);
        buffer.put(blockMap.toByteArray());
        return buffer.array();
    }

    @Override
    public void setData(@NotNull byte[] data) {
        if (data.length >= Long.BYTES + Integer.BYTES) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            firstFreeBlock = buffer.getLong();
            blocksCount = buffer.getInt();
            blockMap.clear();
            byte[] mapData = new byte[data.length - Long.BYTES - Integer.BYTES];
            buffer.get(mapData, 0, data.length - Long.BYTES - Integer.BYTES);
            blockMap.or(Converting.byteArrayToBitSet(mapData));
        }
    }

    public static ClusterHeader fromData(@NotNull byte[] data) {
        ClusterHeader tmp = new ClusterHeader();
        tmp.setData(data);
        return tmp;
    }

    public long getFirstFreeBlock() {
        return firstFreeBlock;
    }

    public void setFirstFreeBlock(long firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    public int getBlocksCount() {
        return blocksCount;
    }

    public void increaseBlocksCount() {
        blocksCount++;
    }

    public BitSet getBlockMap() {
        return blockMap;
    }
}
