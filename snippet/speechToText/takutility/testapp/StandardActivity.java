package takutility.testapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

/**
 * Created by Tak on 24/04/2016.
 */

public class StandardActivity extends Activity implements OnClickListener
{
    private static final String TAG = "StandardActivity";
    protected static final int REQUEST_OK = 1;

    private SpeechRecognizer sr = null;
    private Intent recognizerIntent = null;

    private TextView mText = null;
    private TextView mStatusText = null;
    private boolean mIsListening = false;
    private boolean mIsWorking = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.standard_speech);
        mText = (TextView) findViewById(R.id.text1);
        mStatusText = (TextView) findViewById(R.id.statusText);

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        //unavailable in API21 recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, Boolean.TRUE);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        findViewById(R.id.button1).setOnClickListener(this);

        mIsListening = false;
    }

    @Override
    protected void onDestroy()
    {
        if(sr != null)
        {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        mIsWorking = false;
        if(mIsListening)
            sr.stopListening();

        super.onPause();
    }

    @Override
    protected void onResume()
    {
        if(mIsListening && !mIsWorking)
            sr.startListening(recognizerIntent);

        super.onResume();
    }

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
            mStatusText.setText("onReadyForSpeech");
            mIsWorking = true;
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
            mStatusText.setText("onBeginningOfSpeech");
            mIsWorking = true;
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged: " + Float.toString(rmsdB));
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
            mStatusText.setText("onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
            mStatusText.setText("onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);

            String errorStr = "Unknown error";
            switch (error)
            {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorStr = "ERROR_AUDIO";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorStr = "ERROR_CLIENT";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorStr = "ERROR_INSUFFICIENT_PERMISSIONS";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorStr = "ERROR_NETWORK";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorStr = "ERROR_NETWORK_TIMEOUT";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorStr = "ERROR_NO_MATCH";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorStr = "ERROR_RECOGNIZER_BUSY";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorStr = "ERROR_SERVER";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorStr = "ERROR_SPEECH_TIMEOUT";
                    break;
            }

            mStatusText.setText(errorStr);

            mIsWorking = false;
            if(mIsListening)
                sr.startListening(recognizerIntent);
        }

        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults");

            ArrayList thingsYouSaid = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if( thingsYouSaid.size() > 0)
            {
                Log.d(TAG, " thingsYouSaid.size=" +  thingsYouSaid.size());

                String outValue = "";

                for (int i = 0; i < thingsYouSaid.size(); i++)
                    outValue += thingsYouSaid.get(i) + "\n";

                mText.setText(outValue);
            }

            mIsWorking = false;
            if(mIsListening)
                sr.startListening(recognizerIntent);
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
            mStatusText.setText("onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }
    public void onClick(View v)
    {
        if (v.getId() == R.id.button1)
        {
            if( mIsWorking == false)
            {
                if(mIsListening == false)
                    sr.startListening(recognizerIntent);
                else
                    sr.stopListening();

                mIsListening = !mIsListening;
            }
            else
                mStatusText.setText("start/stop ignored because engine is still working");
        }
    }
}
