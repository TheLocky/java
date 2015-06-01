package tk.thelocky.eazyarch.util;

import javafx.scene.input.DataFormat;

public class Constants {
    public static final int HEADER_MAP_SIZE = 2048;
    public static final int BLOCKS_IN_CLUSTER = 512;
    public static final int CLUSTER_MAP_SIZE = BLOCKS_IN_CLUSTER / 8;
    public static final long ARCHIVE_SIGNATURE = 0xEAA00A01;
    public static final long NO_HAVE_BLOCK = 0;
    public static final long ALL_BLOCKS_NOT_AVAILABLE = 1;

    public static final boolean DEBUG_MODE = true;

    public static final DataFormat dragFormat = new DataFormat("drop.esa.decompress");
}
