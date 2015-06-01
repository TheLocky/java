package tk.thelocky.eazyarch.util;

import tk.thelocky.eazyarch.stream.InnerTree;
import tk.thelocky.eazyarch.stream.NativeFileIOStream;
import tk.thelocky.eazyarch.struct.ArchiveHeader;
import tk.thelocky.eazyarch.struct.BlockHeader;
import tk.thelocky.eazyarch.struct.ClusterHeader;

import java.util.Map;
import java.util.TreeMap;

public class BlockManager {
    ArchiveHeader archiveHeader;
    PosCalculator calculator;
    NativeFileIOStream fileIOStream;

    class ClusterStorage {
        ClusterHeader head;
        TreeMap<Integer, BlockHeader> map;

        public ClusterStorage(ClusterHeader head) {
            this.head = head;
            this.map = new TreeMap<>();
        }
    }

    TreeMap<Integer, ClusterStorage> storage;

    public BlockManager(ArchiveHeader archiveHeader, NativeFileIOStream stream) {
        storage = new TreeMap<>();
        this.archiveHeader = archiveHeader;
        if (archiveHeader != null)
            calculator = new PosCalculator(archiveHeader);
        else calculator = null;
        fileIOStream = stream;
    }

    private ClusterStorage getClusterStorage(int idx) {
        if (storage.containsKey(idx)) return storage.get(idx);
        else if (!archiveHeader.getClusterMap().get(idx)) {
            archiveHeader.getClusterMap().set(idx);
            ClusterStorage cs = new ClusterStorage(new ClusterHeader());
            storage.put(idx, cs);
            return cs;
        }
        else {
            //load cluster from file
            if (fileIOStream == null || calculator == null) return null;
            long oldPos = fileIOStream.pos();
            fileIOStream.seek(calculator.getClusterPos(idx));
            byte[] cData = new byte[ClusterHeader.size];
            int bytesRead = fileIOStream.read(cData);
            fileIOStream.seek(oldPos);
            if (bytesRead != cData.length && Constants.DEBUG_MODE) {
                System.out.println("Warning! An error detected in reading CLUSTER data from file:");
                System.out.println("Cluster " + idx);
            }
            ClusterStorage cs = new ClusterStorage(ClusterHeader.fromData(cData));
            storage.put(idx, cs);
            return cs;

        }
    }

    public ClusterHeader getClusterHeader(int idx) {
        ClusterStorage cs = getClusterStorage(idx);
        if (cs == null) return null;
        else return cs.head;
    }

    public BlockHeader getBlockHeader(int clIdx, int idx) {
        ClusterStorage cs = getClusterStorage(clIdx);
        if (cs == null) return null;
        if (cs.map.containsKey(idx)) return cs.map.get(idx);
        else {
            //load block from file
            long oldPos = fileIOStream.pos();
            fileIOStream.seek(calculator.getBlockPos(clIdx, idx));
            byte[] bData = new byte[BlockHeader.size];
            int bytesRead = fileIOStream.read(bData);
            fileIOStream.seek(oldPos);
            if (bytesRead != bData.length && Constants.DEBUG_MODE) {
                System.out.println("Warning! An error detected in reading BLOCK data from file:");
                System.out.println("Cluster " + clIdx + " Block " + idx);
            }
            BlockHeader bHead = BlockHeader.fromData(bData);
            cs.map.put(idx, bHead);
            return bHead;
        }
    }

    private void addBlockHeaderInStorage(int clIdx, int idx, BlockHeader header) {
        ClusterStorage cs = storage.getOrDefault(clIdx, null);
        if (cs == null) {
            if (Constants.DEBUG_MODE) {
                System.out.println("Warning! Add block header in not loaded cluster: " + clIdx);
                System.out.println("Add new cluster in storage (possible loos data)");
            }
            cs = new ClusterStorage(new ClusterHeader());
            storage.put(clIdx, cs);
        }
        cs.map.put(idx, header);
    }

