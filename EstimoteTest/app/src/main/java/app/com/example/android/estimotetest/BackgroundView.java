package app.com.example.android.estimotetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by bkach on 9/2/15.
 */
public class BackgroundView extends View {
    private final Drawable drawable;

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawable = context.getResources().getDrawable(R.drawable.target);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = drawable.getIntrinsicWidth() * canvas.getHeight() / drawable.getIntrinsicHeight();
        int deltaX = (width - canvas.getWidth()) / 2;
        drawable.setBounds(-deltaX, 0, width - deltaX, canvas.getHeight());
        drawable.draw(canvas);
    }
}
