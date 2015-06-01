package tk.thelocky.eazyarch.compress;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import tk.thelocky.eazyarch.stream.DataIOStream;
import tk.thelocky.eazyarch.stream.NativeFileIOStream;

import java.io.*;

public class DefaultCompressor implements Compressor {
    private final IntegerProperty progress = new SimpleIntegerProperty(0);

    @Override
    public IntegerProperty getProgressProperty() {
        return progress;
    }

    @Override
    public long compress(NativeFileIOStream inStream, DataIOStream outStream) throws IOException {
        long count = 0;
        progress.setValue(0);
        long size = inStream.size();
        while (true) {
            byte ch = inStream.getc();
            if (inStream.eof())
                break;
            outStream.putc(ch);
            count++;
            progress.setValue((int) ((float) count / size * 100));
        }
        return count;
    }

    @Override
    public boolean decompress(DataIOStream inStream, NativeFileIOStream outStream) throws IOException {
        long count = 0;
        progress.setValue(0);
        long size = inStream.size();

        while (!inStream.eof()) {
            outStream.putc(inStream.getc());
            count++;
            progress.setValue((int) ((float) count / size * 100));
            if (inStream.fail()) {
                throw new IOException("Found error in inStream (DataIOStream)");
            }
        }
        System.out.println("decompressed " + count + " bytes");
        return true;
    }
}
