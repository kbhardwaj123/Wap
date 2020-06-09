package com.example.wap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.wap.Chat.MediaAdapter;
import com.example.wap.Chat.MessageAdapter;
import com.example.wap.Chat.MessageObject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mChat, mMedia;
    private RecyclerView.Adapter mChatAdapter, mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager, mMediaLayoutManager;

    ArrayList<MessageObject> messageList;
    String chatID;

    DatabaseReference mChatDB;
    EditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");
        mChatDB = FirebaseDatabase.getInstance().getReference().child("chat")
                .child(chatID);

        Button mSend = findViewById(R.id.send);
        Button mAddMedia = findViewById(R.id.addMedia);
        mSend.setOnClickListener(view -> {
            sendMessage();
        });
        mAddMedia.setOnClickListener(view -> {
            openGallery();
        });
        initMessage();
        initMedia();
        getChatMessages();
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaURIList = new ArrayList<>();
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT); // caution
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_INTENT) {
                if(data.getClipData() == null) {
                    mediaURIList.add(data.getData().toString());
                } else {
                    for(int i=0;i<data.getClipData().getItemCount();i++) {
                        mediaURIList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getChatMessages() {
        mChatDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    String text = "",creatorId="";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if(dataSnapshot.child("text").getValue()!=null) {
                        text = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("creator").getValue()!=null) {
                        creatorId = dataSnapshot.child("creator").getValue().toString();
                    }
                    if(dataSnapshot.child("media").getChildrenCount()>0) {
                        for(DataSnapshot mediaSnapshot: dataSnapshot.child("media").getChildren())
                            mediaUrlList.add(mediaSnapshot.getValue().toString());
                    }
                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(),creatorId,text,mediaUrlList);
                    messageList.add(mMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int mediauploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    private void sendMessage() {
        mMessage = findViewById(R.id.messageDialogue);
            String messageId = mChatDB.push().getKey();
            DatabaseReference newMessageDB = mChatDB.child(messageId);
            final Map newMessageMap = new HashMap<>();
            if(!mMessage.getText().toString().isEmpty()) {
                newMessageMap.put("text",mMessage.getText().toString());
            }
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            if(!mediaURIList.isEmpty()) {
                for(String mediaURI: mediaURIList) {
                    String mediaId = newMessageDB.child("media").push().getKey();
                    mediaIdList.add(mediaId);
                    final StorageReference filepath = FirebaseStorage.getInstance().getReference()
                            .child("chat").child(chatID).child(messageId).child(mediaId);
                    UploadTask uploadTask = filepath.putFile(Uri.parse(mediaURI));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + mediaIdList.get(mediauploaded) + "/",uri.toString());
                                    mediauploaded++;
                                    if(mediauploaded == mediaURIList.size()) {
                                        updateDatabaseWithNewMessage(newMessageDB,newMessageMap);
                                    }
                                }
                            });
                        }
                    });
                }
            } else {
                if(!mMessage.getText().toString().isEmpty()) {
                    updateDatabaseWithNewMessage(newMessageDB,newMessageMap);
                }
            }

    }

    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDB,Map newMessage) {
        newMessageDB.updateChildren(newMessage);
        mMessage.setText(null);
        mediaURIList.clear();
        mediaIdList.clear();
        mMediaAdapter.notifyDataSetChanged();
    }

    private void initMessage() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                RecyclerView.VERTICAL,
                false
        );
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }

    private void initMedia() {
        mediaURIList = new ArrayList<>();
        mMedia = findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                RecyclerView.HORIZONTAL,
                false
        );
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaURIList);
        mMedia.setAdapter(mMediaAdapter);
    }
}
