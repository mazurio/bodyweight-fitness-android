package com.bodyweight.fitness.adapter;

import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
        Button mDowloadButton;

        @InjectView(R.id.view_change_routine_set_button)
        ImageButton mSetButton;

        int mPosition;

        public ChangeRoutineItemPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.view_change_routine_set_button)
        @SuppressWarnings("unused")
        public void onClickSetRoutine() {
            if (mPosition == 1) {
                RoutineStream.getInstance().setRoutine(0);
            } else if (mPosition == 3) {
                RoutineStream.getInstance().setRoutine(2);
            } else if (mPosition == 4) {
                RoutineStream.getInstance().setRoutine(1);
            }
        }

        @OnClick(R.id.view_change_routine_download_button)
        @SuppressWarnings("unused")
        public void onClickDownloadRoutine() {
            if (mPosition == 3) {

            } else if (mPosition == 4) {
                if (isMoldingMobilityFullyDownloaded()) {
                    new AlertDialog.Builder(itemView.getContext())
                            .setTitle("Remove Downloaded Files?")
                            .setPositiveButton("Ok", (dialog, which) -> {
                                // TODO: Set routine0 as default and send observable.

                                // delete files
                                deleteMoldingMobility();

                                mDowloadButton.setText(checkMoldingMobility());
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {})
                            .show();

                    return;
                }

                FileDownloadListener mFileDownloadListener = new FileDownloadListener() {
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

                        mDowloadButton.setText(checkMoldingMobility());
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

                // Download Molding Mobility

                Routine routine = RoutineStream.getInstance().getRoutine(R.raw.molding_mobility);

                for (Exercise exercise : routine.getExercises()) {
                    Logger.d("Downloading for exercise: " + exercise.getTitle());

                    String filePath = String.format("%s%s%s%s%s.gif",
                            FileDownloadUtils.getDefaultSaveRootPath(),
                            File.separator,
                            "molding_mobility",
                            File.separator,
                            exercise.getGifId());

                    FileDownloader.getImpl()
                            .create(exercise.getGifUrl())
                            .setPath(filePath)
                            .setCallbackProgressTimes(0)
                            .setAutoRetryTimes(3)
                            .setListener(mFileDownloadListener)
                            .ready();
                }

                FileDownloader.getImpl().start(mFileDownloadListener, true);
            }
        }

        @Override
        public void onBindView(int position) {
            mPosition = position;

            switch (position) {
                case 3: {
                    int color = Color.parseColor("#EEA200");

//                    mCardView.setCardBackgroundColor(color);
//                    mToolbar.setBackgroundColor(color);

                    mTitle.setText("Starting Stretching");
                    mSubtitle.setText("Flexibility Routine");

                    mPanel.setVisibility(View.VISIBLE);

                    mDowloadButton.setText("Unavailable");

                    break;
                }

                case 4: {
                    int color = Color.parseColor("#CE331B");

//                    mCardView.setCardBackgroundColor(color);
//                    mToolbar.setBackgroundColor(color);

                    mTitle.setText("Molding Mobility");
                    mSubtitle.setText("Flexibility Routine");

                    mPanel.setVisibility(View.VISIBLE);

                    mDowloadButton.setText(checkMoldingMobility());

                    break;
                }

                default: {
                    int color = Color.parseColor("#363736");

//                    mCardView.setCardBackgroundColor(color);
//                    mToolbar.setBackgroundColor(color);

                    mTitle.setText("Bodyweight Fitness");
                    mSubtitle.setText("Recommended Routine");

                    mPanel.setVisibility(View.GONE);

                    break;
                }
            }
        }

        private boolean isMoldingMobilityFullyDownloaded() {
            HashSet<String> mNames = new HashSet<>();

            Routine routine = RoutineStream.getInstance().getRoutine(R.raw.molding_mobility);

            int totalGifs = 0;
            int downloadedGifs = 0;

            for (Exercise exercise : routine.getExercises()) {
                totalGifs += 1;

                String fileName = exercise.getGifId() + ".gif";

                mNames.add(fileName);
            }

            File dir = new File(FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "molding_mobility");

            if (!dir.exists()) {
                return false;
            }

            for (final File file : dir.listFiles()) {
                if (mNames.contains(file.getName())) {
                    downloadedGifs += 1;
                } else {
                    file.delete();
                }
            }

            return (downloadedGifs == totalGifs);
        }

        private String checkMoldingMobility() {
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

            File dir = new File(FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "molding_mobility");

            if (!dir.exists()) {
                downloadedGifs = 0;
                removedGifs = 0;
            } else {
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
                mSetButton.setVisibility(View.INVISIBLE);

                return "Download";
            }
        }

        private void deleteStartingStretching() {
            Logger.d("Deleting Starting Stretching");

            int count = 0;

            File file = new File(FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "starting_stretching");

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
        }

        private void deleteMoldingMobility() {
            Logger.d("Deleting Molding Mobility");

            int count = 0;

            File file = new File(FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "molding_mobility");

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
        }
    }
}
