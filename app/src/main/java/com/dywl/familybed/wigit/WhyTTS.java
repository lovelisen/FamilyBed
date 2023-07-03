package com.dywl.familybed.wigit;

public interface WhyTTS {
    void speak(final String content);
    void pause();
    void resume();
    void setSpeechRate(float newRate);
    void setSpeechPitch(float newPitch);
    void release();
    //WhyTTS getInstance();
}
