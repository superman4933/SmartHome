package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.speech.util.ApkInstaller;
import com.iflytek.speech.util.FucUtil;
import com.iflytek.speech.util.JsonParser;
import com.iflytek.sunflower.FlowerCollector;

import java.util.Timer;
import java.util.TimerTask;

public class AsrDemo extends Activity implements OnClickListener {
    TtsDemo ttsDemo;
    private static String TAG = AsrDemo.class.getSimpleName();
    // 语音识别对象
    private SpeechRecognizer mAsr;
    private Toast mToast;
    // 缓存
    private SharedPreferences mSharedPreferences;
    // 本地语法文件
    private String mLocalGrammar = null;
    private static final String KEY_GRAMMAR_ABNF_ID = "grammar_abnf_id";
    private static final String GRAMMAR_TYPE_BNF = "bnf";
    private String mEngineType = null;
    // 语记安装助手类
    ApkInstaller mInstaller;
    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.isrdemo);
        ttsDemo = new TtsDemo();
        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(AsrDemo.this, mInitListener);
        // 初始化语法、命令词
        mLocalGrammar = FucUtil.readFile(this, "call.bnf", "utf-8");
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mInstaller = new ApkInstaller(AsrDemo.this);
        init();
        timer.schedule(timerTask, 1000);
    }
//延迟一秒钟启动本地语法监听器，如果不延迟则会出错，原因未知
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            buildGra();
        }
    };
    Timer timer = new Timer();


    private void init() {
        findViewById(R.id.isr_recognize).setOnClickListener(AsrDemo.this);
        mEngineType = SpeechConstant.TYPE_LOCAL;
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            mInstaller.install();
        }
        Log.d("speech", "init成功");
    }

    // 语法、词典临时变量
    String mContent;
    // 函数调用返回值
    int ret = 0;

    private void buildGra() {
        showTip("上传预设关键词/语法文件");
        Log.d("speech", "开始构建语法");
        if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
            mContent = new String(mLocalGrammar);
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            //指定引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            Log.d("speech", "开始构建语法2");
            ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mContent, mLocalGrammarListener);
            if (mLocalGrammarListener == null) {
                Log.d("speech", "mLocalGrammarListener为空");
            }
            Log.d("speech", "开始构建语法3");
            if (ret != ErrorCode.SUCCESS) {
                if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                    //未安装则跳转到提示安装页面
                    mInstaller.install();
                } else {
                    showTip("语法构建失败1,错误码：" + ret);
                }
            }
        }
        Log.d("speech", "开始构建语法4");
    }

    @Override
    public void onClick(View view) {
        if (null == mEngineType) {
            showTip("请先选择识别引擎类型");
            return;
        }
        switch (view.getId()) {
            // 开始识别
            case R.id.isr_recognize:
                // 设置参数
                if (!setParam()) {
                    showTip("请先构建语法。");
                    return;
                }
                ret = mAsr.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                        //未安装则跳转到提示安装页面
                        mInstaller.install();
                    } else {
                        showTip("识别失败,错误码: " + ret);
                    }
                }
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 本地构建语法监听器。
     */
    private GrammarListener mLocalGrammarListener = new GrammarListener() {
        @Override

        public void onBuildFinish(String grammarId, SpeechError error) {
            Log.d("speech", "初始化本地语法监听器");
            if (error == null) {
                showTip("语法构建成功：" + grammarId);
            } else {
                showTip("语法构建失败2,错误码：" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
//            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                String text;
                if ("cloud".equalsIgnoreCase(mEngineType)) {
//                    如果是网络语音识别则调用下面的解析方法
                    text = JsonParser.parseGrammarResult(result.getResultString());

                } else {
//                    本地识别的解析方法
                    text = JsonParser.parseLocalGrammarResult(result.getResultString());
                }

                // 显示
                showTip("识别成功");
                if (text.contains("open") && text.contains("airCondition")) {
                    ttsDemo.speakVoice(AsrDemo.this, "打开空调");
                } else {
                    ttsDemo.speakVoice(AsrDemo.this, "好哒");
                }

                ((EditText) findViewById(R.id.isr_text)).setText(text);
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip("onError Code：" + error.getErrorCode() + "无法识别你说了什么");
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    /**
     * 参数设置
     *
     * @param param
     * @return
     */
    public boolean setParam() {
        boolean result = false;
        //设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");

        if ("cloud".equalsIgnoreCase(mEngineType)) {
            String grammarId = mSharedPreferences.getString(KEY_GRAMMAR_ABNF_ID, null);
            if (TextUtils.isEmpty(grammarId)) {
                result = false;
            } else {
                //设置云端识别使用的语法id
                mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
                result = true;
            }
        } else {
            //设置本地识别使用语法id
            mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");
            //设置本地识别的门限值
            mAsr.setParameter(SpeechConstant.ASR_THRESHOLD, "30");
            result = true;
        }

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/asr.wav");
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        if (ttsDemo != null) {
            ttsDemo.stopSpeaking();
        }
        mAsr.cancel();
        mAsr.destroy();
    }

    @Override
    protected void onResume() {
        //移动数据统计分析
        FlowerCollector.onResume(AsrDemo.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(AsrDemo.this);
        super.onPause();
    }

}
