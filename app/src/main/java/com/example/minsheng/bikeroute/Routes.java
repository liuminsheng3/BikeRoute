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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Divya on 11/12/2016.
 */

public class Routes extends Fragment {

    private Button mInsert;
    private EditText mRouteName;
    private EditText mstartLat;
    private EditText mStartLong;
    private EditText mEndLat;
    private EditText mEndLong;
    private int Used;
    private String date;
    private String userId;
    private int endLat;
    private int endLong;
    private int startLat;
    private int StartLong;
    private String routeId;
    private IdentityManager identityManager;
    public static final String BIKER_DETAILS = "BikerDetails";
    private static String JSON_request ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.routes_fragment,container,false);

            mEndLat = (EditText)view.findViewById(R.id.endLat);
            mEndLong = (EditText)view.findViewById(R.id.endLong);
            mStartLong = (EditText)view.findViewById(R.id.startLong);
            mstartLat = (EditText)view.findViewById(R.id.startLat);
            mRouteName = (EditText)view.findViewById(R.id.routename);

        mInsert =(Button) view.findViewById(R.id.saveRoute);
        mInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = getActivity().getSharedPreferences(BIKER_DETAILS, Context.MODE_PRIVATE);

                AWSMobileClient.initializeMobileClientIfNecessary(getActivity());
                final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
                identityManager = awsMobileClient.getIdentityManager();

                Date currdate = new Date();
                date = currdate.toString();

                Used =0;
                endLat = Integer.parseInt(mEndLat.getText().toString());
                endLong = Integer.parseInt(mEndLong.getText().toString());
                startLat = Integer.parseInt(mstartLat.getText().toString());
                StartLong = Integer.parseInt(mStartLong.getText().toString());


                userId = settings.getString("userId",null);
                routeId = genereateRouteId(userId);

                if (userId != null)
                {
                    JSON_request =  " { \"userId\": \"" + userId + "\", " +
                            "\"Date\": \"" + date + "\",  " +
                            "\"Used\": \"" + Used + "\",  " +
                            " \"endLatitude\": \"" + endLat + "\" ," +
                            " \"endLongitude\": \"" + endLong + "\" ," +
                            " \"startLatitude\": \"" + startLat + "\" ," +
                            " \"startLongitude\": \"" + StartLong + "\" ," +
                            " \"routeID\": \"" + routeId + "\" ," +
                            " \"routeName\": \"" + mRouteName.getText().toString() +"\" }";
                    invokeFunction(JSON_request);
                }
                else
                    Toast.makeText(getActivity(),"Create UserId first",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void invokeFunction(String JSON_request) {

        final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
        final CharsetEncoder ENCODER = CHARSET_UTF8.newEncoder();
        final CharsetDecoder DECODER = CHARSET_UTF8.newDecoder();

        final String functionName = "insertRoutes";
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

                        mRouteName.setText("");
                        mEndLat.setText("");
                        mstartLat.setText("");
                        mStartLong.setText("");
                        mEndLong.setText("");

                        Toast.makeText(getActivity(),"Data is Stored to database",Toast.LENGTH_SHORT).show();

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

    public String genereateRouteId(String UserID)
    {
        int day;
        int month;
        int year;


        String ID;

       Calendar cal = Calendar.getInstance();

        day =cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        int index = (int)(100 * Math.random());

        ID = String.valueOf(index) + String.valueOf(day) + String.valueOf(month)+String.valueOf(year) ;


        return ID;
    }
}
