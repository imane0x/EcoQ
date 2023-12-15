package com.euromedcompany.orderfood;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EcoChatActivity extends AppCompatActivity {

    private EditText queryEdt;
    private RecyclerView messageRV;
    private MessageAdapter messageRVAdapter;
    private ArrayList<MessageModel> messageList;
    private String url = "http://10.0.2.2:1010/generate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_chat); // assuming the layout file is named activity_ecochat.xml

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
                    messageList.add(new MessageModel(queryEdt.getText().toString(), "user"));
                    messageRVAdapter.notifyDataSetChanged();
                    getResponse(queryEdt.getText().toString());
                } else {
                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    private void getResponse(String query) {
        System.out.println(query);
        queryEdt.setText("");
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("question", "explain climate change in simple words. be concise");
            System.out.println(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        String responseMsg = response.getString("result");
                        messageList.add(new MessageModel(responseMsg, "bot"));
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
        };

        queue.add(postRequest);
    }
}
