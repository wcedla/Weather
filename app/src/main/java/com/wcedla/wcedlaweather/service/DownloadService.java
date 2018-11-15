package com.wcedla.wcedlaweather.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.wcedla.wcedlaweather.MainActivity;
import com.wcedla.wcedlaweather.R;
import com.wcedla.wcedlaweather.adapter.DownloadListener;
import com.wcedla.wcedlaweather.tool.DownloadTask;

import java.io.File;

import static android.support.constraint.Constraints.TAG;

public class DownloadService extends Service {

//    final int progressId=21;
//    final int successId=22;
//    final int failedId=23;
//    final int pasueId=24;
    final int foregroundId=20;
    final int statusId=21;

    final int waitDownload=1;
    final int downloading=2;
    final int downloadPause=3;
    final int downloadCancel=4;

    String fileName;
    int downloadStatus;
    File file;

    DownloadBinder downloadBinder = new DownloadBinder();
    myDownloadListener downloadListener=new myDownloadListener();
    DownloadTask downloadTask;

    MainActivity.MyHandler myHandler;


    /*
    实现下载监听的各种方法，下载监听主要是为了Asynctask子线程与downloadservice之间的通信，为什么要用接口实现，不直接新建类的原因是
    新建类难获取到context对象，很多操作无法执行，像操作Toast，发送通知等。
    * */

    public class myDownloadListener implements DownloadListener
    {

        @Override
        public void onProgress(int progress)
        {
            Log.d(TAG, "回调显示进度监控"+progress);
            getNotificationManager().notify(foregroundId, getNotification("正在下载更新文件...", progress));
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "回调显示文件下载成功");
            stopForeground(true);//取消正在下载的通知前台服务
            downloadStatus=waitDownload;
            //getNotificationManager().notify(statusId,getNotification("更新文件下载完毕",-1));
            file=downloadTask.getFile();
            Message message=new Message();
            message.what=2;
            message.obj=file;
            myHandler.handleMessage(message);

        }

        @Override
        public void onFailed() {
            Log.d(TAG, "回调显示文件下载失败");
            stopForeground(true);
            downloadStatus=waitDownload;
            getNotificationManager().notify(statusId,getNotification("更新文件下载失败",-1));

        }

        @Override
        public void onPaused() {
            Log.d(TAG, "回调显示文件下载暂停");
            stopForeground(true);
            getNotificationManager().notify(statusId,getNotification("更新文件下载已被暂停",-1));
        }

        @Override
        public void onCanceled() {
            Log.d(TAG, "回调显示文件下载失败");
            stopForeground(true);
            getNotificationManager().notify(statusId,getNotification("更新文件下载已被取消",-1));

        }

        @Override
        public String getFileName() {
            return fileName;
        }
    }

    public class DownloadBinder extends Binder //继承自binder，然后可以自己定义各种自己需要的方法，具体方法看自己需要了。
    {

        public void startDownload(String str) {
            Log.d(TAG, "binder下载方法执行");
            downloadTask=new DownloadTask(downloadListener);//通过downloadtask的有参构造函数，将下载监听器传入到asynctask中，达到两者之间的通信。
            downloadTask.execute(str);
            startForeground(foregroundId,getNotification("更新文件开始下载...",0));
            getNotificationManager().cancel(statusId);//清除之前执行任务产生的通知，
            downloadStatus=downloading;
            Toast.makeText(DownloadService.this,"开始下载更新文件！",Toast.LENGTH_SHORT).show();
        }

        public void pauseDownload()
        {
            Log.d(TAG, "binder暂停方法执行");
            if(downloadTask!=null)//用于判断下载服务是否启动
            {
                downloadTask.pauseDownload();//asynctask的方法，实则就是在将下载文件写入文件之前使用return返回，强制结束任务
                downloadStatus=downloadPause;
                Toast.makeText(DownloadService.this,"更新文件下载已暂停！",Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(DownloadService.this,"没有正在下载的文件，并且出现了bug",Toast.LENGTH_SHORT).show();
            }
        }

        public void cancelDownload()
        {
            Log.d(TAG, "binder取消下载方法执行");
            if(downloadTask!=null)//用于判断下载服务是否启动
            {
                downloadTask.cancelDownload();//asynctask的方法，实则就是在将下载文件写入文件之前使用return返回，强制结束任务
                downloadStatus=downloadCancel;
                //downloadListener.onCanceled();//下载服务已经暂停，子线程并没有继续执行下载任务，不能再通过return cancel达到创建通知的效果，所以手动调用生成通知。
                getNotificationManager().cancel(statusId);//清除之前执行任务产生的通知，
//                /*
//                将下载的文件删除
//                * */
//                //String fileName = downloadURL.substring(downloadURL.lastIndexOf("/"),downloadURL.indexOf("?"));
//                //文件路径，固定格式，路径存放为手机内存的download文件夹
//                String directory = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"wcedla"+File.separator;
//                File file = new File(directory +fileName);//操作指定路径下的文件，绑定到file。
//                if (file.exists()) {
//                    if(file.delete())
//                    {
//                        Toast.makeText(DownloadService.this, "下载任务已取消,文件已删除", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
            else
            {
                Toast.makeText(DownloadService.this,"没有正在下载的文件",Toast.LENGTH_SHORT).show();
            }
        }

        public void setFileName(String name)
        {
            fileName=name;
            Log.d(TAG, "文件名"+fileName);
        }

        public int getDownloadStatus()
        {
            Log.d(TAG, "binder中获取下载服务状态");
            return downloadStatus;
        }

        public void setHandler(MainActivity.MyHandler myHandler)
        {
            setMyHandler(myHandler);
        }

        public void stopService()
        {
            stopSelf();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return downloadBinder;
    }

    private void setMyHandler(MainActivity.MyHandler myHandler)
    {
        this.myHandler=myHandler;
    }

    private NotificationManager getNotificationManager() {//太多地方需要创建通知了，新建一个方法方便调用使用
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress)
    {
        NotificationChannel channel = null;
        //适配安卓8.0的通知
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("wcedla_2",
                    "DownloadNotification", NotificationManager.IMPORTANCE_LOW);//显示在任何位置没有声音
            getNotificationManager().createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(DownloadService.this, "wcedla_2"); //与channelId对应
            //icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(R.drawable.ic_download)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
            if (progress >= 0) {
                // 当progress大于或等于0时才需显示下载进度
                builder.setContentText(progress + "%");
                builder.setProgress(100, progress, false);
            }
            return builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1");
            builder.setSmallIcon(R.drawable.ic_download);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
            builder.setContentTitle(title);
            if (progress >= 0) {
                // 当progress大于或等于0时才需显示下载进度
                builder.setContentText(progress + "%");
                builder.setProgress(100, progress, false);
            }
            return builder.build();
        }
    }

//    protected void installApk() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            boolean permit = getPackageManager().canRequestPackageInstalls();
//            if (!permit)
//            {
//                //请求安装未知应用来源的权限
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 11);
//            }
//
//        }
//            Intent intent = new Intent();
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        Uri uri = Uri.fromFile(file);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        this.startActivity(intent);
//    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "结束了");
        super.onDestroy();
    }
}
