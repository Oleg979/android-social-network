package com.appsnipp.loginsamples.views.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.models.auth.RegistrationBody;
import com.appsnipp.loginsamples.services.api.ApiService;
import com.appsnipp.loginsamples.services.api.HttpClient;
import com.appsnipp.loginsamples.services.api.SchedulerProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;


public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.inputEmail)  EditText inputEmail;
    @BindView(R.id.inputPassword) EditText inputPassword;
    @BindView(R.id.inputRepeatPassword) EditText inputRepeatPassword;
    @BindView(R.id.inputName) EditText inputName;
    @BindView(R.id.registrationButton) Button registrationButton;

    private ApiService apiService = HttpClient.getApiService();
    private Disposable subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.resetForm) void resetForm() {
        this.inputEmail.setText("");
        this.inputPassword.setText("");
        this.inputRepeatPassword.setText("");
        this.inputName.setText("");
    }

    @OnClick(R.id.registrationButton) void registration() {
        Log.i("Register Activity", "registration");
        String email = inputEmail.getText().toString();
        String password =  inputPassword.getText().toString();
        String repeatPassword = inputRepeatPassword.getText().toString();
        String name = inputName.getText().toString();
        if(email.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty() || repeatPassword.trim().isEmpty()) {
            this.showErrorDialog("Заполните все поля!");
            return;
        }
        if(!password.equals(repeatPassword)) {
            this.showErrorDialog("Пароли не совпадают!");
            return;
        }
        RegistrationBody registrationBody = new RegistrationBody(email, name, password);
        this.registrationButton.setText("Регистрация...");
        subscription = this.apiService
                .registerUser(registrationBody)
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .doFinally(() -> {
                    this.registrationButton.setText("Зарегистрироваться");
                })
                .subscribe(response -> {
                    if(response.isSuccess()) {
                        Log.i("Successful registration", response.getUsername());
                        this.toLogin();
                    } else {
                        this.showErrorDialog(response.getError());
                    }
                }, error -> {
                    this.showErrorDialog(error.getMessage());
                });
    }


    @OnClick(R.id.to_login) void toLogin() {
        Log.i("Register Activity", "to login");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage("Ошибка регистрации: " + message)
                .setPositiveButton("Ok", (dialog, id) -> {

                });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.dispose();
    }
}
