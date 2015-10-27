package app.com.example.android.queuee2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

import app.com.example.android.queuee2.R;

/**
 * Created by bkach on 10/26/15.
 */
public class OverlayFacesImageView extends ImageView {


    private final int DEFAULT = 0;
    private final int ALMOST_THERE = 1;
    private final int YOUR_TURN = 2;
    private final int NEXT = 3;
    private final int SNOOZE = 4;
    private final int OVER_TEN = 5;

    private int mState;
    private int mFaceImageIndex;
    private ArrayList<ArrayList<Integer>> faceDrawables;

    public OverlayFacesImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mFaceImageIndex = 0;
        faceDrawables = getFaceDrawables();
        mState = DEFAULT;
    }

    public void down(){
        setVisibility(VISIBLE);
    }

    public void up(){
        setVisibility(INVISIBLE);
        nextImage();
    }

    private void nextImage(){
        mFaceImageIndex = (mFaceImageIndex + 1) % 4;
        setImageFromState();
    }

    public void setState(int state){
        mState = state;
        setImageFromState();
    }

    public void setImageFromState(){
        if (mState > YOUR_TURN){
            mState = DEFAULT;
        }
        setImageResource(faceDrawables.get(mState).get(mFaceImageIndex));
    }

    private static ArrayList<ArrayList<Integer>> getFaceDrawables(){
        ArrayList<Integer> pushedFace1 = new ArrayList<>();
        pushedFace1.add(R.drawable.pushed_face_0);
        pushedFace1.add(R.drawable.pushed_face_1);
        pushedFace1.add(R.drawable.pushed_face_2);
        pushedFace1.add(R.drawable.pushed_face_3);
        pushedFace1.add(R.drawable.pushed_face_4);
        ArrayList<Integer> pushedFace2 = new ArrayList<>();
        pushedFace2.add(R.drawable.pushed_face_almost_there_0);
        pushedFace2.add(R.drawable.pushed_face_almost_there_1);
        pushedFace2.add(R.drawable.pushed_face_almost_there_2);
        pushedFace2.add(R.drawable.pushed_face_almost_there_3);
        pushedFace2.add(R.drawable.pushed_face_almost_there_4);
        ArrayList<Integer> pushedFace3 = new ArrayList<>();
        pushedFace3.add(R.drawable.pushed_face_your_turn_0);
        pushedFace3.add(R.drawable.pushed_face_your_turn_1);
        pushedFace3.add(R.drawable.pushed_face_your_turn_2);
        pushedFace3.add(R.drawable.pushed_face_your_turn_3);
        pushedFace3.add(R.drawable.pushed_face_your_turn_4);
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        result.add(pushedFace1);
        result.add(pushedFace2);
        result.add(pushedFace3);
        return result;
    }
}
