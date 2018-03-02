package ru.track.io;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.*;
import java.net.InetSocketAddress;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                final InputStream is = new FileInputStream(fin);
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(fout));

        ) {
            byte[] buf3 = new byte[3];
            byte[] buf2 = new byte[2];
            byte[] buf1 = new byte[1];
            while (true){
                String out = null;
                if (is.available() >= 3) {
                    if (is.read(buf3, 0, 3) < 0) break;
                    out = encodeByte(buf3);
                }
                else if (is.available() == 2){      //work only with two symbols
                    if (is.read(buf2, 0, 2) < 0) break;
                    out = encodeByte(buf2);
                }
                else if (is.available() < 2){       //work only with one symbol
                    if (is.read(buf1, 0, 1) < 0) break;
                    out = encodeByte(buf1);
                }
                if (out != null) {
                    os.write(out.getBytes()); //write three bytes
                }
            }
            os.flush();
        }
        return fout;
    }

    private static String encodeByte(byte[] buf){
        int size = buf.length;
        byte s, t;
        byte f = buf[0];     //first byte
        if(size > 1) s = buf[1];     //second
        else s = 0;
        if (size > 2) t = buf[2];     //third
        else t = 0;
        int fc, sc, tc, foc;     //First Code byte etc...
        int[] binArr1, binArr2, binArr3;
        binArr1 = toEightNumbersArray(Integer.toBinaryString(f).toCharArray());
        if (f >= 0) fc = f >> 2;
        else fc = (binArr1[7] << 5) + (binArr1[6] << 4) + (binArr1[5] << 3) + (binArr1[4] << 2) + (binArr1[3] << 1) + binArr1[2];
        binArr2 = toEightNumbersArray(Integer.toBinaryString(s).toCharArray());
        sc = (binArr1[1] << 5) + (binArr1[0] << 4) + (s >= 0 ? (s >> 4) : (binArr2[7] << 3) + (binArr2[6] << 2) + (binArr2[5] << 1) + binArr2[4]);
        binArr3 = toEightNumbersArray(Integer.toBinaryString(t).toCharArray());
        if (size > 1) tc = (binArr2[3] << 5) + (binArr2[2] << 4) + (binArr2[1] << 3) + (binArr2[0] << 2) +(t >= 0 ? (t >> 6) : (binArr3[7] << 1) + binArr3[6]);
        else tc = '=';
        if (size > 2) foc = (binArr3[5] << 5) + (binArr3[4] << 4) + (binArr3[3] << 3) + (binArr3[2] << 2) +(binArr3[1] << 1) + binArr3[0];
        else foc = '=';
        return "" + toBase64[fc] + toBase64[sc] + (size <= 1 ? "=" : toBase64[tc]) + (size <= 2 ? "=" : toBase64[foc]);
    }

    private static int[] toEightNumbersArray(char[] array){
        int[] nullArray = new int[8];
        int i = 0, size = array.length - 1;
        if (size  > 7) {
            char[] eightArray = new char[8];
            for(i = 0; i < 8; i++){
                eightArray[i] = array[size - 7 + i];
            }
            i = 0;
            for(char arr: eightArray){
                nullArray[7 - i] = arr - '0';
                i++;
                if(size - i < 0) break;
            }
        }
        else {
            for (char arr : array) {
                nullArray[size - i] = arr - '0';
                i++;
                if (size - i < 0) break;
            }
        }
        return nullArray;
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws Exception {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        (new Bootstrapper(args, encoder))
                .bootstrap("", new InetSocketAddress("127.0.0.1", 9000));
    }

}
