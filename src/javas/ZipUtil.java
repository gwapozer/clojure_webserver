package javas;

import net.lingala.zip4j.core.ZipFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by ENT_ASUS_LAPTOP on 4/9/2019.
 */
public class ZipUtil
{

//    public static final int firstchar = (int)'0';
//    public static final int lastchar = (int)'9';

    public static final int firstchar = 32;
    public static final int lastchar = 127;

    public static int GetNonLastPosition(String word)
    {
        char[] stringToCharArray = word.toCharArray();
        int len = stringToCharArray.length;
        int eval = 0;

        for (int i = len - 1; i >= 0;i--)
        {
            if((int)stringToCharArray[i] != lastchar)
            {
                eval = i;
                break;
            }
        }

        return eval;
    }

    public static Boolean AllLastPosition(String word)
    {
        char[] stringToCharArray = word.toCharArray();
        int len = stringToCharArray.length;
        Boolean eval = true;

        for (int i = len - 1; i >= 0;i--)
        {
            if((int)stringToCharArray[i] != lastchar)
            {
                eval = false;
                break;
            }
        }

        return eval;
    }

    //Build next combination of password
    public static String nextword(String word)
    {
        String buff_pass = word;
        char[] stringToCharArray = word.toCharArray();
        int len = stringToCharArray.length;

        if(len == 0)
        {
            buff_pass += (char)firstchar;
        }
        else
        {
            int pos = GetNonLastPosition(buff_pass);

            int charint = (int)stringToCharArray[pos];

            if(pos == 0 && charint != lastchar)
            {
                String buff_wd = "";
                for(int j = 0; j < len-1;j++)
                {
                    buff_wd += (char)firstchar;
                }

                buff_pass = (char)(charint + 1) + buff_wd;
            }
            else if (pos == 0 && charint == lastchar)
            {
                String buff_wd = "";
                for(int j = 0; j <= len;j++)
                {
                    buff_wd += (char)firstchar;
                }

                buff_pass =  buff_wd;
            }
            else
            {
                int remainder = (len - 1) - pos;
                if(remainder > 0)
                {
                    for(int i = pos + 1; i < len; i++)
                    {
                        stringToCharArray[i] = (char)firstchar;
                    }
                }

                stringToCharArray[pos] = (char)(charint+1);
                buff_pass = String.valueOf(stringToCharArray);
            }
        }

        return buff_pass;
    }

    private static void writeinfo(String threadName, String msg)
    {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("thread_" + threadName +".txt"));
            out.write(msg);
            out.close();
        }
        catch (IOException e) {
        }
    }

    public static void zipthread( String passwd, String threadName)
    {
        System.out.println("Searching start: " + threadName);

        String rsltMsg = "";
        int counter = 0;
        Boolean found = false;
        while(!found)
        {
            try
            {
                ZipFile zipFile = new ZipFile("C:\\Test\\RAW_DATA\\AddFilesWithAESZipEncryption.zip");
                if (zipFile.isEncrypted())
                {
                    zipFile.setPassword(passwd);
                }
                zipFile.extractAll("C:\\Test\\RAW_DATA\\test_extract");
                found = true;
                rsltMsg = "Password found: " + passwd + "\n\n";
            }
            catch(Exception ex)
            {
                found = AllLastPosition(passwd);
                rsltMsg = "Password not found: " + passwd + "\n\n";
                //found = false;
            }

            passwd = nextword(passwd);
            counter++;
            if(counter % 1000000 == 0) {
                System.out.println(passwd);
                writeinfo(threadName + "currpass", passwd);
            }
        }

        writeinfo(threadName, rsltMsg);
    }

    public static class MyThread extends Thread {

        private String paramstr;
        private String threadName;

        public MyThread(String paramstr, String threadName) {
            this.paramstr = paramstr;
            this.threadName = threadName;
        }

        @Override
        public void run() {
            zipthread(paramstr, threadName);
        }
    }

    public static void main (String[] args){
        try
        {
            //ziptest();
            //unziptest();
            //password_generator();
            //System.out.println(nextword("12" + (char)255 + (char)255));

            new MyThread("0", "Thread 1").start();
            new MyThread("00", "Thread 2").start();
            new MyThread("000", "Thread 3").start();
            new MyThread("0000", "Thread 4").start();

        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}