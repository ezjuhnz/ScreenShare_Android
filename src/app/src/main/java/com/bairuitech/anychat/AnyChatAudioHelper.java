package com.bairuitech.anychat;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Process;

import com.cmbc.av.utils.LogUtils;

public class AnyChatAudioHelper {
    public static final int PLAY_MODE_AUTO = 0;
    public static final int PLAY_MODE_RECEIVER = 1;
    public static final int PLAY_MODE_SPEAKER = 2;
    private static final String TAG = "ANYCHAT";
    private boolean mAudioPlayReleased = false;
    private AudioRecord mAudioRecord = null;
    private boolean mAudioRecordReleased = false;
    private AudioTrack mAudioTrack = null;
    private Context mContext = null;
    private int mMinPlayBufSize = 0;
    private int mMinRecordBufSize = 0;
    private PlayAudioThread mPlayAudioThread = null;
    private int mPlayMode = 2;
    private boolean mPlayThreadExitFlag = false;
    private int mProfile = 0;
    private RecordAudioThread mRecordAudioThread = null;
    private boolean mRecordThreadExitFlag = false;

    class PlayAudioThread extends Thread {
        PlayAudioThread() {
        }

        public void run() {
            if (AnyChatAudioHelper.this.mAudioTrack != null) {
                try {
                    Process.setThreadPriority(-19);
                } catch (Exception e) {
                    LogUtils.dTag(AnyChatAudioHelper.TAG, "Set play thread priority failed: " + e.getMessage());
                }
                try {
                    AnyChatAudioHelper.this.mAudioTrack.play();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                LogUtils.dTag(AnyChatAudioHelper.TAG, "audio play....");
                while (!AnyChatAudioHelper.this.mPlayThreadExitFlag) {
                    try {
                        byte[] data = AnyChatCoreSDK.FetchAudioPlayBuffer(640);
                        AnyChatAudioHelper.this.mAudioTrack.write(data, 0, data.length);
                    } catch (Exception e3) {
                    }
                }
                LogUtils.dTag(AnyChatAudioHelper.TAG, "audio play stop....");
            }
        }
    }

    class RecordAudioThread extends Thread {
        RecordAudioThread() {
        }

        public void run() {
            if (AnyChatAudioHelper.this.mAudioRecord != null) {
                try {
                    Process.setThreadPriority(-19);
                } catch (Exception e) {
                    LogUtils.dTag(AnyChatAudioHelper.TAG, "Set record thread priority failed: " + e.getMessage());
                }
                try {
                    AnyChatAudioHelper.this.mAudioRecord.startRecording();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                LogUtils.dTag(AnyChatAudioHelper.TAG, "audio record....");
                byte[] recordbuf = new byte[640];
                while (!AnyChatAudioHelper.this.mRecordThreadExitFlag) {
                    try {
                        int ret = AnyChatAudioHelper.this.mAudioRecord.read(recordbuf, 0, recordbuf.length);
                        if (ret == -1 || ret == -3 || ret == -2) {
                            break;
                        }
                        AnyChatCoreSDK.InputAudioData(recordbuf, ret, 0);
                    } catch (Exception e3) {
                    }
                }
                LogUtils.dTag(AnyChatAudioHelper.TAG, "audio record stop....");
            }
        }
    }

    public void SetContext(Context ctx) {
        this.mContext = ctx;
    }

