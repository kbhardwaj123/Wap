package com.example.wap.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wap.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends
        RecyclerView.Adapter<MessageAdapter.MessageListViewHolder> {
    ArrayList<MessageObject> messageList;

    public MessageAdapter(ArrayList<MessageObject> MessageList) {
        this.messageList = MessageList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_message,
                null,
                false
        );
        // recycler view has a tendency to take more than wrap content, this fixes that
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutView.setLayoutParams(lp);

        MessageAdapter.MessageListViewHolder rcv = new MessageAdapter.MessageListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageListViewHolder holder, int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderId());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class MessageListViewHolder extends RecyclerView.ViewHolder {

        public TextView mMessage,mSender;
        LinearLayout mLayout;

        public MessageListViewHolder(View view) {
            super(view);
            mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);
            mLayout = view.findViewById(R.id.layout);
        }
    }
}
