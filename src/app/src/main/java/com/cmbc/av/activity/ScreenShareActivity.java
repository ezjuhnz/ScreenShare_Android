package com.cmbc.av.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cmbc.av.Key;
import com.cmbc.av.base.AbsActivity;
import com.cmbc.av.bean.InputVideoFormatFactory;
import com.cmbc.av.jni.ImageFormatUtils;
import com.cmbc.av.service.ScreenShareFloatView;
import com.cmbc.av.tools.ColorFormatTranslator;
import com.cmbc.av.utils.ImageUtils;
import com.cmbc.av.utils.LogUtils;
import com.cmbc.av.utils.StringUtils;
import com.cmbc.av.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xyz.djytest.screenshared.R;

//import android.support.annotation.RequiresApi;

public class ScreenShareActivity extends AbsActivity<ScreenShareControl, ScreenShareActivity, ScreenShareModel, ScreenShareAnychat>
    implements AdapterView.OnItemSelectedListener{
    static {
        System.loadLibrary("CImageUtil");
        System.loadLibrary("yuv");
    }

    final int screen_imageRecord_code = 112;
    public int localUserId, remoteUserId, roomId;
    Button login, enterRoom, openRemoteVideo, openLocalScreenShare, openLocalCamera, openLocalScreenShareMediaRecord;
    Button rotateSupportBtn;
    EditText redEditText;
    EditText greenEditText;
    EditText blueEditText;
    private int mRed  = 0;
    private int mGreen = 0;
    private int mBlue = 0;

    private int mCaptureWidth; //????????????,????????????????????????
    private int mCaptureHeight;//????????????,????????????????????????
    private int mScaleWidth = 0;
    private int mScaleHeight = 0;

    //    SurfaceView localSurface, remoteSurface;
    EditText room, remoteUser;
    Camera mCamera;
    boolean cameraPreviewState = false;
    MediaProjectionManager projectionManager;
    ImageReader imageReader;

    private Surface mSurface; //????????????????????????????????????
    MediaProjection mediaProjection;
    TextView localUser;
    boolean appHasBack = false;
    public ImageView screenImg;
    private boolean isScale = false;
    private int rotateAngles = 0;
    public MediaRecorder mMediaRecorder;
    public ServerSocket mServerSocket;
    private Activity mActivity;

    final SendDataThread sendVideoData = new SendDataThread();

    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private void setRed()
    {


    }

    private void FreshColor()
    {
        String mRedStr = redEditText.getText().toString();
        String mBlueStr = blueEditText.getText().toString();
        String mGreenStr = greenEditText.getText().toString();
        if(!mGreenStr.isEmpty())
        {
            mGreen = Integer.valueOf(mGreenStr);
        }
        if(!mRedStr.isEmpty())
        {
            mRed = Integer.valueOf(mRedStr);
        }

        if(!mBlueStr.isEmpty())
        {
            mBlue = Integer.valueOf(mBlueStr);
        }
    }

    //????????????????????????
    private void initScaleResolution()
    {
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        String defaultRes = "(??????)" + mCaptureWidth + "*" + mCaptureHeight;
        Key.SCALE_WIDTH[0] = mCaptureWidth;
        Key.SCALE_HEIGHT[0] = mCaptureHeight;
        String scaleRes_1 = Key.SCALE_WIDTH[1] + "*" + Key.SCALE_HEIGHT[1];
        String scaleRes_2 = Key.SCALE_WIDTH[2] + "*" + Key.SCALE_HEIGHT[2];
        String scaleRes_3 = Key.SCALE_WIDTH[3] + "*" + Key.SCALE_HEIGHT[3];
        String scaleRes_4 = Key.SCALE_WIDTH[4] + "*" + Key.SCALE_HEIGHT[4];
        String scaleRes_5 = Key.SCALE_WIDTH[5] + "*" + Key.SCALE_HEIGHT[5];
        String scaleRes_6 = Key.SCALE_WIDTH[6] + "*" + Key.SCALE_HEIGHT[6];
        categories.add(defaultRes);
        categories.add(scaleRes_1);
        categories.add(scaleRes_2);
        categories.add(scaleRes_3);
        categories.add(scaleRes_4);
        categories.add(scaleRes_5);
        categories.add(scaleRes_6);
        // Creating adapter for spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected ScreenShareAnychat createAnychat(ScreenShareControl control) {
        return new ScreenShareAnychat(getControl());
    }

    @Override
    protected ScreenShareModel createModel(ScreenShareControl control) {
        return new ScreenShareModel(getControl());
    }

    @Override
    protected ScreenShareControl createControl() {
        ScreenShareControl screenShareControl = new ScreenShareControl(this);
        return screenShareControl;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wbank_activity_screenshare;
    }


    @Override
    public ScreenShareActivity getActivity() {
        return this;
    }


    @Override
    protected void initView() {
        login = findView(R.id.login);
        //openLocalScreenShareMediaRecord = findView(R.id.openLocalScreenShareMediaRecord);
//        openLocalScreenShareMediaRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    mServerSocket = new ServerSocket(9999);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//        });
        screenImg = findView(R.id.screenImg);
        openLocalCamera = findView(R.id.openLocalCamera);
        openLocalScreenShare = findView(R.id.openLocalScreenShare);
        //localUser = findView(R.id.localUserId);
        enterRoom = findView(R.id.enterRoom);
        openRemoteVideo = findView(R.id.openRemoteVideo);

        if (openLocalCamera.getVisibility() == View.VISIBLE) {
            getControl().getAnychat().setInputVideoFormat(InputVideoFormatFactory.SOURCE_FORMAT_IMG_NV21);
        }

        //remoteSurface = findView(R.id.remoteSurface);
        room = findView(R.id.remoteId);
        remoteUser = findView(R.id.remoteId);

        rotateSupportBtn = findViewById(R.id.rotateSupport);

        redEditText = findViewById(R.id.color_red);
        greenEditText = findViewById(R.id.color_green);
        blueEditText = findViewById(R.id.color_blue);


        rotateSupportBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                rotateAngles += Key.ROTATE_90;
                if(rotateAngles > Key.ROTATE_270)
                {
                    rotateAngles = Key.ROTATE_0;
                }
            }
        });

        //????????????????????????????????????(xml????????????)
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.resolution_list, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        String s = room.getText().toString();
        if (StringUtils.isEmpty(s)) {
            roomId = 1;
        } else {
            roomId = Integer.parseInt(s.trim());
        }
        getControl().getAnychat().setAudioConfig();

        /*
        supported format: PixelFormat.RGBA_8888, ImageFormat.JPEG
        unsupported format: ImageFormat.NV21, ImageFormat.YUV_420_888
         */
        mCaptureWidth = getControl().getAnychat().phoneWidth;
        mCaptureHeight =  getControl().getAnychat().phoneHeight;
        imageReader = ImageReader.newInstance(mCaptureWidth, mCaptureHeight,
                PixelFormat.RGBA_8888, 1);

        //????????????????????????????????????(????????????)
        initScaleResolution();



        mSurface = imageReader.getSurface(); //??????ImageReader???surface

        bindFloatViewService();
    }

    private void bindFloatViewService() {
        // ?????????
        mFloatViewShowConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mFloatViewServiceBinder = (ScreenShareFloatView.FloatViewServiceBinder) service;
                mFloatViewServiceBinder.setVideoSpeakActivity(getActivity());
                mFloatViewServiceBinder.createFloatView();
                LogUtils.dTag(TAG, "??????????????????????????????-onServiceConnected-??????");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mFloatViewServiceBinder = null;
            }
        };
        // ?????????????????????service
        Intent floatViewService = new Intent(this, ScreenShareFloatView.class);
        bindService(floatViewService, mFloatViewShowConn, BIND_AUTO_CREATE);
    }

    ScreenShareFloatView.FloatViewServiceBinder mFloatViewServiceBinder;
    ServiceConnection mFloatViewShowConn;

    @Override
    protected void initListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int login = getControl().getAnychat().getAnyChatCoreSDK().Connect("h5vtest.cmbc.com.cn", 8031);
