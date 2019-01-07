package Complete.Util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
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
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ImageWaterMarkToPdfUtil {

    private static final String OSNAME = System.getProperty("os.name").toLowerCase();

    private ImageWaterMarkToPdfUtil() {

    }

    /**
     * 将第一入参目录下的所有图片转为pdf,存放在第二入参文件夹下
     *
     * @param sourceDirector 要转pdf图片所在的目录
     * @return pdf文件的绝对路径
     */
    public static String putImagesToPdfThenWatermark(File sourceDirector) throws IOException, DocumentException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

        //windows系统下
        File[] files = sourceDirector.listFiles();
        List<String> imagePaths = new ArrayList<>();
        for (File f :
                files) {
            imagePaths.add(f.getAbsolutePath());
        }
        File pdfFile = setWatermark(imageToPdf(imagePaths), format.format(new Date(System.currentTimeMillis())), null, null, null, null);
        return pdfFile.getAbsolutePath();
    }

    public static String putImagesToPdfThenWatermark(List<String> imagePaths, String userName, String deptName, String contractNo, String custName) throws IOException, DocumentException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

        File pdfFile = setWatermark(imageToPdf(imagePaths), format.format(new Date(System.currentTimeMillis())), userName, deptName, contractNo, custName);
        return pdfFile.getAbsolutePath();
    }

    private static File imageToPdf(List<String> imagePaths) throws IOException, DocumentException {


        //创建临时文件保存
        File tempFile = File.createTempFile("nxsExportPdf", ".tmp");
        //创建一个文档对象
        Document document = new Document();
        //获得一个实例
        PdfWriter.getInstance(document, new BufferedOutputStream(new FileOutputStream(tempFile)));
        //打开并开始添加图片
        document.open();

        for (String path :
                imagePaths) {
            URL url = null;
            if (OSNAME.contains("windows")) {
                url = new URL("file://" + path);
            } else {
                url = new URL("http", "127.0.0.1", 18080, path);
            }
            //获取图片对象
            Image image = Image.getInstance(path);


            document.newPage();

            //设置图片大小
            image.scaleToFit(1024, 768);
            //设置图片在PDF中的位置
            image.setAbsolutePosition(20, 25);
            //添加
            document.add(image);
        }


        //关闭文档.不关无法打开PDF
        document.close();

        return tempFile;
    }

    private static File setWatermark(File inputFile, String bottomTime, String userName, String deptName, String contractNo, String custName) throws IOException, DocumentException {
        File tempFile = File.createTempFile("watermarktempfile", ".pdf");
        PdfReader pdfReader = new PdfReader(new BufferedInputStream(new FileInputStream(inputFile)));
        PdfStamper stamper = new PdfStamper(pdfReader, new BufferedOutputStream(new FileOutputStream(tempFile)));

        //获取总页数加一用于循环
        int totalPages = pdfReader.getNumberOfPages() + 1;

        PdfContentByte content;

        //String fontPath = ImageWaterMarkToPdfUtil.class.getClassLoader().getResource("SIMYOU.TTF").getPath();
        //fontPath = fontPath.substring(1);
        //使用资源字体,将字体文件放在项目路径内
        //BaseFont baseFont = BaseFont.createFont("/static/font/SIMYOU.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        BaseFont baseFont = BaseFont.createFont("C:\\Users\\DroidEye\\Desktop\\Programme\\IdeaProjects\\WaterMarkTest\\src\\main\\resources\\SIMYOU.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        PdfGState pdfGState = new PdfGState();

        for (int i = 1; i < totalPages; i++) {
            content = stamper.getOverContent(i);
            pdfGState.setFillOpacity(0.2f);
            content.beginText();
            content.setColorFill(BaseColor.LIGHT_GRAY);
            content.setFontAndSize(baseFont, 30);
            content.setTextMatrix(1, 1);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 750, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 500, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 200, 20);

            content.setColorFill(BaseColor.DARK_GRAY);
            content.setFontAndSize(baseFont, 12);


            //content.showTextAligned(Element.ALIGN_LEFT, "导出人:" + userName, 30, 100, 0);
            //content.showTextAligned(Element.ALIGN_LEFT, "导出人部门:" + deptName, 30, 80, 0);
            //if (StrKit.isEmpty(contractNo)) {
            //    content.showTextAligned(Element.ALIGN_LEFT, "合同号:未录入", 30, 60, 0);
            //} else {
            //    content.showTextAligned(Element.ALIGN_LEFT, "合同号:" + contractNo, 30, 60, 0);
            //}
            //content.showTextAligned(Element.ALIGN_LEFT, "客户名:" + custName, 30, 40, 0);

            //String path = ImageWaterMarkToPdfUtil.class.getClassLoader().getResource("/NXSLogo.png").getPath();
            //Image image = Image.getInstance(path);
            //image.setAbsolutePosition(500, 725); // set the first background image of the absolute
            //image.scaleToFit(70, 70);
            //content.addImage(image);

            content.setColorFill(BaseColor.BLACK);
            content.setFontAndSize(baseFont, 12);
            content.showTextAligned(Element.ALIGN_CENTER, "下载时间："
                    + bottomTime + "", 300, 10, 0);
            content.endText();
        }

        stamper.close();
        pdfReader.close();

        //删除只有图片的临时文件
        if (inputFile.exists()) {
            inputFile.delete();
        }

        return tempFile;
    }
}
