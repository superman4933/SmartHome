package com.iflytek.voicedemo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.iflytek.speech.util.RippleBackground;
import com.iflytek.sunflower.FlowerCollector;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/*此类为一个语音交互的activity*/
public class AsrDemo extends Activity implements OnClickListener {

    int volState;
    View toastRoot;
    TextView tv;

    /*发射命令原始码*/
    String TURN_TV = "f7 7f 01 01 01 02 0c 01 01 00 32 00 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff fc 00 00 00 00 00 00 00 00 00 00 07 ff 80 1f fe 00 7f f0 01 ff e0 07 ff 80 1f fc 00 7f f0 00 00 00 07 ff 00 1f fc 00 00 00 03 ff c0 00 00 00 3f fc 00 00 00 03 ff c0 00 00 00 3f fc 00 00 00 03 ff c0 00 00 00 3f f8 00 ff e0 00 00 00 0f fe 00 3f f8 00 00 00 03 ff 80 0f fe 00 7f f8 00 00 00 07 ff 80 1f fe 00 7f f8 01 ff e0 00 00 00 1f fe 00 7f f8 00 00 00 07 ff 80 00 00 00 7f f0 01 ff c0 00 00 00 1f fc 00 00 00 01 ff c0 00 00 00 3f fc 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0f ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff c0 00 00 00 00 07 ff 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7b";
    String VOL_TURN_UP = "f7 7f 01 01 01 02 0c 01 01 00 32 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff fe 00 00 00 00 00 00 00 00 00 00 03 ff c0 0f fe 00 3f f8 00 ff e0 03 ff 80 0f fe 00 3f f8 00 00 00 03 ff 80 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 7f f0 00 00 00 07 ff 00 1f fc 00 00 00 01 ff c0 07 ff 00 00 00 00 ff f0 00 00 00 0f ff 00 3f fc 00 ff f0 03 ff c0 00 00 00 3f fc 00 ff f0 00 00 00 0f ff 00 3f f8 00 ff e0 00 00 00 0f fe 00 00 00 00 ff e0 00 00 00 1f fe 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7c";
    String VOL_TURN_DOWN = "f7 7f 01 01 01 02 0c 01 01 00 32 7f ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff fe 00 00 00 00 00 00 00 00 00 00 03 ff c0 0f ff 00 3f fc 00 ff f0 03 ff 80 0f fe 00 3f f8 00 00 00 03 ff 80 0f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 7f f8 00 00 00 07 ff 00 1f fc 00 00 00 01 ff c0 00 00 00 1f fc 00 00 00 03 ff c0 00 00 00 3f fc 00 ff f0 03 ff c0 0f ff 00 00 00 00 ff f0 03 ff c0 0f ff 00 3f fc 00 ff f0 00 00 00 0f fe 00 00 00 00 ff e0 00 00 00 0f fe 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff f0 00 00 00 00 01 ff e0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3a";
    String SOUND_OFF = "f7 7f 01 01 01 02 0c 01 01 00 32 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff fe 00 00 00 00 00 00 00 00 00 00 03 ff 80 0f fe 00 3f f8 01 ff e0 07 ff 80 1f fe 00 7f f8 00 00 00 07 ff c0 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fc 00 7f f0 00 00 00 07 ff 00 1f fc 00 ff f0 03 ff c0 0f ff 00 00 00 00 ff f0 03 ff c0 0f ff 00 3f fc 00 00 00 03 ff c0 00 00 00 3f fc 00 00 00 03 ff c0 00 00 00 3f f8 00 ff e0 00 00 00 0f fe 00 00 00 00 ff e0 00 00 00 0f fe 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff f0 00 00 00 00 01 ff c0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 f6";
    String VOL_CLOSE = "f7 7f 01 01 01 02 0c 01 01 00 32 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff fe 00 00 00 00 00 00 00 00 00 00 03 ff 80 0f fe 00 3f f8 01 ff e0 07 ff 80 1f fe 00 7f f8 00 00 00 07 ff c0 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fe 00 00 00 01 ff e0 00 00 00 1f fc 00 7f f0 00 00 00 07 ff 00 1f fc 00 ff f0 03 ff c0 0f ff 00 00 00 00 ff f0 03 ff c0 0f ff 00 3f fc 00 00 00 03 ff c0 00 00 00 3f fc 00 00 00 03 ff c0 00 00 00 3f f8 00 ff e0 00 00 00 0f fe 00 00 00 00 ff e0 00 00 00 0f fe 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff f0 00 00 00 00 01 ff c0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 f6";
    String OPEN_LIGHT = "f7 7f 01 01 01 00 19 01 01 00 0d 0f 01 01 0f 01 0f 0f 0f 01 00 00 00 21 12";
    String CLOSE_LIGHT = "f7 7f 01 01 01 00 19 01 01 00 0d 0f 01 01 0f 01 0f 0f 0f 00 00 00 01 21 12";
    String PLAY_MUSIC = "f7 7f 01 01 01 04 0c 01 01 00 09 f0 00 3f 00 03 f0 00 3f 00 00 00 00 3f 00 03 f0 00 00 00 03 e0 00 3e 00 03 e0 00 00 00 07 e0 00 00 00 07 c0 00 7c 00 07 e0 00 7e 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 fc 00 00 00 00 fc 00 00 00 01 f8 00 00 00 01 f8 00 1f 80 01 f8 00 1f 80 00 00 00 1f 00 01 f0 00 00 00 03 f0 00 3f 00 03 f0 00 00 00 03 f0 00 00 00 03 e0 00 3e 00 03 e0 00 3e 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 fc 00 00 00 00 f8 00 00 00 01 f8 00 00 00 01 f0 00 1f 00 01 f0 00 1f 00 00 00 00 3f 00 03 f0 00 00 00 03 f0 00 3e 00 03 e0 00 00 00 03 e0 00 00 00 07 c0 00 7e 00 07 e0 00 7e 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 f8 00 00 00 01 f8 00 00 00 01 f0 00 00 00 03 f0 00 3f 00 03 f0 00 3f 00 00 00 00 3e 00 03 e0 00 00 00 07 e0 00 7e 00 03 f0 00 00 00 07 e0 00 00 00 07 c0 00 7c 00 07 c0 00 7c 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 52";
    String NEXT_SONG = "f7 7f 01 01 01 04 0c 01 01 00 09 07 e0 00 7e 00 07 e0 00 7e 00 00 00 00 7c 00 07 e0 00 7e 00 07 e0 00 00 00 07 c0 00 7c 00 00 00 00 fc 00 0f c0 00 fc 00 0f c0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 f8 00 00 00 01 fc 00 00 00 01 f0 00 00 00 03 f0 00 3f 00 03 f0 00 3f 00 00 00 00 3e 00 03 e0 00 3e 00 03 f0 00 00 00 03 e0 00 3e 00 00 00 00 7e 00 07 e0 00 7e 00 07 e0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 f8 00 00 00 01 f0 00 00 00 03 f0 00 00 00 03 e0 00 3e 00 03 f0 00 3f 00 00 00 00 3e 00 03 e0 00 3e 00 03 e0 00 00 00 07 e0 00 7e 00 00 00 00 7e 00 07 e0 00 7e 00 07 e0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 f8 00 00 00 01 f0 00 00 00 03 f0 00 00 00 03 e0 00 3e 00 03 e0 00 3e 00 00 00 00 7e 00 03 e0 00 3e 00 03 e0 00 00 00 07 e0 00 7e 00 00 00 00 7c 00 07 c0 00 7c 00 07 c0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 b4";
    String PRE_SONG = "f7 7f 01 01 01 04 0c 01 01 00 09 f8 00 1f 80 01 f8 00 1f 80 00 00 00 1f 00 01 f0 00 1f 00 01 f8 00 00 00 03 f0 00 00 00 03 f0 00 00 00 03 e0 00 3e 00 03 e0 00 3e 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7e 00 00 00 00 7c 00 00 00 00 fc 00 00 00 00 f8 00 0f 80 00 f8 00 0f 80 00 00 00 1f 80 00 f8 00 0f 80 00 f8 00 00 00 01 f8 00 00 00 01 f0 00 00 00 03 f0 00 3f 00 03 f0 00 1f 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7c 00 00 00 00 fc 00 00 00 00 fc 00 00 00 00 f8 00 0f 80 00 f8 00 0f 80 00 00 00 1f 80 01 f8 00 1f 80 01 f8 00 00 00 01 f8 00 00 00 03 f0 00 00 00 03 f0 00 3f 00 03 f0 00 3f 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7c 00 00 00 00 fc 00 00 00 00 f8 00 00 00 01 f8 00 1f 80 01 f8 00 1f 80 00 00 00 1f 00 01 f8 00 1f 80 01 f8 00 00 00 07 f0 00 00 00 03 f8 00 00 00 03 e0 00 3e 00 03 e0 00 3e 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ea";
    String PLAY_VIDEO = "f7 7f 01 01 01 02 0c 01 01 00 32 f0 00 3f 00 03 f0 00 00 00 03 e0 00 3e 00 00 00 00 7e 00 03 e0 00 3e 00 00 00 00 7e 00 07 e0 00 7e 00 07 e0 00 7e 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 c0 00 00 00 0f c0 00 00 00 0f 80 00 00 00 1f 80 01 f8 00 1f 80 00 f8 00 00 00 01 f8 00 1f 80 00 00 00 1f 00 01 f0 00 1f 00 00 00 00 3f 00 03 f0 00 1f 00 01 f0 00 1f 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 e0 00 00 00 07 c0 00 00 00 0f c0 00 00 00 0f 80 00 f8 00 0f c0 00 f8 00 00 00 01 f8 00 0f 80 00 00 00 1f 80 01 f8 00 1f 80 00 00 00 1f 00 01 f0 00 1f 80 01 f0 00 1f 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 07 e0 00 00 00 07 c0 00 00 00 07 c0 00 00 00 0f c0 00 fc 00 0f c0 00 fc 00 00 00 00 f8 00 0f c0 00 00 00 1f 80 00 f8 00 0f 80 00 00 00 1f 80 01 f8 00 1f 80 01 f8 00 1f 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 f2";
    String OPEN_AIR = "f7 7f 01 01 01 04 0c 01 01 00 09 ff ff ff ff ff ff ff ff ff ff fe 00 00 00 00 00 00 00 00 00 00 0f ff 00 00 00 03 ff c0 0f fe 00 00 00 03 ff c0 00 00 00 ff f0 03 ff 80 1f fc 00 00 00 07 ff 80 1f fc 01 ff e0 00 00 00 3f f8 01 ff c0 0f ff 00 00 00 03 ff c0 00 00 00 7f f0 03 ff 80 00 00 00 ff f0 07 ff 80 3f fc 00 ff e0 00 00 00 3f f8 00 00 00 0f ff 00 00 00 01 ff c0 00 00 00 7f f8 00 00 00 1f fe 00 00 00 03 ff 80 00 00 00 ff f0 00 00 00 3f fc 01 ff e0 07 ff 00 3f f8 01 ff e0 0f ff 00 00 00 03 ff c0 00 00 00 7f f0 03 ff c0 00 00 00 ff f0 07 ff 80 00 00 00 ff e0 07 ff 00 3f fc 01 ff e0 0f ff 00 00 00 01 ff c0 0f fe 00 00 00 03 ff c0 1f fe 00 00 00 03 ff 80 00 00 00 ff f0 00 00 00 00 00 00 00 00 00 00 00 00 7f ff ff ff ff ff ff ff ff ff ff 00 00 00 00 00 00 00 00 00 00 07 ff 80 00 00 00 ff e0 07 ff 00 00 00 01 ff e0 00 00 00 7f f8 01 ff c0 0f fe 00 00 00 03 ff c0 1f fe 00 ff f0 00 00 00 1f fc 00 ff e0 07 ff 80 00 00 01 ff e0 00 00 00 3f f8 01 ff c0 00 00 00 7f f8 03 ff c0 0f fe 00 7f f0 00 00 00 1f fe 00 00 00 07 ff 80 00 00 00 ff e0 00 00 00 3f fc 00 00 00 0f ff 00 00 00 01 ff c0 00 00 00 7f f8 00 00 00 1f fe 00 7f f0 03 ff 80 1f fc 00 ff f0 07 ff 80 00 00 00 ff e0 00 00 00 3f f8 01 ff e0 00 00 00 7f f8 01 ff c0 00 00 00 7f f0 03 ff c0 1f fe 00 ff f0 03 ff 80 00 00 00 ff e0 07 ff 80 00 00 01 ff e0 07 ff 00 00 00 01 ff c0 00 00 00 7f f8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 4b";
    String CLOSE_AIR = "f7 7f 01 01 01 04 0c 01 01 00 09 00 00 7f ff ff ff ff ff ff ff ff ff ff 00 00 00 00 00 00 00 00 00 00 07 ff 00 00 00 01 ff e0 07 ff 00 00 00 01 ff c0 00 00 00 7f f8 01 ff c0 0f fe 00 00 00 03 ff 80 1f fc 00 7f f0 00 00 00 3f fc 00 ff e0 07 ff 00 00 00 01 ff c0 00 00 00 7f f8 01 ff c0 00 00 00 7f f0 01 ff c0 00 00 00 ff f0 00 00 00 1f fc 00 00 00 07 ff 80 00 00 01 ff e0 07 ff 00 00 00 01 ff c0 00 00 00 7f f8 00 00 00 1f fe 00 7f f0 03 ff 80 1f fe 00 7f f0 00 00 00 3f fc 00 ff e0 07 ff 00 00 00 01 ff e0 00 00 00 7f f8 00 00 00 0f fe 00 7f f8 01 ff c0 0f fe 00 7f f0 03 ff 80 1f fc 00 ff f0 03 ff 80 00 00 01 ff e0 00 00 00 3f f8 00 00 00 0f ff 00 00 00 03 ff c0 00 00 00 7f f0 00 00 00 00 00 00 00 00 00 00 00 00 3f ff ff ff ff ff ff ff ff ff ff 80 00 00 00 00 00 00 00 00 00 03 ff c0 00 00 00 ff f0 03 ff 80 00 00 00 ff e0 00 00 00 3f fc 00 ff e0 07 ff 00 00 00 01 ff e0 07 fe 00 3f f8 00 00 00 0f fe 00 7f f0 03 ff 80 00 00 00 ff f0 00 00 00 1f fc 00 ff e0 00 00 00 3f fc 00 ff e0 00 00 00 7f f8 00 00 00 0f fe 00 00 00 03 ff c0 00 00 00 ff f0 03 ff 80 00 00 00 ff f0 00 00 00 3f fc 00 00 00 07 ff 00 3f f8 01 ff e0 07 ff 00 3f f8 00 00 00 0f fe 00 7f f0 03 ff 80 00 00 00 ff f0 00 00 00 1f fc 00 00 00 07 ff 80 1f fc 00 ff e0 07 ff 00 3f f8 01 ff c0 0f ff 00 3f f8 01 ff c0 00 00 00 7f f0 00 00 00 1f fe 00 00 00 07 ff 80 00 00 00 ff e0 00 00 00 3f f8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 1f";
String ALT_TAB="f7 7f 01 01 01 04 0c 01 01 00 09 01 f8 00 0f 80 00 f8 00 0f 80 00 00 00 1f 80 01 f8 00 00 00 01 f0 00 00 00 03 f0 00 00 00 03 e0 00 3f 00 00 00 00 3e 00 03 e0 00 3e 00 03 e0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3e 00 00 00 00 7e 00 00 00 00 7c 00 00 00 00 fc 00 0f c0 00 7c 00 07 c0 00 00 00 0f c0 00 fc 00 00 00 00 f8 00 00 00 01 f8 00 00 00 01 f8 00 1f 00 00 00 00 1f 00 01 f0 00 1f 00 01 f0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3f 00 00 00 00 7e 00 00 00 00 7e 00 00 00 00 7c 00 07 c0 00 7c 00 07 c0 00 00 00 0f c0 00 fc 00 00 00 00 fc 00 00 00 00 f8 00 00 00 01 f8 00 1f 80 00 00 00 1f 00 01 f0 00 1f 00 01 f0 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3f 00 00 00 00 3e 00 00 00 00 7e 00 00 00 00 7c 00 07 c0 00 7e 00 07 e0 00 00 00 07 c0 00 7c 00 00 00 00 fc 00 00 00 00 f8 00 00 00 01 f8 00 1f 80 00 00 00 1f 80 01 f0 00 1f 80 01 f8 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 90";
    /*发射命令的byte数组，设备只能识别这个格式的*/
    byte[] bTurnTv;
    byte[] bVolTurnUp;
    byte[] bVolTurnDown;
    byte[] bSoundOFF;
    byte[] bVolClose;
    byte[] bopenLight;
    byte[] bcloseLight;
    byte[] bPLAY_MUSIC;
    byte[] bNEXT_SONG ;
    byte[] bPRE_SONG ;
    byte[] bPLAY_VIDEO;
    byte[] bOPEN_AIR ;
    byte[] bCLOSE_AIR ;
    byte[] bALT_TAB ;




