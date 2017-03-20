package com.mozz.htmlnativedemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mozz.htmlnative.common.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Yang Tao, 17/3/2.
 */

public class LayoutExampleActivity extends AppCompatActivity {

    static final String EXTRA_KEY_RV_FILE = "rv_asset_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AssetsViewLoader mLoader = new AssetsViewLoader(this);
        String fileName = getIntent().getStringExtra(EXTRA_KEY_RV_FILE);
        mLoader.load(fileName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout_example, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_code:
                showCode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCode() {
        String code;
        try {
            code = readCode(getIntent().getStringExtra(EXTRA_KEY_RV_FILE));
            CodeReadDialog dialog = new CodeReadDialog();
            Bundle bundle = new Bundle();
            bundle.putString("code", code);

            dialog.setArguments(bundle);

            dialog.show(getFragmentManager(), "dialog");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static class CodeReadDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String code = getArguments().getString("code");

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(code)
                    .setNegativeButton("close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    String readCode(String fileName) throws IOException {
        InputStream s = getAssets().open(fileName);
        byte[] buffer = new byte[s.available()];
        s.read(buffer);
        Utils.closeQuitely(s);

        return new String(buffer);
    }
}
