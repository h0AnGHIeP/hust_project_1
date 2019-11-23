package dev.hoanghiep.project1.musics;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;

import dev.hoanghiep.project1.data.MyMusic;

public class MusicThread extends HandlerThread {
    private static final int MESSAGE_START_PLAY_MUSIC = 999;
    private static final int MAX_PLAY = 3;

    private Context mContext;
    private Handler mMusicHandler;
    private AssetManager mAssetManager;
    private SoundPool mSoundPool;

    public MusicThread(String name, Context packageContext) {
        super(name);
        mContext = packageContext;
        mAssetManager = mContext.getAssets();
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_PLAY)
                .setAudioAttributes(attributes)
                .build();
        setUpMusicHandler();
    }

    private void setUpMusicHandler() {
        mMusicHandler = new Handler(msg -> {
            try {
                MyMusic startingMusic = ((MyMusic) (msg.obj));
                String path = startingMusic.getPath();
                AssetFileDescriptor fileDescriptor = mAssetManager.openFd(path);
                if (startingMusic.getId() == 0) {
                    startingMusic.setId(mSoundPool.load(fileDescriptor, 1));
                }
                play(startingMusic);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }

    private void play(MyMusic music) {
        int id = music.getId();
        if (id == 0) {
            return;
        }
        mSoundPool.play(id, 1F, 1F, 0, 1, 1F);
    }

    public void startMusic(MyMusic music) {
        mMusicHandler.obtainMessage(MESSAGE_START_PLAY_MUSIC, music).sendToTarget();
    }

}
