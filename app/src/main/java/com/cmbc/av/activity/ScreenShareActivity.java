package com.cmbc.av.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.cmbc.av.base.AbsActivity;
import com.cmbc.av.bean.InputVideoFormatFactory;
import com.cmbc.av.jni.ImageFormatUtils;
import com.cmbc.av.service.ScreenShareFloatView;
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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xyz.djytest.screenshared.R;

//import android.support.annotation.RequiresApi;

public class ScreenShareActivity extends AbsActivity<ScreenShareControl, ScreenShareActivity, ScreenShareModel, ScreenShareAnychat> {
    static {
        System.loadLibrary("CImageUtil");
        System.loadLibrary("yuv");
    }

    final int screen_imageRecord_code = 112;
    public int localUserId, remoteUserId, roomId;
    Button login, enterRoom, openRemoteVideo, openLocalScreenShare, openLocalCamera, openLocalScreenShareMediaRecord;
    //    SurfaceView localSurface, remoteSurface;
    EditText room, remoteUser;
    Camera mCamera;
    boolean cameraPreviewState = false;
    MediaProjectionManager projectionManager;
    ImageReader imageReader;
    private Surface mSurface; //用来显示录制的屏幕的图像
    MediaProjection mediaProjection;
    TextView localUser;
    boolean appHasBack = false;
    public ImageView screenImg;
    public MediaRecorder mMediaRecorder;
    public ServerSocket mServerSocket;

    final SendDataThread sendVideoData = new SendDataThread();

    ExecutorService mExecutorService = Executors.newSingleThreadExecutor();


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
        openLocalScreenShareMediaRecord = findView(R.id.openLocalScreenShareMediaRecord);
        openLocalScreenShareMediaRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mServerSocket = new ServerSocket(9999);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
//                mServerSocket.
//                mMediaRecorder = new MediaRecorder();
//                mMediaRecorder.
            }
        });
        screenImg = findView(R.id.screenImg);
        openLocalCamera = findView(R.id.openLocalCamera);
        openLocalScreenShare = findView(R.id.openLocalScreenShare);
        localUser = findView(R.id.localUserId);
        enterRoom = findView(R.id.enterRoom);
        openRemoteVideo = findView(R.id.openRemoteVideo);
        //localSurface = findView(R.id.localSurface);
//        localSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //   localSurface.setZOrderOnTop(true);
        if (openLocalCamera.getVisibility() == View.VISIBLE) {
            getControl().getAnychat().setInputVideoFormat(InputVideoFormatFactory.SOURCE_FORMAT_IMG_NV21);
        }