    ImageView beginButton;
    private boolean isSleepMode;
    String PASSWORD = "79559249";
    boolean isBeginRec;
    TtsDemo ttsDemo;
    private static String TAG = AsrDemo.class.getSimpleName();
    // 语音识别对象
    private SpeechRecognizer mAsr;
    private Toast mToast;
    // 缓存
    public static AsrDemo A;
    private SharedPreferences mSharedPreferences;
    // 本地语法文件
    private String mLocalGrammar = null;
    private static final String KEY_GRAMMAR_ABNF_ID = "grammar_abnf_id";
    private static final String GRAMMAR_TYPE_BNF = "bnf";
    private String mEngineType = null;
    // 语记安装助手类
    ApkInstaller mInstaller;
    WifiAdmin wifiAdmin;
    ConnetSendPacketThread connetSendPacketThread;

    RippleBackground rippleBackground;
    Handler handler;

    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("oncreate", "oncreate方法开始运行");
        A = this;//一个静态Activity变量，把对象传递到语音合成类那里


        setContentView(R.layout.isrdemo1);

        toastRoot = getLayoutInflater().inflate(R.layout.my_toast, null);
        mToast = new Toast(AsrDemo.this);
        mToast.setView(toastRoot);
        mToast.setDuration(Toast.LENGTH_SHORT);
        tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);


        beginButton = (ImageView) findViewById(R.id.isr_recognize);
