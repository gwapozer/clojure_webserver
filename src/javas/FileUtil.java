package javas;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by marcfontaine on 12/6/16.
 */

public class FileUtil {

    public static byte[] readFully(InputStream is, int length, boolean readAll) throws IOException
    {
        byte[] output = {};
        if (length == -1) length = Integer.MAX_VALUE;
        int pos = 0;
        while (pos < length) {
            int bytesToRead;
            if (pos >= output.length) { // Only expand when there's no room
                bytesToRead = Math.min(length - pos, output.length + 1024);
                if (output.length < pos + bytesToRead) {
                    output = Arrays.copyOf(output, pos + bytesToRead);
                }
            } else {
                bytesToRead = output.length - pos;
            }
            int cc = is.read(output, pos, bytesToRead);
            if (cc < 0) {
                if (readAll && length != Integer.MAX_VALUE) {
                    throw new EOFException("Detect premature EOF");
                } else {
                    if (output.length != pos) {
                        output = Arrays.copyOf(output, pos);
                    }
                    break;
                }
            }
            pos += cc;
        }
        return output;
    }

    public static byte[] readFully(InputStream inputStream)throws IOException
    {
        byte[] output = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream buffIS = new BufferedInputStream(inputStream);

        int numByte = buffIS.available();
        if(numByte > 0)
        {
            byte[] buf = new byte[numByte];
            int count = 0;
            while ((count=buffIS.read(buf)) != -1) { baos.write(buf,0,count);}
            output = baos.toByteArray();
        }

        baos.close();
        buffIS.close();

        return output;
    }

    public static void WriteToDirectory(byte[] bytearray, String saveDir) throws IOException
    {
        FileOutputStream outputStream = new FileOutputStream(saveDir);
        outputStream.write(bytearray);
        outputStream.close();
    }

//    public  static  void SetFileModifiedDate(String FilePath, Long ModifiedDate) throws  IOException
//    {
//        File f = new File(FilePath);
//        f.setLastModified(ModifiedDate);
//    }
}
