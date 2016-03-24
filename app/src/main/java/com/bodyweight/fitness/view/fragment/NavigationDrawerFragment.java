package com.bodyweight.fitness.view.fragment;

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

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.stream.DrawerStream;
import com.bodyweight.fitness.stream.RoutineStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.bodyweight.fitness.BuildConfig;
import com.bodyweight.fitness.R;

import rx.Subscription;

public class NavigationDrawerFragment extends Fragment {
    @InjectView(R.id.arrow)
    ImageView mImageViewArrow;

    @InjectView(R.id.routine_title)
    TextView mRoutineTitle;

    @InjectView(R.id.routine_subtitle)
    TextView mRoutineSubtitle;

    @InjectView(R.id.menu)
    View mMenu;

    @InjectView(R.id.recycler_view)
    View mRecyclerView;

    @InjectView(R.id.action_menu_home)
    View mActionMenuHome;

    @InjectView(R.id.action_menu_support_developer)
    TextView mActionMenuSupportDeveloper;

    int mMenuId = -1;
    boolean mShowRecyclerView = false;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mNavigationDrawerLayout;
    private View mFragmentContainerView;

    private Subscription mRoutineSubscription;
    private Subscription mRoutineChangedSubscription;
    private Subscription mExerciseChangedSubscription;
    private Subscription mDrawerMenuSubscription;

    public NavigationDrawerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoutineSubscription = RoutineStream.getInstance()
                .getRoutineObservable()
                .subscribe(routine -> {
                    mRoutineTitle.setText(routine.getTitle());
                    mRoutineSubtitle.setText(routine.getSubtitle());
                });

        mRoutineChangedSubscription = RoutineStream.getInstance()
                .getRoutineChangedObservable()
                .subscribe(routine -> {
                    mRoutineTitle.setText(routine.getTitle());
                    mRoutineSubtitle.setText(routine.getSubtitle());

                    DrawerStream.getInstance().setMenu(R.id.action_menu_home);
                });

        mExerciseChangedSubscription = RoutineStream.getInstance()
                .getExerciseChangedObservable()
                .subscribe(exercise -> {
                    closeDrawer();
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRoutineSubscription != null) {
            mRoutineSubscription.unsubscribe();
            mRoutineSubscription = null;
        }

        if (mRoutineChangedSubscription != null) {
            mRoutineChangedSubscription.unsubscribe();
            mRoutineChangedSubscription = null;
        }

        if (mExerciseChangedSubscription != null) {
            mExerciseChangedSubscription.unsubscribe();
            mExerciseChangedSubscription = null;
        }

        if (mDrawerMenuSubscription != null) {
            mDrawerMenuSubscription.unsubscribe();
            mDrawerMenuSubscription = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMenuId = savedInstanceState.getInt("MENU_ID");
            mShowRecyclerView = savedInstanceState.getBoolean("SHOW_RECYCLER_VIEW");
        }

        final View view = inflater.inflate(R.layout.view_drawer, container, false);

        ButterKnife.inject(this, view);

        if (BuildConfig.FLAVOR.equalsIgnoreCase("free")) {
            mActionMenuSupportDeveloper.setVisibility(View.VISIBLE);
        } else {
            mActionMenuSupportDeveloper.setVisibility(View.GONE);
        }

        subscribe(view);

        if (mShowRecyclerView) {
            showRecyclerView();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("MENU_ID", mMenuId);
        outState.putBoolean("SHOW_RECYCLER_VIEW", mShowRecyclerView);

        super.onSaveInstanceState(outState);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
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
        mNavigationDrawerLayout.post(mActionBarDrawerToggle::syncState);
        mNavigationDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void closeDrawer() {
        if (mNavigationDrawerLayout != null && mFragmentContainerView != null) {
            mNavigationDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    private void subscribe(View view) {
        if (mMenuId == -1) {
            mMenuId = mActionMenuHome.getId();
        }

        mDrawerMenuSubscription = DrawerStream.getInstance()
                .getMenuObservable()
                .subscribe(id -> {
                    setMenu(view, id);
                    closeDrawer();
                });

        DrawerStream.getInstance().setMenu(mMenuId);
    }

    private void setMenu(View view, int id) {
        if (id == R.id.action_menu_support_developer ||
                id == R.id.action_menu_settings) {
            closeDrawer();

            return;
        }

//        if (id == R.id.action_menu_home) {
//            mImageViewArrow.setVisibility(View.VISIBLE);
//        } else if (id == R.id.action_menu_change_routine) {
//            mImageViewArrow.setVisibility(View.INVISIBLE);
//        } else if (id == R.id.action_menu_workout_log) {
//            mImageViewArrow.setVisibility(View.INVISIBLE);
//        }

        ((TextView) view.findViewById(mMenuId)).setTextColor(Color.parseColor("#87000000"));
        ((TextView) view.findViewById(id)).setTextColor(Color.parseColor("#009688"));

        mMenuId = id;
    }

    private void showRecyclerView() {
        return;

//        mShowRecyclerView = true;
//
//        mImageViewArrow.setImageDrawable(App
//                .getContext()
//                .getResources()
//                .getDrawable(R.drawable.ic_arrow_drop_up));
//
//        mRecyclerView.setVisibility(View.VISIBLE);
//        mMenu.setVisibility(View.GONE);
    }

    private void hideRecyclerView() {
        return;

//        mShowRecyclerView = false;
//
//        mImageViewArrow.setImageDrawable(App
//                .getContext()
//                .getResources()
//                .getDrawable(R.drawable.ic_arrow_drop_down));
//
//        mRecyclerView.setVisibility(View.GONE);
//        mMenu.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.header)
    @SuppressWarnings("unused")
    public void onHeaderClick(View view) {
        if (mMenuId != R.id.action_menu_home) {
            return;
        }

        if (mMenu.getVisibility() == View.VISIBLE) {
            showRecyclerView();
        } else {
            hideRecyclerView();
        }
    }

    @OnClick({
            R.id.action_menu_home,
            R.id.action_menu_change_routine,
            R.id.action_menu_workout_log,
            R.id.action_menu_support_developer,
            R.id.action_menu_settings
    })
    @SuppressWarnings("unused")
    public void onMenuClick(View view) {
        DrawerStream.getInstance().setMenu(view.getId());
    }
}
