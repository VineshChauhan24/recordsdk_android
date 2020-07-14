# recordsdk_android
短视频编辑sdk

## 导入module，’filterlibrary',':short',
 
# 调用一下接口
QkApplication.onCreate(getApplicationContext());

RecordSdk.init(this, setLogLevel());

RecordSdk.setAppKey("appkey", "test");

# 注意：开启录制功能前请先申请本地文件存储的权限、摄像头权限 以及 录音权限
开启短视频功能前申请本地文件存储的权限
# 直接使用 NewCameraActivity 即可实现录制功能。
# 直接使用 NewVideoProcessActivity即可实现短视频功能。
