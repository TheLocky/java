package tk.thelocky.eazyarch.compress;

import javafx.beans.property.IntegerProperty;
import tk.thelocky.eazyarch.stream.DataIOStream;
import tk.thelocky.eazyarch.stream.NativeFileIOStream;

import java.io.IOException;

/**
 * ������ 2
 * ��� ���������:
 * - ��������� ��������� @NotNull.
 *
 * ������ 3
 * ��� ���������:
 * - ��� ������������� �������� compress ������� � int �� long.
 *
 * ������ 4
 * ��� ���������:
 * - ��������� ���� ���������
 *
 * ������ 5
 * ��� ���������:
 * - ������ ������� ��������� ������ ������ ������
 * - ������� ��������� @NotNull
 */
public interface Compressor {
    /**
     * ���������� ���� ���������
     * @return ���� ���������
     */
    IntegerProperty getProgressProperty();

    /**
     * ������� ����, ��������� ��� � �����
     * @param inStream ������� �����
     * @param outStream �������� �����
     * @return ���������� ���������� ����
     * @throws IOException
     */
    long compress(NativeFileIOStream inStream, DataIOStream outStream) throws IOException;

    /**
     * ��������������� ������ ���� �� ������
     * @param inStream ������� �����
     * @param outStream �������� �����
     * @throws IOException
     */
    boolean decompress(DataIOStream inStream, NativeFileIOStream outStream) throws IOException;
}
