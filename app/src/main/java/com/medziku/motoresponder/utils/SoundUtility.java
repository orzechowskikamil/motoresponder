package com.medziku.motoresponder.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;

public class SoundUtility {
    public int SAMPLE_RATE = 8000;
    private Context context;
    private int MIN_BUFFER_SIZE;

    public SoundUtility(Context context) {
        this.context = context;
       this.MIN_BUFFER_SIZE= this.getMinBufferSize(this.SAMPLE_RATE);

    }



    public Future<Float> getLoudnessOfEnvironment() {
        final SettableFuture<Float> loudnessDbPromise = SettableFuture.create();

        try {
            AudioRecord audio = this.createAudioRecord();
            audio.startRecording();

            double avgLevel = this.getAverageSoundLevel(audio);

            audio.stop();

            loudnessDbPromise.set((float)avgLevel);

        } catch (SoundLoudnessNotAvailableException e) {
            loudnessDbPromise.setException(e);
        }

        return loudnessDbPromise;
    }

    private int getMinBufferSize(int sampleRate) {
        return AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }


    private AudioRecord createAudioRecord() throws SoundLoudnessNotAvailableException {
        try {
            return new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    this.SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    this.MIN_BUFFER_SIZE
            );
        } catch (Exception e) {
            throw new SoundLoudnessNotAvailableException();
        }
    }

    private double getAverageSoundLevel(AudioRecord audio) throws SoundLoudnessNotAvailableException {
        short[] buffer = new short[this.MIN_BUFFER_SIZE];


        if (audio == null) {
            throw new SoundLoudnessNotAvailableException();
        }

        int bufferLength;
        bufferLength = audio.read(buffer, 0, this.MIN_BUFFER_SIZE);
        if (bufferLength == audio.ERROR_INVALID_OPERATION
                || bufferLength == audio.ERROR_BAD_VALUE
                || bufferLength == audio.ERROR) {
            throw new SoundLoudnessNotAvailableException();
        }

        double sumLevel = 0;
        for (int i = 0; i < bufferLength; i++) {
            sumLevel += buffer[i];
        }

        double averageLevel = Math.abs((sumLevel / bufferLength));

        return averageLevel;
    }
}

class SoundLoudnessNotAvailableException extends Throwable {
}