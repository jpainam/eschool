package com.edis.eschool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.edis.eschool.notes.NotesActivity;
import com.edis.eschool.pojo.Student;

public class InnerMenuActivity extends AppCompatActivity {
    private static final String TAG = "InnerMenuActivity";
    Context context;

    Student currentStudent;
    TextView detailStudent;
    TextView detailClasse;
    CardView noteCard;
    CardView bulletinCard;
    CardView absenceCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_menu);
        context = getApplicationContext();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        Intent i = getIntent();
        currentStudent = (Student) i.getSerializableExtra("student");
        detailStudent = findViewById(R.id.studentDetail);
        detailClasse = findViewById(R.id.detailClasse);
        noteCard = findViewById(R.id.noteCard);
        bulletinCard = findViewById(R.id.bulletinCard);
        absenceCard = findViewById(R.id.absenceCard);


        detailStudent.setText(currentStudent.getFirstName() + " " + currentStudent.getLastName());
        detailClasse.setText(currentStudent.getClasse() + "dee");

        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotesActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
