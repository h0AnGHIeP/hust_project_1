package dev.hoanghiep.project1.userInterface;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;
import dev.hoanghiep.project1.data.ChatFriend;
import dev.hoanghiep.project1.data.FirebaseStructure;

import static dev.hoanghiep.project1.data.FirebaseStructure.USERS;

public class ListChatFragment extends Fragment {
    private static final String TAG = "ListChatFragment";
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserReference;
    private FirebaseUser mCurrentUser;

    @BindView(R.id.chat_list_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.chat_list_loading)
    ProgressBar mProgressBar;


    private FirebaseRecyclerAdapter<ChatFriend, ChatFriendHolder> mAdapter;

    private Unbinder mUnbind;

    @Override
    public void onStart() {
        super.onStart();
        validateUser();
        if (mAdapter != null) mAdapter.startListening();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        mUnbind = ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null) {
            validateUser();
            return null;
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        mUserReference = mDatabaseReference.child(USERS.THIS).child(mCurrentUser.getUid());
        Query query = mUserReference.child(USERS.INFO.LIST_FRIEND).limitToLast(10);
        FirebaseRecyclerOptions<ChatFriend> options = new FirebaseRecyclerOptions.Builder<ChatFriend>()
                .setQuery(query, snapshot -> {
                    ChatFriend result = new ChatFriend();
                    result.setId(snapshot.getKey());
                    result.setConversationId((String) snapshot.child(USERS.INFO.FRIEND_INFO.ID).getValue());

                    result.setName((String) snapshot.child(USERS.INFO.FRIEND_INFO.DISPLAY_NAME).getValue());
                    Log.i(TAG, "onCreateView: "+result.getName());
                    return result;
                }).build();

        mAdapter = new FirebaseRecyclerAdapter<ChatFriend, ChatFriendHolder>(options) {

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public ChatFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View viewHolder = inflater.inflate(R.layout.list_chat_friend, parent, false);
                return new ChatFriendHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatFriendHolder holder, int position, @NonNull ChatFriend model) {
                FirebaseStructure.loadBitmap("dog.jpg", getActivity(), bitmap -> {
                    holder.bind(model, bitmap);
                });
                holder.itemView.setOnClickListener(v -> {
                    Log.i(TAG, "onBindViewHolder: clicked");
                    startActivity(ChatActivity.newIntent(getActivity(),
                            model.getConversationId(),
                            model.getId(),
                            model.getName()));
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    static class ChatFriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_display_name)
        TextView mName;

        @BindView(R.id.friend_image)
        ImageView mFriendImg;

        @BindView(R.id.icon_request)
        ImageView mRequestIcon;
        Bitmap mAvatar;

        ChatFriendHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(ChatFriend chatFriend, Bitmap avatar) {
            mRequestIcon.setVisibility(View.GONE);
            mName.setText(chatFriend.getName());
            mFriendImg.setImageBitmap(avatar);
            mAvatar = avatar;
        }
    }


    public static Fragment newInstance() {
        return new ListChatFragment();
    }

    /*
    Validate whether user has signed in or sending them back to Login Activity
     */
    private void validateUser() {
        if (mCurrentUser == null) {
            startActivity(LoginActivity.newIntent(getActivity()));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) mAdapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbind.unbind();
    }
}
