package com.example.szaman.ocrnote2;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.szaman.ocrnote2.database.Note;
import com.example.szaman.ocrnote2.utils.NotesListViewModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;

public class NoteEditActivity extends AppCompatActivity {

    private Note note;
    private NotesListViewModel notesListViewModel;
    private ViewSwitcher descSwitcher;
    private ViewSwitcher textSwitcher;
    private TextView descText;
    private TextView textView;
    private EditText descEdit;
    private EditText textEdit;
    private TextToSpeech tts;
    private ProgressDialog dialog;
    private static final String QUERY_URL = "http://ws.detectlanguage.com/0.2/detect?q=";
    private static final String KEY = "&key=8aa312e301d39844db5d7ae8b79c5a2c";
    private JSONArray jsonArray = new JSONArray();
    private JSONObject jsonObject = new JSONObject();
    private String language = "";
    private Switch aSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        notesListViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);

        descSwitcher = (ViewSwitcher) findViewById(R.id.desc_switcher);
        aSwitch = (Switch) findViewById(R.id.authSwitch);
        descText = (TextView) descSwitcher.findViewById(R.id.clickableDesc);
        descEdit = (EditText) descSwitcher.findViewById(R.id.hiddenEditDesc);
        descText.setText(note.getDesc());
        descEdit.setText(note.getDesc());
        if(aSwitch.isEnabled()){
            aSwitch.setChecked(note.isProtected());
        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            aSwitch.setVisibility(View.GONE);
        } else {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if(fingerprintManager == null || !fingerprintManager.isHardwareDetected()){
                aSwitch.setVisibility(View.GONE);
            } else if(!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(this, getResources().getString(R.string.no_fingerprints), Toast.LENGTH_LONG).show();
                aSwitch.setEnabled(false);
            } else if(!keyguardManager.isKeyguardSecure()){
                Toast.makeText(this, getResources().getString(R.string.no_security), Toast.LENGTH_LONG).show();
                aSwitch.setEnabled(false);
            }
        }

        descText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                descSwitcher.showNext();
                return true;
            }
        });


        textSwitcher = (ViewSwitcher) findViewById(R.id.text_switcher);
        textView = (TextView) textSwitcher.findViewById(R.id.clickableText);
        textEdit = (EditText) textSwitcher.findViewById(R.id.hiddenEditText);
        textView.setText(note.getText());
        textEdit.setText(note.getText());

        textView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                textSwitcher.showNext();
                return true;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.wait));
        dialog.setCancelable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = descEdit.getText().toString();
                String text = textEdit.getText().toString();
                if(desc.equals("") || text.equals("")){
                    Snackbar.make(view, getResources().getText(R.string.empty_field), Snackbar.LENGTH_LONG).show();
                } else {
                    if(aSwitch.isEnabled()){
                        note.setProtected(aSwitch.isChecked());
                    }
                    note.setDesc(desc);
                    note.setText(text);
                    note.setTimestamp(Calendar.getInstance().getTime());
                    notesListViewModel.updateNote(note);
                    finish();
                }
            }
        });



        if(isOnline()){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            queryLanguage(note.getText());
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                    dialog.dismiss();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.http_info))
                    .setPositiveButton(getResources().getString(R.string.confirm), dialogClickListener)
                    .setNegativeButton(getResources().getString(R.string.cancel), dialogClickListener)
                    .show();
        }

        TextToSpeech.OnInitListener listener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.getDefault());
                }
            }
        };
        tts = new TextToSpeech(getApplicationContext(), listener);

        FloatingActionButton speech = (FloatingActionButton) findViewById(R.id.fabSpeech);
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(note.getDesc() + "\n" + note.getText(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tts != null && tts.isSpeaking()){
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts != null){
            tts.shutdown();
        }
    }


    private void queryLanguage(String text){
        String urlString = "";

        try {
            urlString = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String fullURL = QUERY_URL + urlString + KEY;
        AsyncHttpClient client = new AsyncHttpClient();
        dialog.show();
        client.get(fullURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                dialog.dismiss();
                jsonObject = object.optJSONObject("data");
                jsonArray = jsonObject.optJSONArray("detections");
                if(jsonArray != null){
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject item = jsonArray.optJSONObject(i);
                        boolean reliable = item.optBoolean("isReliable");
                        if(reliable){
                            language = item.optString("language");
                            break;
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.get_success), Toast.LENGTH_LONG).show();
                if(tts != null){
                    tts.setLanguage(Locale.forLanguageTag(language));
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error){
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.get_failure), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

}
