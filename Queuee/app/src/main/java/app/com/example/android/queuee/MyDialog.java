package app.com.example.android.queuee;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Abraham on 9/2/2015.
 */
//This class is used to build the custom alert dialog
public class MyDialog {

    public static AlertDialog.Builder create(final Context context, final LayoutInflater layoutInflater, final String title, final String content) {
        View view = layoutInflater.inflate(R.layout.warning_dialog, null);
        //Not Needed as it is not replicated - Set in xml
        // ((TextView)view.findViewById(R.id.textViewTitleDialog)).setText(title);
        // ((TextView)view.findViewById(R.id.textViewContentDialog)).setText(content);
        return new AlertDialog.Builder(context).setView(view);
    }

}