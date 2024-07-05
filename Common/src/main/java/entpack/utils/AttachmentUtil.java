package entpack.utils;

import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttachmentUtil {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM/dd");

    /**
     * @param uploadFile
     * @return new file relative path
     */
    public static String moveFile(UploadFile uploadFile) {
        return moveFile(uploadFile, "attachment/upload");
    }

    public static String moveFile(UploadFile uploadFile, String uploadPath) {
        if (uploadFile == null)
            return null;

        File file = uploadFile.getFile();
        if (!file.exists()) {
            return null;
        }

        String webRoot = PathKit.getWebRootPath();

        String uuid = StringUtil.UUID().replace("-", "");

        StringBuilder newFileName = new StringBuilder(webRoot).append(File.separator).append(uploadPath)
                .append(File.separator)
                .append(dateFormat.format(new Date()))
                .append("_" + uuid)
                .append(FileUtil.getSuffix(file.getName().toLowerCase()));

        File newfile = new File(newFileName.toString());

        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }

        file.renameTo(newfile);

        return FileUtil.removePrefix(newfile.getAbsolutePath(), webRoot);
    }

    /**
     * @param uploadFile
     * @return new file relative path
     */
    public static String moveTempFile(UploadFile uploadFile) {
        if (uploadFile == null)
            return null;

        File file = uploadFile.getFile();
        if (!file.exists()) {
            return null;
        }

        String webRoot = PathKit.getWebRootPath();

        String uuid = StringUtil.UUID().replace("-", "");

        StringBuilder newFileName = new StringBuilder(webRoot).append(File.separator).append("upload")
                .append(File.separator).append("temp")
                .append(File.separator).append(dateFormat.format(new Date()))
                .append(File.separator).append(uuid)
                .append(FileUtil.getSuffix(file.getName()));

        File newfile = new File(newFileName.toString());

        if (!newfile.getParentFile().exists()) {
            newfile.getParentFile().mkdirs();
        }

        file.renameTo(newfile);

        return FileUtil.removePrefix(newfile.getAbsolutePath(), webRoot);
    }

    static List<String> imageSuffix = new ArrayList<String>();

    static {
        imageSuffix.add(".jpg");
        imageSuffix.add(".jpeg");
        imageSuffix.add(".png");
        imageSuffix.add(".gif");
    }

    public static boolean isImage(String path) {
        String sufffix = FileUtil.getSuffix(path);
        if (StringUtil.isNotBlank(sufffix))
            return imageSuffix.contains(sufffix.toLowerCase());
        return false;
    }

    /**
     * 计算文件MD5唯一码
     *
     * @param file 需计算的文件
     * @return 返回字符串
     * @throws FileNotFoundException
     */
    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println(FileUtil.getSuffix("xxx.jpg"));
    }

}