package com.machines0008.viewlibrary.adbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.Generated;
import lombok.Setter;

/**
 * Created by Android Studio
 * User   : Andy
 * Date   : 2022/3/28
 * Time  : 下午 09:15
 * Usage :
 * To change this template use File | Settings | File and Code Templates.
 */
public class ImageLoader {
    private ExecutorService executorService;
    private int fixedPoolCount = 3;
    private String[] imageUrl;


    public ImageLoader setImageUrl(String[] imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public ImageLoader setFixedPoolCount(int fixedPoolCount) {
        this.fixedPoolCount = fixedPoolCount;
        return this;
    }

    public void load(@NonNull Listener listener) {
        if (null == imageUrl || null == listener) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                executorService = Executors.newFixedThreadPool(fixedPoolCount);
                List<Future<ImageDownloadBean>> result = new ArrayList<>(imageUrl.length);
                for (int index = 0, length = imageUrl.length; index < length; index++) {
                    Future<ImageDownloadBean> future = executorService.submit(new ImageDownloader(imageUrl[index], index));
                    result.add(future);
                }
                executorService.shutdown();
                List<ImageDownloadBean> resultBean = new ArrayList<>();
                for (Future<ImageDownloadBean> future : result) {
                    try {
                        ImageDownloadBean imageDownloadBean = future.get();
                        listener.process(imageDownloadBean.index, imageDownloadBean.urlStr, imageDownloadBean.message);
                        resultBean.add(imageDownloadBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onComplete(resultBean);
            }
        }.start();
    }

    private static class ImageDownloader implements Callable<ImageDownloadBean> {
        private final int index;
        private final String urlStr;

        private ImageDownloader(String urlStr, int index) {
            this.index = index;
            this.urlStr = urlStr;
        }

        @Override
        public ImageDownloadBean call() {
            ImageDownloadBean imageDownloadBean = new ImageDownloadBean();
            imageDownloadBean.setIndex(index);
            imageDownloadBean.setUrlStr(urlStr);
            try {
                URL url = new URL(urlStr);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                try (InputStream is = httpURLConnection.getInputStream()) {
                    imageDownloadBean.setBitmap(BitmapFactory.decodeStream(is));
                }
                httpURLConnection.disconnect();

            } catch (Exception e) {
                imageDownloadBean.setMessage(e.getMessage());
            }
            return imageDownloadBean;
        }
    }

    public interface Listener {
        void process(int index, String url, String message);

        void onComplete(List<ImageDownloadBean> results);
    }

    public static class ImageDownloadBean {
        private String urlStr;
        private int index = -1;
        private Bitmap bitmap = null;
        private String message;

        public String getUrlStr() {
            return urlStr;
        }

        public void setUrlStr(String urlStr) {
            this.urlStr = urlStr;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
