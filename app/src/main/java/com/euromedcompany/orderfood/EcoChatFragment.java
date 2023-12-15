package com.euromedcompany.orderfood;

// import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//public class EcoChatFragment extends Fragment {
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_ecochat, container, false);
//    }
//}


/*
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EcoChatFragment extends Fragment {

    private EditText queryEdt;
    private RecyclerView messageRV;
    private MessageAdapter messageRVAdapter;
    private ArrayList<MessageModel> messageList;
    // private String url = "https://api.openai.com/v1/completions";
    private String url = "http://10.0.2.2:1010/generate";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ecochat, container, false);
        queryEdt = rootView.findViewById(R.id.idEdtQuery);
        messageRV = rootView.findViewById(R.id.idRVMessages);
        messageList = new ArrayList<>();
        messageRVAdapter = new MessageAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        messageRV.setLayoutManager(layoutManager);
        messageRV.setAdapter(messageRVAdapter);

        queryEdt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                if (queryEdt.getText().toString().length() > 0) {
                    messageList.add(new MessageModel(queryEdt.getText().toString(), "user"));
                    messageRVAdapter.notifyDataSetChanged();
                    getResponse(queryEdt.getText().toString());
                } else {
                    Toast.makeText(requireContext(), "Please enter your query..", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        return rootView;
    }

    private void getResponse(String query) {
        System.out.println(query);
        queryEdt.setText("");
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("question", "explain climate change in simple words. be concise");
            System.out.println(jsonObject);
            //jsonObject.put("model", "text-davinci-003");
            //jsonObject.put("prompt", query);
            //jsonObject.put("temperature", 0);
            //jsonObject.put("max_tokens", 100);
            //jsonObject.put("top_p", 1);
            //jsonObject.put("frequency_penalty", 0.0);
            //jsonObject.put("presence_penalty", 0.0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        //String responseMsg = response.getJSONArray("choices")
                        //        .getJSONObject(0)
                        //        .getString("text");
                        String responseMsg = response.getString("result");
                        messageList.add(new MessageModel(responseMsg, "bot"));
                        messageRVAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(requireContext(), "Fail to get response..", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                //params.put("Authorization", "Bearer add your API key");
                return params;
            }
        };

        //postRequest.setRetryPolicy(new DefaultRetryPolicy(
        //       50000,
        //        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        //        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);
    }
}
*/
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

public class EcoChatFragment extends Fragment {

    // ... (existing code)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ecochat, container, false);

        // ... (existing code)

        // Automatically navigate to EcoChatActivity
        navigateToEcoChatActivity();

        return rootView;
    }

    private void navigateToEcoChatActivity() {
        // Create an Intent to start EcoChatActivity
        Intent intent = new Intent(requireContext(), EcoChatActivity.class);

        // You can add extra data to the intent if needed
        // intent.putExtra("key", "value");

        // Start the activity
        startActivity(intent);

        // Optionally, you can finish the current activity if you want to close it after navigation
        // getActivity().finish();
    }

    // ... (existing code)
}
