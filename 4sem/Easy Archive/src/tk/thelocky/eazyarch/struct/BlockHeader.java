package tk.thelocky.eazyarch.struct;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.util.Constants;

import java.nio.ByteBuffer;

public class BlockHeader implements DataHeader {
    public static final int size = Long.BYTES + 1;
    private long nextBlock;
    private byte blockType;

    public BlockHeader() {
        nextBlock = Constants.NO_HAVE_BLOCK;
        blockType = 0;
    }

    @Override
    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.putLong(nextBlock);
        buffer.put(blockType);
        return buffer.array();
    }

    @Override
    public void setData(@NotNull byte[] data) {
        if (data.length >= size) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            nextBlock = buffer.getLong();
            blockType = buffer.get();
        }
    }

    public static BlockHeader fromData(@NotNull byte[] data) {
        BlockHeader tmp = new BlockHeader();
        tmp.setData(data);
        return tmp;
    }

    public long getNextBlock() {
        return nextBlock;
    }

    public void setNextBlock(long nextBlock) {
        this.nextBlock = nextBlock;
    }

    public byte getBlockType() {
        return blockType;
    }

    public void setBlockType(byte blockType) {
        this.blockType = blockType;
    }
}
