package com.edis.eschool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.edis.eschool.pojo.Student;

public class InnerMenuActivity extends AppCompatActivity {
    Student currentStudent;
    TextView detailStudent;
    TextView detailClasse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_menu);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        Intent i = getIntent();
        currentStudent = (Student) i.getSerializableExtra("student");
        //detailStudent = findViewById(R.id.detailStudent);
        //detailClasse = findViewById(R.id.detailsClasse);
        //detailStudent.setText(currentStudent.getFirstName() + " " + currentStudent.getLastName());
        //detailClasse.setText(currentStudent.getClasse() + "dee");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
