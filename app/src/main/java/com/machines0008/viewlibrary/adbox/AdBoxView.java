package com.machines0008.viewlibrary.adbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.machines0008.viewlibrary.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Android Studio
 * User   : Andy
 * Date   : 2022/3/25
 * Time  : 下午 10:30
 * Usage :
 * To change this template use File | Settings | File and Code Templates.
 */
public class AdBoxView<T extends AdBoxVo> extends ConstraintLayout {
    private static final String TAG = AdBoxView.class.getSimpleName();
    private HorizontalScrollView scrollView;
    private LinearLayout container;
    private MarkView markView;
    private OnItemClickListener<T> onItemClickListener; //子項目點擊監聽器
    @ColorRes
    private int selectedColor = android.R.color.holo_red_dark; //選擇標記顏色
    @ColorRes
    private int normalColor = android.R.color.white; //未選擇標記之顏色
    private int position = 0; //當前位置

    private int radius = 10; //標記半徑
    private int bottomPadding = 5; //標記離圖片底部之間距
    private int gap = 10; //標記間之間隔距離
    private int duration = 3; //自動輪巡間隔時間(秒)
    private List<T> adBoxVoList; //廣告盒物件集合
    private float xAxis = 0; //紀錄當scrollview之觸控事件 按下時x之位置，用以與彈起時之x進行比較，藉以判斷向左滑動或向右滑動
    private boolean autoPlay = true; //是否自動輪巡 (由使用者自行決定)
    private boolean shouldRunTicker; //是否應輪巡 (由view之可見性決定，無須經由view之生命週期決定)
    private boolean isLoaded = false; //是否下載完成(用以告知選擇框是否需畫圖，若未完成，則無需出選擇項目)
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            if (!autoPlay || !shouldRunTicker) {
                return;
            }
            setNextChild(1);
            postDelayed(this, duration * 1000);
        }
    };


    public AdBoxView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdBoxView);
        if (typedArray.hasValue(R.styleable.AdBoxView_selected_color)) {
            selectedColor = typedArray.getResourceId(R.styleable.AdBoxView_selected_color, R.color.white);
        }
        if (typedArray.hasValue(R.styleable.AdBoxView_unselected_color)) {
            normalColor = typedArray.getResourceId(R.styleable.AdBoxView_unselected_color, R.color.black);
        }
        if (typedArray.hasValue(R.styleable.AdBoxView_duration)) {
            duration = typedArray.getInt(R.styleable.AdBoxView_duration, 3);
        }
        if (typedArray.hasValue(R.styleable.AdBoxView_autoPlay)) {
            autoPlay = typedArray.getBoolean(R.styleable.AdBoxView_autoPlay, true);
        }
        typedArray.recycle();
    }

    private void init(Context context) {
        initScrollView(context);
        initMarkView(context);
    }

    /**
     * 初始化標記View
     *
     * @param context
     */
    private void initMarkView(Context context) {
        markView = new MarkView(context);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2 * (radius + bottomPadding));
        layoutParams.bottomToBottom = getId();
        layoutParams.leftToLeft = getId();
        layoutParams.rightToRight = getId();
        markView.setLayoutParams(layoutParams);
        addView(markView);
    }

    /**
     * 初始化滑動View(含裝載之linearLayout)
     *
     * @param context
     */
    private void initScrollView(Context context) {
        scrollView = new HorizontalScrollView(context);
        scrollView.setOverScrollMode(OVER_SCROLL_NEVER);
        scrollView.setSmoothScrollingEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topToTop = getId();
        layoutParams.leftToLeft = getId();
        layoutParams.rightToRight = getId();
        scrollView.setLayoutParams(layoutParams);
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.setOnTouchListener((view, motionEvent) -> {
            int actionCode = motionEvent.getAction();
            switch (actionCode) {
                case MotionEvent.ACTION_DOWN:
                    xAxis = motionEvent.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                    int moveDirect = xAxis - motionEvent.getX() < 0 ? 0 : 1;
                    setNextChild(moveDirect);
                    return true;
            }
            return false;
        });
        scrollView.addView(container);
        addView(scrollView);
    }


    /**
     * 設定內容，先遍歷找出需經網路進行下載之圖片
     * 若有：進行網路圖片下載後，完成時將取得之Bitmap物件放回原集合中，再更新容器內之所有子view
     * 若無：直接更新容器內所有子view
     *
     * @param adBoxVoList 廣告盒集合
     */
    public void setContent(List<T> adBoxVoList) {
        this.adBoxVoList = adBoxVoList;
        position = 0;
        isLoaded = false;
        Set<String> url = new HashSet<>();
        for (int index = 0; index < adBoxVoList.size(); index++) {
            T adBoxVo = adBoxVoList.get(index);
            if (AdType.NETWORK_URL != adBoxVo.getType() || null == adBoxVo.getImageUrl() || "".equals(adBoxVo.getImageUrl())) {
                continue;
            }
            url.add(adBoxVo.getImageUrl());
        }
        if (url.size() == 0) {
            refreshAdBoxView();
            return;
        }
        downloadImage(adBoxVoList, url);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 下載網路圖片
     * {@link com.machines0008.viewlibrary.adbox.ImageLoader}
     *
     * @param adBoxVoList 原始資料
     * @param url         不重複之url (避免同一位址請求多次)
     */
    private <T extends AdBoxVo> void downloadImage(List<T> adBoxVoList, Set<String> url) {
        String[] urlArray = new String[url.size()];
        new ImageLoader()
                .setImageUrl(url.toArray(urlArray))
                .load(new ImageLoader.Listener() {
                    @Override
                    public void process(int index, String url, String message) {
                        Log.i(TAG, String.format("index = %d, url = %s,\r\nmessage = %s", index, url, message));
                    }

                    @Override
                    public void onComplete(final List<ImageLoader.ImageDownloadBean> results) {
                        for (T adBoxVo : adBoxVoList) {
                            if (AdType.NETWORK_URL != adBoxVo.getType()) {
                                continue;
                            }
                            for (ImageLoader.ImageDownloadBean bean : results) {
                                String imageUrl = adBoxVo.getImageUrl();
                                if (imageUrl != null && imageUrl.equals(bean.getUrlStr()) && bean.getMessage() == null) {
                                    adBoxVo.setBitmap(bean.getBitmap());
                                }
                            }
                        }
                        refreshAdBoxView();
                    }
                });
    }

    /**
     * 依據參數，推算下一個位置，並根據容器內所裝載子view之大小，累加至推算後之結果
     *
     * @param moveDirector 移動方向 0:向左翻頁 1:向右翻頁
     */
    private void setNextChild(int moveDirector) {
        scrollView.post(() -> {
            int lastPosition = position - 1 < 0 ? container.getChildCount() - 1 : position - 1;
            int nextPosition = position + 1 >= container.getChildCount() ? 0 : position + 1;
            position = moveDirector == 0 ? lastPosition : nextPosition;
            int scrollToX = 0;
            for (int i = 0; i < position; i++) {
                scrollToX += container.getChildAt(i).getWidth();
            }
            scrollView.smoothScrollTo(scrollToX, 0);
            markView.invalidate();
        });
    }

    /**
     * 更新AdBoxView
     * 此時網路圖片已下載完畢
     * 若高度為 LayoutParams#WRAP_CONTENT 則以首個Bitmap、NetworkUrl、Drawable之高度作為整體高度，並以該寬高對後續圖片進行統一等比例置中調整
     */
    private  void refreshAdBoxView() {
        post(() -> {
            isLoaded = true;
            container.removeAllViews();
            if (null == adBoxVoList || adBoxVoList.size() == 0) {
                markView.invalidate();
                return;
            }
            int height = getLayoutParams().height == LayoutParams.WRAP_CONTENT ? LayoutParams.WRAP_CONTENT : getMeasuredHeight();
            if (height == LayoutParams.WRAP_CONTENT) {
                for (AdBoxVo adBoxVo : adBoxVoList) {
                    Bitmap bitmap;
                    switch (adBoxVo.getType()) {
                        case BITMAP:
                        case NETWORK_URL:
                            bitmap = adBoxVo.getBitmap();
                            break;
                        case DRAWABLE:
                            bitmap = BitmapFactory.decodeResource(getResources(), adBoxVo.getDrawableRes());
                            break;
                        default:
                            continue;
                    }
                    float bitmapWidth = bitmap.getWidth();
                    float adBoxWidth = getMeasuredWidth();
                    float ratio = adBoxWidth / bitmapWidth;
                    height = (int) (bitmap.getHeight() * ratio);
                    break;
                }

            }
            for (T adBoxVo : adBoxVoList) {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new LayoutParams(getMeasuredWidth(), height));
                switch (adBoxVo.getType()) {
                    case BITMAP:
                    case NETWORK_URL:
                        imageView.setImageBitmap(adBoxVo.getBitmap());
                        break;
                    case DRAWABLE:
                        imageView.setImageResource(adBoxVo.getDrawableRes());
                        break;
                    case FILE_URL:
                        imageView.setImageURI(Uri.parse(adBoxVo.getImageUrl()));
                        break;
                }
                if (null != onItemClickListener) {
                    imageView.setOnClickListener((v)-> {
                        onItemClickListener.onItemClick(v, adBoxVo);
                    });
                }

                container.addView(imageView);
            }
            markView.invalidate();
        });
    }

    /**
     * 標記View (底部圓圈)
     */
    private class MarkView extends View {
        private Paint paint;

        public MarkView(Context context) {
            super(context);
            this.paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!isLoaded) {
                return;
            }
            List<RectF> rectList = genRect(getWidth(), getHeight());
            paint.setColor(getResources().getColor(normalColor, null));
            for (RectF rectF : rectList) {
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, paint);
            }
            paint.setColor(getResources().getColor(selectedColor, null));
            canvas.drawCircle(rectList.get(position).centerX(), rectList.get(position).centerY(), radius, paint);
        }

        /**
         * 返還所有經計算之標記點位置
         *
         * @param width  該view總寬
         * @param height 該view總高
         * @return 標記點位置集合
         */
        private List<RectF> genRect(float width, float height) {
            List<RectF> rectList = new ArrayList<>();
            if (adBoxVoList == null) {
                return rectList;
            }
            float size = 2 * radius;
            int count = adBoxVoList.size();
            int gapCount = count - 1;
            float middlePoint = width / 2; //X軸中心點
            float gapDistance = gap * (gapCount / 2) + (gap * (gapCount % 2) / 2); //作圖起始點間距長度所需扣除值
            float objDistance = size * (count / 2) + (size * (count % 2) / 2);
            float startXPoint = middlePoint - gapDistance - objDistance;
            for (int i = 0; i < count; i++) {
                RectF rectF = new RectF(startXPoint, height - size - bottomPadding, startXPoint + size, height - bottomPadding);
                rectList.add(rectF);
                startXPoint += gap + size;
            }
            return rectList;
        }
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (!shouldRunTicker && isVisible) {
            shouldRunTicker = true;
            ticker.run();
        } else if (shouldRunTicker && !isVisible) {
            shouldRunTicker = false;
            removeCallbacks(ticker);
        }
    }

    public enum AdType {
        DRAWABLE, NETWORK_URL, FILE_URL, BITMAP
    }
}
