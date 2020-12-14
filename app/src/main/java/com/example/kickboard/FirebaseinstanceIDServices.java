package com.example.kickboard;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class FirebaseinstanceIDServices extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";



    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG,token);

        sendRegistrationToServer(token);

    }

    private void sendRegistrationToServer (String token){

    }
}