package com.example.android.guideme;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import java.util.Locale;

/**
 * Created by Agile 2016 on 1/31/2017.
 */

public class PlaceSpeech extends TextToSpeech{
    Locale localeSpanish = new Locale("es","","");
    public PlaceSpeech(Context context, OnInitListener listener) {
        super(context, listener);
        setLanguage(localeSpanish);
    }
    public void speakPlaceName(String placeName){
        speak(placeName,TextToSpeech.QUEUE_ADD,null);
    }

}
