package dev.hoanghiep.project1.userInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;
import dev.hoanghiep.project1.data.FirebaseStructure;
import dev.hoanghiep.project1.data.User;

import static dev.hoanghiep.project1.data.FirebaseStructure.USERS;

public class ListChatLookUp extends Fragment {
   
   @BindView(R.id.recycler_view_look_up)
   RecyclerView mRecyclerLookUp;
   
   private FirebaseRecyclerAdapter mAdapter;
   private DatabaseReference mAllUserRef;
   private FirebaseUser mCurrentUser;
   private Unbinder mUnbind;
   
   @Override
   public void onStart() {
      super.onStart();
      mAdapter.startListening();
   }
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      super.onCreateView(inflater, container, savedInstanceState);
      View v = inflater.inflate(R.layout.fragment_look_up, container, false);
      mUnbind = ButterKnife.bind(this, v);
      mAllUserRef = FirebaseDatabase.getInstance().getReference().child(USERS.THIS);
      mRecyclerLookUp.setLayoutManager(new LinearLayoutManager(getActivity()));
      mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
      return v;
   }
   
   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
        /*
        Data query options
         */
      
      Query query = mAllUserRef.limitToFirst(20);
      FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, snapshot -> {
               User u = new User();
               u.setId(snapshot.getKey());
               u.setDisplayName((String) snapshot.child(USERS.INFO.DISPLAY_NAME).getValue());
               u.setPhone((String) snapshot.child(USERS.INFO.PHONE).getValue());
               return u;
            }).build();
      mAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(options) {
         @NonNull
         @Override
         public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_chat_friend, parent, false);
            return new UserHolder(view);
         }
         
         @Override
         protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
            holder.bind(model);
            holder.itemView.setOnClickListener(v -> {
               holder.mRequestIcon.setImageResource(R.drawable.ic_request);
               String conversationId = UUID.randomUUID().toString();
               DatabaseReference thisUser = mAllUserRef.child(mCurrentUser.getUid());
               DatabaseReference friend = mAllUserRef.child(model.getId());
               DatabaseReference initMessage = mAllUserRef.getRoot().child(FirebaseStructure.MESSAGE.THIS).push();
               initMessage.child(FirebaseStructure.MESSAGE.CONVERSATION.CONTENT).setValue("Hello");
               initMessage.child(FirebaseStructure.MESSAGE.CONVERSATION.SENDER).child(mCurrentUser.getUid());
                    /*
                    Set this user's name for friend's list friends alias
                     */
               thisUser.child(USERS.INFO.DISPLAY_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     String displayName = (String) dataSnapshot.getValue();
                     DatabaseReference reference = friend.child(USERS.INFO.LIST_FRIEND)
                           .child(mCurrentUser.getUid());
                     reference.child(USERS.INFO.FRIEND_INFO.DISPLAY_NAME).setValue(displayName);
                     reference.child(USERS.INFO.FRIEND_INFO.ID).setValue(conversationId);
                  }
                  
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                  }
               });
                    /*
                    Set friend's name alias
                     */
               friend.child(USERS.INFO.DISPLAY_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     String displayName = (String) dataSnapshot.getValue();
                     DatabaseReference reference = thisUser.child(USERS.INFO.LIST_FRIEND)
                           .child(model.getId());
                     reference.child(USERS.INFO.FRIEND_INFO.DISPLAY_NAME).setValue(displayName);
                     reference.child(USERS.INFO.FRIEND_INFO.ID).setValue(conversationId);
                  }
                  
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {
                  }
               });
            });
         }
      };
      mRecyclerLookUp.setAdapter(mAdapter);
   }
   
   static class UserHolder extends RecyclerView.ViewHolder {
      @BindView(R.id.friend_display_name)
      TextView mName;
      
      @BindView(R.id.icon_request)
      ImageView mRequestIcon;
      
      UserHolder(@NonNull View itemView) {
         super(itemView);
         ButterKnife.bind(this, itemView);
      }
      
      void bind(User user) {
         mName.setText(user.getDisplayName());
         mRequestIcon.setImageResource(R.drawable.ic_add_people);
      }
   }
   
   public static ListChatLookUp newInstance() {
      return new ListChatLookUp();
   }
   
   @Override
   public void onDestroyView() {
      super.onDestroyView();
      mUnbind.unbind();
   }
}
