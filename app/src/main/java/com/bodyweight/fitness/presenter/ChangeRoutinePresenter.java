package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;
import com.bodyweight.fitness.view.ChangeRoutineView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

public class ChangeRoutinePresenter extends IPresenter<ChangeRoutineView> {
    @Override
    public void onSubscribe() {
        super.onSubscribe();
    }

    public void onClickRoutine0() {
        RoutineStream.getInstance().setRoutine(0);
    }

    public void onClickRoutine0Download() {
        String savePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator;

        FileDownloader.getImpl()
                .create("http://i.imgur.com/R8iHPri.jpg")
                .setPath(savePath + "ted.jpg")
                .setListener(new FileDownloadListener() {
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
                }).start();
    }

    public void onClickRoutine1() {
        RoutineStream.getInstance().setRoutine(1);
    }
}
