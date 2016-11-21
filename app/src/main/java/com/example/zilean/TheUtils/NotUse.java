package com.example.zilean.TheUtils;

/**
 * 这里是一些使用过但后来不用的类，先放在这里
 * Created by 王跃_ on 2016/11/21.
 */

public class NotUse {
    /*MainActivity:Runnable实现倒计时*/
//        public class TomatoThread implements Runnable {
//        int min = 25;
//        int sec = 0;
//        int type = 0;
//        long time1 = 0;
//        long time2 = 0;
//        boolean isBreak = false;
//        Message message = Message.obtain();
//
//        public TomatoThread(int min, int sec, int type) {
//            if (isTest) {
//                this.sec = min;
//                this.min = sec;
//            } else {
//                this.min = min;
//                this.sec = sec;
//            }
//
//            this.type = type;
//
//            message.what = 1;
//            message.arg1 = this.min;
//            message.arg2 = this.sec;
//            myTimeHandler.handleMessage(message);
//        }
//
//        public int getType() {
//            return type;
//        }
//
//        public void setBreak(boolean aBreak) {
//            isBreak = aBreak;
//        }
//
//        @Override
//        public void run() {
//            while (min > 0 || sec > 0) {
//                if (isBreak) break;
//                time1 = time2;
//                if (sec <= 0) {
//                    min--;
//                    sec = 59;
//                } else sec--;
//                message.what = 1;
//                message.arg1 = min;
//                message.arg2 = sec;
//                Log.e("time", min + ":" + sec);
//                try {
//                    if (time1 == 0) {
//                        time2 = System.currentTimeMillis();
//                        Thread.sleep(1000);
//                    } else {
//                        time2 = System.currentTimeMillis();
//                        Log.e("time2", (time2 - time1) + "");
//                        Thread.sleep(2000 - (time2 - time1));
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (isBreak) break;
//                myTimeHandler.handleMessage(message);
//            }
//            if (!isBreak) myGoHandler.sendEmptyMessage(2);
//        }
//    }
    /*同上：MyTimeHandler中使用*/
//        TomatoThread tomatoThread = null;
//        Thread thread = null;
//        public void setTomatoThread(TomatoThread tomatoThread) {
//            this.tomatoThread = tomatoThread;
//        }
//
//
//        public void startThread() {
//            thread = new Thread(tomatoThread);
//            thread.start();
//        }
//
//        public void nextThread() {
//            sendEmptyMessage(0);
//        }
//
//        public void stopThread() {
//            tomatoThread.setBreak(true);
//            sendEmptyMessage(2);
//            myGoHandler.sendEmptyMessage(3);
//        }

    /*DialogActivity:系统自带Crop实现，REQUEST_CODE*/
//    private final int REQUEST_CAMERA = 1;
//    private final int REQUEST_PHOTO = 2;
//    private final int REQUEST_CROP = 3;

    /*DialogActivity:系统自带Crop实现，onActivityResult*/
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
//            // 从相册返回的数据
//            if (data != null) {
//                // 得到图片的全路径
//                Uri uri = data.getData();
////                crop(uri);
//                beginCrop(uri);
//            }
//        } else if (requestCode == REQUEST_CROP) {
//            // 从剪切图片返回的数据
//            if (data != null) {
//                isBackground = true;
//                if (!isSDExist) {
//                    bitmap = data.getParcelableExtra("data");
//                    try {
//                        Utils.saveBitmap2PNG(bitmap, tempFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//    super.onActivityResult(requestCode, resultCode, data);
//}

    /*DialogActivity:系统自带Crop实现，crop意图*/
//    private void crop(Uri uri) {
//        Utils.deleteFile(tempFile);
//        // 裁剪图片意图
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");// 发送裁剪信号
//        // 裁剪框的比例
//        intent.putExtra("aspectX", screenWidth / screenGcd);
//        intent.putExtra("aspectY", screenHeight / screenGcd);
//        intent.putExtra("scale", true);
//        intent.putExtra("scaleUpIfNeeded", true);// 解决当图片大小小于输出大小时，部分机器截取图片黑边
//        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", screenWidth);
//        intent.putExtra("outputY", screenHeight);
//
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式
//        intent.putExtra("noFaceDetection", true);// 取消人脸识别
//        if (isSDExist) {
//            //以Uri格式可以保存大分辨率的图，但必须要SD卡支持
//            intent.putExtra("return-data", false);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
////        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/"+FILENAME_STORAGE));
//        } else {
//            //Bitmap格式传的是小分辨率的图，只适合小图裁剪
//            intent.putExtra("return-data", true);// 是否将数据保留在Bitmap中返回
//        }
//        // 开启一个带有返回值的Activity，请求码为RESULT_CROP
//        startActivityForResult(intent, REQUEST_CROP);
//    }

}
