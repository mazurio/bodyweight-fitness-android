package io.mazur.fit.presenter;

import java.io.Serializable;

import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.PreviewView;

import rx.Subscription;

public class PreviewPresenter extends IPresenter<PreviewView> implements Serializable {
    private transient Subscription mSubscription;

    @Override
    public void onCreateView(PreviewView view) {
        super.onCreateView(view);

        mSubscription = RoutineStream.getInstance()
                .getExerciseObservable()
                .subscribe(exercise -> {
                    /**
                     * This is a leak, why?
                     */
                    if (mView.getPreviewGifImageView() != null) {
                        mView.getPreviewGifImageView().setImageResource(
                                mView.getContext()
                                        .getResources()
                                        .getIdentifier(exercise.getId(), "drawable", mView.getContext().getPackageName())
                        );
                    }
                });
    }

    @Override
    public void onSaveInstanceState() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    @Override
    public void onRestoreInstanceState() {}
}
