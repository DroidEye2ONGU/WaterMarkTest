package Complete.Util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import Complete.Thread.ImageThread;

public class CompressImageUtil {

    private CompressImageUtil() {

    }

    public static void compressImages(List<String> fileList, List<String> outputPath, float quality, int threadNum) throws IOException, InterruptedException {
        if (threadNum == 1) {
            //直接压缩
            for (int i = 0; i < fileList.size(); i++) {
                compressPictureByQuality(fileList.get(i), outputPath.get(i), quality);
            }
        } else {
            int imageNum = fileList.size();
            int splitIndex = imageNum / threadNum;

            List<Map<String, List<String>>> handleList = new ArrayList<>();

            for (int i = 0; i < threadNum; i++) {
                Map<String, List<String>> stringListMap = new HashMap<String, List<String>>();
                if (i != threadNum - 1) {
                    int multiple = i + 1;//跃迁倍数
                    stringListMap.put("fileList", fileList.subList(i * splitIndex, multiple * splitIndex));
                    stringListMap.put("outputPath", outputPath.subList(i * splitIndex, multiple * splitIndex));
                } else {
                    //最后一个
                    stringListMap.put("fileList", fileList.subList(i * splitIndex, imageNum));
                    stringListMap.put("outputPath", outputPath.subList(i * splitIndex, imageNum));
                }
                handleList.add(stringListMap);
            }

            //开始开启线程处理
            //该对象用于等待线程结束
            CountDownLatch countDownLatch = new CountDownLatch(threadNum);
            Long start = System.currentTimeMillis();
            for (int i = 0; i < threadNum; i++) {
                new ImageThread(handleList.get(i).get("fileList"), handleList.get(i).get("outputPath"), i + 1, countDownLatch).start();
            }
            countDownLatch.await();
            Long end = System.currentTimeMillis();
            System.err.println("压缩" + imageNum + "张图片共用时:" + ((end - start) / 1000) + "秒");
        }
    }

    /**
     * 压缩图片
     *
     * @param imagePath  要压缩的图片原图路径
     * @param outputPath 压缩后图片的保存路径
     * @param quality    参数qality是取值0~1范围内  代表压缩的程度 值越低压缩越强烈
     * @return 压缩后图片的File对象
     */
    public static File compressPictureByQuality(String imagePath, String outputPath, float quality) throws IOException {
        BufferedImage src = null;
        FileOutputStream out = null;
        ImageWriter imgWrier;
        ImageWriteParam imgWriteParams;

        File imageFile = new File(imagePath);
        File outputFile = new File(outputPath);

        //System.out.println("开始设定压缩图片参数");
        // 指定写图片的方式为 jpg
        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
        imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
        // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
        imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
        // 这里指定压缩的程度，参数quality是取值0~1范围内，
        imgWriteParams.setCompressionQuality(quality);
        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
        ColorModel colorModel = ImageIO.read(imageFile).getColorModel();// ColorModel.getRGBdefault();
        imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(32, 32)));
        //System.out.println("结束设定压缩图片参数");


        if (!imageFile.exists()) {
            System.out.println("Not Found Img File,文件不存在");
            throw new FileNotFoundException("Not Found Img File,文件不存在");
        } else {
            System.out.println("图片转换前大小" + imageFile.length() + "字节");
            src = ImageIO.read(imageFile);
            out = new FileOutputStream(outputFile);
            imgWrier.reset();
            // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
            // OutputStream构造
            imgWrier.setOutput(ImageIO.createImageOutputStream(out));
            // 调用write方法，就可以向输入流写图片
            imgWrier.write(null, new IIOImage(src, null, null),
                    imgWriteParams);
            out.flush();
            out.close();
            System.out.println("图片转换后大小" + outputFile.length() + "字节");

            return outputFile;
        }
    }

}
