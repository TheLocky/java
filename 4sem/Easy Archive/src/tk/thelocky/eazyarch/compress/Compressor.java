package tk.thelocky.eazyarch.compress;

import javafx.beans.property.IntegerProperty;
import tk.thelocky.eazyarch.stream.DataIOStream;
import tk.thelocky.eazyarch.stream.NativeFileIOStream;

import java.io.IOException;

/**
 * Версия 2
 * Лог изменений:
 * - добавлена аннотация @NotNull.
 *
 * Версия 3
 * Лог изменений:
 * - тип возвлащаемого значения compress изменен с int на long.
 *
 * Версия 4
 * Лог изменений:
 * - добавлено поле прогресса
 *
 * Версия 5
 * Лог изменений:
 * - теперь функции принимают потоки вместо файлов
 * - удалена аннотация @NotNull
 */
public interface Compressor {
    /**
     * Возвращает поле прогресса
     * @return поле прогресса
     */
    IntegerProperty getProgressProperty();

    /**
     * Сжимает файл, записывая его в поток
     * @param inStream Входной поток
     * @param outStream Выходной поток
     * @return Количество записанных байт
     * @throws IOException
     */
    long compress(NativeFileIOStream inStream, DataIOStream outStream) throws IOException;

    /**
     * Восстанавливает сжатый файл из потока
     * @param inStream Входной поток
     * @param outStream Выходной поток
     * @throws IOException
     */
    boolean decompress(DataIOStream inStream, NativeFileIOStream outStream) throws IOException;
}
