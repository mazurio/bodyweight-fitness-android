package com.bodyweight.fitness.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodyweight.fitness.R;
import com.bodyweight.fitness.stream.RoutineStream;
import com.bodyweight.fitness.utils.Logger;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ChangeRoutineListAdapter extends RecyclerView.Adapter<ChangeRoutineListAdapter.AbstractPresenter> {
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
            Logger.d(task.getUrl() + " " + "Completed");
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

        int mPosition;

        public ChangeRoutineItemPresenter(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.view_change_routine_set_button)
        public void setRoutine(View view) {
            if (mPosition == 1) {
                RoutineStream.getInstance().setRoutine(0);
            } else if (mPosition == 4) {
                RoutineStream.getInstance().setRoutine(1);
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

                    break;
                }

                case 4: {
                    int color = Color.parseColor("#CE331B");

//                    mCardView.setCardBackgroundColor(color);
//                    mToolbar.setBackgroundColor(color);

                    mTitle.setText("Molding Mobility");
                    mSubtitle.setText("Flexibility Routine");

                    mPanel.setVisibility(View.VISIBLE);

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
    }
}
