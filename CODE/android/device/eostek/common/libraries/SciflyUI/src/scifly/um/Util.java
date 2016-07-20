
package scifly.um;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Psso.Song, This is a tool class.
 */
public class Util {
    private static final int MAX_BYTE = 4096;

    private static final int MD5_LENGTH = 20;

    private static final int OFFSET_0XFF = 0xff;

    private static final int OFFSET_0X3F = 0x3f;

    private static final int MD5_SUBSTRING_LENGTH = 16;

    private static final int BASE64_BYTE_LENGTH = 128;

    private static final int OFFSET_1 = 1;

    private static final int OFFSET_2 = 2;

    private static final int OFFSET_3 = 3;

    private static final int OFFSET_4 = 4;

    private static final int OFFSET_6 = 6;

    private static final int OFFSET_8 = 8;

    private static final int OFFSET_10 = 10;

    private static final int OFFSET_12 = 12;

    private static final int OFFSET_16 = 16;

    private static final int OFFSET_18 = 18;

    public static final String CHARSET_NAME = "UTF-8";

    /**
     * @param is {@link java.io.InputStream} that need to be compressed.
     * @param os {@link java.io.OutputStream} result of this compression
     * @throws Exception throws unexpected Exception
     */
    public static void compress(InputStream is, OutputStream os) throws Exception {
        GZIPOutputStream gos = new GZIPOutputStream(os);

        int count = 0;
        byte[] data = new byte[MAX_BYTE];
        while ((count = is.read(data, 0, MAX_BYTE)) != -1) {
            gos.write(data, 0, count);
        }
        gos.finish();
        gos.flush();
        gos.close();
    }

    /**
     * @param is {@link java.io.InputStream} that need to be decompressed.
     * @param os {@link java.io.OutputStream} result of this decompression
     * @throws Exception throws unexpected Exception
     */
    public static void decompress(InputStream is, OutputStream os) throws Exception {
        GZIPInputStream gis = new GZIPInputStream(is);

        int count = 0;
        byte[] data = new byte[MAX_BYTE];
        while ((count = gis.read(data, 0, MAX_BYTE)) != -1) {
            os.write(data, 0, count);
        }
        gis.close();
    }

    /**
     * @param path Absolute path of the file that need to be Base64Encode.
     * @return Base64 encoded String.
     */
    public static String fileToString(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(MAX_BYTE);
            byte[] b = new byte[MAX_BYTE];
            int count = -1;
            while ((count = fis.read(b)) != -1) {
                bos.write(b, 0, count);
            }
            fis.close();
            bos.close();
            return base64Encode(bos.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param str String that need to be Gzip.
     * @return String that be gzip.
     */
    public static String gzipString(String str) {
        String res = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes(CHARSET_NAME));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            compress(bais, baos);

            res = baos.toString(CHARSET_NAME);
            // Close all the stream
            baos.flush();
            baos.close();
            bais.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * @param input String that need to be calculate md5.
     * @return Md5 String.
     */
    public static String calcMD5Lim2(String input) {
        String result = calcMD5(input.getBytes());

        return result.substring(0, MD5_LENGTH);
    }

    private static String calcMD5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        // generate MessageDigest
        md.update(data);
        byte[] hash = md.digest();

        // translate to string
        StringBuffer sbRet = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            int v = hash[i] & OFFSET_0XFF;
            if (v < MD5_SUBSTRING_LENGTH) {
                sbRet.append("0");
            }
            sbRet.append(Integer.toString(v, MD5_SUBSTRING_LENGTH));
        }

        return sbRet.toString();
    }

    /**
     * @param data Byte array that need to be encoded.
     * @return String of encoded result.
     */
    public static String base64Encode(byte[] data) {
        final char[] sBASE64CHAR = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', '+', '/'
        };
        final char sBASE64PAD = '=';
        final byte[] sDECODETABLE = new byte[BASE64_BYTE_LENGTH];

        for (int i = 0; i < sDECODETABLE.length; i++) {
            sDECODETABLE[i] = Byte.MAX_VALUE; // 127
        }
        for (int i = 0; i < sBASE64CHAR.length; i++) { // 0 to 63
            sDECODETABLE[sBASE64CHAR[i]] = (byte) i;
        }

        if (data.length <= 0) {
            return "";
        }
        char[] out = new char[data.length / OFFSET_3 * OFFSET_4 + OFFSET_4];
        int rindex = 0;
        int windex = 0;
        int rest = data.length;
        while (rest >= OFFSET_3) {
            int i = ((data[rindex] & OFFSET_0XFF) << OFFSET_16) + ((data[rindex + OFFSET_1] & OFFSET_0XFF) << OFFSET_8)
                    + (data[rindex + OFFSET_2] & OFFSET_0XFF);
            out[windex++] = sBASE64CHAR[i >> OFFSET_18];
            out[windex++] = sBASE64CHAR[(i >> OFFSET_12) & OFFSET_0X3F];
            out[windex++] = sBASE64CHAR[(i >> OFFSET_6) & OFFSET_0X3F];
            out[windex++] = sBASE64CHAR[i & OFFSET_0X3F];
            rindex += OFFSET_3;
            rest -= OFFSET_3;
        }
        if (rest == 1) {
            int i = data[rindex] & OFFSET_0XFF;
            out[windex++] = sBASE64CHAR[i >> OFFSET_2];
            out[windex++] = sBASE64CHAR[(i << OFFSET_4) & OFFSET_0X3F];
            out[windex++] = sBASE64PAD;
            out[windex++] = sBASE64PAD;
        } else if (rest == OFFSET_2) {
            int i = ((data[rindex] & OFFSET_0XFF) << OFFSET_8) + (data[rindex + 1] & OFFSET_0XFF);
            out[windex++] = sBASE64CHAR[i >> OFFSET_10];
            out[windex++] = sBASE64CHAR[(i >> OFFSET_4) & OFFSET_0X3F];
            out[windex++] = sBASE64CHAR[(i << OFFSET_2) & OFFSET_0X3F];
            out[windex++] = sBASE64PAD;
        }
        return new String(out, 0, windex);
    }
}
