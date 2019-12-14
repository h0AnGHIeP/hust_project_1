package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import dev.hoanghiep.project1.MainActivity;

public class ChatActivity extends MainActivity {
    private static final String CLASS_NAME = ChatActivity.class.getCanonicalName();
    private static final String EXTRA_CONVERSATION_UID = CLASS_NAME + "extra_conversation_uid";
    private static final String EXTRA_FRIEND_UID = CLASS_NAME + "friend_uid";
    private static final String EXTRA_FRIEND_ALIAS = CLASS_NAME + "friend_alias";

    @Override
    public Fragment getFragment() {
        Bundle extras = getIntent().getExtras();
        return ChatFragment.newInstance(extras.getString(EXTRA_CONVERSATION_UID),
                extras.getString(EXTRA_FRIEND_UID),
                extras.getString(EXTRA_FRIEND_ALIAS));
    }

    public static Intent newIntent(Context packageContext, String mConversationId, String friendId, String alias) {
        return new Intent(packageContext, ChatActivity.class)
                .putExtra(EXTRA_CONVERSATION_UID, mConversationId)
                .putExtra(EXTRA_FRIEND_UID, friendId)
                .putExtra(EXTRA_FRIEND_ALIAS, alias);
    }

}
