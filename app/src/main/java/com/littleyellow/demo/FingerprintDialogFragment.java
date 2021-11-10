package com.littleyellow.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.Cipher;

/**
 * Created by guolin
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintDialogFragment extends DialogFragment {

    private FingerprintManagerCompat fingerprintManager;

    private CancellationSignal mCancellationSignal;

    private Cipher mCipher;


    private TextView errorMsg;

    /**
     * 标识是否是用户主动取消的认证。
     */
    private boolean isSelfCancelled;

    public void setCipher(Cipher cipher) {
        mCipher = cipher;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fingerprintManager = FingerprintManagerCompat.from(getContext());
//        fingerprintManager = getContext().getSystemService(FingerprintManagerCompat.class);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        setCancelable(false);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_dialog, container, false);
        errorMsg = v.findViewById(R.id.error_msg);
        TextView cancel = v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                stopListening();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 开始指纹认证监听
        startListening(mCipher);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止指纹认证监听
        stopListening();
    }

    private void startListening(Cipher cipher) {
        isSelfCancelled = false;
        mCancellationSignal = new CancellationSignal();
        final Context context = getContext();
//        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode, CharSequence errString) {
//                if (!isSelfCancelled) {
//                    errorMsg.setText(errString);
//                    if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
//                        Toast.makeText(context, errString, Toast.LENGTH_SHORT).show();
//                        dismiss();
//                    }
//                }
//            }
//
//            @Override
//            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//                errorMsg.setText(helpString);
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//                Toast.makeText(context, "指纹认证成功", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                errorMsg.setText("指纹认证失败，请再试一次");
//            }
//        }, null);
        fingerprintManager.authenticate(new FingerprintManagerCompat.CryptoObject(cipher),0, mCancellationSignal,  new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (!isSelfCancelled) {
                    errorMsg.setText(errString);
                    if (errMsgId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        Toast.makeText(context, errString, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                errorMsg.setText(helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                Toast.makeText(context, "指纹认证成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                errorMsg.setText("指纹认证失败，请再试一次");
            }
        },new Handler());
    }

    private void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            isSelfCancelled = true;
        }
    }

}
