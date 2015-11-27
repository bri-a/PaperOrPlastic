/**************************************************************************************************
*   File:     MainSignInActivity.java
*   Author:   Brianna Alcoran
*   Date:     10/28/15
*   Class:    Capstone/Software Engineering
*   Project:  PaperOrPlastic Application
*   Purpose:  This activity will be the opening activity of the application.
*             A user may create an account on this page, or sign-in with an existing one.
*             Users also have the option to save their password for automatic sign-in.
*             Recovering forgotten passwords is also available on this screen.
*             It is also an option to skip these steps and continue on -> the user is then
*             essentially using the application offline.
***************************************************************************************************/


package edu.pacificu.cs493f15_1.paperorplasticapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.pacificu.cs493f15_1.paperorplasticjava.KitchenList;
import edu.pacificu.cs493f15_1.paperorplasticjava.ListItem;
import edu.pacificu.cs493f15_1.paperorplasticjava.FirebaseUser;

public class MainSignInActivity extends AppCompatActivity implements View.OnClickListener
{
  private String FIREBASE_URL = "https://boiling-fire-3734.firebaseio.com/";
  private String SIGNIN_PREFS = "signinPrefs";
  private String SIGNIN_PREFS_BOOLEAN = "saveSignIn";

  //  buttons
  private Button  mButtonSignIn,
                  mButtonCreateAccount,
                  mButtonRecoverPassword,
                  mButtonResetPassword,
                  mButtonContinue;

  //  checkboxes
  private CheckBox mcbRememberPass;

  //testing new email input box
  private AutoCompleteTextView mEmailView;

  //  edit text fields
  private EditText  mEditPassword;

  private SharedPreferences mSignInPreferences;
  private SharedPreferences.Editor mSignInPrefsEditor;

  private boolean saveSignIn;

  //  firebase reference
  private Firebase  myFirebaseRef;

  private FirebaseUser mfCurrentUser;
  private boolean mAuthSuccess;

  private View mLoginFormView;



/***************************************************************************************************
 *   Method:        onCreate
 *   Description:   is called when the activity is created. Sets the content view and initializes
 *                  our buttons, text fields, and firebase (connection to the cloud database).
 *                  As of right now, there are also testing functions being called after the
 *                  necessary initialization.
 *   Parameters:    savedInstanceState
 *   Returned:      N/A
 ***************************************************************************************************/
  @Override
  protected void onCreate (Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main_sign_in);

    initializeActivity();

    //testWritingDataToFirebase();
    //testCreateFirebaseUsers();

  }

/***************************************************************************************************
 *   Method:       initializeActivity
 *   Description:  calls all initialization functions
 *   Parameters:   N/A
 *   Returned:     N/A
 ***************************************************************************************************/
  private void initializeActivity()
  {
    initializeButtons();
    initializeEditFields();
    initializeCheckboxes();
    initializeFirebase();
    initializeSignInPrefs();

    mAuthSuccess = false;
    mLoginFormView = findViewById(R.id.loginForm);
  }


/***************************************************************************************************
 *   Method:       initializeButtons
 *   Description:  pairs member variable buttons with the views on this activity by ID
 *   Parameters:   N/A
 *   Returned:     N/A
 ***************************************************************************************************/
private void initializeButtons ()
{
  //  initializing/pairing buttons
  mButtonSignIn = (Button) findViewById (R.id.bSignIn);
  mButtonCreateAccount = (Button) findViewById (R.id.bCreateAccount);
  mButtonRecoverPassword = (Button) findViewById (R.id.bRecoverPassword);
  mButtonContinue = (Button) findViewById (R.id.bContinue);
  mButtonResetPassword = (Button) findViewById (R.id.bResetPassword);

  //  on click listener for buttons (connect to the view)
  mButtonSignIn.setOnClickListener(this);
  mButtonCreateAccount.setOnClickListener(this);
  mButtonRecoverPassword.setOnClickListener(this);
  mButtonContinue.setOnClickListener(this);
  mButtonResetPassword.setOnClickListener(this);
}

/***************************************************************************************************
 *   Method:       initializeCheckboxes
 *   Description:  pairs member variable checkboxes with the views on this activity by ID
 *   Parameters:   N/A
 *   Returned:     N/A
 ***************************************************************************************************/
