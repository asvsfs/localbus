package amir.app.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.strongloop.android.loopback.AccessToken;
import com.strongloop.android.loopback.UserRepository;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

import java.util.ArrayList;
import java.util.List;

import amir.app.business.callbacks.SimpleCallback;
import amir.app.business.models.Customer;
import amir.app.business.models.Token;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;


/**
 * A login screen that offers login via editEmail/editPassword.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "palizesoftware@gmail.com:dc17klm2"
    };

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.login_progress)
    ProgressBar loginProgress;
    @NotEmpty(messageId = R.string.error_invalid_email)
    @BindView(R.id.email)
    AutoCompleteTextView editEmail;
    @NotEmpty(messageId = R.string.error_incorrect_password)
    @BindView(R.id.password)
    EditText editPassword;
    @BindView(R.id.login_form)
    ScrollView loginForm;
    @BindView(R.id.loginlayout)
    View loginlayout;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //config toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup back button on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        populateAutoComplete();

        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //Check Current Auth Token to see if it's still valid to use
//        showProgress(true);
//        Customer.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);
//        repository.authCheck(new SimpleCallback() {
//            @Override
//            public void onSuccess(String response) {
//                showProgress(false);
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                finish();
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                showProgress(false);
//                Log.w("SIMPLE CALLBACK", t.toString());
//            }
//        });
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid editEmail, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        //Check fields validation
        if (!util.isValid(this))
            return;

        util.hidekeyboard(this, editEmail);
        util.hidekeyboard(this, editPassword);

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Customer.Repository repository = GuideApplication.getLoopBackAdapter().createRepository(Customer.Repository.class);
                repository.loginUser(editEmail.getText().toString(),
                        editPassword.getText().toString(), new UserRepository.LoginCallback<Customer>() {
                            @Override
                            public void onSuccess(AccessToken token, Customer currentUser) {
                                showProgress(false);
                                GuideApplication.getLoopBackAdapter().setAccessToken(token.getId().toString());
                                config.token = new Token();
                                config.token.id = token.getId().toString();
                                config.token.userId = token.getUserId().toString();

                                config.setValue(LoginActivity.this, "token", config.token.id);
                                config.setValue(LoginActivity.this, "userid", config.token.userId);

                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onError(Throwable t) {
                                util.alertDialog(LoginActivity.this, "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        finish();
                                    }
                                }, SweetAlertDialog.ERROR_TYPE);
                            }
                        });
//                repository.loginUserA(
//                        editEmail.getText().toString(),
//                        editPassword.getText().toString(),
//                        true,
//                        new Customer.LoginCallback() {
//                            @Override
//                            public void onSuccess(Token token) {
//                                showProgress(false);
//                                GuideApplication.getLoopBackAdapter().setAccessToken(token.id);
//                                config.token = token;
//
//                                finish();
//
////                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
////                                finish();
//                            }
//
//                            @Override
//                            public void onError(Throwable t) {
//                                showProgress(false);
//                                util.alertDialog(LoginActivity.this, "بستن", "خطا در ارتباط با شبکه", "خطا", new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                        finish();
//                                    }
//                                }, SweetAlertDialog.ERROR_TYPE);
//                            }
//                        });
            }
        }, 2000);

//            mAuthTask = new UserLoginTask(editEmail, editPassword);
//            mAuthTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginlayout.setVisibility(show ? View.VISIBLE : View.GONE);

//            loginForm.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//            loginProgress.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
        } else {
            loginlayout.setVisibility(show ? View.VISIBLE : View.GONE);

            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
//            loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//            loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only editEmail addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary editEmail addresses first. Note that there won't be
                // a primary editEmail address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        editEmail.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                //Network access.


            } catch (Exception e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the editPassword matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                editPassword.setError(getString(R.string.error_incorrect_password));
                editPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