//        localSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
//
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//
//            }
//
//            public int setCameraDisplayOrientation() {
//                try {
//                    int result;
//                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//                    Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
//                    int degrees = 0;
//
//                    switch (((WindowManager) getSystemService("window")).getDefaultDisplay().getRotation()) {
//                        case 0:
//                            degrees = 0;
//                            break;
//                        case 1:
//                            degrees = 90;
//                            break;
//                        case 2:
//                            degrees = 180;
//                            break;
//                        case 3:
//                            degrees = 270;
//                            break;
//                    }
//                    if (cameraInfo.facing == 1) {
//                        result = (360 - ((cameraInfo.orientation + degrees) % 360)) % 360;
//                    } else {
//                        result = ((cameraInfo.orientation - degrees) + 360) % 360;
//                    }
//                    return result;
//                } catch (Exception e) {
//                }
//                return 0;
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                if (mCamera == null) {
//                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//                }
//                LogUtils.dTag(TAG, "surfaceChanged");
//                try {
//                    mCamera.setPreviewDisplay(holder);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Camera.Parameters parameters = mCamera.getParameters();
//                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//                final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
//                Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
//                parameters.setPreviewFormat(getControl().getAnychat().getSourceFormat());
//                int i;
//                Camera.Size s;
//                List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
//                Collections.sort(previewSizes, new Comparator<Camera.Size>() {
//                    @Override
//                    public int compare(Camera.Size lhs, Camera.Size rhs) {
//                        if (lhs.width == rhs.width) {
//                            if (lhs.height == rhs.height) {
//                                return 0;
//                            }
//                            if (lhs.height <= rhs.height) {
//                                return -1;
//                            }
//                            return 1;
//                        } else if (lhs.width <= rhs.width) {
//                            return -1;
//                        } else {
//                            return 1;
//                        }
//                    }
//                });
//                int iSettingsWidth = 640;
//                int iSettingsHeight = 480;
//                boolean bSetPreviewSize = false;
//                boolean z = false;
//                if (previewSizes.size() == 1) {
//                    bSetPreviewSize = true;
//                    parameters.setPreviewSize(((Camera.Size) previewSizes.get(0)).width, ((Camera.Size) previewSizes.get(0)).height);
//                } else {
//                    i = 0;
//                    while (i < previewSizes.size()) {
//                        try {
//                            s = (Camera.Size) previewSizes.get(i);
//                            if (s.width == iSettingsWidth && s.height == iSettingsHeight) {
//                                bSetPreviewSize = true;
//                                previewSize.height = iSettingsHeight;
//                                previewSize.width = iSettingsWidth;
//                                parameters.setPreviewSize(iSettingsWidth, iSettingsHeight);
//                                break;
//                            }
//                            if (s.width == 320 && s.height == 240) {
//                                z = true;
//                            }
//                            i++;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            return;
//                        }
//                    }
//                    if (!bSetPreviewSize) {
//                        for (i = 0; i < previewSizes.size(); i++) {
//                            s = (Camera.Size) previewSizes.get(i);
//                            if (s.width >= iSettingsWidth || s.height >= iSettingsHeight) {
//                                bSetPreviewSize = true;
//                                previewSize.height = s.height;
//                                previewSize.width = s.width;
//                                parameters.setPreviewSize(s.width, s.height);
//                                break;
//                            }
//                        }
//                    }
//                }
//                if (!bSetPreviewSize) {
//                    if (z) {
//                        parameters.setPreviewSize(320, 240);
//                    } else if (previewSizes.size() > 0) {
//                        s = (Camera.Size) previewSizes.get(0);
//                        parameters.setPreviewSize(s.width, s.height);
//                    }
//                }
//                List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
//                for (i = 0; i < fpsRange.size(); i++) {
//                    int[] r = (int[]) fpsRange.get(i);
//                    if (r[0] >= 25000 && r[1] >= 25000) {
//                        LogUtils.dTag(TAG, "setPreviewFpsRange  = " + r[0] + "  " + r[1]);
//                        parameters.setPreviewFpsRange(r[0], r[1]);//设置fps  ， 我们理解的fps需要这个值 / 1000
//                        break;
//                    }
//                }
//
//                final int[] iCurPreviewRange = new int[2];
//                parameters.getPreviewFpsRange(iCurPreviewRange);
//                int orientation = setCameraDisplayOrientation();
//                mCamera.setDisplayOrientation(orientation);
//                mCamera.setParameters(parameters);
//                final int bufSize = ((previewSize.width * previewSize.height) * ImageFormat.getBitsPerPixel(17)) / 8;
//                mCamera.addCallbackBuffer(new byte[bufSize]);
//                mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//
//                    private int getImgO(int devicesO) {
//                        switch (devicesO) {
//                            case 0:
//                            case 360:
//                                return 270;
//                            case 90:
//                                return 180;
//                            case 180:
//                                return 90;
//                            case 270:
//                                return 0;
//                        }
//                        return 0;
//                    }
//
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    @Override
//                    public void onPreviewFrame(byte[] data, Camera camera) {
//                        success1(data, camera);
//                    }
//                    boolean flag = false;
//                    private void success1(byte[] data, Camera camera) {
//                        camera.addCallbackBuffer(data);
//                        byte[] cc = new byte[bufSize];
//                        cc = Arrays.copyOf(data, data.length);
//                        getControl().getAnychat().inputVideoData(cc);
//                        if (!flag){
//                            flag = true;
//                            getControl().getAnychat().setInputDataFormat();
//                        }
//                    }
//                });
//                AnyChatCoreSDK.SetSDKOptionInt(100, cameraInfo.facing);
//                if (cameraPreviewState) {
//                    mCamera.startPreview();
//                }
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
////                mCamera.stopPreview();
////                mCamera.release();
////                mCamera = null;
//            }
//        });
        //remoteSurface = findView(R.id.remoteSurface);
        room = findView(R.id.remoteId);
        remoteUser = findView(R.id.remoteId);
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
        imageReader = ImageReader.newInstance(getControl().getAnychat().phoneWidth,
                getControl().getAnychat().phoneHeight,
                PixelFormat.RGBA_8888, 1);


        mSurface = imageReader.getSurface(); //使用ImageReader的surface
//        imageReader = ImageReader.newInstance(
//                getControl().getAnychat().phoneWidth,
//               getControl().getAnychat().phoneHeight,
//                ImageFormat.YUV_420_888,
//                5
//        );
        bindFloatViewService();
    }

    private void bindFloatViewService() {
        // 悬浮窗
        mFloatViewShowConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mFloatViewServiceBinder = (ScreenShareFloatView.FloatViewServiceBinder) service;
                mFloatViewServiceBinder.setVideoSpeakActivity(getActivity());
                mFloatViewServiceBinder.createFloatView();
                LogUtils.dTag(TAG, "绑定状态栏悬浮窗服务-onServiceConnected-成功");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mFloatViewServiceBinder = null;
            }
        };
        // 绑定启动悬浮窗service
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
                    ToastUtils.showShort("没有登录");
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
//                    ToastUtils.showLong("没有进入房间");
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
        getControl().getAnychat().writeAVLog("app前台");
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
                //子线程定时拿数据
                mFloatViewServiceBinder.addView();
