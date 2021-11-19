package com.huawei.scanlah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;

public class MainActivity extends AppCompatActivity {

    private AccountAuthService mAuthService;
    private AccountAuthParams mAuthParam;
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    private static final String TAG = "Scan Lah";
    private ImageView logo;
    HuaweiIdAuthButton huaweiIdAuthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        huaweiIdAuthButton = findViewById(R.id.btnSignIn);
        huaweiIdAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                silentSignInByHwId();
            }
        });
        logo=findViewById(R.id.logo);
    }

    private void silentSignInByHwId(){
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setIdToken()
                .createParams();
        mAuthService = AccountAuthManager.getService(this,mAuthParam);
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>(){
            @Override
                    public void onSuccess(AuthAccount authAccount){
                dealWithResultOfSignIn(authAccount);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Intent signInIntent = mAuthService.getSignInIntent();
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                }
            }
        });
    }
    private void dealWithResultOfSignIn(AuthAccount authAccount) {
        Log.i(TAG, "idToken:" + authAccount.getIdToken());
        Log.i(TAG, "Name:" + authAccount.getDisplayName());
        Log.i(TAG, "Email:" + authAccount.getEmail());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivityResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i(TAG, "onActivityResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
                Toast.makeText(getApplicationContext(),"Sign In success", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(myIntent);

            } else {
                Log.e(TAG, "sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
                Toast.makeText(getApplicationContext(),"Sign In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}