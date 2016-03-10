package com.bodyweight.fitness.presenter;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.stream.ToolbarStream;
import com.bodyweight.fitness.utils.Logger;
import com.bodyweight.fitness.view.ChangeRoutineView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class ChangeRoutinePresenter extends IPresenter<ChangeRoutineView> {
    private int mCompleted = 0;
    private int mTotal = 0;

    @Override
    public void onCreateView(ChangeRoutineView view) {
        super.onCreateView(view);

        checkMoldingMobility();
    }

    private void checkMoldingMobility() {
        HashSet<String> mNames = new HashSet<>();

        // for every routine
        // for every exercise
        // delete files that are no longer used

        Routine routine = RoutineStream.getInstance().getRoutine(R.raw.molding_mobility);

        int totalGifs = 0;
        int downloadedGifs = 0;
        int removedGifs = 0;

        for (Exercise exercise : routine.getExercises()) {
            totalGifs += 1;

            String fileName = exercise.getGifId() + ".gif";

            mNames.add(fileName);
        }

        File dir = new File(FileDownloadUtils.getDefaultSaveRootPath());

        // should change so it checks the routine directory
        for (final File file : dir.listFiles()) {
            if (mNames.contains(file.getName())) {
                downloadedGifs += 1;
            } else {
                // This file is no longer needed. Delete it.
                removedGifs += 1;

                file.delete();
            }
        }

        Logger.d("Total gifs needed: " + totalGifs + " and downloaded " + downloadedGifs);
        Logger.d("Removed today: " + removedGifs);

        if (downloadedGifs < totalGifs) {
            Logger.d("Update the routine please!");
        }
    }

    @Override
    public void onSubscribe() {
        super.onSubscribe();

        subscribe(ToolbarStream.getInstance()
                .getMenuObservable()
                .subscribe(id -> {
                    Logger.d("Change Routine");
                }));
    }

    public void onClickRemoveCache() {
        int count = 0;

        File file = new File(FileDownloadUtils.getDefaultSaveRootPath());

        do {
            if (!file.exists()) {
                break;
            }

            if (!file.isDirectory()) {
                break;
            }

            File[] files = file.listFiles();

            if (files == null) {
                break;
            }

            for (File file1 : files) {
                count++;
                file1.delete();
            }

        } while (false);

        Logger.d("Deleted " + count + " files");

        mCompleted = 0;
        mTotal = 0;

        updateProgress();
    }

    public void onClickRoutine0() {
        RoutineStream.getInstance().setRoutine(0);
    }

    private transient FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.d(task.getUrl() + " " + soFarBytes + " out of " + totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Logger.d(task.getUrl() + " " + "Block complete");
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            mCompleted += 1;

            Logger.d(task.getUrl() + " " + "Completed");

            updateProgress();
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.d(task.getUrl() + " " + "Paused " + soFarBytes + " out of " + totalBytes);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Logger.d(task.getUrl() + " " + "Error");
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Logger.d(task.getUrl() + " " + "Warning");
        }
    };

    public void onClickRoutine1Download() {
        mCompleted = 0;
        mTotal = 0;

        Routine routine = RoutineStream.getInstance().getRoutine(R.raw.molding_mobility);

        for (Exercise exercise : routine.getExercises()) {
            mTotal += 1;

            Logger.d("Downloading for exercise: " + exercise.getTitle());

            String filePath = String.format("%s%s%s.gif",
                    FileDownloadUtils.getDefaultSaveRootPath(),
                    File.separator, exercise.getGifId());

            FileDownloader.getImpl()
                    .create(exercise.getGifUrl())
                    .setPath(filePath)
                    .setCallbackProgressTimes(0)
                    .setListener(mFileDownloadListener)
                    .ready();
        }

        updateProgress();

        FileDownloader.getImpl().start(mFileDownloadListener, true);
    }

    public void onClickRoutine1() {
        RoutineStream.getInstance().setRoutine(1);
    }

    public void updateProgress() {
        mView.mRoutine1ProgressBar.setMax(mTotal);
        mView.mRoutine1ProgressBar.setProgress(mCompleted);
    }
}
