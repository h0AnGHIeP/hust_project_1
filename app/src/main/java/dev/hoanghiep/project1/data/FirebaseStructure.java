package dev.hoanghiep.project1.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class FirebaseStructure {
    private static final String TAG = FirebaseStructure.class.getSimpleName();
    
    public final static class USERS {
        public static final String THIS = "USERS";
        
        public static final class INFO {
            public static final String CREATED_DATE = "created";
            public static final String DISPLAY_NAME = "display";
            public static final String LIST_FRIEND = "friends";
            public static final String PHONE = "phone";
            
            public static final class FRIEND_INFO {
                public static final String ID = "id";
                public static final String DISPLAY_NAME = "name";
                
            }
        }
    }
    
    public final static class MESSAGE {
        public static final String THIS = "MESSAGES";
        
        public static final class CONVERSATION {
            public static final String CONTENT = "content";
            public static final String SENDER = "sender";
        }
    }
    
    private static final String AVATAR = "avatar";
    
    public interface After {
        void callback(Bitmap bitmap);
    }
    
    public static void loadBitmap(String bitmapRef, Activity activity, After after) {
        FirebaseStorage.getInstance().getReference().child(FirebaseStructure.AVATAR).child(bitmapRef)
                .getBytes(1024 * 1024).addOnSuccessListener(activity, bytes -> {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            after.callback(bitmap);
        });
    }
    
    public static void putBitmap(Bitmap image,
                                 Activity activity, String fileName) {
        StorageReference reference =
                FirebaseStorage.getInstance()
                        .getReference()
                        .child(FirebaseStructure.AVATAR)
                        .child(fileName + ".jpg");
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            reference.putBytes(outputStream.toByteArray()
            ).addOnSuccessListener(activity, taskSnapshot -> {
                Log.i(TAG, "putBitmap: succeed");
            }).addOnFailureListener(activity, taskSnapshot -> {
                Log.i(TAG, "putBitmap: failed");
            });
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
