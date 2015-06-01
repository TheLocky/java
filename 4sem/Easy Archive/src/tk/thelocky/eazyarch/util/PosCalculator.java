package tk.thelocky.eazyarch.util;

import com.sun.istack.internal.NotNull;
import tk.thelocky.eazyarch.struct.ArchiveHeader;
import tk.thelocky.eazyarch.struct.ClusterHeader;

public class PosCalculator {
    private long _s_b_size;
    private ArchiveHeader _arch_header;

    public PosCalculator(@NotNull ArchiveHeader header) {
        _arch_header = header;
        _s_b_size = Constants.BLOCKS_IN_CLUSTER * _arch_header.getBlockSize();
    }

    public int getClusterNum(long pos) {
        long sbOffset = pos - ArchiveHeader.size;
        return (int) (sbOffset / _s_b_size);
    }

    public int getBlockNum(long pos) {
        long bOffset = pos - ArchiveHeader.size -
                (getClusterNum(pos) * _s_b_size) - ClusterHeader.size;
        return (int) (bOffset / _arch_header.getBlockSize());
    }

    public long getClusterPos(int num) {
        return ArchiveHeader.size + num * _s_b_size;
    }

    public long getBlockPos(int superBlockNum, int num) {
        return getClusterPos(superBlockNum) +
                ClusterHeader.size + num * _arch_header.getBlockSize();
    }
}