//                int login = getControl().getAnychat().getAnyChatCoreSDK().Connect("197.3.179.123", 8031);
//                int connect = getControl().getAnychat().getAnyChatCoreSDK().Login(UUID.randomUUID().toString(), "");
//                LogUtils.dTag(TAG, "loginCode = " + login + ",connect = " + connect);
            }
        });
        enterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int login = getControl().getAnychat().getAnyChatCoreSDK().Connect("h5vtest.cmbc.com.cn", 8031);
//                int login = getControl().getAnychat().getAnyChatCoreSDK().Connect("vtc.cmbc.com.cn", 8031);
//                int login = getControl().getAnychat().getAnyChatCoreSDK().Connect("197.3.179.123", 8031);
                int connect = getControl().getAnychat().getAnyChatCoreSDK().Login(UUID.randomUUID().toString(), "");
                LogUtils.dTag(TAG, "loginCode = " + login + ",connect = " + connect);

            }
        });
        openLocalCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localUserId >= 0) {
                    ToastUtils.showShort("????????????");
                    return;
                }
                getControl().getAnychat().setOrientationSensor();
                mCamera.startPreview();
                cameraPreviewState = true;
                getControl().getAnychat().getAnyChatCoreSDK().UserCameraControl(-1, 1);
            }
        });
        openLocalScreenShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!getControl().getAnychat().atRoomState){
//                    ToastUtils.showLong("??????????????????");
//                }
//                getControl().getAnychat().setInputVideoFormat(InputVideoFormatFactory.SOURCE_FORMAT_IMG_NV21);
//                getControl().getAnychat().setInputVideoFormat(InputVideoFormatFactory.SOURCE_FORMAT_VIDEO_H264);
                openScreenShare();
//                getControl().getAnychat().getAnyChatCoreSDK().UserCameraControl(-1, 1);
                screenShareState = true;
//
//                int code = getControl().getAnychat().setInputDataFormat();
//                LogUtils.dTag(TAG,"setInputDataFormat = "+code);
            }
        });
        openRemoteVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String s = remoteUser.getText().toString();
                if (!StringUtils.isEmpty(s)) {
                    remoteUserId = Integer.parseInt(s.trim());
                    if (remoteUserId > 0) {
                        remoteUserId = 0 - remoteUserId;
                    }
//                    int bindVideo = getControl().getAnychat().getAnyChatCoreSDK().mVideoHelper.bindVideo(remoteSurface.getHolder());
//                    getControl().getAnychat().getAnyChatCoreSDK().mVideoHelper.SetVideoUser(bindVideo, remoteUserId);
//                    int i = getControl().getAnychat().getAnyChatCoreSDK().UserCameraControl(remoteUserId, 1);
//                    LogUtils.dTag(TAG, "UserCameraControl = " + i + ",remoteUserId = " + remoteUserId);
                }
            }
        });
    }

    boolean screenShareState = false;

    private void openScreenShare() {
        getScreenRecord4ImageReader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getControl().getAnychat().writeAVLog("app??????");
        if (screenShareState) {
//            int code = getControl().getAnychat().setInputDataFormat();
//            LogUtils.dTag(TAG,"setInputDataFormat = "+code);
            sendVideoData.flag = false;
//            sendVideoData.whileState = false;
//            sendVideoData = new SendDataThread();
//            mExecutorService.execute(sendVideoData);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.w("TAG", "hello onActivityResult start--");
        if (requestCode == screen_imageRecord_code) {
            if (resultCode == RESULT_OK) {
                mediaProjection = projectionManager.getMediaProjection(resultCode, data);

                /*
                VirtualDisplay mVirtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror",
                        getControl().getAnychat().phoneWidth,
                        getControl().getAnychat().phoneHeight,
                        Resources.getSystem().getDisplayMetrics().densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(), null, null);

                 */
                VirtualDisplay mVirtualDisplay = mediaProjection.createVirtualDisplay("screen-mirror",
                        getControl().getAnychat().phoneWidth,
                        getControl().getAnychat().phoneHeight,
                        Resources.getSystem().getDisplayMetrics().densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mSurface, null, null);
                //????????????????????????
                mFloatViewServiceBinder.addView();
//                sendVideoData = new SendDataThread();
                mExecutorService.execute(sendVideoData);
            } else {
                new AlertDialog.Builder(ScreenShareActivity.this)
                        .setTitle("??????")
                        .setMessage("??????????????????????????????")
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getScreenRecord4ImageReader() {
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent intent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, screen_imageRecord_code);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appHasBack = true;
        getControl().getAnychat().writeAVLog("app??????");
        if (screenShareState) {
            sendVideoData.flag = false;
//            sendVideoData.whileState = false;
//            sendVideoData = new SendDataThread();
//            mExecutorService.execute(sendVideoData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendVideoData.whileState = false;
        mExecutorService.shutdown();
        unbindService(mFloatViewShowConn);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode== KeyEvent.KEYCODE_VOLUME_DOWN){
//            int code = getControl().getAnychat().setInputDataFormat();
//            LogUtils.dTag(TAG,"setInputDataFormat = "+code);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    File file = new File(path, "demo.jpg");
    FileOutputStream fileOutputStream;

    {
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    int test = 0;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object obj = parent.getItemAtPosition(position);
        mScaleWidth = Key.SCALE_WIDTH[position];
        mScaleHeight = Key.SCALE_HEIGHT[position];
        Log.e("TAG","hello onItemSelected position,obj=" +position + "," + obj);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class SendDataThread implements Runnable {
        public SendDataThread() {
            this.whileState = true;
        }

        byte[] lastData;
        long tmpBpStartTime;
        long tmpBpendTime;
        long beginTime;
        boolean flag = false;
        public volatile boolean whileState;
        Bitmap bitmap;
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        @Override
        public void run() {
            while (whileState) {
                Image image = imageReader.acquireLatestImage();

                if (image == null) {
                    if (bitmap != null/*lastData != null*/) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //screenImg.setImageBitmap(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                                //mFloatViewServiceBinder.updateView(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                                screenImg.setImageBitmap(bitmap);
                                mFloatViewServiceBinder.updateView(bitmap);
                            }
                        });
                    }
                } else {

                    final Image.Plane[] planes = image.getPlanes();
                    int width = image.getWidth();
                    int height = image.getHeight();
                    ByteBuffer imgBuffer = planes[0].getBuffer();

                    //???image????????????byte[]???
                    byte[] rgbaArray = new byte[imgBuffer.capacity()]; //4669440
                    imgBuffer.get(rgbaArray);  //???ByteBuffer????????????copy???byte[]
                    int pixelStride = planes[0].getPixelStride(); //???????????????????????????,?????????bytesPerPixel
                    int rowStride = planes[0].getRowStride();    //?????????????????????????????????,??????: width*bytesPerPixel
                    //Log.e("TAG", "hello pixelStride,rowStride=" + pixelStride + "," + rowStride);
                    int rowPadding = rowStride - pixelStride * width; //????????????????????????????????????????????????????????????????????????:??????byte
                    int aStride = width * 4 + rowPadding;
                    int aWidth = width + rowPadding / pixelStride;

                    /**
                     * ????????????:??????????????????????????????720*1520(RGB??????), ?????????????????????????????????1280*720(NV21??????)
                     * ???????????????????????????:
                     * 1.RGB???I420
                     * 2.I420??????(???UI?????????,??????????????????,???????????????90): ???????????????0???,?????????????????????,?????????????????????????????????;
                     *   ???????????????90??????270???,?????????????????????????????????,???720*1520??????1520*720
                     * 3.I420????????????(???UI????????????????????????): ??????????????????2?????????,?????????????????????????????????I420,?????????1280*720
                     * 4.???I420??????NV21
                     */
                    //1.rgba???I420
                    byte[] yuvbuffer = new byte[width * height * 3 / 2];
                    tmpBpStartTime = System.currentTimeMillis();
                    beginTime = System.currentTimeMillis();
                    ImageFormatUtils.rgbaToI420(rgbaArray, aStride, width, height, yuvbuffer, width, height); //10~15ms
                    tmpBpendTime = System.currentTimeMillis();
                    Log.e("TAG","hello rgbaToI420 ??????: " + (tmpBpendTime - tmpBpStartTime) + " ms");
                    lastData = yuvbuffer;
                    if(test == 0)
                    {
                        try {
                            ColorFormatTranslator.writeBytesToFile(lastData,"I420BeforeRotate.yuv");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //2.??????,0???????????????,?????????????????????????????????????????????
                    int rotateWidth = width;
                    int rotateHeight = height;
                    byte[] rotateData = new byte[width * height * 3/2];
                    tmpBpStartTime = System.currentTimeMillis();

                    ImageFormatUtils.rotateI420(yuvbuffer, width, height, rotateData, rotateAngles); //I420??????: 10ms
                    lastData = rotateData;
                    if(test == 0)
                    {
                        try {
                            ColorFormatTranslator.writeBytesToFile(lastData,"I420AfterRotate.yuv");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //???????????????90??????270????????????,???????????????????????????
                    if(rotateAngles == Key.ROTATE_90 || rotateAngles == Key.ROTATE_270)
                    {
                        width = rotateHeight;
                        height = rotateWidth;
                    }

                    tmpBpendTime = System.currentTimeMillis();
                    Log.e("TAG","hello rotateI420 ??????: " + (tmpBpendTime - tmpBpStartTime) + " ms");

                    lastData = rotateData;
//                    //??????
//                    byte[] mirrorData = new byte[yuvbuffer.length];
//                    ImageFormatUtils.i420Mirror(yuvbuffer, width, height, mirrorData);
//                    lastData = mirrorData;


                    //3.I420??????,?????????????????????
                    int scaleWidth = mScaleWidth;
                    int scaleHeight = mScaleHeight;
                    if(scaleWidth != mCaptureWidth || scaleHeight != mCaptureHeight)
                    {
                        byte[] scaledData = new byte[scaleWidth * scaleHeight * 3/2];
                        tmpBpStartTime = System.currentTimeMillis();
                        FreshColor();
                        int rgbColor = Color.argb(128,mRed,mGreen,mBlue);
                        ImageFormatUtils.scaleI420(lastData, width, height, scaledData, scaleWidth, scaleHeight, rgbColor); //15~25ms with res 640*480
                        tmpBpendTime = System.currentTimeMillis();
                        lastData = scaledData;
                        width = scaleWidth;
                        height = scaleHeight;
                        Log.e("TAG","hello scaleI420 ??????: " + (tmpBpendTime - tmpBpStartTime) + " ms");
                    }
                    if(test == 0)
                    {
                        try {
                            ColorFormatTranslator.writeBytesToFile(lastData,"I420Scale.yuv");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    test++;
                    //4.I420???NV21
                    byte[] nv21Data = new byte[width * height * 3/2];
                    tmpBpStartTime = System.currentTimeMillis();
                    ImageFormatUtils.i420ToNV21(lastData, width, height, nv21Data);
                    tmpBpendTime = System.currentTimeMillis();
                    Log.e("TAG","hello i420ToNV21 ??????: " + (tmpBpendTime - tmpBpStartTime) + " ms");
                    lastData = nv21Data;

                    Log.e("TAG","\n\n");
                    Log.e("TAG","hello ?????????: " + (System.currentTimeMillis() - beginTime) + " ms");
                    bitmap = ImageUtils.nv21ToBitmap(lastData, width, height, getActivity());
                    //bitmap = ImageUtils.convertNV21ToBitmap(lastData, width, height, getActivity());
                    /****************************** BEGIN  rgbaToNV21********************************/
//                    tmpBpStartTime = System.currentTimeMillis();
//                    ImageFormatUtils.rgbaToNV21(rgbaArray,aWidth,height,yuvbuffer,width,height);
//                    tmpBpendTime = System.currentTimeMillis();
//                    Log.e("TAG","hello rgbaToNV21 consume " + (tmpBpendTime - tmpBpStartTime)); //15~20ms
//
//                    lastData = yuvbuffer;
//                    bitmap = ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity());
                    /******************************END  rgbaToNV21********************************/

                    /*
                    ???????????? ARGB ???????????????yuv??????,???????????????:
                    **1.??????java????????????,??????I420,????????????????????????,???????????????,???60ms
                    **2.??????JNI??????????????????ARGB??????NV21,??????libyuv???,????????????,???20ms
                    **I420??????yuv420P,?????????YY...UU...VV..., NV21??????yuv420SP,?????????YY??? VUVU???
                     */
                    //1.??????java???ARGB ?????? I420
                    /*---------------------------- BEGIN rgbToI420 --------------------*/
//                    tmpBpStartTime = System.currentTimeMillis();
//                    ColorFormatTranslator.rgbToI420(rgbaArray,aWidth,height,yuvbuffer);
//                    tmpBpendTime = System.currentTimeMillis();
//                    //???I420??????bitmap,???????????????view???.
//                    byte[] rgbaData = new byte[aWidth * height * 4];
//                    ImageFormatUtils.I420ToRgba(Key.I420_TO_RGBA,yuvbuffer,rgbaData,aWidth, height);
//                    bitmap = Bitmap.createBitmap(aWidth,height, Bitmap.Config.ARGB_8888);
//                    ByteBuffer buffer = ByteBuffer.allocate(aWidth*height*4);
//                    buffer.put(rgbaData);
//                    buffer.rewind();
//                    bitmap.copyPixelsFromBuffer(buffer);
//                    Log.e("TAG","hello rgbToI420 consume " + (tmpBpendTime-tmpBpStartTime));
                    /*---------------------------- END rgbToI420 --------------------*/

                    //2.??????libyuv??????ARGB ??? NV21 ??????,????????????;NV21???YUV420SP?????????,?????????YYY...VUVU...
                    /*----------------------------- BEGIN ARGBToNV21-------------------------------*/
//                    tmpBpStartTime = System.currentTimeMillis();
//                    ImageFormatUtils.RGBAToNV21(rgbaArray,aStride, width, height, yuvbuffer); //????????????
//                    tmpBpendTime = System.currentTimeMillis();
//                    Log.e("TAG","hello ARGBToNV21 consume " + (tmpBpendTime-tmpBpStartTime));
//
//                    bitmap = ImageUtils.nv21ToBitmap(lastData,dstWidth,dstHeight,getActivity());
                    /*----------------------------- END ARGBToNV21-------------------------------*/


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //screenImg.setImageBitmap(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                            //mFloatViewServiceBinder.updateView(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                            screenImg.setImageBitmap(bitmap);
                            mFloatViewServiceBinder.updateView(bitmap);
                        }
                    });
//                    if (lastData != null) {
//                        int i = getControl().getAnychat().inputVideoData(lastData);
//                        if (!flag){
//                            getControl().getAnychat().setInputDataFormat();
//                            flag = true;
//                        }
//                    }
                    image.close();
                }
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    class SendDataThread1 extends SendDataThread {
        public SendDataThread1() {
            this.whileState = true;
        }

        byte[] lastData;
        Bitmap sendBitmap;
        boolean flag = false;
        public volatile boolean whileState;

        @Override
        public void run() {
            while (whileState) {
                Image image = imageReader.acquireLatestImage();
                if (image == null) {
                    if (lastData != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                screenImg.setImageBitmap(ImageUtils.nv21ToBitmap(lastData, getControl().getAnychat().videoWidth, getControl().getAnychat().videoHeight, getActivity()));
                                mFloatViewServiceBinder.updateView(ImageUtils.nv21ToBitmap(lastData, getControl().getAnychat().videoWidth, getControl().getAnychat().videoHeight, getActivity()));
                            }
                        });
                        int i = getControl().getAnychat().inputVideoData(lastData);
                        if (!flag) {
                            getControl().getAnychat().setInputDataFormat();
                            flag = true;
                        }
                    }
                } else {
                    int w = image.getWidth(), h = image.getHeight();
                    // size???????????????1.5??? ????????????ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)??????
                    int i420Size = w * h * 3 / 2;

                    Image.Plane[] planes = image.getPlanes();
                    //remaining0 = rowStride*(h-1)+w => 27632= 192*143+176 Y??????byte?????????size
                    int remaining0 = planes[0].getBuffer().remaining();
                    int remaining1 = planes[1].getBuffer().remaining();
                    //remaining2 = rowStride*(h/2-1)+w-1 =>  13807=  192*71+176-1 V??????byte?????????size
                    int remaining2 = planes[2].getBuffer().remaining();
                    //??????pixelStride????????????width????????????????????????
                    int pixelStride = planes[2].getPixelStride();
                    int rowOffest = planes[2].getRowStride();
                    byte[] nv21 = new byte[i420Size];
                    //??????????????????????????????YUV?????????
                    byte[] yRawSrcBytes = new byte[remaining0];
                    byte[] uRawSrcBytes = new byte[remaining1];
                    byte[] vRawSrcBytes = new byte[remaining2];
                    planes[0].getBuffer().get(yRawSrcBytes);
                    planes[1].getBuffer().get(uRawSrcBytes);
                    planes[2].getBuffer().get(vRawSrcBytes);
                    //?????????????????????size?????????byte??????
                    byte[] ySrcBytes = new byte[w * h];
                    byte[] uSrcBytes = new byte[w * h / 2 - 1];
                    byte[] vSrcBytes = new byte[w * h / 2 - 1];
                    for (int row = 0; row < h; row++) {
                        //??????????????? rowOffest ???bytes ?????? w ???bytes???????????????
                        System.arraycopy(yRawSrcBytes, rowOffest * row, ySrcBytes, w * row, w);
                        //y???????????????uv????????????
                        if (row % 2 == 0) {
                            //????????????????????????
                            if (row == h - 2) {
                                System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w - 1);
                            } else {
                                System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w);
                            }
                        }
                    }
                    //yuv???????????????????????????
                    System.arraycopy(ySrcBytes, 0, nv21, 0, w * h);
                    System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1);

//                    byte[] frameBuffer = YUVUtils.getNV21Byte(sendBitmap);

//************************************************

//                    tmpBpStartTime = System.currentTimeMillis();
//                    lastData = ImageUtils.fetchNV21(sendBitmap);
//                    tmpBpendTime = System.currentTimeMillis();
//                    LogUtils.dTag(TAG,"java time = "+(tmpBpendTime-tmpBpStartTime));

                    lastData = nv21;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            screenImg.setImageBitmap(ImageUtils.nv21ToBitmap(lastData, getControl().getAnychat().videoWidth, getControl().getAnychat().videoHeight, getActivity()));
                            mFloatViewServiceBinder.updateView(ImageUtils.nv21ToBitmap(lastData, getControl().getAnychat().videoWidth, getControl().getAnychat().videoHeight, getActivity()));
                        }
                    });
                    if (lastData != null) {
                        int i = getControl().getAnychat().inputVideoData(lastData);
                        if (!flag) {
                            getControl().getAnychat().setInputDataFormat();
                            flag = true;
                        }
                    }
                    image.close();
                }
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class ServerSocketListen implements Runnable {

        volatile boolean flag = true;

        @Override
        public void run() {
            while (flag) {
                try {
                    Socket accept = mServerSocket.accept();
                    BufferedReader bufrin =
                            new BufferedReader(new InputStreamReader(accept.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

}
