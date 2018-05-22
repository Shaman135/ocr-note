package com.example.szaman.ocrnote2.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.szaman.ocrnote2.NoteEditActivity;
import com.example.szaman.ocrnote2.R;
import com.example.szaman.ocrnote2.database.Note;

/**
 * Created by szaman on 28.12.17.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private Note note;

    public FingerprintHandler(Context context, Note note) {
        this.context = context;
        this.note = note;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update(false);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update(false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update(false);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update(true);
    }


    public void update(Boolean success){
        if(success){
            Intent intent = new Intent(context, NoteEditActivity.class);
            intent.putExtra("note", note);
            context.startActivity(intent);
            Activity activity = (Activity) context;
            activity.finish();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.auth_fail), Toast.LENGTH_LONG).show();
        }
    }
}
