import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WaterMarkTest {
    public static void main(String[] args) throws DocumentException, IOException {
        File file = new File("x://a.pdf");
        if (file.exists()) {
            file.delete();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        setWatermark(bos, "C:\\Users\\DroidEye\\Desktop\\1.pdf", format.format(new Date(System.currentTimeMillis())), 16);
    }

    public static void setWatermark(BufferedOutputStream bos, String input, String waterMarkName, int permission) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(input);
        PdfStamper stamper = new PdfStamper(reader, bos);
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
            content.setColorFill(BaseColor.DARK_GRAY);
            content.setFontAndSize(base, 30);
            content.setTextMatrix(1, 1);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 750, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 600, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 500, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 300, 20);
            content.showTextAligned(Element.ALIGN_CENTER, "仅用于查看,注意保密!", 300, 250, 20);
            Image image = Image.getInstance(WaterMarkTest.class.getResource("NXSLogo.png"));
            image.setAbsolutePosition(435, 705); // set the first background image of the absolute
            image.scaleToFit(70, 70);
            content.addImage(image);
            content.setColorFill(BaseColor.BLACK);
            content.setFontAndSize(base, 8);
            content.showTextAligned(Element.ALIGN_CENTER, "下载时间："
                    + waterMarkName + "", 300, 10, 0);
            content.endText();

        }
        stamper.close();
    }


}
