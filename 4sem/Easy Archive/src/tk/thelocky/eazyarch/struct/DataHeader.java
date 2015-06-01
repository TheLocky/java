package tk.thelocky.eazyarch.struct;

import com.sun.istack.internal.NotNull;

public interface DataHeader {
    byte[] getData();
    void setData(@NotNull byte [] data);
}
