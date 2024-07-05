package entpack;

import com.jfinal.core.JFinal;
import entpack.utils.FileUtil;

import java.io.IOException;

public class JarRun {

    public static void main(String[] args) {


        String baseBath = String.valueOf(JarRun.class.getProtectionDomain().getCodeSource().getLocation());
        boolean windows = System.getProperties().getProperty("os.name").contains("Windows");

        String jarPath = (windows ? "" : "/") + baseBath.substring("file:/".length());
        String classPath = (windows ? "" : "/") + jarPath.substring(0, jarPath.lastIndexOf("/")) + "/class-path";
        String path = JarRun.class.getResource("").getPath();
        System.out.println(path);

        try {
            FileUtil.unzip(jarPath, classPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFinal.start(classPath + "/webapp", 8090, "/", 3600);
    }
}
