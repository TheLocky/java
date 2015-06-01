package tk.thelocky.eazyarch.stream;

public class NativeFileIOStream {
    private String filePath;
    private long filePointer;

    public NativeFileIOStream(String filePath) {
        this.filePath = filePath;
        filePointer = 0;
    }

    private native long _open(byte[] file, String mode);
    private native boolean _close(long pointer);
    private native int _getc(long pointer);
    private native boolean _putc(long pointer, int ch);
    private native boolean _seek(long pointer, long pos);
    private native long _pos(long pointer);
    private native boolean _eof(long pointer);
    private native void _flush(long pointer);
    private native long _size(long pointer);

    public boolean open(String mode) {
        filePointer = _open((filePath + '\0').getBytes(), mode);
        return filePointer > 0;
    }

    public boolean close() {
        return _close(filePointer);
    }

    public int read(byte[] data) {
        int count = 0;
        if (data != null && data.length > 0) {
            while (!_eof(filePointer) && count < data.length) {
                data[count] = (byte) _getc(filePointer);
                count++;
            }
        }
        return count;
    }

    public int write(byte[] data) {
        int count = 0;
        if (data != null && data.length > 0) {
            while (count < data.length) {
                _putc(filePointer, (int) data[count]);
                count++;
            }
        }
        return count;
    }

    public byte getc() {
        return (byte)_getc(filePointer);
    }

    public boolean putc(byte ch) {
        return filePointer != 0 && _putc(filePointer, (int) ch);
    }

    public boolean seek(long pos) {
        return filePointer != 0 && _seek(filePointer, pos);
    }

    public long pos() {
        return filePointer != 0 ? _pos(filePointer) : -1;
    }

    public boolean eof() {
        return filePointer != 0 && _eof(filePointer);
    }

    public long size() {
        return filePointer != 0 ? _size(filePointer) : -1;
    }

    public void flush() {
        _flush(filePointer);
    }
}
