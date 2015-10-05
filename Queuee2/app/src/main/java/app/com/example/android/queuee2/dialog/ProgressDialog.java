package app.com.example.android.queuee2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/5/2015.
 */
public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context);
        setDialogComponents();
        setDialogFunctionality();
    }

    private void setDialogComponents() {
        setContentView(R.layout.loading_progress_dialog);
        setTitle("Loading");
        prepareAndRunAnimation();
    }

    private void setDialogFunctionality() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private void prepareAndRunAnimation() {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.waiting_animation))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        SimpleDraweeView sdv = (SimpleDraweeView) findViewById(R.id.waiting_animation);
        sdv.setController(controller);
    }
}