private void initializeCheckboxes()
{
  mcbRememberPass = (CheckBox) findViewById(R.id.cbRememberPassword);
  mcbRememberPass.setChecked(false);
  saveSignIn = false;
}


/***************************************************************************************************
 *   Method:       initializeEditFields
 *   Description:  pairs member variable edit text fields with the views on this activity
 *   Parameters:   N/A
 *   Returned:     N/A
 ***************************************************************************************************/
private void initializeEditFields()
{
  mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
  mEditPassword = (EditText) findViewById (R.id.editTextPassword);
  mLoginFormView = findViewById(R.id.loginForm);
}



/***************************************************************************************************
 *   Method:       initializeSignInPrefs
 *   Description:  sets shared preferences for signing in with this app
 *   Parameters:   N/A
 *   Returned:     N/A
 ***************************************************************************************************/
private void initializeSignInPrefs()
{
  mSignInPreferences = getSharedPreferences(SIGNIN_PREFS, MODE_PRIVATE);
  mSignInPrefsEditor = mSignInPreferences.edit();
  saveSignIn = mSignInPreferences.getBoolean(SIGNIN_PREFS_BOOLEAN, false);
  if (saveSignIn)
  {
    mEmailView.setText(mSignInPreferences.getString("email", ""));
    mEditPassword.setText(mSignInPreferences.getString("password",""));
    mcbRememberPass.setChecked(true);
  }
}



  /***************************************************************************************************
 *   Method:         initializeFirebase
 *   Description:    links firebase with our application by setting our member variable myFirebaseRef
 *                   to the link of our cloud database
 *   Parameters:     N/A
 *   Returned:       N/A
 ***************************************************************************************************/
private void initializeFirebase()
{
  Firebase.setAndroidContext(this);
  myFirebaseRef = new Firebase (FIREBASE_URL);
}

/***************************************************************************************************
 *   Method:      testWritingDataToFirebase
 *   Description: just some testing...
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
private void testWritingDataToFirebase()
{
  myFirebaseRef.child ("message").setValue ("YO WADDUP");
  myFirebaseRef.child ("testing").setValue ("Testing123");


  // test creating a new reference "ref" to save data
  Firebase ref = new Firebase (FIREBASE_URL + "/test-saving-data");

  // creating another reference "alanRef" (which is the child of the above reference)
  // creating reference to a child object -  a user with id alanisawesome
  Firebase alanRef = ref.child ("users").child ("alanisawesome");

  // creating new object testUser
  TestUser alan = new TestUser ("Alan Turing", 1912);

  // setting the value of the reference we created to the java object we created
  alanRef.setValue(alan);

  //now test retrieving this information -- Attach a listener to read the data at our posts reference (test saving data)
  alanRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot snapshot) {
      System.out.println(snapshot.getValue());
      System.out.println("There are " + snapshot.getChildrenCount() + " fields to this user.");
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
      System.out.println("The read failed: " + firebaseError.getMessage());
    }
  });
}

/***************************************************************************************************
 *   Method:      testCreateFirebaseUsers
 *   Description: just more testing....
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
private void testCreateFirebaseUsers ()
{
  //hard coding the addition of a registered user

  myFirebaseRef.createUser("alco8653@pacificu.edu", "password1234",
          new Firebase.ValueResultHandler<Map<String, Object>>()
          {
            @Override
            public void onSuccess(Map<String, Object> result)
            {
              System.out.println("Successfully created user account with uid: " + result.get("uid"));
            }

            @Override
            public void onError(FirebaseError firebaseError)
            {
              // there was an error
              System.out.println("Error creating user. ");
            }
          });

  myFirebaseRef.authWithPassword("alco8653@pacificu.edu", "password1234", new Firebase.AuthResultHandler() {
    @Override
    public void onAuthenticated(AuthData authData) {
      System.out.println("Authenticated the user.");
      System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
    }

    @Override
    public void onAuthenticationError(FirebaseError firebaseError) {
      // there was an error
      System.out.println("Error authenticating user. ");
    }
  });
}


/***************************************************************************************************
 *   Method:      testWriteListToFirebase
 *   Description: just more testing....
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  private void testWriteListToFirebase ()
  {
    KitchenList sTest = new KitchenList("Test List");
    ListItem  s1 = new ListItem("milk"),
              s2 = new ListItem("cookies"),
              s3 = new ListItem("orange juice");

    sTest.addItem(s1);
    sTest.addItem(s2);
    sTest.addItem(s3);


    KitchenList sTest2 = new KitchenList("Groceries");
    ListItem  s12 = new ListItem("pancakes"),
              s22 = new ListItem("cups"),
              s32 = new ListItem("paper towels");

    sTest2.addItem(s12);
    sTest2.addItem(s22);
    sTest2.addItem(s32);

    //user should be signed in at this point
    if (null != mfCurrentUser.getMyRef())
    {
      mfCurrentUser.getMyRef().child("Kitchen Lists").child(sTest.getListName()).setValue(sTest);
      mfCurrentUser.getMyRef().child("Kitchen Lists").child(sTest2.getListName()).setValue(sTest2);
    }

  }

/***************************************************************************************************
 *   Method:      testAddSharedUser
 *   Description: test a user "adding another user to see their lists"
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  private void testAddSharedUser()
  {
    if (null != mfCurrentUser.getMyRef())
    {
      //test adding a shared user

      Map<String, String> test = new HashMap<String, String>();
      test.put("0","TestShareUser");
      mfCurrentUser.getMyRef().child("SharedUsers").child("uid-test123").setValue(test); //TODO: add the uid based on email input

      //Firebase sCurrentList = new Firebase(myFirebaseUserRef.child("Kitchen Lists").child(sTest.getListName()).toString());

      //sCurrentList.child("shared").setValue();
    }
  }


/***************************************************************************************************
 *   Method:      rememberPass
 *   Description: remember the email and password if the checkbox is checked. otherwise, clear
 *                the sign in preferences data
 *   Parameters:  email - user email
 *                password - user password
 *   Returned:    N/A
 ***************************************************************************************************/
