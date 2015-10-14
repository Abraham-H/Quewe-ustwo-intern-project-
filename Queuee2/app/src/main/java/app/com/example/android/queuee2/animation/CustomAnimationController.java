package app.com.example.android.queuee2.animation;
import android.graphics.drawable.Animatable;
import android.util.Log;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import javax.annotation.Nullable;

/**
 * Created by bkach on 10/14/15.
 */
public class CustomAnimationController extends BaseControllerListener<ImageInfo> {

    private static final String TAG = CustomAnimationController.class.getSimpleName();

    @Override
    public void onFinalImageSet(
            String id,
            @Nullable ImageInfo imageInfo,
            @Nullable Animatable animatable) {
        Log.d(TAG, "onFinalImageSet() called with: " + "id = [" + id + "], imageInfo = [" + imageInfo + "], animatable = [" + animatable + "]");
        animatable.start();
    }
}
