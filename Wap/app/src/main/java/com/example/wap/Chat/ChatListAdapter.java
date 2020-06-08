package com.example.wap.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wap.ChatActivity;
import com.example.wap.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends
        RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
    ArrayList<ChatObject> chatList;

    public ChatListAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_chat,
                null,
                false
        );
        // recycler view has a tendency to take more than wrap content, this fixes that
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutView.setLayoutParams(lp);

        ChatListAdapter.ChatListViewHolder rcv = new ChatListAdapter.ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ChatListViewHolder holder, int position) {
        holder.mTitle.setText(chatList.get(position).getChatId());
        holder.mLayout.setOnClickListener((view -> {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("chatID",chatList.get(holder.getAdapterPosition()).getChatId());
            intent.putExtras(bundle);
            view.getContext().startActivity(intent);
        }));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitle;
        public LinearLayout mLayout;

        public ChatListViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.title);
            mLayout = view.findViewById(R.id.layoutTitle);
        }
    }
}
