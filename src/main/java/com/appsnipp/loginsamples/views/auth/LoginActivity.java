package com.appsnipp.loginsamples.views.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.models.auth.LoginBody;
import com.appsnipp.loginsamples.services.Storage;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;
import com.appsnipp.loginsamples.views.feed.PostListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.editTextEmail) EditText inputEmail;
    @BindView(R.id.editTextPassword) EditText inputPassword;
    @BindView(R.id.cirLoginButton) Button loginButton;

    private ApiService apiService = HttpClient.getApiService();
    private Disposable subscription;

    @OnClick(R.id.resetForm) void resetForm() {
        this.inputEmail.setText("");
        this.inputPassword.setText("");
    }

    @OnClick(R.id.cirLoginButton) void login() {
        Log.i("Login Activity", "login");
        String email = inputEmail.getText().toString();
        String password =  inputPassword.getText().toString();
        if(email.trim().isEmpty() || password.trim().isEmpty()) {
            this.showErrorDialog("Заполните все поля!");
            return;
        }
        LoginBody loginBody = new LoginBody(email, password);
        this.loginButton.setText("Авторизация...");
        subscription = this.apiService
                .loginUser(loginBody)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .doFinally(() -> {
                    this.loginButton.setText("Войти");
                })
                .subscribe(response -> {
                    if(response.isSuccess()) {
                        Log.i("Successful login", response.getUsername());
                        Storage.currentUser = response.getUsername();
                        resetForm();
                        Intent intent = new Intent(this, PostListActivity.class);
                        startActivity(intent);
                    } else {
                        this.showErrorDialog(response.getError());
                    }
                }, error -> {
                    Log.i("Login Activity", error.toString());
                    this.showErrorDialog(error.getMessage());
                });
    }

    @OnClick(R.id.to_register) void toRegister() {
        Log.i("Login Activity", "to register");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Ошибка авторизации: " + message)
                .setPositiveButton("Ok", (dialog, id) -> {

                });
         builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.dispose();
    }
}
