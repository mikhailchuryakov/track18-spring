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
            byte[] buf = new byte[3];
            int count;
            while (true) {
                byte[] out;
                if ((count = is.read(buf, 0, 3)) < 0) break;
                else out = encodeByte(buf, count);
                os.write(out);
            }
            os.flush();
        }
        return fout;
    }

    private static byte[] encodeByte(byte[] buf, int count){
        byte fc, sc, tc, foc;     //First Code letter etc...
        if (count > 2) foc = (byte) toBase64[(buf[2] & 0b00111111)];
        else {
            foc = '=';
            buf[2] = 0;
        }
        if (count > 1) tc = (byte) toBase64[((buf[1] & 0b00001111) << 2) + ((buf[2] & 0b11000000) >> 6)];
        else {
            tc = '=';
            buf[1] = 0;
        }
        sc = (byte) toBase64[((buf[0] & 0b00000011) << 4) + ((buf[1] & 0b11110000) >> 4)];
        fc  = (byte) toBase64[(buf[0] & 0b11111100) >> 2];
        return new byte[]{fc, sc, tc, foc};
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
