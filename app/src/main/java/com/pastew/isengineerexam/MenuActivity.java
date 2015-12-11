package com.pastew.isengineerexam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pastew.isengineerexam.data.FileParser;
import com.pastew.isengineerexam.data.Subject;
import com.pastew.isengineerexam.data.Subjects;

import java.io.IOException;
import java.util.List;

public class MenuActivity extends Activity {

    public final static String QUESTIONS_NUMBER = "QUESTIONS_NUMBER";
    public final static String MODE = "MODE";

    public final static String RANDOM_TEST_MODE = "RANDOM_TEST_MODE";
    public final static String RANDOM_RANGE_TEST_MODE = "RANDOM_RANGE_TEST_MODE";

    public final static String START_QUESTION_ID = "START_QUESTION_ID";
    public final static String END_QUESTION_ID = "END_QUESTION_ID";

    private Subjects subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        populateSubjectsSpinner();
        addButtonsListeners();

    }

    private void addButtonsListeners() {
        (findViewById(R.id.minus_questions_number)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestionsNumber(-5);
            }
        });

        (findViewById(R.id.plus_questions_number)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestionsNumber(5);
            }
        });

        (findViewById(R.id.random_test_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRandomTest();
            }
        });

        (findViewById(R.id.subject_test_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner subjectSpinner = (Spinner)findViewById(R.id.subjects_spinner);
                String selectedSubject = subjectSpinner.getSelectedItem().toString();
                Subject subject = subjects.getSubject(selectedSubject);

                if(subject == null)
                    Toast.makeText(getApplicationContext(), "Coś poszło nie tak...", Toast.LENGTH_LONG); // This should not happen.

                int startQuestionId = subject.getFirstQuestionId();
                int endQuestionId = subject.getLastQuestionId();

                startRandomTest(startQuestionId, endQuestionId);
            }
        });
    }

    private void startRandomTest() {
        Intent intent = new Intent(this, TestActivity.class);
        int questionsNumber = Integer.parseInt(((TextView) findViewById(R.id.questions_number)).getText().toString());

        intent.putExtra(MODE, RANDOM_TEST_MODE);
        intent.putExtra(QUESTIONS_NUMBER, questionsNumber);

        startActivity(intent);
    }

    private void startRandomTest(int startQuestionId, int endQuestionId) {
        Intent intent = new Intent(this, TestActivity.class);
        int questionsNumber = Integer.parseInt(((TextView) findViewById(R.id.questions_number)).getText().toString());

        intent.putExtra(MODE, RANDOM_RANGE_TEST_MODE);
        intent.putExtra(QUESTIONS_NUMBER, questionsNumber);
        intent.putExtra(START_QUESTION_ID, startQuestionId);
        intent.putExtra(END_QUESTION_ID, endQuestionId);

        startActivity(intent);
    }

    private void addQuestionsNumber(int number) {
        TextView questionsNumberTV = (TextView) findViewById(R.id.questions_number);
        int questionsNumber = Integer.parseInt(questionsNumberTV.getText().toString());

        if(questionsNumber + number > 0 && questionsNumber + number < 711) //TODO hardcode
            questionsNumber += number;

        questionsNumberTV.setText(Integer.toString(questionsNumber));
    }

    private void populateSubjectsSpinner() {
        try {
            subjects = FileParser.readSubjects(this.getAssets().open("subjects.txt"));
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.cant_load_answers), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        List<String> subjectsNameList = subjects.getNamesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectsNameList);
        ((Spinner) findViewById(R.id.subjects_spinner)).setAdapter(adapter);
    }
}