public void rememberPass(String email, String password)
{
  if (mcbRememberPass.isChecked())
  {
    mSignInPrefsEditor.putBoolean(SIGNIN_PREFS_BOOLEAN, true);
    mSignInPrefsEditor.putString("email", email);
    mSignInPrefsEditor.putString("password", password);
    mSignInPrefsEditor.commit();
    if (mfCurrentUser != null)
    {
      mfCurrentUser.setmbRememberPass(true);
    }
  }
  else
  {
    mSignInPrefsEditor.clear();
    mSignInPrefsEditor.commit();
    if (mfCurrentUser != null)
    {
      mfCurrentUser.setmbRememberPass(false);
    }
  }
}


/***************************************************************************************************
 *   Method:      onClick
 *   Description: called when a click has been captured.
 *                If selected:
 *                Sign-In:        - capture information in edit text fields and pass to
 *                                  firebase to attempt to authenticate the user
 *                Create Account: - capture information in edit text fields and
 *                                  pass to firebase to attempt to create the user
 *                Recover Password: - an email to recover password is sent to the email
 *                                    typed in the email field
 *                Reset Password: - resets the password associated with the user with token passed in
 *                Continue:       - starts the next activity (home screen)
 *   Parameters:  view - the view that has been clicked
 *   Returned:    N/A
 ***************************************************************************************************/
  public void onClick (View view)
  {
    Intent intent = new Intent (this, ContinueActivity.class); //will be using this to launch to new activity (e.g. clicking continue)

    if (mButtonSignIn == view)
    {
      signInAttempt();
    }

    if (mButtonCreateAccount == view)
    {
      createAccountAttempt();
    }

    if (mButtonRecoverPassword == view)
    {
      recoverPasswordAttempt();
    }

    if (mButtonResetPassword == view)
    {
      resetPasswordAttempt();
    }

    if (mButtonContinue == view)
    {
      intent = new Intent (this, ContinueActivity.class);
      startActivity(intent);
    }

  }


