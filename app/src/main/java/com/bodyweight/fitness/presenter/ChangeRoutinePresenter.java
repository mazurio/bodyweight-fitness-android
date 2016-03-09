package com.bodyweight.fitness.presenter;

import android.app.Notification;
import android.support.v7.app.NotificationCompat;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;
import com.bodyweight.fitness.view.ChangeRoutineView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationListener;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

public class ChangeRoutinePresenter extends IPresenter<ChangeRoutineView> {
    private transient FileDownloadNotificationHelper<NotificationItem> notificationHelper;

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        notificationHelper = new FileDownloadNotificationHelper<>();
    }

    public void onClickRemoveCache() {

    }

    public void onClickRoutine0() {
        RoutineStream.getInstance().setRoutine(0);
    }

    private FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.d(soFarBytes + " out of " + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.d(soFarBytes + " out of " + totalBytes);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Logger.d("Block complete");
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Logger.d("Completed");
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.d("Paused " + soFarBytes + " out of " + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Logger.d("Error");
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Logger.d("Warning");
        }
    };

    public void onClickRoutine1Download() {
        Routine routine = RoutineStream.getInstance().getRoutine(R.raw.molding_mobility);

        for (Exercise exercise : routine.getExercises()) {
            Logger.d("Downloading for exercise: " + exercise.getTitle());

            String filePath = String.format("%s%s%s.gif",
                    FileDownloadUtils.getDefaultSaveRootPath(),
                    File.separator, exercise.getGifId());

            FileDownloader.getImpl()
                    .create(exercise.getGifUrl())
                    .setPath(filePath)
                    .setListener(mFileDownloadListener)
                    .setListener(new FileDownloadNotificationListener(notificationHelper) {
                        @Override
                        protected BaseNotificationItem create(BaseDownloadTask task) {
                            return new NotificationItem(task.getDownloadId(), "demo title", "demo desc");
                        }
                    })
                    .start();
        }
    }

    public void onClickRoutine1() {
        RoutineStream.getInstance().setRoutine(1);
    }

    public static class NotificationItem extends BaseNotificationItem {

        private NotificationItem(int id, String title, String desc) {
            super(id, title, desc);
        }

        @Override
        public void show(boolean statusChanged, int status, boolean isShowProgress) {
            NotificationCompat.Builder builder = new NotificationCompat.
                    Builder(FileDownloadHelper.getAppContext());

            String desc = getDesc();
            switch (status) {
                case FileDownloadStatus.pending:
                    desc += " pending";
                    break;
                case FileDownloadStatus.progress:
                    desc += " progress";
                    break;
                case FileDownloadStatus.retry:
                    desc += " retry";
                    break;
                case FileDownloadStatus.error:
                    desc += " error";
                    break;
                case FileDownloadStatus.paused:
                    desc += " paused";
                    break;
                case FileDownloadStatus.completed:
                    desc += " completed";
                    break;
                case FileDownloadStatus.warn:
                    desc += " warn";
                    break;
            }

            builder.setDefaults(Notification.DEFAULT_LIGHTS)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setContentTitle(getTitle())
                    .setContentText(desc)
                    .setSmallIcon(R.mipmap.ic_launcher);

            if (statusChanged) {
                builder.setTicker(desc);
            }

            builder.setProgress(getTotal(), getSofar(), !isShowProgress);
            getManager().notify(getId(), builder.build());
        }
    }

}
