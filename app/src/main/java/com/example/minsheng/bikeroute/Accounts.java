package com.example.minsheng.bikeroute;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Divya on 11/12/2016.
 */

public class Accounts extends Fragment {

    private EditText mLogin;
    private EditText mPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private Button mSave;
    private static String JSON_request ;
    private String userID;
    private String Date;
    private IdentityManager identityManager;
    public static final String BIKER_DETAILS = "BikerDetails";
    private String userId;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.account_fragment,container,false);

        mLogin =(EditText)v.findViewById(R.id.login);
        mFirstName = (EditText)v.findViewById(R.id.firstname);
        mLastName = (EditText)v.findViewById(R.id.lastname);
        mPassword = (EditText)v.findViewById(R.id.password);

        Date currDate = new Date();
        Date = currDate.toString();

        final SharedPreferences settings =getActivity().getSharedPreferences(BIKER_DETAILS, Context.MODE_PRIVATE);

        AWSMobileClient.initializeMobileClientIfNecessary(getActivity());
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        identityManager = awsMobileClient.getIdentityManager();


        mSave = (Button)v.findViewById(R.id.save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSON_request =  " { \"Login\": \"" + mLogin.getText().toString() + "\", " +
                        "\"Password\": \"" + mPassword.getText().toString() + "\",  " +
                        "\"firstName\": \"" + mFirstName.getText().toString() + "\",  " +
                        " \"lastName\": \"" + mLastName.getText().toString() + "\" ," +
                        " \"Date\": \"" + Date + "\" ," +
                        " \"userId\": \"" + "" +"\" }";
                userID = mFirstName.getText().toString() + mLastName.getText().toString();
                String USERID = settings.getString("userId",null);

                if(!USERID.equals(userID))
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.putString("userId",userID);
                    editor.commit();
                    invokeFunction(JSON_request);
                }
                else
                    Toast.makeText(getActivity(),"Account already created",Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void invokeFunction(String JSON_request) {

        final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
        final CharsetEncoder ENCODER = CHARSET_UTF8.newEncoder();
        final CharsetDecoder DECODER = CHARSET_UTF8.newDecoder();

        final String functionName = "BikeRoute-insertBikerDetails-mobilehub-999666314";
        final String requestPayLoad = JSON_request;

        AsyncTask<Void, Void, InvokeResult> myTask = new AsyncTask<Void, Void, InvokeResult>()
        {
            @Override
            protected InvokeResult doInBackground(Void... params) {

                    try {

                        final ByteBuffer payload =
                                ENCODER.encode(CharBuffer.wrap(requestPayLoad));

                        final InvokeRequest invokeRequest =
                                new InvokeRequest()
                                        .withFunctionName(functionName)
                                        .withInvocationType(InvocationType.RequestResponse)
                                        .withPayload(payload);

                        final InvokeResult invokeResult =
                                AWSMobileClient
                                        .defaultMobileClient()
                                        .getCloudFunctionClient()
                                        .invoke(invokeRequest);
                        return invokeResult;
                    } catch (final Exception e) {
                        Log.e("AWSLAMBDA:", "AWS Lambda invocation failed : " + e.getMessage(), e);
                        final InvokeResult result = new InvokeResult();
                        result.setStatusCode(500);
                        result.setFunctionError(e.getMessage());
                        return result;
                    }
                }

            @Override
            protected void onPostExecute(final InvokeResult invokeResult) {

                try {
                    final int statusCode = invokeResult.getStatusCode();
                    final String functionError = invokeResult.getFunctionError();
                    final String logResult = invokeResult.getLogResult();

                    if (statusCode != 200) {
                        showError(invokeResult.getFunctionError());
                    } else {
                        final ByteBuffer resultPayloadBuffer = invokeResult.getPayload();
                        final String resultPayload = DECODER.decode(resultPayloadBuffer).toString();
                        Toast.makeText(getActivity(),"Data is Stored to database",Toast.LENGTH_SHORT).show();
                       mFirstName.setText("");
                        mLogin.setText("");
                        mLastName.setText("");
                        mPassword.setText("");

                    }

                    if (functionError != null) {
                        Log.e("AWSLAMBDA", "AWS Lambda Function Error: " + functionError);
                    }

                    if (logResult != null) {
                        Log.d("AWSLAMBDA", "AWS Lambda Log Result: " + logResult);
                    }
                } catch (final Exception e) {
                    Log.e("AWSLAMBDA", "Unable to decode results. " + e.getMessage(), e);
                    showError(e.getMessage());
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            myTask.execute();
    }

    public void showError(final String errorMessage) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error AWS Backend Contact")
                .setMessage(errorMessage)
                .setNegativeButton("Dissmiss", null)
                .create().show();
    }
}
