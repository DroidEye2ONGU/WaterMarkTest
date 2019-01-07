package Complete.Service;

import com.itextpdf.text.DocumentException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Complete.Util.CompressImageUtil;
import Complete.Util.ImageWaterMarkToPdfUtil;
import Complete.Util.ZipUtilForPdf;

public class ImageService {
    /**
     * 将入参目录下的所有图片压缩转成PDF后打成压缩包,返回压缩包所在的全路径
     *
     * @param director 要处理的图片所在的文件夹路径
     * @param outputDirector 存放压缩后图片的文件夹路径
     * @param quality 压缩图片质量 0~2以内的浮点数,越低压缩感越强
     * @param compressThreadNum 压缩图片时要启动的线程数量
     * @return 返回最后生成的压缩包路径,若出现错误,返回error字符串
     * */
    public String transferImagePdfToZip(String director,String outputDirector,float quality,int compressThreadNum) throws IOException, InterruptedException, DocumentException {

        long start = System.currentTimeMillis();

        File directorFile = new File(director);
        if (!directorFile.exists()) {
            return "error";
        }

        //获取目录下的所有文件
        File[] files = directorFile.listFiles();
        List<String> fileList = new ArrayList<>();
        List<String> outputList = new ArrayList<>();
        int index = 1;
        for (File file :
                files) {
            String path = file.getAbsolutePath();
            fileList.add(path);
            String fileName = file.getName();
            outputList.add(outputDirector + index++ + ".jpg");
        }

        //压缩图片
        CompressImageUtil.compressImages(fileList,outputList,quality,compressThreadNum);

        //将图片转成pdf
        String pdfPath = ImageWaterMarkToPdfUtil.putImagesToPdfThenWatermark(new File(outputDirector));

        //将pdf打包成zip
        ZipUtilForPdf.zipFile(pdfPath,"x://测试.zip");

        System.err.println("\n\n共用时:" + ((System.currentTimeMillis() - start) / 1000));

        return "x://测试.zip";
    }
}
