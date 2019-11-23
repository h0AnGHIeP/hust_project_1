package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import dev.hoanghiep.project1.MainActivity;

public class ChatActivity extends MainActivity {
    private static final String EXTRA_CONVERSATION_UID = "friend_uid";

    @Override
    public Fragment getFragment() {
        return ChatFragment.newInstance(getIntent()
                .getExtras()
                .getString(EXTRA_CONVERSATION_UID));
    }

    public static Intent newInstance(Context packageContext, String mConversationId) {
        return new Intent(packageContext, ChatActivity.class).putExtra(EXTRA_CONVERSATION_UID, mConversationId);
    }

}
