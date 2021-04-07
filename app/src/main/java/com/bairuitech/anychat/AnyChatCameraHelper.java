package com.bairuitech.anychat;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager;

import com.cmbc.av.utils.LogUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnyChatCameraHelper implements Callback {
    private static final String TAG = "ANYCHAT";
    public final int CAMERA_FACING_BACK = 0;
    public final int CAMERA_FACING_FRONT = 1;
    private boolean bIfPreview = false;
    private boolean bNeedCapture = false;
    private SurfaceHolder currentHolder = null;
    private final int iCaptureBuffers = 3;
    private int iCurrentCameraId = 0;
    private Camera mCamera = null;
    private int mCameraFacing = 0;
    private int mCameraOrientation = 0;
    private Context mContext = null;
    private int mDeviceOrientation = 0;
    private int mVideoPixfmt = -1;

    public boolean getCameraSuccess() {
        return mCamera != null;
    }

    class CameraSizeComparator implements Comparator<Size> {
        CameraSizeComparator() {
        }

        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                if (lhs.height == rhs.height) {
                    return 0;
                }
                if (lhs.height <= rhs.height) {
                    return -1;
                }
                return 1;
            } else if (lhs.width <= rhs.width) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public void SetContext(Context ctx) {
        this.mContext = ctx;
    }

    private void initCamera() {
        if (this.mCamera != null) {
            int i;
            Size s;
            if (this.bIfPreview) {
                this.mCamera.stopPreview();
                this.mCamera.setPreviewCallbackWithBuffer(null);
            }
            CameraInfo cameraInfo = new CameraInfo();
            Camera.getCameraInfo(this.iCurrentCameraId, cameraInfo);
            this.mCameraOrientation = cameraInfo.orientation;
            this.mCameraFacing = cameraInfo.facing;
            this.mDeviceOrientation = getDeviceOrientation();
            LogUtils.dTag(TAG, "allocate: device orientation=" + this.mDeviceOrientation + ", camera orientation=" + this.mCameraOrientation + ", facing=" + this.mCameraFacing);
            setCameraDisplayOrientation();
            Parameters parameters = this.mCamera.getParameters();
            List<Size> previewSizes = this.mCamera.getParameters().getSupportedPreviewSizes();
            Collections.sort(previewSizes, new CameraSizeComparator());
            int iSettingsWidth = AnyChatCoreSDK.GetSDKOptionInt(38);
            int iSettingsHeight = AnyChatCoreSDK.GetSDKOptionInt(39);
            boolean bSetPreviewSize = false;
            boolean z = false;
            if (previewSizes.size() == 1) {
                bSetPreviewSize = true;
                parameters.setPreviewSize(((Size) previewSizes.get(0)).width, ((Size) previewSizes.get(0)).height);
            } else {
                i = 0;
                while (i < previewSizes.size()) {
                    try {
                        s = (Size) previewSizes.get(i);
                        if (s.width == iSettingsWidth && s.height == iSettingsHeight) {
                            bSetPreviewSize = true;
                            parameters.setPreviewSize(iSettingsWidth, iSettingsHeight);
                            break;
                        }
                        if (s.width == 320 && s.height == 240) {
                            z = true;
                        }
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if (!bSetPreviewSize) {
                    for (i = 0; i < previewSizes.size(); i++) {
                        s = (Size) previewSizes.get(i);
                        if (s.width >= iSettingsWidth || s.height >= iSettingsHeight) {
                            bSetPreviewSize = true;
                            parameters.setPreviewSize(s.width, s.height);
                            break;
                        }
                    }
                }
            }
            if (!bSetPreviewSize) {
                if (z) {
                    parameters.setPreviewSize(320, 240);
                } else if (previewSizes.size() > 0) {
                    s = (Size) previewSizes.get(0);
                    parameters.setPreviewSize(s.width, s.height);
                }
            }
            List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
            for (i = 0; i < fpsRange.size(); i++) {
                int[] r = (int[]) fpsRange.get(i);
                AnyChatCoreSDK.SetSDKOptionString(AnyChatDefine.BRAC_SO_CORESDK_WRITELOG, "Camera FrameRate: " + r[0] + " , " + r[1]);
                if (r[0] >= 25000 && r[1] >= 25000) {
                    parameters.setPreviewFpsRange(r[0], r[1]);
                    break;
                }
            }
            parameters.setPreviewFormat(17);
            try {
                this.mCamera.setParameters(parameters);
            } catch (Exception e2) {
            }
            Size captureSize = this.mCamera.getParameters().getPreviewSize();
            int bufSize = ((captureSize.width * captureSize.height) * ImageFormat.getBitsPerPixel(17)) / 8;
            for (i = 0; i < 3; i++) {
                this.mCamera.addCallbackBuffer(new byte[bufSize]);
            }
            this.mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (data.length != 0 && AnyChatCameraHelper.this.bNeedCapture) {
                        try {
                            AnyChatCoreSDK.InputVideoData(data, data.length, 0);
                        } catch (Exception e) {
                        }
                    }
                    AnyChatCameraHelper.this.mCamera.addCallbackBuffer(data);
                }
            });
            this.mCamera.startPreview();
            this.bIfPreview = true;
            if (this.mCamera.getParameters().getPreviewFormat() == 17) {
                this.mVideoPixfmt = 8;
            } else if (this.mCamera.getParameters().getPreviewFormat() == 842094169) {
                this.mVideoPixfmt = 2;
            } else if (this.mCamera.getParameters().getPreviewFormat() == 16) {
                this.mVideoPixfmt = 9;
            } else if (this.mCamera.getParameters().getPreviewFormat() == 20) {
                this.mVideoPixfmt = 3;
            } else if (this.mCamera.getParameters().getPreviewFormat() == 4) {
                this.mVideoPixfmt = 5;
            } else {
                LogUtils.eTag(TAG, "unknow camera privew format:" + this.mCamera.getParameters().getPreviewFormat());
            }
            Size previewSize = this.mCamera.getParameters().getPreviewSize();
            AnyChatCoreSDK.SetSDKOptionInt(26, 1);
            int[] iCurPreviewRange = new int[2];
            parameters.getPreviewFpsRange(iCurPreviewRange);
            AnyChatCoreSDK.SetInputVideoFormat(this.mVideoPixfmt, previewSize.width, previewSize.height, iCurPreviewRange[1] / 1000, 0);
            AnyChatCoreSDK.SetSDKOptionInt(100, cameraInfo.facing);
        }
    }

    public void CaptureControl(boolean bCapture) {
        this.bNeedCapture = bCapture;
        if (!this.bNeedCapture || this.mVideoPixfmt == -1) {
            AnyChatCoreSDK.SetSDKOptionInt(26, 0);
            return;
        }
        try {
            Size previewSize = this.mCamera.getParameters().getPreviewSize();
            AnyChatCoreSDK.SetSDKOptionInt(26, 1);
            AnyChatCoreSDK.SetInputVideoFormat(this.mVideoPixfmt, previewSize.width, previewSize.height, this.mCamera.getParameters().getPreviewFrameRate(), 0);
            AnyChatCoreSDK.SetSDKOptionInt(100, this.mCameraFacing);
        } catch (Exception e) {
        }
    }

    public void CloseCamera() {
        try {
            if (this.mCamera != null) {
                this.mCamera.stopPreview();
                this.mCamera.setPreviewCallbackWithBuffer(null);
                this.bIfPreview = false;
                this.mVideoPixfmt = -1;
                this.mCamera.release();
                this.mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    public int GetCameraNumber() {
        try {
            return Camera.getNumberOfCameras();
        } catch (Exception e) {
            return 0;
        }
    }

    public void CameraAutoFocus() {
        if (this.mCamera != null && this.bIfPreview) {
            try {
                this.mCamera.autoFocus(null);
            } catch (Exception e) {
            }
        }
    }

    public void SwitchCamera() {
        int i = 1;
        try {
            if (Camera.getNumberOfCameras() != 1 && this.currentHolder != null) {
                if (this.iCurrentCameraId != 0) {
                    i = 0;
                }
                this.iCurrentCameraId = i;
                if (this.mCamera != null) {
                    this.mCamera.stopPreview();
                    this.mCamera.setPreviewCallbackWithBuffer(null);
                    this.bIfPreview = false;
                    this.mVideoPixfmt = -1;
                    this.mCamera.release();
                    this.mCamera = null;
                }
                this.mCamera = Camera.open(this.iCurrentCameraId);
                this.mCamera.setPreviewDisplay(this.currentHolder);
                initCamera();
            }
        } catch (Exception e) {
            if (this.mCamera != null) {
                this.mCamera.release();
                this.mCamera = null;
                this.mVideoPixfmt = -1;
            }
        }
    }

    public void SelectVideoCapture(int facing) {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                this.iCurrentCameraId = i;
                return;
            }
        }
    }

    public void SelectCamera(int iCameraId,SurfaceHolder holder) {
        try {
            if (currentHolder == null){
                currentHolder = holder;
            }
            if (Camera.getNumberOfCameras() > iCameraId ) {
                if (this.mCamera == null || this.iCurrentCameraId != iCameraId) {
                    this.iCurrentCameraId = iCameraId;
                    if (this.mCamera != null) {
                        this.mCamera.stopPreview();
                        this.mCamera.setPreviewCallbackWithBuffer(null);
                        this.bIfPreview = false;
                        this.mVideoPixfmt = -1;
                        this.mCamera.release();
                        this.mCamera = null;
                    }
                    this.mCamera = Camera.open(iCameraId);
                    this.mCamera.setPreviewDisplay(this.currentHolder);
                    initCamera();
                }
            }
        } catch (Exception e) {
            if (this.mCamera != null) {
                this.mCamera.release();
                this.mCamera = null;
                this.mVideoPixfmt = -1;
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (this.mCamera == null) {
                this.mCamera = Camera.open(this.iCurrentCameraId);
            }
            this.currentHolder = holder;
            this.mCamera.setPreviewDisplay(holder);
            initCamera();
        } catch (Exception e) {
            if (this.mCamera != null) {
                this.mCamera.release();
                this.mCamera = null;
                this.mVideoPixfmt = -1;
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mCamera != null) {
            try {
                this.mCamera.stopPreview();
                this.mCamera.setPreviewCallbackWithBuffer(null);
                this.bIfPreview = false;
                this.mCamera.release();
                this.mCamera = null;
            } catch (Exception e) {
                this.mCamera = null;
                this.bIfPreview = false;
            }
        }
        this.currentHolder = null;
        this.mVideoPixfmt = -1;
    }

    private int getDeviceOrientation() {
        if (this.mContext == null) {
            return 0;
        }
        switch (((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                return 0;
        }
    }

    public void setCameraDisplayOrientation() {
        if (this.mContext != null) {
            try {
                int result;
                CameraInfo cameraInfo = new CameraInfo();
                Camera.getCameraInfo(this.iCurrentCameraId, cameraInfo);
                int degrees = 0;
                switch (((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
                    case 0:
                        degrees = 0;
                        break;
                    case 1:
                        degrees = 90;
                        break;
                    case 2:
                        degrees = 180;
                        break;
                    case 3:
                        degrees = 270;
                        break;
                }
                if (cameraInfo.facing == 1) {
                    result = (360 - ((cameraInfo.orientation + degrees) % 360)) % 360;
                } else {
                    result = ((cameraInfo.orientation - degrees) + 360) % 360;
                }
                this.mCamera.setDisplayOrientation(result);
            } catch (Exception e) {
            }
        }
    }
}
