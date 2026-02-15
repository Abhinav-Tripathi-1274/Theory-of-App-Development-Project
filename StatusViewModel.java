package com.ap.sutra;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// This class holds the AI status so the Fragment can see it
public class StatusViewModel extends ViewModel {

    // This is the "Message" box that holds the text
    private final MutableLiveData<String> status = new MutableLiveData<>("Checking usage...");

    // This method lets the Activity change the message
    public void setStatus(String text) {
        status.setValue(text);
    }

    // This method lets the Fragment "listen" to the message
    public MutableLiveData<String> getStatus() {
        return status;
    }
}