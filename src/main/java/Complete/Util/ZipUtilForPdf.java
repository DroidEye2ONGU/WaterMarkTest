package Complete.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtilForPdf {

    private ZipUtilForPdf() {

    }

    /**
     * 压缩单个文件
     *
     * @param sourcePath 需要压缩的文件绝对路径
     * @param targetPath 生成的压缩文件路径
     */
    public static void zipFile(String sourcePath, String targetPath) {
        File srcfile = new File(sourcePath);
        if (!srcfile.exists()) {
            return;
        }
        byte[] buf = new byte[1024];
        FileInputStream in = null;
        ZipOutputStream out = null;
        try {
            File targetFile = new File(targetPath);
            out = new ZipOutputStream(new FileOutputStream(targetFile));
            int len;
            in = new FileInputStream(srcfile);
            out.putNextEntry(new ZipEntry("waterMark.pdf"));

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
