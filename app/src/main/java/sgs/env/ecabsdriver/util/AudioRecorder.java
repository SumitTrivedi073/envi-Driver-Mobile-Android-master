package sgs.env.ecabsdriver.util;

import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;


    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }


    public void start(String filePath) throws IOException {
        if (mediaRecorder == null) {
            initMediaRecorder();
        }

        mediaRecorder.setOutputFile(filePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void stop() {
        try {
            if (mediaRecorder!=null) {
                mediaRecorder.stop();
                destroyMediaRecorder();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void destroyMediaRecorder() {
        mediaRecorder.release();
        mediaRecorder = null;
    }

}
