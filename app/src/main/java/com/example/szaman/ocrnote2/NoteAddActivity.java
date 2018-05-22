package com.example.szaman.ocrnote2;

import android.app.KeyguardManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.szaman.ocrnote2.database.Note;
import com.example.szaman.ocrnote2.utils.NotesListViewModel;

import java.util.Calendar;

public class NoteAddActivity extends AppCompatActivity {

    private EditText descEdit;
    private EditText textEdit;
    private NotesListViewModel notesListViewModel;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        descEdit = findViewById(R.id.noteDescInput);
        textEdit = findViewById(R.id.noteTextInput);
        notesListViewModel = ViewModelProviders.of(this).get(NotesListViewModel.class);
        aSwitch = findViewById(R.id.authSwitch);

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
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            String text = (String) bundle.get("text");
            textEdit.setText(text);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = descEdit.getText().toString();
                String text = textEdit.getText().toString();
                if(desc.equals("") || text.equals("")){
                    Snackbar.make(view, getResources().getText(R.string.empty_field), Snackbar.LENGTH_LONG).show();
                } else {
                    Note note = new Note(desc, text, Calendar.getInstance().getTime());
                    if(aSwitch.isChecked() && aSwitch.isEnabled()){
                        note.setProtected(true);
                    }
                    notesListViewModel.addNote(note);
                    finish();
                }
            }
        });

    }


}