//        动画效果
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.setRippleAmount(1);
        handler = new Handler();


        /*打开wifi*/
        wifiAdmin = new WifiAdmin(this);
        wifiAdmin.openWifi();
        Log.d("oncreate", "创建wifiAdmin对象");
//        扫描前检查是否连接了指定的wifi,未连接则进行wifi扫描，连接指定wifi
        String nowSSID = wifiAdmin.getNowSSid();
        Log.d("oncreate", "获取目前连接的ssid" + nowSSID);
//        检查是否可以成功获取到发射器的无线热点，能获取则连接，不能则弹出提示
        if (!nowSSID.contains("IYK")) {
            wifiAdmin.startScan();
            String IYKSSID = wifiAdmin.getIYKSSID();
            if (IYKSSID != null) {
                Log.d("oncreate", "执行IYKSSID不为空时指令");
                WifiConfiguration wcg = wifiAdmin.CreateWifiInfo(wifiAdmin.getIYKSSID(), PASSWORD, 3);
                wifiAdmin.addNetwork(wcg);
            } else {
                Toast.makeText(this, "无法检测到设备，您应该不在家", Toast.LENGTH_SHORT).show();
                Log.d("oncreate", "执行IYKSSID为空时指令");
            }
        }
        ttsDemo = new TtsDemo();
        Log.d("oncreate", "初始化语音合成对象");
        // 初始化识别对象
        mAsr = SpeechRecognizer.createRecognizer(AsrDemo.this, mInitListener);
        Log.d("oncreate", "初始化识别对象");
        // 初始化语法、命令词
        mLocalGrammar = FucUtil.readFile(this, "call.bnf", "utf-8");
        Log.d("oncreate", "初始化本地语法规则");
        mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        Log.d("oncreate", "初始化配置");
        mInstaller = new ApkInstaller(AsrDemo.this);
        init();
        Log.d("oncreate", "初始化本地引擎");
        timer.schedule(timerTask, 1500);
