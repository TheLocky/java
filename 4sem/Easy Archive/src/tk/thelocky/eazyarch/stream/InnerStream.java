package tk.thelocky.eazyarch.stream;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.struct.ArchiveHeader;
import tk.thelocky.eazyarch.struct.BlockHeader;
import tk.thelocky.eazyarch.struct.ClusterHeader;
import tk.thelocky.eazyarch.util.Constants;


public class InnerStream implements DataIOStream {
    private ArchiveStream stream;
    private int clusterNumber;
    private BlockHeader blockHeader;
    private int blockNumber;

    private long beginOffset;
    private long counter;
    private long length;
    private boolean lastOperationError;
    private boolean fileOpened;
    private byte blockType;

    private int usedBlocks;

    InnerStream(@NotNull ArchiveStream archStream, long begin, long length, byte blockType) {
        this.stream = archStream;
        this.blockType = blockType;
        this.length = length;
        beginOffset = begin;
        fileOpened = true;
        assert (begin > ArchiveHeader.size + ClusterHeader.size + BlockHeader.size);

        clusterNumber = stream.calc.getClusterNum(begin);
        blockNumber = stream.calc.getBlockNum(begin);
        blockHeader = stream.blockManager.getBlockHeader(clusterNumber, blockNumber);
        stream.accessFile.seek(begin);
        counter = 0;
        usedBlocks = 1;
    }

    @Override
    public int read(byte[] b) {
        int readLen = 0;
        while (!eof() && (readLen < b.length)) {
            b[readLen] = getc();
            if (fail())
                break;
            readLen++;
        }
        //TODO fail после чтения массива
        lastOperationError = false;
        return readLen;
    }

    @Override
    public int write(byte[] b) {
        int writeLen = 0;
        while (writeLen < b.length) {
            putc(b[writeLen]);
            if (fail())
                break;
            writeLen++;
        }
        lastOperationError = false;
        return writeLen;
    }

    @Override
    public byte getc() {
        if (fileOpened) {
            long curPos = stream.accessFile.pos();
            long startPos = stream.calc.getBlockPos(clusterNumber, blockNumber);
            if (curPos >= startPos + stream.header.getBlockSize()) {
                long newPos = blockHeader.getNextBlock();
                if (newPos != 0) {
                    int newClusterNum = stream.calc.getClusterNum(newPos);
                    if (clusterNumber != newClusterNum) {
                        clusterNumber = newClusterNum;
                    }
                    blockNumber = stream.calc.getBlockNum(newPos);
                    blockHeader = stream.blockManager.getBlockHeader(clusterNumber, blockNumber);
                    stream.accessFile.seek(newPos + BlockHeader.size);
                } else {
                    lastOperationError = true;
                    return 0;
                }
            }
            if (!eof()) {
                byte b = stream.accessFile.getc();
                counter++;
                lastOperationError = false;
                return b;
            }
        }
        lastOperationError = true;
        return 0;
    }

    @Override
    public void putc(byte b) {
        if (fileOpened) {
            long curPos = stream.accessFile.pos();
            long startPos = stream.calc.getBlockPos(clusterNumber, blockNumber);
            if (curPos >= startPos + stream.header.getBlockSize()) {
                long newPos = stream.blockManager.getFreeBlock(clusterNumber);
                int newClusterNum = stream.calc.getClusterNum(newPos);
                if (clusterNumber != newClusterNum) {
                    clusterNumber = newClusterNum;
                }
                blockHeader.setNextBlock(newPos);
                blockHeader.setBlockType(blockType);
                int newBlockNum = stream.calc.getBlockNum(newPos);
                blockNumber = newBlockNum;
                blockHeader = stream.blockManager.getBlockHeader(clusterNumber, blockNumber);
                usedBlocks++;
                stream.accessFile.seek(newPos + BlockHeader.size);
            }
            stream.accessFile.putc(b);
            counter++;
            lastOperationError = false;
        } else
            lastOperationError = true;
    }

    @Override
    public void rewind() {
        if (counter == 0) return;
        stream.accessFile.seek(beginOffset);
        clusterNumber = stream.calc.getClusterNum(beginOffset);
        blockNumber = stream.calc.getBlockNum(beginOffset);
        blockHeader = stream.blockManager.getBlockHeader(clusterNumber, blockNumber);
        counter = 0;
    }

    @Override
    public void seek(long pos) {
        if (((pos >= 0) && (pos < length)) || (length < 0)) {
            rewind();
            while (counter < pos) {
                getc();
                if (fail())
                    break;
            }
        }
    }

    @Override
    public long pos() {
        return counter;
    }

    @Override
    public boolean eof() {
        return length >= 0 && counter >= length;
    }

    @Override
    public boolean fail() {
        return lastOperationError;
    }

    @Override
    public long size() {
        return length;
    }

    public void close() {
        if (Constants.DEBUG_MODE) {
            System.out.println("Close file stream: " + this.toString() + " Used blocks " + usedBlocks);
        }
        stream.locker = null;
    }

    void freeAllContent() {
        rewind();
        BlockHeader bHead = blockHeader;
        long cur = beginOffset;
        do {
            long next = bHead.getNextBlock();
            stream.blockManager.freeBlock(cur);
            if (next == Constants.NO_HAVE_BLOCK)
                break;
            int clIdx = stream.calc.getClusterNum(next);
            int idx = stream.calc.getBlockNum(next);
            bHead = stream.blockManager.getBlockHeader(clIdx, idx);
            cur = next;
        } while (true);
    }
}