/***************************************************************************************************
 *   Method:      messageDialog
 *   Description: for now, using this as a template to see how to get a dialog box to appear
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  private void messageDialog (String title, String message, final boolean bContinue)
  {
    final Intent intent = new Intent (this, ContinueActivity.class);

    new AlertDialog.Builder (this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int ok) { //user clicked ok
                if (bContinue)
                {
                  if (mAuthSuccess)
                  {
                    intent.putExtra("currentUser", mfCurrentUser);
                  }
                  startActivity(intent);
                }
              }
            }).show();
  }

/***************************************************************************************************
 *   Method:      signInAttempt
 *   Description: to be executed when the user clicks on sign-in button. captures text in the email
 *                 and password edit text fields -> attempts to signin using these credentials. A
 *                 dialog appears after the sign in button has been pressed in order to indicate
 *                 if the sign in attempt was or was not successful
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  public void signInAttempt()
  {
    final String email = mEmailView.getText().toString();
    final String password = mEditPassword.getText().toString();

    //capture text from password editText and email editText ->pass this to firebase authentication
    myFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
      @Override
      public void onAuthenticated(AuthData authData)
      {
        mAuthSuccess = true;

        mfCurrentUser = new FirebaseUser(authData.getUid());
        mfCurrentUser.setmProvider(authData.getProvider());
        mfCurrentUser.getMyRef().child("provider").setValue(authData.getProvider());
        mfCurrentUser.setmEmail(email);

        //testWriteListToFirebase();

        rememberPass(email, password);

        messageDialog("Sign-In", "Successful login.", true);
      }

      @Override
      public void onAuthenticationError(FirebaseError firebaseError)
      {
        // there was an error
        System.out.println("Error authenticating user. ");
        messageDialog("Unable to Sign-In", "Invalid password or email.", false);
      }
    });
  }

/***************************************************************************************************
 *   Method:      createAccountAttempt
 *   Description: to be executed when the user clicks on the create account button.
 *                  captures text in the email and password edit text fields -> attempts to create
 *                  a firebase account using these credentials. A dialog appears after the create
 *                  account button has been pressed in order to indicate if an account using those
 *                  credentials has been successfully created or not. if an account is successfully
 *                  created, the user is then signed in
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  public void createAccountAttempt()
  {
    //capture text from password editText and email editText -> pass this to firebase user create
    final String email = mEmailView.getText().toString();
    final String password = mEditPassword.getText().toString();

    myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
      @Override
      public void onSuccess(Map<String, Object> result) {
        //after create sign-in
        myFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
          @Override
          public void onAuthenticated(AuthData authData) {
            mAuthSuccess = true;

            mfCurrentUser = new FirebaseUser(authData.getUid());
            mfCurrentUser.setmProvider(authData.getProvider());
            mfCurrentUser.getMyRef().child("provider").setValue(authData.getProvider());
            mfCurrentUser.setmEmail(email);
            rememberPass(email, password);

            messageDialog("Create Account", "Account has been created.", true);
          }

          @Override
          public void onAuthenticationError(FirebaseError firebaseError) {
            messageDialog("Unable to Sign-In", "Invalid password or email.", false);
          }
        });
      }

      @Override
      public void onError(FirebaseError firebaseError) {
        // there was an error
        System.out.println("Error creating user. ");
        messageDialog("Unable to Create Account", "Email in use or invalid Email.", false);
      }
    });
  }

/***************************************************************************************************
 *   Method:      recoverPasswordAttempt
 *   Description: to be executed when the user clicks the recover password button. captures the text
 *                in the email text field and attempts to send a recover password email to the email
 *                address typed in. a dialog box appears to display the result of this action
 *                (if the recovery email was successful or not)
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  public void recoverPasswordAttempt()
  {
    final String email = mEmailView.getText().toString();
    myFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
      @Override
      public void onSuccess() {
        messageDialog(getString(R.string.passRecover), "Recovery Email has been sent to: \n" + email, false);
      }

      @Override
      public void onError(FirebaseError firebaseError)// error encountered
      {
        messageDialog(getString(R.string.passRecover), "Error sending recovery Email to: \n" + email, false);
      }
    });
  }

/***************************************************************************************************
 *   Method:      resetPasswordAttempt
 *   Description: to be executed when the user clicks the reset password button. a dialog box appears
 *                  with 3 fields: email, token, and new password.
 *                  email - the email for the account whose password is to be reset
 *                  token - the token sent in the recover password email
 *                  new password - the new password to be set for the account
 *                  TODO: indicate to the user if the reset was executed successfully or not
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/
  public void resetPasswordAttempt()
  {
    final Dialog login = new Dialog (this);

    login.setContentView(R.layout.dialog_pass_recovery);
    login.setTitle("Reset Password");

    Button btnReset = (Button) login.findViewById(R.id.btnReset);
    Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

    final EditText  txtUser = (EditText) login.findViewById(R.id.userEmail),
                    txtToken = (EditText) login.findViewById(R.id.passwordToken),
                    txtNewPass = (EditText) login.findViewById(R.id.newPassword);

    //recoverPasswordDialog();

    btnReset.setOnClickListener (new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        final String  userEmail = txtUser.getText().toString(),
                      userToken = txtToken.getText().toString(),
                      userPass = txtNewPass.getText().toString();

        myFirebaseRef.authWithPassword(userEmail, userToken, new Firebase.AuthResultHandler()
          {
            @Override
            public void onAuthenticated (AuthData authData)
            {
              myFirebaseRef.changePassword (userEmail,userToken, userPass,
                new Firebase.ResultHandler()
                {
                  @Override
                  public void onSuccess()
                  {
                    System.out.println("Password changed. "); //TODO: indicate success to user
                  }

                  @Override
                  public void onError(FirebaseError firebaseError)
                  {
                    System.out.println("Error changing password. "); //TODO: indicate failure to user
                  }
                });
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError)
            {
              // there was an error
            }
          });

        login.dismiss();
      }
    });

    btnCancel.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        login.dismiss();
      }
    });

    login.show();
  }


/***************************************************************************************************
 *   Method:      captureFirebaseError
 *   Description: upon receiving a firebase error, interpret this error and output to the system
 *                 the type of error. TODO: create a dialog box to indicate the error to the user
 *   Parameters:  N/A
 *   Returned:    N/A
 ***************************************************************************************************/

  public void captureFirebaseError (FirebaseError error)
  {
    switch (error.getCode())
    {
      case FirebaseError.EMAIL_TAKEN:
        break;
      case FirebaseError.EXPIRED_TOKEN:
        break;
      case FirebaseError.INVALID_EMAIL:
        break;
      case FirebaseError.INVALID_PASSWORD:
        break;
      case FirebaseError.INVALID_TOKEN:
        break;
      case FirebaseError.INVALID_CREDENTIALS:
        break;
      case FirebaseError.PERMISSION_DENIED:
        break;
      case FirebaseError.OPERATION_FAILED:
        break;
      default:
        System.out.println("Firebase error");

    }
  }



