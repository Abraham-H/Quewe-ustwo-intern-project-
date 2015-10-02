package app.com.example.android.queuee2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import java.lang.ref.SoftReference;


/**
 * Created by bkach on 10/1/15.
 */
public class AnimationsContainer {

    // single instance procedures
    private static AnimationsContainer mInstance;

    private AnimationsContainer() {
    };

    public static AnimationsContainer getInstance() {
        if (mInstance == null)
            mInstance = new AnimationsContainer();
        return mInstance;
    }

    /**
     * @param imageView The image view to play the animation in.
     * @param frames Resource id's to the images in this animation.
     * @param looping true if this animation should loop.
     * @return splash screen animation
     */
    public FramesSequenceAnimation createAnim(ImageView imageView, int[] frames, boolean looping) {
        return new FramesSequenceAnimation(imageView, frames, 30, looping);
    }

    /**
     * AnimationPlayer. Plays animation frames sequence in loop
     */
    public class FramesSequenceAnimation {
        private int[] mFrames; // animation frames
        private int mIndex; // current frame
        private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
        private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
        private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
        private Handler mHandler;
        private int mDelayMillis;
        private boolean mLooping;
        private OnAnimationStoppedListener mOnAnimationStoppedListener;

        private Bitmap mBitmap = null;
        private BitmapFactory.Options mBitmapOptions;

        public void setOnAnimationStoppedListener(OnAnimationStoppedListener listener) {
            mOnAnimationStoppedListener = listener;
        }

        public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps, boolean looping) {
            mHandler = new Handler();
            mFrames = frames;
            mIndex = -1;
            mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
            mShouldRun = false;
            mIsRunning = false;
            mDelayMillis = 3000 / fps;
            mLooping = looping;

            imageView.setImageResource(mFrames[0]);

            // use in place bitmap to save GC work (when animation images are the same size & type)
            if (Build.VERSION.SDK_INT >= 11) {
                Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int width = bmp.getWidth();
                int height = bmp.getHeight();
                Bitmap.Config config = bmp.getConfig();
                mBitmap = Bitmap.createBitmap(width, height, config);
                mBitmapOptions = new BitmapFactory.Options();
                // setup bitmap reuse options.
                mBitmapOptions.inBitmap = mBitmap;
                mBitmapOptions.inMutable = true;
                mBitmapOptions.inPreferQualityOverSpeed = false;
                mBitmapOptions.inSampleSize = 1;
            }
        }

        private int getNext() {
            mIndex++;
            if(mLooping) {
                if(mIndex >= mFrames.length) {
                    mIndex = 0;
                }
            } else if (mIndex >= mFrames.length - 1) {
                mShouldRun = false;
                if(mOnAnimationStoppedListener != null) {
                    mOnAnimationStoppedListener.onAnimationEnded();
                }
                return mFrames[mFrames.length - 1];
            }
            return mFrames[mIndex];
        }

        /**
         * Starts the animation
         */
        public synchronized void start() {
            mShouldRun = true;
            if (mIsRunning)
                return;

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = mSoftReferenceImageView.get();
                    if (!mShouldRun || imageView == null) {
                        mIsRunning = false;
                        if (mOnAnimationStoppedListener != null) {
                            mOnAnimationStoppedListener.onAnimationStopped();
                        }
                        return;
                    }

                    mIsRunning = true;
                    mHandler.postDelayed(this, mDelayMillis);

                    if (imageView.isShown()) {
                        int imageRes = getNext();
                        if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(imageRes);
                                mBitmap.recycle();
                                mBitmap = null;
                            }
                        } else {
                            imageView.setImageResource(imageRes);
                        }
                    }

                }
            };

            mHandler.post(runnable);
        }

        /**
         * Stops the animation
         */
        public synchronized void stop() {
            mShouldRun = false;
        }
    }

    public interface OnAnimationStoppedListener {
        public void onAnimationStopped();
        public void onAnimationEnded();
    }
}

