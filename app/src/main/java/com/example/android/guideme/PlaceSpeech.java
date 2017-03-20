package com.example.android.guideme;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import java.util.Locale;

/**
 * Created by Agile 2016 on 1/31/2017.
 */

public class PlaceSpeech extends TextToSpeech{
    public String origen;
    public String destiny;
    Locale localeSpanish = new Locale("es","","");
    public PlaceSpeech(Context context, OnInitListener listener) {
        super(context, listener);
        setLanguage(localeSpanish);
    }
    public void speakPlaceName(String placeName){
        speak(placeName,TextToSpeech.QUEUE_ADD,null);
    }

    public void Origen(String origen){
        this.origen = origen;
    }

    public String getOrigen(){
        return origen;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }
}
