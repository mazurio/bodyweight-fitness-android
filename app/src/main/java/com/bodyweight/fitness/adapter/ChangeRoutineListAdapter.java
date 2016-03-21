package com.bodyweight.fitness.adapter;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.model.Exercise;
import com.bodyweight.fitness.model.Routine;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.HashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChangeRoutineListAdapter extends RecyclerView.Adapter<ChangeRoutineListAdapter.AbstractPresenter> {
    @Override
    public AbstractPresenter onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ChangeRoutineTitlePresenter(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.view_change_routine_title, parent, false)
            );
        }

        return new ChangeRoutineItemPresenter(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.view_change_routine_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(AbstractPresenter holder, int position) {
        holder.onBindView(position);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 2) {
            return 1;
        }

        return 0;
    }

    public abstract class AbstractPresenter extends RecyclerView.ViewHolder {
        public AbstractPresenter(View itemView) {
            super(itemView);
        }

        public abstract void onBindView(int position);
    }

    public class ChangeRoutineTitlePresenter extends AbstractPresenter {
        @InjectView(R.id.title)
        TextView mTitle;

        public ChangeRoutineTitlePresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onBindView(int position) {
            if (position == 0) {
                mTitle.setText("Bodyweight Fitness");
            } else if (position == 2) {
                mTitle.setText("Flexibility");
            }
        }
    }

    public class ChangeRoutineItemPresenter extends AbstractPresenter {
        @InjectView(R.id.view_change_routine_card)
        CardView mCardView;

        @InjectView(R.id.view_change_routine_toolbar)
        Toolbar mToolbar;

        @InjectView(R.id.view_change_routine_title)
        TextView mTitle;

        @InjectView(R.id.view_change_routine_subtitle)
        TextView mSubtitle;

        @InjectView(R.id.view_change_routine_panel)
        View mPanel;

        @InjectView(R.id.view_change_routine_download_button)
        Button mDownloadButton;

        @InjectView(R.id.view_change_routine_set_button)
        Button mSetButton;

        private boolean mShowingToast = false;

        private Routine mRoutine;

        private FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
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
                Logger.d(task.getUrl() + " " + "Completed");

                mDownloadButton.setText(checkRoutine(mRoutine));
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Logger.d(task.getUrl() + " " + "Paused " + soFarBytes + " out of " + totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Logger.d(task.getUrl() + " " + "Error");

                showToast();
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                Logger.d(task.getUrl() + " " + "Warning");
            }
        };

        public ChangeRoutineItemPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.view_change_routine_set_button)
        @SuppressWarnings("unused")
        public void onClickSetRoutine() {
            RoutineStream.getInstance().setRoutine(mRoutine);
        }

        @OnClick(R.id.view_change_routine_download_button)
        @SuppressWarnings("unused")
        public void onClickDownloadRoutine() {
            if (mRoutine.getRoutineId().equalsIgnoreCase("routine0")) {
                return;
            }

            if (isRoutineFullyDownloaded(mRoutine)) {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Remove Downloaded Files?")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            // TODO: Set routine0 as default and send observable.

                            // delete files
                            deleteRoutineFiles(mRoutine);

                            mDownloadButton.setText(checkRoutine(mRoutine));
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {})
                        .show();

                return;
            }

            mShowingToast = false;

            for (Exercise exercise : mRoutine.getExercises()) {
                Logger.d("Downloading for exercise: " + exercise.getTitle());

                String filePath = String.format("%s%s%s%s%s.gif",
                        FileDownloadUtils.getDefaultSaveRootPath(),
                        File.separator,
                        mRoutine.getRoutineId(),
                        File.separator,
                        exercise.getGifId());

                BaseDownloadTask task = FileDownloader.getImpl()
                        .create(exercise.getGifUrl())
                        .setPath(filePath)
                        .setCallbackProgressTimes(0)
                        .setAutoRetryTimes(0)
                        .setTag(mRoutine.getRoutineId())
                        .setListener(mFileDownloadListener);

                task.start();
            }
        }

        @Override
        public void onBindView(int position) {
            switch (position) {
                case 3: {
                    mRoutine = RoutineStream.getInstance().getRoutine(R.raw.starting_stretching);

                    mToolbar.inflateMenu(R.menu.change_routine);
                    mDownloadButton.setVisibility(View.VISIBLE);

                    break;
                }

                case 4: {
                    mRoutine = RoutineStream.getInstance().getRoutine(R.raw.molding_mobility);

                    mToolbar.inflateMenu(R.menu.change_routine);
                    mDownloadButton.setVisibility(View.VISIBLE);

                    break;
                }

                default: {
                    mRoutine = RoutineStream.getInstance().getRoutine(R.raw.beginner_routine);

                    mToolbar.invalidate();
                    mDownloadButton.setVisibility(View.GONE);

                    break;
                }
            }

            mToolbar.setOnMenuItemClickListener((item) -> {
                if (item.getItemId() == R.id.action_remove_files) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Remove Downloaded Files?")
                            .setPositiveButton("Ok", (dialog, which) -> {
                                deleteRoutineFiles(mRoutine);

                                mDownloadButton.setText(checkRoutine(mRoutine));
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {})
                            .show();

                    return true;
                }

                return false;
            });

            mTitle.setText(mRoutine.getTitle());
            mSubtitle.setText(mRoutine.getSubtitle());

            mDownloadButton.setText(checkRoutine(mRoutine));
        }

        private void showToast() {
            if (!mShowingToast) {
                mShowingToast = true;

                Toast.makeText(itemView.getContext(), "Error downloading one of the files. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isRoutineFullyDownloaded(Routine routine) {
            HashSet<String> mNames = new HashSet<>();

            int totalGifs = 0;
            int downloadedGifs = 0;

            for (Exercise exercise : routine.getExercises()) {
                String fileName = exercise.getGifId() + ".gif";

                if (!mNames.contains(fileName)) {
                    totalGifs += 1;
                }

                mNames.add(fileName);
            }

            String path = String.format("%s%s%s", FileDownloadUtils.getDefaultSaveRootPath(), File.separator, routine.getRoutineId());

            File directory = new File(path);

            if (!(directory.exists())) {
                return false;
            }

            for (final File file : directory.listFiles()) {
                if (mNames.contains(file.getName())) {
                    downloadedGifs += 1;
                } else {
                    file.delete();
                }
            }

            return (downloadedGifs == totalGifs);
        }

        private String checkRoutine(Routine routine) {
            if (routine.getRoutineId().equalsIgnoreCase("routine0")) {
                mSetButton.setVisibility(View.VISIBLE);

                return "Downloaded";
            }

            HashSet<String> mNames = new HashSet<>();

            // for every routine
            // for every exercise
            // delete files that are no longer used

            int totalGifs = 0;
            int downloadedGifs = 0;
            int removedGifs = 0;

            for (Exercise exercise : routine.getExercises()) {
                String fileName = exercise.getGifId() + ".gif";

                if (!mNames.contains(fileName)) {
                    totalGifs += 1;
                }

                mNames.add(fileName);
            }

            String path = String.format("%s%s%s", FileDownloadUtils.getDefaultSaveRootPath(), File.separator, routine.getRoutineId());

            File directory = new File(path);

            if (!directory.exists()) {
                downloadedGifs = 0;
                removedGifs = 0;
            } else {
                // should change so it checks the routine directory
                for (final File file : directory.listFiles()) {
                    if (mNames.contains(file.getName())) {
                        downloadedGifs += 1;
                    } else {
                        // This file is no longer needed. Delete it.
                        removedGifs += 1;

                        file.delete();
                    }
                }
            }

            Logger.d("Total gifs needed: " + totalGifs + " and downloaded " + downloadedGifs);
            Logger.d("Removed today: " + removedGifs);

            if (downloadedGifs == totalGifs) {
                mSetButton.setVisibility(View.VISIBLE);

                return "Remove";
            } else if (downloadedGifs > 0 && downloadedGifs < totalGifs) {
                mSetButton.setVisibility(View.VISIBLE);

                return "Update";
            } else {
                mSetButton.setVisibility(View.GONE);

                return "Download";
            }
        }

        private void deleteRoutineFiles(Routine routine) {
            Logger.d("Deleting " + routine.getTitle());

            String path = String.format("%s%s%s", FileDownloadUtils.getDefaultSaveRootPath(), File.separator, routine.getRoutineId());

            File directory = new File(path);

            do {
                if (!(directory.exists())) {
                    break;
                }

                if (!(directory.isDirectory())) {
                    break;
                }

                File[] files = directory.listFiles();

                if (files == null) {
                    break;
                }

                for (File file : files) {
                    Logger.d("Deleted " + file.getName());

                    file.delete();
                }

            } while (false);

            checkRoutine(routine);
        }
    }
}