/*  private void recoverPasswordDialog()
  {
    LayoutInflater inflater = this.getLayoutInflater();

    AlertDialog.Builder test;
    DialogInterface.OnClickListener resetDialog;
    test = new AlertDialog.Builder (this).setView(inflater.inflate(R.layout.dialog_pass_recovery, null));



    test.setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      @Override
      public void onClick(DialogInterface dialog, int ok)
      {
        //user clicked ok

        myFirebaseRef.authWithPassword(dUserEmail.getText().toString(),
                dPassToken.getText().toString(),
                new Firebase.AuthResultHandler()
                {
                  @Override
                  public void onAuthenticated(AuthData authData)
                  {
                    myFirebaseRef.changePassword(dUserEmail.getText().toString(),
                            dPassToken.getText().toString(), dPassNew.getText().toString(),
                            new Firebase.ResultHandler()
                            {
                              @Override
                              public void onSuccess()
                              {
                                System.out.println("Password changed. ");
                              }

                              @Override
                              public void onError(FirebaseError firebaseError)
                              {
                                System.out.println("Error changing password. ");
                              }
                            });
                  }

                  @Override
                  public void onAuthenticationError(FirebaseError firebaseError)
                  {
                    // there was an error
                  }
                });
      }
    })
    .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int cancel)
      {
        //user clicked cancel
        //LoginDialogFragment.this.getDialog().cancel();
      }
    });

    test.show();

  }*/



}
