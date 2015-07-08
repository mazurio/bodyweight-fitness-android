package io.mazur.fit.presenter;

import io.mazur.fit.adapter.RoutineAdapter;
import io.mazur.fit.stream.RoutineStream;
import io.mazur.fit.view.ListView;

public class ListPresenter {
    private transient ListView mListView;

    private RoutineAdapter mRoutineAdapter;

    public ListPresenter() {
        mRoutineAdapter = new RoutineAdapter();
    }

    public void onCreateView(ListView listView) {
        mListView = listView;

        mListView.getRecyclerView().setAdapter(mRoutineAdapter);

        RoutineStream.getInstance().getRoutineObservable()
                .subscribe(routine -> mRoutineAdapter.setRoutine(routine));
    }
}
