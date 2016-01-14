package com.bodyweight.fitness.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.bodyweight.fitness.R;
import com.bodyweight.fitness.network.AuthenticationProvider;
import com.bodyweight.fitness.network.LoginImpl;
import com.bodyweight.fitness.network.LoginInterface;
import com.bodyweight.fitness.network.LoginRequest;
import com.bodyweight.fitness.network.LoginResponse;
import com.bodyweight.fitness.utils.Logger;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    @InjectView(R.id.input_email)
    EditText _emailText;

    @InjectView(R.id.input_password)
    EditText _passwordText;

    @InjectView(R.id.button_login)
    Button _loginButton;

    @InjectView(R.id.link_signup)
    TextView _signupLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_login)
    public void onClickButtonLogin(View view) {
        if (!validate()) {
            onLoginFailed();

            return;
        }

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        Observable.OnSubscribe<LoginResponse> subscribeFunction = (subscriber) -> {
            LoginResponse loginResponse = LoginImpl.getInstance().invokeLogin(
                    new LoginRequest(email, password)
            );

            if (!loginResponse.getLogin()) {
                subscriber.onError(new Exception("Failed to login."));
                subscriber.onCompleted();
            }

            subscriber.onNext(loginResponse);
            subscriber.onCompleted();
        };

        Observable.create(subscribeFunction)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (response) -> {
                            LoginImpl.getInstance().login(
                                    email,
                                    response.getIdentityId(),
                                    response.getToken()
                            );

                            print("Logged in : " + response.getIdentityId());

                            LoginActivity.this.finish();
                        },
                        (error) -> {
                            print(error.getMessage());
                        },
                        () -> {
                            Logger.d("Completed");
                        });
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public void print(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
