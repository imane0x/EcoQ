package com.euromedcompany.orderfood;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<MessageModel> messageList;

    public MessageAdapter(ArrayList<MessageModel> messageList) {
        this.messageList = messageList;
    }

    public class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMsgTV;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            userMsgTV = itemView.findViewById(R.id.idTVUser);
        }
    }

    public class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView botMsgTV;

        public BotMessageViewHolder(View itemView) {
            super(itemView);
            botMsgTV = itemView.findViewById(R.id.idTVBot);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_message_item, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_message_item, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String sender = messageList.get(position).getSender();

        switch (sender) {
            case "user":
                ((UserMessageViewHolder) holder).userMsgTV.setText(messageList.get(position).getMessage());
                break;
            case "bot":
                ((BotMessageViewHolder) holder).botMsgTV.setText(messageList.get(position).getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String sender = messageList.get(position).getSender();

        switch (sender) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return 1;
        }
    }
}
