package Complete.Thread;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import Complete.Util.CompressImageUtil;

public class ImageThread extends Thread {
    List<String> path;
    List<String> outputPath;
    int id;

    private CountDownLatch countDownLatch;

    public ImageThread(List<String> path, List<String> outputPath, int id, CountDownLatch countDownLatch) {
        this.path = path;
        this.outputPath = outputPath;
        this.id = id;
        this.countDownLatch = countDownLatch;
    }

    public void run() {
        for (int i = 0; i < path.size(); i++) {
            System.out.println("线程" + id + ":");
            try {
                CompressImageUtil.compressPictureByQuality(path.get(i), outputPath.get(i), 0.2f);
            } catch (IOException e) {
                System.out.println(path.get(i));
                e.printStackTrace();
            }
        }
        countDownLatch.countDown();
    }
}
