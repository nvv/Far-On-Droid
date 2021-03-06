package com.openfarmanager.android.filesystem.actions;

import android.util.Log;
import com.openfarmanager.android.App;
import com.openfarmanager.android.model.exeptions.RootOperationException;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by sergii on 1/27/2014.
 */
public class RootTask {

    public static void delete(File file) {
        if (!LinuxShell.isRoot() ||
                LinuxShell.execute("rm -r " + LinuxShell.getCmdPath(file.getAbsolutePath())) == null) {
            throw new RootOperationException("Root access is not granted or operation is failed");
        }
    }

    public static boolean create(File file, boolean createDirectory) {
        return LinuxShell.isRoot() && (createDirectory ?
                LinuxShell.execute("mkdir " + LinuxShell.getCmdPath(file.getAbsolutePath())) != null :
                LinuxShell.execute("cat > " + LinuxShell.getCmdPath(file.getAbsolutePath())) != null);
    }

    public static boolean move(File file, File destination) {
        if (copy(file, new File(destination.getAbsolutePath() + "/" + file.getName()))) {
            delete(file);
            return true;
        }

        return false;
    }

    public static boolean rename(String absolutePath, String destinationFilePath) {
        return LinuxShell.isRoot() &&
                LinuxShell.execute("mv " + LinuxShell.getCmdPath(absolutePath) + " " +
                LinuxShell.getCmdPath(destinationFilePath)) != null;

    }

    public static String[] ls(File path) {
        if (!LinuxShell.isRoot()) {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = LinuxShell.execute("ls " + LinuxShell.getCmdPath(path.getAbsolutePath()));
            if (reader == null)
                return null;

            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toArray(new String[result.size()]);
    }

    public static boolean copy(File file, File destination) {
        return LinuxShell.isRoot() && LinuxShell.execute("cat " + LinuxShell.getCmdPath(file.getAbsolutePath()) + " > " +
                LinuxShell.getCmdPath(destination.getAbsolutePath())) != null;
    }

    public static boolean canReadFile(File file) {
        if(!RootTask.LinuxShell.isRoot()){
            return false;
        }
        BufferedReader reader = LinuxShell.execute("cat " + LinuxShell.getCmdPath(file.getAbsolutePath()));
        try {
            reader.read();
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static BufferedReader readFile(File file) {
        if(!RootTask.LinuxShell.isRoot()){
            return null;
        }
        try {
            return LinuxShell.execute("cat " + LinuxShell.getCmdPath(file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveFile(File file, String content) {
        if(!RootTask.LinuxShell.isRoot()) {
            return false;
        }

        try {
            LinuxShell.execute("echo " + LinuxShell.getCmdPath(content) + " > "  +
                    LinuxShell.getCmdPath(file.getAbsolutePath()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean requestRootsAccess(){
        return LinuxShell.isRootTools();
    }

    private static class LinuxShell {

        public static String getCmdPath(String path) {
            return path.replace(" ", "\\ ").replace("'", "\\'");
        }

        public static BufferedReader execute(String cmd) {
            BufferedReader reader;
            try {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(cmd + "\n");
                os.writeBytes("exit\n");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String err = (new BufferedReader(new InputStreamReader(process.getErrorStream()))).readLine();
                os.flush();

                if (process.waitFor() != 0 || (!"".equals(err) && null != err)) {
                    Log.e("920TERoot", err);
                    return null;
                }
                return reader;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public static boolean isRoot() {
            return App.sInstance.getSettings().getRootEnabled() && isRootTools();
        }

        public static boolean isRootTools() {
            boolean retval = false;
            Process suProcess;

            try {
                suProcess = Runtime.getRuntime().exec("su");

                DataOutputStream os =
                        new DataOutputStream(suProcess.getOutputStream());
                DataInputStream osRes =
                        new DataInputStream(suProcess.getInputStream());

                if (null != os && null != osRes) {
                    // Getting the id of the current user to check if this is root
                    os.writeBytes("id\n");
                    os.flush();

                    String currUid = osRes.readLine();
                    boolean exitSu;
                    if (null == currUid) {
                        retval = false;
                        exitSu = false;
                        Log.e("ROOT", "Can't get root access or denied by user");
                    } else if (currUid.contains("uid=0")) {
                        retval = true;
                        exitSu = true;
                    } else {
                        retval = false;
                        exitSu = true;
                        Log.e("ROOT", "Root access rejected: " + currUid);
                    }

                    if (exitSu) {
                        os.writeBytes("exit\n");
                        os.flush();
                    }
                }
            } catch (Exception e) {
                // Can't get root !
                // Probably broken pipe exception on trying to write to output
                // stream after su failed, meaning that the device is not rooted

                retval = false;
                Log.e("ROOT", "Root access rejected [" +
                        e.getClass().getName() + "] : " + e.getMessage());
            }

            return retval;
        }

    }
}
