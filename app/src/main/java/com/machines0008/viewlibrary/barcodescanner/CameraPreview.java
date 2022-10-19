package com.machines0008.viewlibrary.barcodescanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.machines0008.viewlibrary.R;
import com.machines0008.viewlibrary.barcodescanner.camera.CameraInstance;
import com.machines0008.viewlibrary.barcodescanner.camera.CameraParametersCallback;
import com.machines0008.viewlibrary.barcodescanner.camera.CameraSettings;
import com.machines0008.viewlibrary.barcodescanner.camera.CameraSurface;
import com.machines0008.viewlibrary.barcodescanner.camera.DisplayConfiguration;
import com.machines0008.viewlibrary.barcodescanner.camera.PreviewScalingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/5
 * Usage:
 **/
public class CameraPreview extends ViewGroup {

    public interface StateListener {
        /**
         * Preview and frame sizes are determined.
         */
        void previewSized();

        /**
         * Preview has started.
         */
        void previewStarted();

        /**
         * Preview has stopped.
         */
        void previewStopped();

        /**
         * The camera has error, and cannot display a preview.
         *
         * @param error the error
         */
        void cameraError(Exception error);

        /**
         * The camera has been closed.
         */
        void cameraClosed();
    }

    private static final String TAG = CameraPreview.class.getSimpleName();

    private CameraInstance cameraInstance;
    private WindowManager windowManager;
    private Handler stateHandler;
    private boolean useTextureView = false;
    private SurfaceView surfaceView;
    private TextureView textureView;
    private boolean previewActive = false;
    private RotationListener rotationListener;
    private int openedOrientation = -1;
    private static final int ROTATION_LISTENER_DELAY_MS = 250;
    private List<StateListener> stateListeners = new ArrayList<>();
    private DisplayConfiguration displayConfiguration;
    private CameraSettings cameraSettings = new CameraSettings();
    private Size containerSize;
    private Size previewSize;
    private Rect surfaceRect;
    private Size currentSurfaceSize;
    private Rect framingRect = null;
    private Rect previewFramingRect = null;
    private Size framingRectSize = null;
    private double marginFraction = 0.1d;
    private PreviewScalingStrategy previewScalingStrategy = null;
    private boolean torchOn = false;

    public CameraPreview(Context context) {
        super(context);
        init(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null == getBackground()) {
            setBackgroundColor(Color.BLACK);
        }
        initializeAttributes(attrs);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        stateHandler = new Handler(stateCallback);
        rotationListener = new RotationListener();
    }

