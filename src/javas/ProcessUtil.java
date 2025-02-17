package javas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Kimberly on 11/19/2019.
 */
public class ProcessUtil {

    public static void ExecProcCmd(String exe, String path, String cmd) throws IOException
    {
        //"runas /profile /user:Administrator"
        ProcessBuilder pb = new ProcessBuilder(exe, path, cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = inStreamReader.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void ExecCmd(String cmd) throws IOException
    {
        //"runas /profile /user:Administrator"
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

//        BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line = null;
//        while ((line = inStreamReader.readLine()) != null) {
//            System.out.println(line);
//        }

    }

    static String WIN_PROGRAMFILES = System.getenv("programfiles");
    static String FILE_SEPARATOR   = System.getProperty("file.separator");

    public static void main (String[] args) throws Exception
    {
//        String[] commands =
//                {"cmd.exe",
//                        "/c",
//                        WIN_PROGRAMFILES
//                                + FILE_SEPARATOR
//                                + "textpad 4"
//                                + FILE_SEPARATOR + "textpad.exe"};
//        Runtime.getRuntime().exec(commands);

        try
        {
            //ExecProcCmd("cmd.exe", "/c", "netsh wlan show interfaces");
            //"netsh wlan connect name=Ethernet"
            //ExecProcCmd("cmd.exe", "/c", "netsh interface set interface Ethernet disable");
            //ExecProcCmd("cmd.exe", "/c", "netsh wlan connect name=Ethernet");
            //ExecProcCmd("cmd.exe", "/c", "netsh interface show interface");
            //ExecProcCmd("C:\\Program Files\\Microsoft SQL Server\\Client SDK\\ODBC\\130\\Tools\\Binn\\SQLCMD.EXE", "/c", "netsh interface set interface Ethernet disable");

        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
