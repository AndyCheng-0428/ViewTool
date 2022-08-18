package com.machines0008.viewlibrary.wheelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.machines0008.viewlibrary.R;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import lombok.Setter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/18
 * Usage:
 **/
public class WheelView<T extends WheelViewVo> extends View {
    @Setter
    private boolean isLoop = true; //是否循環

    @ColorRes
    private int selectedColor = R.color.black;

    @ColorRes
    private int unselectedColor = R.color.gray;


    private static final float MARGIN_ALPHA = 2.8f;
    private static final float AUTO_SCROLL_SPEED = 10;
    private List<T> dataList; //資料叢集
    private int selectedIndex; //選中項目之索引
    private Paint mPaint, nPaint; //mPaint當項選中項目之畫筆, nPaint為選中滾軸項目之畫筆
    private float maxTextSize = 80;
    private float minTextSize = 40;
    private final float maxTextAlpha = 255;
    private final float minTextAlpha = 120;
    private int viewHeight;
    private int viewWidth;
    private float lastDownY;

    private float moveDistance = 0;
    private boolean isInit = false;
    @Setter
    private boolean scrollEnable = true;
    @Setter
    private OnSelectListener onSelectListener;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (Math.abs(moveDistance) < AUTO_SCROLL_SPEED) {
                moveDistance = 0;
                if (null != timerTask) {
                    timerTask.cancel();
                    timerTask = null;
                    performSelect();
                }
            } else {
                moveDistance = moveDistance - moveDistance / Math.abs(moveDistance) * AUTO_SCROLL_SPEED; //移動距離 = 移動距離±自動移動速度
            }
            invalidate();
        }
    };

    public void setSelectedIndex(int selectedIndex) {
        int size = CollectionUtils.size(dataList);
        if (size - 1 < selectedIndex) {
            this.selectedIndex = size - 1;
        }
        if (selectedIndex < 0) {
            this.selectedIndex = 0;
        }
        if (this.selectedIndex < 0 || size == 0) {
            return;
        }
        if (isLoop) {
            int indexGap = CollectionUtils.size(dataList) / 2 - this.selectedIndex;
            if (indexGap < 0) {
                for (int i = 0; i < -indexGap; i++) {
                    moveHeadToTail();
                    this.selectedIndex--;
                }
            } else {
                for (int i = 0; i < indexGap; i++) {
                    moveTailToHead();
                    this.selectedIndex++;
                }
            }
        }
        invalidate();
    }

    public void setDataList(List<T> dataList, T defaultItem) {
        this.dataList = dataList;
        selectedIndex = defaultItem != null && CollectionUtils.isNotEmpty(dataList) ? dataList.indexOf(defaultItem) : CollectionUtils.size(dataList) / 4;
        invalidate();
    }

    private void moveTailToHead() {
        if (!isLoop || CollectionUtils.isEmpty(dataList)) {
            return;
        }
        int lastIndex = CollectionUtils.size(dataList) - 1;
        T tailElement = CollectionUtils.get(dataList, lastIndex);
        dataList.remove(lastIndex);
        dataList.add(0, tailElement);
    }

    private void moveHeadToTail() {
        if (!isLoop || CollectionUtils.isEmpty(dataList)) {
            return;
        }
        T headElement = CollectionUtils.get(dataList, 0);
        dataList.remove(0);
        dataList.add(headElement);
    }

    private void performSelect() {
        if (null == onSelectListener) {
            return;
        }
        onSelectListener.onSelect(CollectionUtils.get(dataList, selectedIndex));
    }

    /**
     * 初始化
     */
    private void init() {
        timer = new Timer();
        dataList = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(getContext().getColor(selectedColor));

        nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Paint.Style.FILL);
        nPaint.setTextAlign(Paint.Align.CENTER);
        nPaint.setColor(getContext().getColor(unselectedColor));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();
        maxTextSize = viewHeight / 7.0f;
        minTextSize = maxTextSize / 2.2f;
        isInit = true;
        invalidate();
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        if (typedArray.hasValue(R.styleable.WheelView_selected_text_color)) {
            selectedColor = typedArray.getResourceId(R.styleable.WheelView_selected_text_color, R.color.black);
        }
        if (typedArray.hasValue(R.styleable.WheelView_unselected_text_color)) {
            unselectedColor = typedArray.getResourceId(R.styleable.WheelView_unselected_text_color, R.color.gray);
        }
        typedArray.recycle();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInit) {
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        float scale = parabola(viewHeight / 4.0f, moveDistance);
        float size = (maxTextSize - minTextSize) * scale / 4 + minTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((maxTextAlpha - minTextAlpha) * scale + minTextAlpha));
        float x = (float) (viewWidth / 2.0);
        float y = (float) (viewHeight / 2.0 + moveDistance);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        if (CollectionUtils.isNotEmpty(dataList)) {
            canvas.drawText(CollectionUtils.get(dataList, selectedIndex).getName(), x, baseline, mPaint);
        }
        // 繪製上方滾軸
        for (int i = 1; (selectedIndex - i) >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        // 繪製下方滾軸
        for (int i = 1; (selectedIndex + i) < CollectionUtils.size(dataList); i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                moveDistance += (event.getY() - lastDownY);
                if (moveDistance > MARGIN_ALPHA * minTextSize / 2) {
                    if (!isLoop && selectedIndex == 0) {
                        lastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!isLoop) {
                        selectedIndex--;
                    }
                    moveTailToHead();
                    moveDistance = moveDistance - MARGIN_ALPHA * minTextSize;
                } else if (moveDistance < -MARGIN_ALPHA * minTextSize / 2) {
                    if (selectedIndex == CollectionUtils.size(dataList) - 1) {
                        lastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!isLoop) {
                        selectedIndex++;
                    }
                    moveHeadToTail();
                    moveDistance = moveDistance + MARGIN_ALPHA * minTextSize;
                }
                lastDownY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                doUp();
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        lastDownY = event.getY();
    }

    private void doUp() {
        if (Math.abs(moveDistance) < 0.0001) {
            moveDistance = 0;
            return;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = new TimerTask(updateHandler);
        timer.schedule(timerTask, 0, 10);
    }

    /**
     * @param canvas   本布局畫布
     * @param position 距離選中項之差項數目
     * @param type     1:向下繪製 -1:向上繪製
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * minTextSize * position + type * moveDistance;
        float scale = parabola(viewHeight / 4.0f, d);
        float size = (maxTextSize - minTextSize) * scale / 4 + minTextSize;
        nPaint.setTextSize(size);
        nPaint.setAlpha((int) ((maxTextAlpha - minTextAlpha) * scale + minTextAlpha));
        float y = (float) (viewHeight / 2.0 + type * d);
        Paint.FontMetricsInt fmi = nPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(CollectionUtils.get(dataList, selectedIndex + type * position).getName(), (float) (viewWidth / 2.0), baseline, nPaint);
    }

    /**
     * 拋物線
     *
     * @param zero       零點座標
     * @param moveDistance 偏移量
     * @return
     */
    private float parabola(float zero, float moveDistance) {
        float f = (float) (1 - Math.pow(moveDistance / zero, 2));
        return Math.max(0, f);
    }

    public T getSelectedItem() {
        return CollectionUtils.get(dataList, selectedIndex);
    }

    public interface OnSelectListener<T extends WheelViewVo> {
        void onSelect(T item);
    }

    private static class TimerTask extends java.util.TimerTask {
        private final Handler handler;

        private TimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }
}
