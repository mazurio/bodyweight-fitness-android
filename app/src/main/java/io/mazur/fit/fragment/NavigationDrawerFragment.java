package io.mazur.fit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.mazur.fit.R;
import io.mazur.fit.stream.RoutineStream;

public class NavigationDrawerFragment extends Fragment {
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerLayout mNavigationDrawerLayout;

    private View mFragmentContainerView;

    public NavigationDrawerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_drawer, container, false);
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
            mNavigationDrawerLayout.closeDrawer(mFragmentContainerView);
        });
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }
}
