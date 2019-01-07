package Complete.Controller;

import com.itextpdf.text.DocumentException;

import java.io.IOException;

import Complete.Service.ImageService;

public class ImageController {

    public static void main(String[] args) throws InterruptedException, DocumentException, IOException {
        ImageService imageService = new ImageService();
        imageService.transferImagePdfToZip("C:\\Users\\DroidEye\\Desktop\\原图",
                "C:\\Users\\DroidEye\\Desktop\\压缩图片\\", 0.2f, 3);
    }

}
