package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class ListChatFragment extends Fragment {
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserReference;
    private FirebaseUser mCurrentUser;

    @BindView(R.id.chat_list_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.list_chat_toolbar)
    Toolbar mListChatToolBar;
    private FirebaseRecyclerAdapter<ChatFriend, ChatFriendHolder> mAdapter;

    private Unbinder mUnbinder;

    @Override
    public void onStart() {
        super.onStart();
        validateUser();
        mAdapter.startListening();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_friend, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                mCurrentUser = null;
                validateUser();
                return true;
            case R.id.item_setting:
                startActivity(SettingActivity.newIntent(getActivity()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mCurrentUser==null){
            validateUser();
            return null;
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        mUserReference = mDatabaseReference.child("USERS").child(mCurrentUser.getUid());
        Query query = mUserReference.child("friends").limitToLast(10);
        FirebaseRecyclerOptions<ChatFriend> options = new FirebaseRecyclerOptions.Builder<ChatFriend>()
                .setQuery(query, snapshot -> {
                    ChatFriend result = new ChatFriend();
                    result.setId(snapshot.getKey());
                    result.setConversationId((String) snapshot.child("id").getValue());
                    result.setName((String) snapshot.child("name").getValue());
                    return result;
                }).build();
        mAdapter = new FirebaseRecyclerAdapter<ChatFriend, ChatFriendHolder>(options) {
            @NonNull
            @Override
            public ChatFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View viewHolder = inflater.inflate(R.layout.list_chat_friend, parent, false);
                return new ChatFriendHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatFriendHolder holder, int position, @NonNull ChatFriend model) {
                holder.bind(model);
                holder.itemView.setOnClickListener(v -> {
                    startActivity(ChatActivity.newInstance(getActivity(),model.getConversationId()));
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        /*
        Set up toolbar
         */
        ((AppCompatActivity) getActivity()).setSupportActionBar(mListChatToolBar);

        setHasOptionsMenu(true);
        return view;
    }

    static class ChatFriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.friend_display_name)
        TextView mName;

        private Context mHostActivity;

        ChatFriendHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(ChatFriend chatFriend) {
            mName.setText(chatFriend.getName());
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
        mAdapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