//        创建发送命令的线程对象（用handler控制），并把命令解析成byte数组
        connetSendPacketThread = new ConnetSendPacketThread(this);
//        connetSendPacketThread.start();
        bTurnTv = connetSendPacketThread.parseCMD(TURN_TV);
        bVolTurnUp = connetSendPacketThread.parseCMD(VOL_TURN_UP);
        bVolTurnDown = connetSendPacketThread.parseCMD(VOL_TURN_DOWN);
        bSoundOFF = connetSendPacketThread.parseCMD(SOUND_OFF);
        bVolClose = connetSendPacketThread.parseCMD(VOL_CLOSE);
        bopenLight = connetSendPacketThread.parseCMD(OPEN_LIGHT);
        bcloseLight = connetSendPacketThread.parseCMD(CLOSE_LIGHT);
        bPLAY_MUSIC=connetSendPacketThread.parseCMD(PLAY_MUSIC);
        bNEXT_SONG =connetSendPacketThread.parseCMD(NEXT_SONG);
        bPRE_SONG =connetSendPacketThread.parseCMD(PRE_SONG);
        bPLAY_VIDEO=connetSendPacketThread.parseCMD(PLAY_VIDEO);
        bOPEN_AIR =connetSendPacketThread.parseCMD(OPEN_AIR);
        bCLOSE_AIR=connetSendPacketThread.parseCMD(CLOSE_AIR);
        bALT_TAB=connetSendPacketThread.parseCMD(ALT_TAB);
    }

    //延迟0.5秒启动本地语法监听器，如果不延迟则会出错，原因未知
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            buildGra();
            Log.d("oncreate", "延迟0.8秒启动本地语法监听器");
        }
    };
    Timer timer = new Timer();


    private void init() {

        beginButton.setOnClickListener(AsrDemo.this);
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
        showTip("创建成功");//创建语法文件
        Log.d("oncreate", "开始构建语法1");
        if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
            mContent = new String(mLocalGrammar);
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            //指定引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            Log.d("oncreate", "开始构建语法2");
            ret = mAsr.buildGrammar(GRAMMAR_TYPE_BNF, mContent, mLocalGrammarListener);
            if (mLocalGrammarListener == null) {
                Log.d("oncreate", "mLocalGrammarListener为空");
            }
            Log.d("oncreate", "开始构建语法3");
            if (ret != ErrorCode.SUCCESS) {
                if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                    //未安装则跳转到提示安装页面
                    mInstaller.install();
                } else {
                    Log.d("oncreate", "语法构建失败" + ret);
                    showTip("语法构建失败1,错误码：" + ret);
                }
            }
        }
        Log.d("speech", "开始构建语法4");
        showTip("创建成功");//创建语法文件
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

                if (!isBeginRec) {
                    voiceRecognition();
                }
                isBeginRec = true;
                // 设置参数

