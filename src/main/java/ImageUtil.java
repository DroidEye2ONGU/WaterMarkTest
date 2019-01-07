import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

class Test {
    public static void main(String[] args) throws IOException, InterruptedException {

        File director = new File("C:\\Users\\DroidEye\\Desktop\\individual");
        File[] files = director.listFiles();
        List<String> fileList = new ArrayList<>();
        List<String> outputList = new ArrayList<>();
        int index = 1;
        for (File file :
                files) {
            String path = file.getAbsolutePath();
            fileList.add(path);
            String fileName = file.getName();
            outputList.add("C:\\Users\\DroidEye\\Desktop\\压缩图片\\" + index++ + ".jpg");
        }
        Long startTime = System.currentTimeMillis();
        //for (int i = 0; i < fileList.size(); i++) {
        //    ImageUtil.compressPictureByQality(fileList.get(i), outputList.get(i), 0.01f);
        //}

        int length = fileList.size();
        int splitIndex = length / 3;

        //将原集合拆分为3个集合
        List<String> inputOne = fileList.subList(0, splitIndex);
        List<String> inputTwo = fileList.subList(splitIndex, splitIndex * 2);
        List<String> inputThree = fileList.subList(splitIndex*2, fileList.size());

        List<String> outputOne = outputList.subList(0, splitIndex);
        List<String> outputTwo = outputList.subList(splitIndex, splitIndex * 2);
        List<String> outputThree = outputList.subList(splitIndex*2, outputList.size());
        
        CountDownLatch countDownLatch = new CountDownLatch(2);

        Long start = System.currentTimeMillis();
        //启动两个线程进行转换
        new ImageThread(inputOne, outputOne, 1, countDownLatch).start();
        new ImageThread(inputTwo, outputTwo, 2, countDownLatch).start();
        new ImageThread(inputThree, outputThree, 3, countDownLatch).start();

        countDownLatch.await();
        Long end = System.currentTimeMillis();

        System.err.println("共用时:" + ((end - start) / 1000) + "秒");

    }
}

public class ImageUtil {


    /**
     * 压缩图片
     *
     * @param imagePath  要压缩的图片原图路径
     * @param outputPath 压缩后图片的保存路径
     * @param qality     参数qality是取值0~1范围内  代表压缩的程度 值越低压缩越强烈
     * @return 压缩后图片的File对象
     */
    public static File compressPictureByQality(String imagePath, String outputPath, float qality) throws IOException {
        BufferedImage src = null;
        FileOutputStream out = null;
        ImageWriter imgWrier;
        ImageWriteParam imgWriteParams;

        File imageFile = new File(imagePath);
        File outputFile = new File(outputPath);

        System.out.println("开始设定压缩图片参数");
        // 指定写图片的方式为 jpg
        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
        imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
        // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
        imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
        // 这里指定压缩的程度，参数qality是取值0~1范围内，
        imgWriteParams.setCompressionQuality(qality);
        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
        ColorModel colorModel = ImageIO.read(imageFile).getColorModel();// ColorModel.getRGBdefault();
        imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(32, 32)));
        System.out.println("结束设定压缩图片参数");


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
