package hcmute.edu.vn.zalo_05.Services;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import hcmute.edu.vn.zalo_05.R;

public class LoadingDialogService {
    private static LoadingDialogService instance;
    private Dialog progressDialog;

    private LoadingDialogService() {
    }

    public static synchronized LoadingDialogService getInstance() {
        if(instance == null) {
            instance = new LoadingDialogService();
        }
        return instance;
    }

    public void show(Context context) {
        if(progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = new Dialog(context);
        onCreate(progressDialog);
        progressDialog.show();
    }

    public void dismiss() {
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void onCreate(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading_layout);
        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
    }
}
