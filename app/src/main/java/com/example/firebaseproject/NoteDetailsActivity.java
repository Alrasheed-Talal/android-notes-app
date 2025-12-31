package com.example.firebaseproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn,deleteNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    Boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.note_title_txt);
        contentEditText = findViewById(R.id.note_content_txt);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteBtn = findViewById(R.id.delete_note_btn);

        //Receive data for edit note
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId!= null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteBtn.setVisibility(TextView.VISIBLE);
        }


        saveNoteBtn.setOnClickListener( (v)-> saveNote());
        
        deleteNoteBtn.setOnClickListener((v)-> deleteNoteToFirebase());
    }




    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){

        DocumentReference documentReference;
        if (isEditMode){
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Note is added
                    Toast.makeText(NoteDetailsActivity.this, "Note added successfully",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NoteDetailsActivity.this, "Failed while adding note",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }
    private void deleteNoteToFirebase() {
        DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Note is deleted
                    Toast.makeText(NoteDetailsActivity.this, "Note is deleted successfully",Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NoteDetailsActivity.this, "Failed while deleting note",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

}