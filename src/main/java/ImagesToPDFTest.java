import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.print.attribute.standard.PagesPerMinute;

public class ImagesToPDFTest {

    private static final int A4URX = 595;
    private static final int A4URY = 942;

    public static void main(String[] args) throws Exception {
        //准备一文件路径数组
        //String[] paths = {
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\1.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670001.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670002.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670003.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670004.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670005.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670006.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670007.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670008.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670009.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670010.jpg",
        //        "C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\pic\\A15394057670011.jpg",
        //};

        String[] paths = {
                "C:\\Users\\DroidEye\\Desktop\\原图\\A15465735000001.jpg"
        };


        List<String> filePaths = new ArrayList<>();
        File directory = new File("C:\\Users\\DroidEye\\Desktop\\原图");
        File[] files = directory.listFiles();
        for (File file :
                files) {
            String absolutePath = file.getAbsolutePath();
            filePaths.add(absolutePath);
        }
        String[] paths2 = new String[filePaths.size()];
        filePaths.toArray(paths2);


        File file = imageToPdf(paths2);

        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

        setWatermark("C:\\Users\\DroidEye\\Desktop\\压缩图片\\压缩.pdf", file, format.format(new Date(System.currentTimeMillis())));

        file.delete();
    }

    public static File imageToPdf(String[] images) throws Exception {
        File tempFile = File.createTempFile("expeortPDFt", ".tmp");
        Document document = new Document();
        PdfWriter.getInstance(document, new BufferedOutputStream(new FileOutputStream(tempFile)));
        document.open();
        for (String path :
                images) {
            //获取图片对象
            Image image = Image.getInstance(path);

            float width = image.getWidth();
            float height = image.getHeight();

            if (width < height) {
                //竖向
                document.setPageSize(PageSize.A4);
                //设置图片在PDF中的位置
                image.setAbsolutePosition(30, 35);
                //设置图片在PDF中的大小
                //默认
                image.scaleToFit(1024, 768);
            } else {
                //横向
                document.setPageSize(PageSize.A4.rotate());
                image.scaleToFit(770, 523);

                float offsetX = (770 - image.getScaledWidth()) / 2;
                float offsetY = (523 - image.getScaledHeight()) / 2;

                image.setAbsolutePosition(36 + offsetX, 36 + offsetY);
            }
            document.newPage();
            document.add(image);
        }
        document.close();

        return tempFile;
    }

    public static void setWatermark(String outputPath, File inputFile, String waterMarkName) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(new BufferedInputStream(new FileInputStream(inputFile)));
        PdfStamper stamper = new PdfStamper(reader, new BufferedOutputStream(new FileOutputStream(outputPath)));
        int totalPages = reader.getNumberOfPages() + 1;

        PdfContentByte content;
        //使用text-asian.jar,itext新版本不支持
        //BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);

        //使用Windows系统资源字体,直接指定路径无法在服务器使用
        //BaseFont base = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);

        //使用资源字体,将字体文件放在项目路径内
        BaseFont base = BaseFont.createFont("/SIMYOU.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        PdfGState gs = new PdfGState();

        for (int i = 1; i < totalPages; i++) {
            content = stamper.getOverContent(i);
            gs.setFillOpacity(0.2f);
            content.beginText();
            content.setColorFill(BaseColor.LIGHT_GRAY);
            content.setFontAndSize(base, 30);
            content.setTextMatrix(1, 1);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 650, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 550, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 450, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 240, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 100, 20);
            //Image image = Image.getInstance(WaterMarkTest.class.getResource("NXSLogo.png"));
            //image.setAbsolutePosition(435, 705); // set the first background image of the absolute
            //image.scaleToFit(70, 70);
            //content.addImage(image);
            content.setColorFill(BaseColor.BLACK);
            content.setFontAndSize(base, 8);


            Rectangle pageSize = reader.getPageSize(i);
            float width = pageSize.getWidth();
            float height = pageSize.getHeight();


            if (width < height) {
                //竖向
                content.showTextAligned(Element.ALIGN_CENTER, "下载时间："
                        + waterMarkName + "", 300, 10, 0);
            } else {
                //横向
                content.showTextAligned(Element.ALIGN_CENTER, "下载时间："
                        + waterMarkName + "", 1000, 600, 0);
            }
            content.endText();

        }
        stamper.close();

        //ZipUtil.zip("x://1010795511//", "X://b.zip");
    }

}
