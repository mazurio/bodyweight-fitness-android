package io.mazur.fit.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.mazur.fit.App;
import io.mazur.fit.R;
import io.mazur.fit.stream.RoutineStream;
import rx.Observable;
import rx.subjects.PublishSubject;

public class NavigationDrawerFragment extends Fragment {
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mNavigationDrawerLayout;
    private View mFragmentContainerView;
    private int mMenuId = -1;

    private final PublishSubject<Integer> mDrawerMenuSubject = PublishSubject.create();

    public NavigationDrawerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.view_drawer, container, false);

        final ImageView arrow = (ImageView) view.findViewById(R.id.arrow);

        final View menu = view.findViewById(R.id.menu);
        final View routine = view.findViewById(R.id.recycler_view);
        final View header = view.findViewById(R.id.header);

        header.setOnClickListener(v -> {
            if(menu.getVisibility() == View.VISIBLE) {
                arrow.setImageDrawable(App
                        .getContext()
                        .getResources()
                        .getDrawable(R.drawable.ic_arrow_drop_up));

                routine.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
            } else {
                arrow.setImageDrawable(App
                        .getContext()
                        .getResources()
                        .getDrawable(R.drawable.ic_arrow_drop_down));

                routine.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
            }
        });

        final View actionMenuHome = view.findViewById(R.id.action_menu_home);

        mMenuId = actionMenuHome.getId();
        setActionActive(mMenuId);

        actionMenuHome.setOnClickListener(v -> setActionActive(v.getId()));

        final View actionMenuWorkoutLog = view.findViewById(R.id.action_menu_workout_log);
        actionMenuWorkoutLog.setOnClickListener(v -> setActionActive(v.getId()));

        mDrawerMenuSubject.subscribe(id -> {
            ((TextView) view.findViewById(mMenuId)).setTextColor(Color.parseColor("#87000000"));
            ((TextView) view.findViewById(id)).setTextColor(Color.parseColor("#009688"));
        });

        return view;
    }

    public void setDrawer(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mNavigationDrawerLayout = drawerLayout;

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mNavigationDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mNavigationDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mNavigationDrawerLayout.post(() -> mActionBarDrawerToggle.syncState());

        mNavigationDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        RoutineStream.getInstance().getExerciseChangedObservable().subscribe(exercise -> {
            closeDrawer();
        });
    }

    public void closeDrawer() {
        if(mNavigationDrawerLayout != null && mFragmentContainerView != null) {
            mNavigationDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionActive(int id) {
        mDrawerMenuSubject.onNext(id);
        mMenuId = id;

        closeDrawer();
    }

    public Observable<Integer> getMenuObservable() {
        return mDrawerMenuSubject.asObservable();
    }
}
