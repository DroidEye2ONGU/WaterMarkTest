import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class ZipTest {

    public static void main(String[] args) throws IOException {
        zipFile("压缩.pdf", "x:/");
    }

    /**
     * 压缩单个文件
     *
     * @param fileName 需要压缩的文件名
     * @param filePath 需要压缩的文件路径
     */
    public static void zipFile(String fileName, String filePath) {
        File srcfile = new File(filePath + fileName);
        if (!srcfile.exists()) {
            return;
        }
        byte[] buf = new byte[1024];
        FileInputStream in = null;
        ZipOutputStream out = null;
        try {
            File targetFile = new File(srcfile.getPath() + ".zip");
            out = new ZipOutputStream(new FileOutputStream(targetFile));
            int len;
            in = new FileInputStream(srcfile);
            out.putNextEntry(new ZipEntry(fileName));

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.closeEntry();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