//                sendVideoData = new SendDataThread();
                mExecutorService.execute(sendVideoData);
            } else {
                new AlertDialog.Builder(ScreenShareActivity.this)
                        .setTitle("警告")
                        .setMessage("必须开启视频输出权限")
                        .setNegativeButton("确认", new DialogInterface.OnClickListener() {
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
        getControl().getAnychat().writeAVLog("app后台");
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

    class SendDataThread implements Runnable {
        public SendDataThread() {
            this.whileState = true;
        }

        byte[] lastData;
        Bitmap sendBitmap;
        boolean flag = false;
        public volatile boolean whileState;
        long beginTime = 0;
        long endTime = 0;
        Bitmap bitmap;

        @Override
        public void run() {
            while (whileState) {
                beginTime = System.currentTimeMillis();
                Image image = imageReader.acquireLatestImage();

                if (image == null) {
                    if (bitmap != null/*lastData != null*/) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                screenImg.setImageBitmap(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                                mFloatViewServiceBinder.updateView(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                                //screenImg.setImageBitmap(bitmap);
                                //mFloatViewServiceBinder.updateView(bitmap);
                            }
                        });
//                        int i = getControl().getAnychat().inputVideoData(lastData);
//                        if (!flag){
//                            getControl().getAnychat().setInputDataFormat();
//                            flag = true;
//                        }
                    }
                } else {

                    long tmpBpStartTime = System.currentTimeMillis();
                    long tmpBpendTime;
                    final Image.Plane[] planes = image.getPlanes();
                    int width = image.getWidth();
                    int height = image.getHeight();
                    ByteBuffer imgBuffer = planes[0].getBuffer();

                    //将image数据放到byte[]中
                    byte[] rgbaArray = new byte[imgBuffer.capacity()];
                    imgBuffer.get(rgbaArray);  //将ByteBuffer中的数据copy到byte[]
                    int pixelStride = planes[0].getPixelStride(); //相邻像素之间的间隔,大小为bytesPerPixel
                    int rowStride = planes[0].getRowStride();    //两行开始像素之间的间隔,大小: width*bytesPerPixel
                    Log.e("TAG", "hello pixelStride,rowStride=" + pixelStride + "," + rowStride);
                    int rowPadding = rowStride - pixelStride * width; //当前行的最后一个像素到下一行的第一个像素的填充量:单位byte
                    int aStride = width * 4 + rowPadding;
                    int aWidth = width + rowPadding / pixelStride;


                    //下面是生成bitmap的代码,将废弃
                    /*
                    Bitmap tmp = Bitmap.createBitmap(aWidth, height, Bitmap.Config.ARGB_8888);
                    Log.e("TAG", "hello bimap width,height=" + tmp.getWidth() + "," + tmp.getHeight());

                    tmp.copyPixelsFromBuffer(imgBuffer);
                    //从Image拿到bitmap
                    tmp = Bitmap.createBitmap(tmp, 0, 0, aWidth, height);

                    //bitmap等比例压缩并添加底色,no longer needed
                    //sendBitmap = ImageUtils.zoomBitmap4Color(Color.WHITE, tmp, getControl().getAnychat().videoWidth, getControl().getAnychat().videoHeight);
                    long tmpBpendTime = System.currentTimeMillis();
                    Log.e(TAG, "bitmap takes time = " + (tmpBpendTime- tmpBpStartTime));//takes time 20 ms
                     */


                    //将bitmap保存成文件,仅用于测试

                    if (0 == 1) {
                        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String fileName = "/original_bitmap.jpg";
                        String savePath = dir.concat(fileName);
                        try {
                            Log.e("TAG", "hello save bitmap start=");
                            File file = new File(savePath);
                            FileOutputStream out = new FileOutputStream(file);
                            //tmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            Log.e("TAG", "hello save bitmap end=");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    test++;

                    //将bitmap rgb转成yuv,该做法不够简洁,将废弃
                    //ByteBuffer buffer = ByteBuffer.allocate(tmp.getByteCount());
                    //tmp.copyPixelsToBuffer(buffer);
                    //byte[] argbArray = buffer.array();

                    tmpBpStartTime = System.currentTimeMillis();

                    //2.Y分量和UV分量
                    byte[]  ybuffer = new byte[aWidth * height];//用于保存y分量数据
                    byte[]  uvbuffer=new byte[width * height/2];//用于保存uv分量数据

                    /*
                    //bitmap is no needed anymore because we use the byte from ImageReader directly
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bg);
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();
                    ByteBuffer buffer = ByteBuffer.allocate(bitmap.getWidth()*bitmap.getHeight()*4);
                    bitmap.copyPixelsToBuffer(buffer);
                     */

                    Log.e("TAG","junhong ARGBToNV21 begin");
                    tmpBpStartTime = System.currentTimeMillis();
                    //使用libyuv库，ARGB转NV21 格式,颜色异常;NV21是YUV420SP的一种,排列是YYY...VUVU...
                    ImageFormatUtils.ARGBToNV21(rgbaArray,aStride, width, height, ybuffer,uvbuffer); //色差fixed
                    tmpBpendTime = System.currentTimeMillis();
                    Log.e("TAG","--junhong ARGBToNV21 take time: " + (tmpBpendTime- tmpBpStartTime));

                    //ImageFormatUtils.RgbaToI420(Key.RGBA_TO_I420, buffer.array(), yuvData,w, h);
                    //ImageFormatUtils.RgbaToI420(Key.RGBA_TO_I420, imgBuffer.array(), yuvData,width, tmp.getHeight());
                    Log.e("TAG","hello ARGBToNV21 end");


                    byte[] frameBuffer = new byte[aWidth * height * 3/2];
                    System.arraycopy(ybuffer,0,frameBuffer,0,width * height);
                    System.arraycopy(uvbuffer,0,frameBuffer,width * height,width * height/2);

                    lastData = frameBuffer;

                    //将yuv写到文件中,仅用于测试.可删除
                    try {
                        writeBytesToFile(lastData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            screenImg.setImageBitmap(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                            mFloatViewServiceBinder.updateView(ImageUtils.nv21ToBitmap(lastData,getControl().getAnychat().videoWidth,getControl().getAnychat().videoHeight,getActivity()));
                            //screenImg.setImageBitmap(bitmap);
                            //mFloatViewServiceBinder.updateView(bitmap);
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
                    // size是宽乘高的1.5倍 可以通过ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)得到
                    int i420Size = w * h * 3 / 2;

                    Image.Plane[] planes = image.getPlanes();
                    //remaining0 = rowStride*(h-1)+w => 27632= 192*143+176 Y分量byte数组的size
                    int remaining0 = planes[0].getBuffer().remaining();
                    int remaining1 = planes[1].getBuffer().remaining();
                    //remaining2 = rowStride*(h/2-1)+w-1 =>  13807=  192*71+176-1 V分量byte数组的size
                    int remaining2 = planes[2].getBuffer().remaining();
                    //获取pixelStride，可能跟width相等，可能不相等
                    int pixelStride = planes[2].getPixelStride();
                    int rowOffest = planes[2].getRowStride();
                    byte[] nv21 = new byte[i420Size];
                    //分别准备三个数组接收YUV分量。
                    byte[] yRawSrcBytes = new byte[remaining0];
                    byte[] uRawSrcBytes = new byte[remaining1];
                    byte[] vRawSrcBytes = new byte[remaining2];
                    planes[0].getBuffer().get(yRawSrcBytes);
                    planes[1].getBuffer().get(uRawSrcBytes);
                    planes[2].getBuffer().get(vRawSrcBytes);
                    //根据每个分量的size先生成byte数组
                    byte[] ySrcBytes = new byte[w * h];
                    byte[] uSrcBytes = new byte[w * h / 2 - 1];
                    byte[] vSrcBytes = new byte[w * h / 2 - 1];
                    for (int row = 0; row < h; row++) {
                        //源数组每隔 rowOffest 个bytes 拷贝 w 个bytes到目标数组
                        System.arraycopy(yRawSrcBytes, rowOffest * row, ySrcBytes, w * row, w);
                        //y执行两次，uv执行一次
                        if (row % 2 == 0) {
                            //最后一行需要减一
                            if (row == h - 2) {
                                System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w - 1);
                            } else {
                                System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w);
                            }
                        }
                    }
                    //yuv拷贝到一个数组里面
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

    int index = 0;

    public void writeBytesToFile(byte[] bs) throws IOException {
        if (index != 0) {
            return;
        }
        Log.e("TAG","hello bs length=" + bs.length);
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "/nv21image.yuv";
        String savePath = dir.concat(fileName);
        OutputStream out = new FileOutputStream(savePath);
        Log.e("TAG","junhong write yuv file savePath=" + savePath);
        InputStream is = new ByteArrayInputStream(bs);
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        is.close();
        out.close();
        Log.e("TAG","junhong writing file finished");
        index++;
    }


    public void rgbToYuv(byte[] rgba, int width, int height, byte[] yuv) {

        final int frameSize = width * height;
        int yIndex = 0;
        int uIndex = frameSize;
        int vIndex = frameSize + frameSize / 4;

        int R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                index = j * width + i;
                if (rgba[index * 4] > 127 || rgba[index * 4] < -128) {
                    Log.e("color", "-->" + rgba[index * 4]);
                }
                R = rgba[index * 4] & 0xFF;
                G = rgba[index * 4 + 1] & 0xFF;
                B = rgba[index * 4 + 2] & 0xFF;

                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv[uIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                    yuv[vIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                }
            }
        }
    }


    /*
RGB to yuv method 2
 */
    static public void encodeYUV420SP(byte[] yuv420sp, int[] rgba,
                                      int width, int height) {
        final int frameSize = width * height;

        int[] U, V;
        U = new int[frameSize];
        V = new int[frameSize];

        final int uvwidth = width / 2;

        int r, g, b, y, u, v;
        for (int j = 0; j < height; j++) {
            int index = width * j;
            for (int i = 0; i < width; i++) {

                r = Color.red(rgba[index]);
                g = Color.green(rgba[index]);
                b = Color.blue(rgba[index]);

                // rgb to yuv
                y = (66 * r + 129 * g + 25 * b + 128) >> 8 + 16;
                u = (-38 * r - 74 * g + 112 * b + 128) >> 8 + 128;
                v = (112 * r - 94 * g - 18 * b + 128) >> 8 + 128;

                // clip y
                yuv420sp[index] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
                U[index] = u;
                V[index++] = v;
            }
        }
    }

}






