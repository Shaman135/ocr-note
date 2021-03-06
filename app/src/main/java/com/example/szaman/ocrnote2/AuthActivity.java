package com.example.szaman.ocrnote2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.szaman.ocrnote2.database.Note;
import com.example.szaman.ocrnote2.utils.FingerprintHandler;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AuthActivity extends AppCompatActivity {

    private KeyStore keyStore;
    private static final String KEY_NAME = "OCRNote2";
    private Cipher cipher;
    private static final int FINGERPRINT_CODE = 53;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            if(fingerprintManager == null || !fingerprintManager.isHardwareDetected()){
                Toast.makeText(this, getResources().getString(R.string.detection_fail), Toast.LENGTH_LONG).show();
            } else {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FINGERPRINT_CODE);
                } else {
                    if(!fingerprintManager.hasEnrolledFingerprints()){
                        Toast.makeText(this, getResources().getString(R.string.no_fingerprints), Toast.LENGTH_LONG).show();
                    } else {
                        if(keyguardManager == null || !keyguardManager.isKeyguardSecure()){
                            Toast.makeText(this, getResources().getString(R.string.no_security), Toast.LENGTH_LONG).show();
                        } else {
                            generateKey();
                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                Intent intent = getIntent();
                                note = (Note) intent.getSerializableExtra("note");
                                FingerprintHandler helper = new FingerprintHandler(this, note);
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        }
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey(){
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setUserAuthenticationRequired(true)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                            .build());
            keyGenerator.generateKey();
        } catch (IOException | NoSuchAlgorithmException | CertificateException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean cipherInit(){
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == FINGERPRINT_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                if(!fingerprintManager.hasEnrolledFingerprints()){
                    Toast.makeText(this, getResources().getString(R.string.no_fingerprints), Toast.LENGTH_LONG).show();
                } else {
                    if(keyguardManager == null || !keyguardManager.isKeyguardSecure()){
                        Toast.makeText(this, getResources().getString(R.string.no_security), Toast.LENGTH_LONG).show();
                    } else {
                        generateKey();
                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            Intent intent = getIntent();
                            note = (Note) intent.getSerializableExtra("note");
                            FingerprintHandler helper = new FingerprintHandler(this, note);
                            helper.startAuth(fingerprintManager, cryptoObject);
                        }
                    }
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.missing_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }
}
