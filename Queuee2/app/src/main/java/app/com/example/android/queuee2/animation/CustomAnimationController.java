package app.com.example.android.queuee2.animation;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.util.Log;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import rx.Observable;

/**
 * Created by bkach on 10/14/15.
 */
public class CustomAnimationController extends BaseControllerListener<ImageInfo> {

    private static final String TAG = CustomAnimationController.class.getSimpleName();
    private Animatable mAnimatable;

    private Handler mHandler;

    @Override
    public void onFinalImageSet(
            String id,
            @Nullable ImageInfo imageInfo,
            @Nullable Animatable animatable) {

        mAnimatable = animatable;

        mAnimatable.start();
    }

    @Override
    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
        super.onIntermediateImageSet(id, imageInfo);
    }

    @Override
    public void onRelease(String id) {
        super.onRelease(id);
    }
}
