package com.example.rivenlee.recorddialog.ui;

import android.Manifest;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rivenlee.recorddialog.BaseDialogFragment;
import com.example.rivenlee.recorddialog.R;
import com.example.rivenlee.recorddialog.utils.DateUtils;
import com.example.rivenlee.recorddialog.utils.FileUtils;
import com.example.rivenlee.recorddialog.view.WaveView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import tech.oom.idealrecorder.IdealRecorder;
import tech.oom.idealrecorder.StatusListener;
import www.linwg.org.lib.LCardView;

/**
 * author: rivenlee
 * date: 2018/10/30
 * email: rivenlee0@gmail.com
 */
public class RecordDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_recording)
    TextView tvRecording;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.tv_start_recording)
    TextView tvStartRecording;
    @BindView(R.id.img_volic)
    ImageView imgVolic;
    @BindView(R.id.tv_card_record_status)
    TextView tvCardRecordStatus;
    @BindView(R.id.tv_card_record_duration)
    TextView tvCardRecordDuration;
    @BindView(R.id.tv_card_record_date)
    TextView tvCardRecordDate;
    @BindView(R.id.card_broadcast)
    LCardView cardBroadcast;
    @BindView(R.id.wave_view)
    WaveView waveView;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;
    @BindView(R.id.img_cancel)
    ImageView imgCancel;
    @BindView(R.id.img_send)
    ImageView imgSend;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_send)
    TextView tvSend;

    private IdealRecorder idealRecorder;
    private IdealRecorder.RecordConfig recordConfig;
    private String fileName;
    private Subscription subscribe;
    private long currentMilliseconds = 0;//当前录音的毫秒数
    private int seconds = 0;

    public static RecordDialogFragment newInstance() {
        return new RecordDialogFragment();
    }

    @Override
    protected int setView() {
        return R.layout.dialog_fragment_record;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idealRecorder = IdealRecorder.getInstance();
        //Recorder的配置信息 采样率 采样位数
        recordConfig = new IdealRecorder.RecordConfig(MediaRecorder.AudioSource.MIC,
                IdealRecorder.RecordConfig.SAMPLE_RATE_22K_HZ, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        tvStartRecording.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                readyRecord();
                return true;
            }
        });

        tvStartRecording.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        return false;

                }
                return false;
            }
        });
        //检查权限
        checkPermission();
    }

    private void checkPermission() {
        Acp.getInstance(mContext).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(List permissions) {
                        Toast.makeText(mContext, "请授权,否则无法录音", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }

    /**
     * 准备录音
     */
    private void readyRecord() {
        FileUtils.deleteFile(FileUtils.getFilePath());
        setClickable(false);
        record();
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        setClickable(true);
        idealRecorder.stop();
    }

    private void setClickable(boolean clickable){
        llCancel.setClickable(clickable);
        llSend.setClickable(clickable);
    }

    /**
     * 开始录音
     */
    private void record() {
        fileName = "recode" + System.currentTimeMillis() + ".mp3";
        //如果需要保存录音文件  设置好保存路径就会自动保存  也可以通过onRecordData 回调自己保存  不设置 不会保存录音
        idealRecorder.setRecordFilePath(FileUtils.getCacheFilePath(fileName));
        //设置录音配置 最长录音时长 以及音量回调的时间间隔
        idealRecorder.setRecordConfig(recordConfig).setMaxRecordTime(Integer.MAX_VALUE).setVolumeInterval(200);
        //设置录音时各种状态的监听
        idealRecorder.setStatusListener(statusListener);
        idealRecorder.start(); //开始录音
    }

    /**
     * 启用计时器功能
     */
    private void countDownTimer() {
        Observable<Long> observable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
        subscribe = observable.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                currentMilliseconds += 1000;
                String hms = DateUtils.getFormatHMS(currentMilliseconds);
                tvRecordTime.setText(hms);
            }
        });
    }

    /**
     * 录音状态监听回调
     */
    private StatusListener statusListener = new StatusListener() {
        @Override
        public void onStartRecording() {
            countDownTimer();
            changeRed();
            tvStartRecording.setTextColor(getResources().getColor(R.color.gray_7b7b7b));
            tvRecording.setVisibility(View.GONE);
            cardBroadcast.setVisibility(View.GONE);
            waveView.setVisibility(View.VISIBLE);
            tvRecordTime.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRecordData(short[] data, int length) {
            for (int i = 0; i < length; i += 60) {
                waveView.addData(data[i]);
            }
        }

        @Override
        public void onVoiceVolume(int volume) {

        }

        @Override
        public void onRecordError(int code, String errorMsg) {
        }

        @Override
        public void onFileSaveFailed(String error) {
            Toast.makeText(getActivity(), "文件保存失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFileSaveSuccess(String fileUri) {
//            Toast.makeText(getActivity(), "文件保存成功,路径是" + fileUri, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStopRecording() {
            seconds = (int) (currentMilliseconds /1000);
            currentMilliseconds = 0;
            tvStartRecording.setTextColor(getResources().getColor(R.color.white));
            //rx取消订阅关联
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
    };

    @OnClick({R.id.ll_cancel, R.id.ll_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_cancel:
                onCancelInterface.onCancel();
                FileUtils.deleteFile(FileUtils.getFilePath());
                dismiss();
                break;
            case R.id.ll_send:
                if(seconds < 3){
                    Toast.makeText(mContext, "录音时间不得少于3秒", Toast.LENGTH_LONG).show();
                    return;
                }
                notifyServerStatus();
                break;
        }
    }


    /**
     * 此处由服务器返回结果，结合网络请求返回结果实现。
     */
    private void notifyServerStatus(){
        Toast.makeText(mContext, "录音发送成功，文件路径->" + FileUtils.getFilePath(), Toast.LENGTH_LONG).show();
        notifyServerSuccess();
//        notifyServerFails("录音发送失败！");
    }

    /**
     * 创建取消dialog的接口
     */
    public interface OnCancelInterface {
        void onCancel();
    }

    private OnCancelInterface onCancelInterface;

    /**
     * 取消dialog的接口回调方法
     *
     * @param onCancelInterface
     */
    public void setOnCancelListener(OnCancelInterface onCancelInterface) {
        this.onCancelInterface = onCancelInterface;
    }

    private void changeRed(){
        llSend.setEnabled(true);
        imgCancel.setImageResource(R.mipmap.cancel_red);
        imgSend.setImageResource(R.mipmap.send_red);
        tvCancel.setTextColor(getResources().getColor(R.color.color_333333));
        tvSend.setTextColor(getResources().getColor(R.color.color_333333));
    }
    private void changeGray(){
        llSend.setEnabled(false);
        imgCancel.setImageResource(R.mipmap.cancle);
        imgSend.setImageResource(R.mipmap.send);
        tvCancel.setTextColor(getResources().getColor(R.color.gray_9b9b9b));
        tvSend.setTextColor(getResources().getColor(R.color.gray_9b9b9b));
    }

    /**
     * 广播发送成功回调
     */
    private void notifyServerSuccess() {
        FileUtils.deleteFile(FileUtils.getFilePath());
        changeGray();
        renderCardView("您的录音已经发送成功",R.mipmap.broadcast_success);
    }
    /**
     * 广播发送失败回调
     */
    private void notifyServerFails(String message) {
        changeGray();
        renderCardView("出现网络错误",R.mipmap.broadcast_fail);
    }

    /**
     * 渲染广播发送成功或失败后的显示效果
     * @param msg
     * @param imgRes
     */
    private void renderCardView(String msg, int imgRes) {
        cardBroadcast.setVisibility(View.VISIBLE);
        waveView.setVisibility(View.GONE);
        tvRecordTime.setVisibility(View.INVISIBLE);
        tvCardRecordStatus.setText(msg);
        tvCardRecordDate.setText(DateUtils.getDate());
        tvCardRecordDuration.setText("时长 " + DateUtils.secondToTime(seconds));
        imgVolic.setImageResource(imgRes);
    }
}
