package tk.thelocky.eazyarch.util;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.BitSet;

public class Converting {

    public static BitSet byteArrayToBitSet(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i / 8] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }

    public static long getUnixTime() {
        return System.currentTimeMillis();
    }

    public static String getCorrectPath(@NotNull String path) {
        path = path.replaceAll("\\\\", "/");
        if ((path.length() > 0) && (path.charAt(0) == '/'))
            path = path.substring(1);
        return path;
    }

    public static String getShortPath(String path, int maxLength) {
        Path p = new File(path).toPath();
        boolean left = false;
        int pathCount = p.getNameCount();
        if (pathCount < 3)
            return p.toString();
        Path r = p.getRoot();
        String root = r != null ? r.toString() : "";
        int offset = r != null ? 3 : 2;
        int leftCount = 1;
        int rightCount = 1;
        String prefix = root + p.getName(0).toString();
        String postfix = p.getName(pathCount - 1).toString();
        String resultPath = prefix + "/.../" + postfix;
        String oldPath = resultPath;
        while (resultPath.length() < maxLength) {
            if (leftCount + rightCount - offset == pathCount)
                return getCorrectPath(p.toString());
            if (left) {
                prefix = root + p.subpath(0, leftCount).toString();
                leftCount++;
                left = false;
            } else {
                postfix = p.subpath(pathCount - rightCount, pathCount).toString();
                rightCount++;
                left = true;
            }
            oldPath = resultPath;
            resultPath = prefix + "/.../" + postfix;
        }
        return getCorrectPath(oldPath);
    }

}
