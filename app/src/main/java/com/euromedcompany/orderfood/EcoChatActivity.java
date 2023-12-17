package com.euromedcompany.orderfood;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordPermissionHandler;
import com.devlomi.record_view.RecordView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EcoChatActivity extends AppCompatActivity {

    private EditText queryEdt;
    private RecyclerView messageRV;
    private MessageAdapter messageRVAdapter;
    private ArrayList<MessageModel> messageList;
    private final String url = "http://10.0.2.2:1010/generate"; // Your server URL
    private RequestQueue queue;
    private DatabaseReference databaseReference;
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_chat);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        queue = Volley.newRequestQueue(this);
        queryEdt = findViewById(R.id.idEdtQuery);
        messageRV = findViewById(R.id.idRVMessages);
        messageList = new ArrayList<>();
        messageRVAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageRV.setLayoutManager(layoutManager);
        messageRV.setAdapter(messageRVAdapter);

        queryEdt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (queryEdt.getText().toString().length() > 0) {
                    // Store user message in Firebase
                    // storeMessage(queryEdt.getText().toString(), "user");
                    Log.d("text", queryEdt.getText().toString());
                    messageList.add(new MessageModel(queryEdt.getText().toString(), "user", "text"));
                    messageRVAdapter.notifyDataSetChanged();
                    getResponse(queryEdt.getText().toString());
                } else {
                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        // Initialize audio recording components
        RecordView recordView = findViewById(R.id.record_view);
        RecordButton recordButton = findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
               Log.d("RecordView", "onStart");
                startRecording();
            }

            @Override
            public void onCancel() {
               Log.d("RecordView", "onCancel");
              cancelRecording();
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
//                Log.d("RecordView", "onFinish");
               stopRecording();
//
//                // Upload audio to Firebase
               String time = getHumanTimeText(recordTime);
               uploadAudioToFirebase("audio", time);
//                Log.d("RecordTime", time);
                String path = getRecordingFilePath();
                messageList.add(new MessageModel(path, "user", "audio"));
                messageRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLessThanSecond() {
//                Log.d("RecordView", "onLessThanSecond");
//                cancelRecording();
            }

            @Override
            public void onLock() {
//                Log.d("RecordView", "onLock");
            }
        });

        recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                return checkAndRequestAudioPermission();
            }
        });
    }

    private boolean checkAndRequestAudioPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "Permission Available", Toast.LENGTH_SHORT).show();
            return true;
        }

        boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
        if (recordPermissionAvailable) {
            return true;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        return false;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(getRecordingFilePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void cancelRecording() {
        stopRecording();
    }

    private void getResponse(String query) {
        System.out.println(query);
        queryEdt.setText("");
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        try {
            // "explain water scarcity in simple words. be concise"
            jsonObject.put("question", query);
            System.out.println(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        System.out.println(response);
                        String responseMsg = response.getString("result");
                        messageList.add(new MessageModel(responseMsg, "bot", "text"));
                        messageRVAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Fail to get response..", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                // Set timeout to zero for no timeout (wait indefinitely)
                return new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                );
            }
        };

        queue.add(postRequest);
    }

//    private void storeMessage(String message, String sender) {
//        // Create a unique key for the message
//        String messageId = databaseReference.push().getKey();
//
//        // Create a MessageModel object
//        MessageModel messageModel = new MessageModel(message, sender);
//
//        // Store message in Firebase
//       // databaseReference.child(messageId).setValue(messageModel);
//    }

    private String getHumanTimeText(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private String getRecordingFilePath() {
        String downloadsDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(downloadsDirPath, "audio_file"+".wav");
        return file.getPath();
    }

    private void uploadAudioToFirebase(String audioName, String duration) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the audio file in Firebase Storage
        StorageReference audioRef = storageRef.child("audio/" + audioName + ".wav");

        // Get the local file URI
        Uri file = Uri.fromFile(new File(getRecordingFilePath()));

        // Upload file to Firebase Storage
        UploadTask uploadTask = audioRef.putFile(file);

        // Register observers to listen for when the upload is successful or if it fails
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Audio upload successful

            // Get the download URL of the uploaded file
            audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Store additional information in Firebase Realtime Database
                saveAudioInfoToDatabase(audioName, duration, uri.toString());

                Toast.makeText(EcoChatActivity.this, "Audio uploaded successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
                Toast.makeText(EcoChatActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(EcoChatActivity.this, "Failed to upload audio", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveAudioInfoToDatabase(String audioName, String duration, String storagePath) {
        // Assuming you have a Firebase Realtime Database reference
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("audio_info");

        // Create a unique key for the audio entry
        String key = databaseRef.push().getKey();

        // Create a data structure to store audio information
        AudioInfo audioInfo = new AudioInfo(audioName, duration, storagePath);

        // Store the information in the database
        databaseRef.child(key).setValue(audioInfo);
    }
}






































//package com.euromedcompany.orderfood;
//////////////////////////////////////////////////////////////////////////////
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.euromedcompany.orderfood.MessageModel;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class EcoChatActivity extends AppCompatActivity {
//
//    private EditText queryEdt;
//    private RecyclerView messageRV;
//    private MessageAdapter messageRVAdapter;
//    private ArrayList<MessageModel> messageList;
//    private String url = "http://10.0.2.2:1010/generate"; // Your server URL
//    private RequestQueue queue;
//    private DatabaseReference databaseReference;
//    JSONObject jsonObject = new JSONObject();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_eco_chat);
//
//        // Initialize Firebase
//        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
//        queue = Volley.newRequestQueue(this);
//        queryEdt = findViewById(R.id.idEdtQuery);
//        messageRV = findViewById(R.id.idRVMessages);
//        messageList = new ArrayList<>();
//        messageRVAdapter = new MessageAdapter(messageList);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        messageRV.setLayoutManager(layoutManager);
//        messageRV.setAdapter(messageRVAdapter);
//
//        queryEdt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                if (queryEdt.getText().toString().length() > 0) {
//                    // Store user message in Firebase
//                    storeMessage(queryEdt.getText().toString(), "user");
//
//                    messageList.add(new MessageModel(queryEdt.getText().toString(), "user"));
//                    messageRVAdapter.notifyDataSetChanged();
//                    getResponse(queryEdt.getText().toString());
//                } else {
//                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//            return false;
//        });
//    }
//
//    private void getResponse(String query) {
//        // ...
//
//        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//                response -> {
//                    try {
//                        String responseMsg = response.getString("result");
//
//                        // Store bot message in Firebase
//                        storeMessage(responseMsg, "bot");
//
//                        messageList.add(new MessageModel(responseMsg, "bot"));
//                        messageRVAdapter.notifyDataSetChanged();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                },
//                error -> Toast.makeText(this, "Fail to get response..", Toast.LENGTH_SHORT).show()) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
//                return params;
//            }
//        };
//
//        queue.add(postRequest);
//    }
//
//    private void storeMessage(String message, String sender) {
//        // Create a unique key for the message
//        String messageId = databaseReference.push().getKey();
//
//        // Create a MessageModel object
//        MessageModel messageModel = new MessageModel(message, sender);
//
//        // Store message in Firebase
//        databaseReference.child(messageId).setValue(messageModel);
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////package com.euromedcompany.orderfood;
////import android.os.Bundle;
////import android.view.KeyEvent;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.view.inputmethod.EditorInfo;
////import android.widget.EditText;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.android.volley.Request;
////import com.android.volley.RequestQueue;
////import com.android.volley.toolbox.JsonObjectRequest;
////import com.android.volley.toolbox.Volley;
////
////import org.json.JSONException;
////import org.json.JSONObject;
////
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.Map;
////
////public class EcoChatActivity extends AppCompatActivity {
////
////    private EditText queryEdt;
////    private RecyclerView messageRV;
////    private MessageAdapter messageRVAdapter;
////    private ArrayList<MessageModel> messageList;
////    private String url = "http://10.0.2.2:1010/generate";
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_eco_chat); // assuming the layout file is named activity_ecochat.xml
////
////        queryEdt = findViewById(R.id.idEdtQuery);
////        messageRV = findViewById(R.id.idRVMessages);
////        messageList = new ArrayList<>();
////        messageRVAdapter = new MessageAdapter(messageList);
////
////        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
////        messageRV.setLayoutManager(layoutManager);
////        messageRV.setAdapter(messageRVAdapter);
////
////        queryEdt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
////            if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
////                if (queryEdt.getText().toString().length() > 0) {
////                    messageList.add(new MessageModel(queryEdt.getText().toString(), "user"));
////                    messageRVAdapter.notifyDataSetChanged();
////                    getResponse(queryEdt.getText().toString());
////                } else {
////                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
////                }
////                return true;
////            }
////            return false;
////        });
////    }
////
////    private void getResponse(String query) {
////        System.out.println(query);
////        queryEdt.setText("");
////        RequestQueue queue = Volley.newRequestQueue(this);
////        JSONObject jsonObject = new JSONObject();
////
////        try {
////            jsonObject.put("question", "explain climate change in simple words. be concise");
////            System.out.println(jsonObject);
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////
////        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
////                response -> {
////                    try {
////                        String responseMsg = response.getString("result");
////                        messageList.add(new MessageModel(responseMsg, "bot"));
////                        messageRVAdapter.notifyDataSetChanged();
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                },
////                error -> Toast.makeText(this, "Fail to get response..", Toast.LENGTH_SHORT).show()) {
////            @Override
////            public Map<String, String> getHeaders() {
////                Map<String, String> params = new HashMap<>();
////                params.put("Content-Type", "application/json");
////                return params;
////            }
////        };
////
////        queue.add(postRequest);
////    }
////}
