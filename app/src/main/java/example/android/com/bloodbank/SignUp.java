package example.android.com.bloodbank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SignUp extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private EditText mPasswordCheck;
    private Button mSignUp;
    private CheckBox mRedCross;

    ArrayList<Person> allUsers = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference fireRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

         class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return new PasswordCharSequence(source);
            }

            class PasswordCharSequence implements CharSequence {
                private CharSequence mSource;
                public PasswordCharSequence(CharSequence source) {
                    mSource = source; // Store char sequence
                }
                public char charAt(int index) {
                    return '*'; // This is the important part
                }
                public int length() {
                    return mSource.length(); // Return default
                }
                public CharSequence subSequence(int start, int end) {
                    return mSource.subSequence(start, end); // Return default
                }
            }
        };


        mUsername = (EditText) findViewById(R.id.edit_text_username);
        mPassword = (EditText) findViewById(R.id.edit_text_password);
        mPasswordCheck = (EditText) findViewById(R.id.edit_text_password_check);
        mSignUp = (Button) findViewById(R.id.sign_up_button);
        mRedCross = (CheckBox) findViewById(R.id.check_box_redcross);

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String passCheck = mPasswordCheck.getText().toString();

                for (Person user : allUsers) {
                    if (!user.getUsername().equals(username) && username.length() > 0) {
                        if (password.equals(passCheck) && mRedCross.isChecked() && password.length() > 0) {
                            Person newUser = new Person(username, password, true);
                            String ID = fireRef.push().getKey();

                            newUser.setID(ID);
                            fireRef.child(ID).setValue(newUser);

                            Intent goToSearch = new Intent(getApplicationContext(), RedCrossSearchActivity.class);
                            startActivity(goToSearch);
                            finish();
                            break;
                        }
                        else if (password.equals(passCheck) && password.length() > 0) {
                            Person newUser = new Person(username,password);
                            Intent newIntent = new Intent(getApplicationContext(), UserInfo.class);
                            newIntent.putExtra("USER", newUser);
                            startActivity(newIntent);
                            finish();
                            break;
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Your passwords do not match or are invalid.", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Username already taken or invalid.", Toast.LENGTH_LONG).show();
                        break;
                    }
                }



            }
        });

        mPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        mPasswordCheck.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        fireRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (allUsers != null)
                    allUsers.clear();

                Log.e("Count " ,""+snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Person post = postSnapshot.getValue(Person.class);
                    Log.e("Get Data", "Got the data");
                    allUsers.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FAILURE", "reading failure");
            }
        });

    }

}