    private void initializeAttributes(AttributeSet attrs) {
        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.CameraPreview);
        int framingRectWidth = (int) styledAttributes.getDimension(R.styleable.CameraPreview_framing_rect_width, -1);
        int framingRectHeight = (int) styledAttributes.getDimension(R.styleable.CameraPreview_framing_rect_height, -1);
        if (framingRectWidth > 0 && framingRectHeight > 0) {
            framingRectSize = new Size(framingRectWidth, framingRectHeight);
        }
        useTextureView = styledAttributes.getBoolean(R.styleable.CameraPreview_use_texture_view, true);
    }

    @TargetApi(14)
    private TextureView.SurfaceTextureListener surfaceTextureListener() {
        return new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                onSurfaceTextureSizeChanged(surface, width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                currentSurfaceSize = new Size(width, height);
                startPreviewIfReady();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        };
    }

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            if (null == holder) {
                Log.e(TAG, " *** WARNING *** surfaceChanged() gave us a null surface!");
                return;
            }
            currentSurfaceSize = new Size(width, height);
            startPreviewIfReady();
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            currentSurfaceSize = null;
        }
    };

    private final Handler.Callback stateCallback = msg -> {
        switch (msg.what) {
            case CameraInstance.MSG_READY:
                previewSized((Size) msg.obj);
                return true;
            case CameraInstance.MSG_ERROR:
                Exception e = (Exception) msg.obj;
                if (isActive()) {
                    pause();
                    this.fireState.cameraError(e);
                }
                break;
            case CameraInstance.MSG_CLOSE:
                this.fireState.cameraClosed();
                break;
        }
        return false;
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupSurfaceView();
    }

    private RotationCallback rotationCallback = rotation -> stateHandler.postDelayed(() -> rotationChanged(), ROTATION_LISTENER_DELAY_MS);

    private void rotationChanged() {
        if (!isActive()) {
            return;
        }
        if (openedOrientation == getDisplayRotation()) {
            return;
        }
        pause();
        resume();
    }

    public void addStateListener(StateListener listener) {
        stateListeners.add(listener);
    }

    public void resume() {
        Utils.validateMainThread();
        Log.d(TAG, "resume()");
        initCamera();
        if (null != currentSurfaceSize) {
            startPreviewIfReady();
        } else if (null != surfaceView) {
            surfaceView.getHolder().addCallback(surfaceCallback);
        } else if (null != textureView) {
            if (textureView.isAvailable()) {
                surfaceTextureListener().onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
            } else {
                textureView.setSurfaceTextureListener(surfaceTextureListener());
            }
        }
        requestLayout();
        rotationListener.listen(getContext(), rotationCallback);
    }

    private void initCamera() {
        if (null != cameraInstance) {
            Log.w(TAG, "initCamera called twice.");
            return;
        }
        cameraInstance = createCameraInstance();
        cameraInstance.setReadyHandler(stateHandler);
        cameraInstance.open();
        openedOrientation = getDisplayRotation();
    }

    private CameraInstance createCameraInstance() {
        CameraInstance instance = new CameraInstance(getContext());
        instance.setCameraSettings(cameraSettings);
        return instance;
    }

    private int getDisplayRotation() {
        return windowManager.getDefaultDisplay().getRotation();
    }

    private void setupSurfaceView() {
        if (useTextureView) {
            textureView = new TextureView(getContext());
            textureView.setSurfaceTextureListener(surfaceTextureListener());
            addView(textureView);
        } else {
            surfaceView = new SurfaceView(getContext());
            surfaceView.getHolder().addCallback(surfaceCallback);
            addView(surfaceView);
        }
    }

    private void previewSized(Size size) {
        this.previewSize = size;
        if (null != containerSize) {
            calculateFrames();
            requestLayout();
            startPreviewIfReady();
        }
    }

    private boolean isActive() {
        return null != cameraInstance;
    }

    private void calculateFrames() {
        if (null == containerSize || previewSize == null || displayConfiguration == null) {
            previewFramingRect = null;
            framingRect = null;
            surfaceRect = null;
            throw new IllegalStateException("containerSize or previewSize is not set yet");
        }
        int previewWidth = previewSize.width;
        int previewHeight = previewSize.height;

        int width = containerSize.width;
        int height = containerSize.height;

        Rect scaledPreview = displayConfiguration.scalePreview(previewSize);
        if (scaledPreview.width() <= 0 || scaledPreview.height() <= 0) {
            // Something is not ready yet - Cannot start the preview.
            return;
        }
        surfaceRect = scaledPreview;
        Rect container = new Rect(0, 0, width, height);
        framingRect = calculateFramingRect(container, surfaceRect);
        Rect frameInPreview = new Rect(framingRect);
        frameInPreview.offset(-surfaceRect.left, -surfaceRect.top);
        previewFramingRect = new Rect(
                frameInPreview.left * previewWidth / surfaceRect.width(),
                frameInPreview.top * previewHeight / surfaceRect.height(),
                frameInPreview.right * previewWidth / surfaceRect.width(),
                frameInPreview.bottom * previewHeight / surfaceRect.height()
        );
        if (null == previewFramingRect || previewFramingRect.width() <= 0 || previewFramingRect.height() <= 0) {
            previewFramingRect = null;
            framingRect = null;
            Log.w(TAG, "Preview frame is too small.");
            return;
        }
        fireState.previewSized();
    }

    private Rect calculateFramingRect(Rect container, Rect surface) {
        Rect intersection = new Rect(container);
        intersection.intersect(surface);
        if (null != framingRectSize) {
            int horizontalMargin = Math.max(0, (intersection.width() - framingRectSize.width) / 2);
            int verticalMargin = Math.max(0, (intersection.height() - framingRectSize.height) / 2);
            intersection.inset(horizontalMargin, verticalMargin);
            return intersection;
        }
        int margin = (int) Math.min(intersection.width() * marginFraction, intersection.height() * marginFraction);
        intersection.inset(margin, margin);
        if (intersection.height() > intersection.width()) {
            intersection.inset(0, (intersection.height() - intersection.width()) / 2);
        }
        return intersection;
    }

    private void startPreviewIfReady() {
        if (null == currentSurfaceSize) {
            return;
        }
        if (null == previewSize) {
            return;
        }
        if (null == surfaceRect) {
            return;
        }
        if (surfaceView != null && currentSurfaceSize.equals(new Size(surfaceRect.width(), surfaceRect.height()))) {
            startCameraPreview(new CameraSurface(surfaceView.getHolder()));
        } else if (textureView != null && textureView.getSurfaceTexture() != null) {
            if (null != previewSize) {
                Matrix transform = calculateTextureTransform(new Size(textureView.getWidth(), textureView.getHeight()), previewSize);
                textureView.setTransform(transform);
            }
            startCameraPreview(new CameraSurface(textureView.getSurfaceTexture()));
        }
    }

    private final StateListener fireState = new StateListener() {
        @Override
        public void previewSized() {
            for (StateListener listener : stateListeners) {
                listener.previewSized();
            }
        }

        @Override
        public void previewStarted() {
            for (StateListener listener : stateListeners) {
                listener.previewStarted();
            }
        }

        @Override
        public void previewStopped() {
            for (StateListener listener : stateListeners) {
                listener.previewStopped();
            }
        }

        @Override
        public void cameraError(Exception error) {
            for (StateListener listener : stateListeners) {
                listener.cameraError(error);
            }
        }

        @Override
        public void cameraClosed() {
            for (StateListener listener : stateListeners) {
                listener.cameraClosed();
            }
        }
    };

    private void startCameraPreview(CameraSurface surface) {
        if (previewActive) {
            return;
        }
        if (null == cameraInstance) {
            return;
        }
        Log.i(TAG, "Starting preview");
        cameraInstance.setSurface(surface);
        cameraInstance.startPreview();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle state = new Bundle();
        state.putParcelable("super", superState);
        state.putBoolean("torch", torchOn);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle stateBundle = (Bundle) state;
        Parcelable superState = stateBundle.getParcelable("super");
        super.onRestoreInstanceState(superState);
        boolean torch = stateBundle.getBoolean("torch");
        setTorch(torch);
    }

    protected Matrix calculateTextureTransform(Size textureSize, Size previewSize) {
        float ratioTexture = (float) textureSize.width / (float) textureSize.height;
        float ratioPreview = (float) previewSize.width / (float) previewSize.height;
        float scaleX;
        float scaleY;

        if (ratioTexture < ratioPreview) {
            scaleX = ratioPreview / ratioTexture;
            scaleY = 1;
        } else {
            scaleX = 1;
            scaleY = ratioTexture / ratioPreview;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        float scaledWidth = textureSize.width * scaleX;
        float scaledHeight = textureSize.height * scaleY;
        float dx = (textureSize.width - scaledWidth) / 2;
        float dy = (textureSize.height - scaledHeight) / 2;
        matrix.postTranslate(dx, dy);
        return matrix;
    }

    private void pause() {
        Utils.validateMainThread();
        Log.d(TAG, "pause()");
        openedOrientation = -1;
        if (null != cameraInstance) {
            cameraInstance.close();
            cameraInstance = null;
            previewActive = false;
        } else {
            stateHandler.sendEmptyMessage(CameraInstance.MSG_CLOSE);
        }
        if (null == currentSurfaceSize) {
            if (null != surfaceView) {
                SurfaceHolder holder = surfaceView.getHolder();
                holder.removeCallback(surfaceCallback);
            }
            if (null != textureView) {
                textureView.setSurfaceTextureListener(null);
            }
        }
        containerSize = null;
        previewSize = null;
        previewFramingRect = null;
        rotationListener.stop();
        fireState.previewStopped();
    }

    public void setTorch(boolean on) {
        torchOn = on;
        if (null == cameraInstance) {
            return;
        }
        cameraInstance.setTorch(on);
    }

    public void changeCameraParameters(CameraParametersCallback callback) {
        if (null == cameraInstance) {
            return;
        }
        cameraInstance.changeCameraParameters(callback);
    }

    private void containerSized(Size containerSize) {
        this.containerSize = containerSize;
        if (null == cameraInstance) {
            return;
        }
        if (cameraInstance.getDisplayConfiguration() != null) {
            return;
        }
        displayConfiguration = new DisplayConfiguration(getDisplayRotation(), containerSize);
        cameraInstance.setDisplayConfiguration(displayConfiguration);
        cameraInstance.configureCamera();
        if (!torchOn) {
            return;
        }
        cameraInstance.setTorch(true);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        containerSized(new Size(r - l, b - t));
        if (null != surfaceView) {
            if (null == surfaceRect) {
                surfaceView.layout(0, 0, getWidth(), getHeight());
            } else {
                surfaceView.layout(surfaceRect.left, surfaceRect.top, surfaceRect.right, surfaceRect.bottom);
            }
        } else if (null != textureView) {
            textureView.layout(0, 0, getWidth(), getHeight());
        }
    }

    public boolean isCameraClosed() {
        return null == cameraInstance || cameraInstance.isCameraClosed();
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible) {
            resume();
        } else {
            pause();
        }
    }
}