    public int InitAudioPlayer(int profile) {
        if (this.mAudioTrack != null) {
            return 0;
        }
        int samplerate;
        int channel;
        int samplebit;
        this.mProfile = profile;
        LogUtils.dTag(TAG, "InitAudioPlayer, profile: " + profile);
        if (profile == 1) {
            samplerate = 16000;
            channel = 2;
            samplebit = 2;
        } else if (profile != 2) {
            return -1;
        } else {
            samplerate = 44100;
            channel = 3;
            samplebit = 2;
        }
        try {
            int i;
            this.mAudioPlayReleased = false;
            this.mMinPlayBufSize = AudioTrack.getMinBufferSize(samplerate, channel, samplebit);
            if (this.mPlayMode == 2) {
                i = 3;
            } else {
                i = 0;
            }
            this.mAudioTrack = new AudioTrack(i, samplerate, channel, samplebit, this.mMinPlayBufSize, 1);
            if (this.mPlayAudioThread == null) {
                this.mPlayThreadExitFlag = false;
                this.mPlayAudioThread = new PlayAudioThread();
                this.mPlayAudioThread.start();
            }
            LogUtils.dTag(TAG, "mMinPlayBufSize = " + this.mMinPlayBufSize);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public void ReleaseAudioPlayer() {
        if (!this.mAudioPlayReleased) {
            this.mAudioPlayReleased = true;
            LogUtils.dTag(TAG, "ReleaseAudioPlayer");
            if (this.mPlayAudioThread != null) {
                this.mPlayThreadExitFlag = true;
                this.mPlayAudioThread = null;
            }
            if (this.mAudioTrack != null) {
                try {
                    this.mAudioTrack.stop();
                    this.mAudioTrack.release();
                    this.mAudioTrack = null;
                } catch (Exception e) {
                }
            }
        }
    }

    public Boolean IsSpeakerMode() {
        return this.mPlayMode == 2 ? Boolean.valueOf(true) : Boolean.valueOf(false);
    }

    public void SwitchPlayMode(int mode) {
        try {
            AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
            if (mode == 0) {
                if (IsSpeakerMode().booleanValue()) {
                    audioManager.setMode(2);
                    this.mPlayMode = 1;
                } else {
                    audioManager.setMode(0);
                    this.mPlayMode = 2;
                }
            } else if (mode == 1) {
                audioManager.setMode(2);
                this.mPlayMode = 1;
            } else if (mode == 2) {
                audioManager.setMode(0);
                this.mPlayMode = 2;
            }
            ReleaseAudioPlayer();
            InitAudioPlayer(this.mProfile);
        } catch (Exception e) {
        }
    }

    public int InitAudioRecorder(int profile) {
        if (this.mAudioRecord != null) {
            return 0;
        }
        int samplerate;
        int channel;
        int samplebit;
        LogUtils.dTag(TAG, "InitAudioRecorder, profile: " + profile);
        if (profile == 1) {
            samplerate = 16000;
            channel = 2;
            samplebit = 2;
        } else if (profile != 2) {
            return -1;
        } else {
            samplerate = 44100;
            channel = 3;
            samplebit = 2;
        }
        try {
            this.mAudioRecordReleased = false;
            this.mMinRecordBufSize = AudioRecord.getMinBufferSize(samplerate, channel, samplebit);
            this.mAudioRecord = new AudioRecord(1, samplerate, channel, samplebit, this.mMinRecordBufSize);
            AnyChatCoreSDK.SetInputAudioFormat(this.mAudioRecord.getChannelCount(), this.mAudioRecord.getSampleRate(), 16, 0);
            if (this.mRecordAudioThread == null) {
                this.mRecordThreadExitFlag = false;
                this.mRecordAudioThread = new RecordAudioThread();
                this.mRecordAudioThread.start();
            }
            LogUtils.dTag(TAG, "mMinRecordBufSize = " + this.mMinRecordBufSize);
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    public void ReleaseAudioRecorder() {
        if (!this.mAudioRecordReleased) {
            this.mAudioRecordReleased = true;
            LogUtils.dTag(TAG, "ReleaseAudioRecorder");
            if (this.mRecordAudioThread != null) {
                this.mRecordThreadExitFlag = true;
                this.mRecordAudioThread = null;
            }
            if (this.mAudioRecord != null) {
                try {
                    this.mAudioRecord.stop();
                    this.mAudioRecord.release();
                    this.mAudioRecord = null;
                } catch (Exception e) {
                }
            }
        }
    }
}
