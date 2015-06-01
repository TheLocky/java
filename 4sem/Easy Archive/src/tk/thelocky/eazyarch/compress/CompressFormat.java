package tk.thelocky.eazyarch.compress;

public class CompressFormat {
    public static final int DO_COMPRESS = 1;
    public static final int DO_DECOMPRESS = 2;

    public static final int NO_COMPRESS = 0;
    //public static final int RLE = 1;
    //....

    public static String formatToString(int type) {
        switch (type) {
            case 0:
                return "Без сжатия";
            //TODO: add formats
            default:
                return "Неизвестный";
        }
    }

    public static Compressor getCompressor(int type) {
        switch (type) {
            case 0:
            default:
                return new DefaultCompressor();
        }
    }
}