//                rippleBackground.setRippleAmount(8);
                rippleBackground.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleAnimation();
                    }
                }, 3000);

                break;
        }
    }

    private void rippleAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<Animator>();
        animatorSet.playTogether(animatorList);
        animatorSet.start();

    }


    public void voiceRecognition() {
        if (!setParam()) {
            showTip("请先构建语法。");
            return;
        }

        Log.d("准备打开监听", "准备1");
        ret = mAsr.startListening(mRecognizerListener);
        Log.d("准备打开监听", "已结获取监听对象，并运行监听方法");
        if (ret != ErrorCode.SUCCESS) {
            if (ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                Log.d("打开监听失败", "安装语记");
                //未安装则跳转到提示安装页面
                mInstaller.install();
            } else {
                Log.d("打开监听失败", "错误码" + ret);
                showTip("识别失败,错误码: " + ret);
            }
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
            Log.d("oncreate", "初始化本地语法监听器");
            if (error == null) {
                showTip("构建成功");
                Log.d("oncreate", "初始化本地语法监听器成功");
            } else {
                showTip("语法构建失败2,错误码：" + error.getErrorCode());
                Log.d("oncreate", "初始化本地语法监听器失败" + error.getErrorCode());
            }
        }
    };

    /**
     * 识别监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            /**判断目前的声音状态，是否该重启波浪动画，0为小，1为大*/
            if (rippleBackground.isRippleAnimationRunning() && volState == 0) {
                volState = 1;
                rippleBackground.stopRippleAnimation();
                rippleBackground.setRippleAmount(1);
                rippleBackground.startRippleAnimation();
            }
            showTip("等待指令。。。");
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (result != null) {
//            if (false) {
                Log.d(TAG, "recognizer result：" + result.getResultString());
                String text;
                if ("cloud".equalsIgnoreCase(mEngineType)) {
//                    如果是网络语音识别则调用下面的解析方法
                    text = JsonParser.parseGrammarResult(result.getResultString());

                } else {
//                    本地识别的解析方法
                    text = JsonParser.parseLocalGrammarResult(result.getResultString());
                }
                rippleBackground.stopRippleAnimation();
                rippleBackground.setRippleAmount(6);
                rippleBackground.startRippleAnimation();
                volState = 0;
                /**  首先判断系统是否处于睡眠模式，处于此模式只有指定指令才能唤醒，避免噪音下的误识别,睡眠模式下要注意在这里就开启语音识别方法，因为无法
                 * 通过完成说话后调用完成说话方法来调用开启语音识别方法*/
                if (isSleepMode == true) {
                    if (text.contains("close") && text.contains("sleep")) {
                        ttsDemo.speakVoice(AsrDemo.this, "说吧，想让我干嘛");
                        isSleepMode = false;
                    } else {
                        voiceRecognition();
                    }
                } else {
                    showTip("识别成功");
                    if (text.contains("open") && text.contains("airCondition")) {
                        connetSendPacketThread.sendCMD(bOPEN_AIR);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，打开空调");
                    } else if (text.contains("hello")) {
                        ttsDemo.speakVoice(AsrDemo.this, "愚蠢的地球人，找我有什么事");
                    } else if ((text.contains("start") && text.contains("sleep")) || text.contains("shutUp")) {
                        ttsDemo.speakVoice(AsrDemo.this, "好的，我不说话了");
                        isSleepMode = true;
                    } else if (text.contains("gender")) {
                        ttsDemo.speakVoice(AsrDemo.this, "关你屁事，机器人可攻可守，男女通吃，只有愚蠢的人类不知道");
                    } else if (text.contains("name")) {
                        ttsDemo.speakVoice(AsrDemo.this, "我也不知道我叫什么名字，我就是个萌萌哒机器人");
                    } else if (text.contains("age")) {
                        ttsDemo.speakVoice(AsrDemo.this, "机器人是没有年龄的，你不知道吗，你好笨啊");
                    } else if (text.contains("fuck")) {
                        ttsDemo.speakVoice(AsrDemo.this, "我心情不好就会骂人，呵呵哒");
                    } else if (text.contains("shutDown")) {
                        if (ttsDemo != null) {
                            ttsDemo.stopSpeaking();
                        }
                        mAsr.cancel();
                        mAsr.destroy();
                        finish();
                    } else if (text.contains("power")) {
                        ttsDemo.speakVoice(AsrDemo.this, "好吧，地球人，你赢了，说吧，想让我干什么");
                    } else if (text.contains("close") && text.contains("airCondition")) {
                        connetSendPacketThread.sendCMD(bCLOSE_AIR);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，关闭空调");
                    } else if (text.contains("close") && text.contains("TV")) {
                        connetSendPacketThread.sendCMD(bTurnTv);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，关闭电视");
                    } else if (text.contains("open") && text.contains("TV")) {
                        connetSendPacketThread.sendCMD(bTurnTv);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，打开电视");
                    } else if (text.contains("playMusic")) {
                        connetSendPacketThread.sendCMD(bPLAY_MUSIC);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒");
                    } else if (text.contains("nextSong")) {
                        connetSendPacketThread.sendCMD(bNEXT_SONG);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒,换歌");
                    } else if (text.contains("preSong")) {
                        connetSendPacketThread.sendCMD(bPRE_SONG);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒,上一首");
                    } else if (text.contains("playVideo")) {
                        connetSendPacketThread.sendCMD(bPLAY_VIDEO);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒");
                    } else if (text.contains("open") && text.contains("light")) {
                        if (text.contains("hold")) {
                            ttsDemo.speakVoice(AsrDemo.this, "今天心情不好，不想给你开灯");
                        } else {
                            connetSendPacketThread.sendCMD(bopenLight);
                            ttsDemo.speakVoice(AsrDemo.this, "好哒，现在就开灯");
                        }
                    } else if (text.contains("vol") && text.contains("volTurnDown")) {

                        ttsDemo.speakVoice(AsrDemo.this, "好哒，减小音量");
                        connetSendPacketThread.sendCMD(bVolTurnDown);
                        connetSendPacketThread.sendCMD(bVolTurnDown);
                        connetSendPacketThread.sendCMD(bVolTurnDown);
                        connetSendPacketThread.sendCMD(bVolTurnDown);
                        connetSendPacketThread.sendCMD(bVolTurnDown);

                    } else if (text.contains("vol") && text.contains("volTurnUp")) {
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，增大音量");
                        connetSendPacketThread.sendCMD(bVolTurnUp);
                        connetSendPacketThread.sendCMD(bVolTurnUp);
                        connetSendPacketThread.sendCMD(bVolTurnUp);
                        connetSendPacketThread.sendCMD(bVolTurnUp);
                        connetSendPacketThread.sendCMD(bVolTurnUp);
                    } else if (text.contains("vol") && text.contains("close")) {
                        connetSendPacketThread.sendCMD(bVolClose);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，关闭声音");
                    } else if (text.contains("vol") && text.contains("open")) {
                        connetSendPacketThread.sendCMD(bVolClose);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，打开声音");
                    } else if (text.contains("close") && text.contains("light")) {
                        connetSendPacketThread.sendCMD(bcloseLight);
                        ttsDemo.speakVoice(AsrDemo.this, "好哒，正在关灯");
                    }
                    else if (text.contains("avPlayer")) {
                        ttsDemo.speakVoice(AsrDemo.this, "愚蠢的地球人，苍老师已经很久没有新片了，" +
                                "根据知乎的数据，现在年产电影量最高的明明是武藤兰老师，一年产量：304部");
                    }
                    else if (text.contains("openAV")) {
                        connetSendPacketThread.sendCMD(bTurnTv);
                        ttsDemo.speakVoice(AsrDemo.this, "好的，正在打开电视，老司机，请准备上车");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        connetSendPacketThread.sendCMD(bPLAY_VIDEO);


                    }

                    else {
                        ttsDemo.speakVoice(AsrDemo.this, "你说啥");
                    }

                }

//                ((EditText) findViewById(R.id.isr_text)).setText(text);
            } else {
                Log.d(TAG, "recognizer result : null");
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {


            Log.d("语音识别错误回调", "错误码：" + error.getErrorCode());
            if (!isFinishing()) {
                Log.d("onError", "准备启动语音识别1");
                if (!isBeginRec | error.getErrorCode() == 20005) {//此处做此判断的目的在于，当系统已结准备好识别时你不能再次去启动识别方法，否则出错
                    voiceRecognition();
                    Log.d("onError", "准备启动语音识别2");
                }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tv.setText(str);
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
        if (mAsr != null) {
            mAsr.cancel();
            mAsr.destroy();
        }
        if (connetSendPacketThread.UDPSocket != null) {
            connetSendPacketThread.UDPSocket.close();
        }
        if (connetSendPacketThread.sendHandlerThread != null) {
            connetSendPacketThread.sendHandlerThread.quit();
        }


//        if (connetSendPacketThread.wifiLock != null) {
//            connetSendPacketThread.wifiLock.release();
//        }

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
