package com.sciencesquad.health.core.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSManager {
    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTts.setLanguage(Locale.US);
                        isLoaded = true;

                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("error", "This Language is not supported");
                        }
                    } else {
                        Log.e("error", "Initialization Failed!");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addQueue(String text) {
        if (isLoaded) {
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            Log.e("error", "TTS Not Initialized");
        }
    }

    public void initQueue(String text) {
        if (isLoaded) {
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Log.e("error", "TTS Not Initialized");
        }
    }
}