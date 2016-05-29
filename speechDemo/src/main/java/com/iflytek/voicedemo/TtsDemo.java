package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.speech.util.ApkInstaller;
import com.iflytek.sunflower.FlowerCollector;

public class TtsDemo {
    private static String TAG = TtsDemo.class.getSimpleName();
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 语记安装助手类
    ApkInstaller mInstaller;
    private Toast mToast;
    String text2;

    @SuppressLint("ShowToast")
    public void speakVoice(Context context, String text) {
        text2 = text;
        // 初始化合成对象
        if (mTts == null) {
            mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        if (mInstaller == null) {
            mInstaller = new ApkInstaller(new AsrDemo());
        }
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            mInstaller.install();
        }

        FlowerCollector.onEvent(context, "tts_play");
        // 设置参数
        setParam();

        int code = mTts.startSpeaking(text2, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
                mInstaller.install();
            } else {
//                showTip("语音合成: " + code);
            }
        }
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int codeError) {
            Log.d(TAG, "InitListener init() code = " + codeError);
            if (codeError != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + codeError);
            } else {
//实测发现只有在第一次在外部调用说话方法时才会调用此处代码，第二次调用的是上面的speckvoice方法
                mTts.startSpeaking(text2, mTtsListener);
                Log.d("mttsinit","初始化语音合成监听器");
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

//上下分界线

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            Log.d("speechDemo","开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度

        }

        @Override
        public void onCompleted(SpeechError error) {
            Log.d("oncompleted","音频播放完成");
            if (error == null) {
//             语音播放完成后自动进入语音监听状态
AsrDemo.A.voiceRecognition();
                Log.d("oncompleted","开启语音识别方法");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
                Log.d("oncompleted","发生错误，无法开启语音识别方法");
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
//        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//            // 设置在线合成发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
//            //设置合成语速
//            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
//            //设置合成音调
//            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
//            //设置合成音量
//            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
//        } else {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
        mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        /**
         * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
         * 开发者如需自定义参数，请参考在线合成参数设置
         */
//        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    protected void stopSpeaking() {
        if (mTts != null) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }

    }


}