    public long getFreeBlock(int startClusterIdx) {
        ClusterHeader cHead = getClusterHeader(startClusterIdx);
        if (cHead == null) return -1;
        long free = cHead.getFirstFreeBlock();
        if (free == Constants.NO_HAVE_BLOCK) {
            if (cHead.getBlocksCount() >= Constants.BLOCKS_IN_CLUSTER) {
                if (Constants.DEBUG_MODE) {
                    System.out.println("Warning! An error is detected in cluster firstFreeBlock:");
                    System.out.println("Blocks not available, but value is NO_HAVE_BLOCK");
                }
                cHead.setFirstFreeBlock(Constants.ALL_BLOCKS_NOT_AVAILABLE);
                return getFreeBlock(startClusterIdx + 1);
            }
            //create new block on back
            long firstBlockPos = calculator.getClusterPos(startClusterIdx) + ClusterHeader.size;
            long newBlockPos = firstBlockPos + cHead.getBlocksCount() * archiveHeader.getBlockSize();
            addBlockHeaderInStorage(startClusterIdx, cHead.getBlocksCount(), new BlockHeader());
            cHead.getBlockMap().set(cHead.getBlocksCount());
            if (cHead.getBlocksCount() >= Constants.BLOCKS_IN_CLUSTER - 1)
                cHead.setFirstFreeBlock(Constants.ALL_BLOCKS_NOT_AVAILABLE);
            cHead.increaseBlocksCount();
            return newBlockPos;
        } else if (free == Constants.ALL_BLOCKS_NOT_AVAILABLE) {
            //go to next cluster
            return getFreeBlock(startClusterIdx + 1);
        } else {
            if (Constants.DEBUG_MODE) {
                int cNum = calculator.getClusterNum(free);
                System.out.print("Cluster " + startClusterIdx + "; free block pos " + free);
                System.out.println(cNum == startClusterIdx ? " valid" : " not valid");
            }
            //reserve block and set next
            int blockIdx = calculator.getBlockNum(free);
            cHead.getBlockMap().set(blockIdx);
            if (blockIdx >= Constants.BLOCKS_IN_CLUSTER - 1) { //last block
                cHead.setFirstFreeBlock(Constants.ALL_BLOCKS_NOT_AVAILABLE);
            } else {
                BlockHeader bHead = getBlockHeader(startClusterIdx, blockIdx);
                cHead.setFirstFreeBlock(bHead.getNextBlock());
            }
            return free;
        }
    }

    public void freeBlock(long blockPos) {
        if (archiveHeader == null) return;
        int clIdx = calculator.getClusterNum(blockPos);
        if (!archiveHeader.getClusterMap().get(clIdx)) { //cluster not created
            if (Constants.DEBUG_MODE) {
                System.out.println("Warning! An attempt to free the block in uncreated cluster:");
                System.out.println("Cluster " + clIdx + " Block pos " + blockPos);
            }
            return;
        }
        int blockIdx = calculator.getBlockNum(blockPos);
        ClusterHeader cHead = getClusterHeader(clIdx);
        if (blockIdx >= cHead.getBlocksCount()) {
            if (Constants.DEBUG_MODE) {
                System.out.println("Warning! An attempt to free uncreated block:");
                System.out.println("Cluster " + clIdx + " Block " + blockIdx +
                        " Blocks in cluster" + cHead.getBlocksCount());
            }
            return;
        }
        cHead.getBlockMap().clear(blockIdx);
        long startPosOfThisBlock = calculator.getBlockPos(clIdx, blockIdx);
        if (cHead.getFirstFreeBlock() == Constants.NO_HAVE_BLOCK ||
                cHead.getFirstFreeBlock() == Constants.ALL_BLOCKS_NOT_AVAILABLE) {
            cHead.setFirstFreeBlock(startPosOfThisBlock);
        } else {
            BlockHeader bHead = getBlockHeader(clIdx, blockIdx);
            int prevFreeBlock = -1;
            for (int i = blockIdx - 1; i >= calculator.getBlockNum(cHead.getFirstFreeBlock()); i--) {
                if (!cHead.getBlockMap().get(i)) {
                    prevFreeBlock = i;
                    break;
                }
            }
            if (prevFreeBlock == -1) {
                bHead.setNextBlock(cHead.getFirstFreeBlock());
                cHead.setFirstFreeBlock(startPosOfThisBlock);
            } else {
                BlockHeader prevBHead = getBlockHeader(clIdx, prevFreeBlock);
                bHead.setNextBlock(prevBHead.getNextBlock());
                prevBHead.setNextBlock(startPosOfThisBlock);
            }
        }
    }

    public void saveAllChanges() {
        if (fileIOStream == null) return;
        fileIOStream.seek(0);
        int written = fileIOStream.write(archiveHeader.getData());
        if (written != ArchiveHeader.size && Constants.DEBUG_MODE) {
            System.out.println("Warning! An error detected in writing HEADER data to file");
        }
        for (Map.Entry<Integer, ClusterStorage> cluster : storage.entrySet()) {
            int clIdx = cluster.getKey();
            ClusterStorage cs = cluster.getValue();
            fileIOStream.seek(calculator.getClusterPos(clIdx));
            written = fileIOStream.write(cs.head.getData());
            if (written != ClusterHeader.size && Constants.DEBUG_MODE) {
                System.out.println("Warning! An error detected in writing CLUSTER data to file:");
                System.out.println("Cluster " + clIdx);
            }
            for (Map.Entry<Integer, BlockHeader> block : cs.map.entrySet()) {
                int idx = block.getKey();
                BlockHeader bHead = block.getValue();
                fileIOStream.seek(calculator.getBlockPos(clIdx, idx));
                written = fileIOStream.write(bHead.getData());
                if (written != BlockHeader.size && Constants.DEBUG_MODE) {
                    System.out.println("Warning! An error detected in writing BLOCK data to file");
                    System.out.println("Cluster " + clIdx + " Block " + idx);
                }
            }
        }
    }
}
