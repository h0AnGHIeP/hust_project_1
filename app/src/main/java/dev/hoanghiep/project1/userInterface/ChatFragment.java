package dev.hoanghiep.project1.userInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;
import dev.hoanghiep.project1.data.ChatMessage;
import dev.hoanghiep.project1.data.FirebaseStructure;

import static dev.hoanghiep.project1.data.FirebaseStructure.MESSAGE;

public class ChatFragment extends Fragment {
    private static final String className = ChatFragment.class.getCanonicalName();
    private static final int VIEW_TYPE_OWNER = 88;
    private static final int VIEW_TYPE_OTHER = 66;
    private static final String ARG_CONVERSATION_UID = className + "argument_conversation_id";
    private static final String ARG_FRIEND_UID = className + "argument_friend_uid";
    private static final String ARG_FRIEND_ALIAS = className + "argument_friend_alias";
    
    @BindView(R.id.chat_recycler_view)
    RecyclerView mChatRecyclerView;
    private MessageAdapter mMessageAdapter = new MessageAdapter();
    
    @BindView(R.id.chat_edit)
    EditText mChatEdit;
    
    @BindView(R.id.chat_send)
    ImageButton mSendButton;
    @BindView(R.id.other_image)
    ImageView mFriendImage;
    
    @BindView(R.id.display_name)
    TextView mFriendText;
    @BindView(R.id.alias_name)
    TextView mAliasText;
    
    private List<ChatMessage> mListMessages = new ArrayList<>();
    
    private Unbinder mUnbind;
    /*
    Refer to the conversation node
     */
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    
    /*
    Add message to the conversation
    */
    @OnClick(R.id.chat_send)
    void sendToServer() {
        if (mChatEdit.getText().toString().trim().length() > 0) {
            DatabaseReference newMsg = mDatabaseReference.push();
            newMsg.child(MESSAGE.CONVERSATION.CONTENT).setValue(mChatEdit.getText().toString());
            newMsg.child(MESSAGE.CONVERSATION.SENDER).setValue(mCurrentUser.getUid());
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            startActivity(LoginActivity.newIntent(getActivity()));
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        /*
        Validate whether user signed in or send them back to LoginActivity
         */
        String mFriendId;
        Bundle args = getArguments();
        String conversationId = args.getString(ARG_CONVERSATION_UID);
        mFriendId = args.getString(ARG_FRIEND_UID);
        String aliasName = args.getString(ARG_FRIEND_ALIAS);
        
        FirebaseStructure.loadBitmap("dog.jpg", getActivity(), bitmap -> {
            mFriendImage.setImageBitmap(bitmap);
        });
        mUnbind = ButterKnife.bind(this, view);
        /*
        Get current user information
         */
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        /*
        Get MESSAGES reference of the database and set user's database references after that.
         */
        DatabaseReference root = FirebaseDatabase
                .getInstance()
                .getReference()
                .getRoot();
        mDatabaseReference = root
                .child(MESSAGE.THIS)
                .child(conversationId);
        DatabaseReference friendRef = root.child(FirebaseStructure.USERS.THIS)
                .child(mFriendId);
        /*
        Set up Recycler View
         */
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL,
                false));
        mChatRecyclerView.setAdapter(mMessageAdapter);
        /*
        Set friend's name
         */
        friendRef.child(FirebaseStructure.USERS.INFO.DISPLAY_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFriendText.setText((CharSequence) dataSnapshot.getValue());
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mFriendText.setText(databaseError.getMessage());
            }
        });
        mAliasText.setText(aliasName);
        mDatabaseReference.orderByKey()
                .limitToFirst(50)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ChatMessage chat = new ChatMessage();
                        chat.setContent((String) dataSnapshot.child(MESSAGE.CONVERSATION.CONTENT).getValue());
                        chat.setSenderId((String) dataSnapshot.child(MESSAGE.CONVERSATION.SENDER).getValue());
                        chat.who(mCurrentUser.getUid());
                        mListMessages.add(chat);
                        mMessageAdapter.notifyItemInserted(mListMessages.size() - 1);
                        mChatRecyclerView.scrollToPosition(mListMessages.size() - 1);
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
        return view;
    }
    
    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
        @Override
        public int getItemViewType(int position) {
            return mListMessages.get(position).isSender() ? VIEW_TYPE_OWNER : VIEW_TYPE_OTHER;
        }
        
        @NonNull
        @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (viewType == VIEW_TYPE_OWNER) {
                item = inflater.inflate(R.layout.list_chat_item_owner, parent, false);
            } else {
                item = inflater.inflate(R.layout.list_chat_item_other, parent, false);
            }
            
            return new MessageHolder(item);
        }
        
        @Override
        public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
            holder.bind(mListMessages.get(position));
        }
        
        @Override
        public int getItemCount() {
            return mListMessages.size();
        }
    }
    
    static class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_item_content)
        TextView mContent;
        
        MessageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        
        void bind(ChatMessage message) {
            mContent.setText(message.getContent());
        }
    }
    
    public static ChatFragment newInstance(String conversationId,
                                           @NonNull String friendId,
                                           @NonNull String friendAlias) {
        ChatFragment newIns = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRIEND_UID, friendId);
        args.putString(ARG_CONVERSATION_UID, conversationId);
        args.putString(ARG_FRIEND_ALIAS, friendAlias);
        newIns.setArguments(args);
        return newIns;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbind.unbind();
    }
    
}
