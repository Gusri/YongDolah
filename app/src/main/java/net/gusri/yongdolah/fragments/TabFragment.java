package net.gusri.yongdolah.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gusri.yongdolah.R;
import net.gusri.yongdolah.klass.TabAdapter;

/**
 * Created by ghost on 4/1/17.
 */

public class TabFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.group_tab, container, false);

        mAdapter = new TabAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager) mView. findViewById(R.id.vp_Tab);
        mViewPager.setAdapter(mAdapter);

        mTabLayout = (TabLayout) mView.findViewById(R.id.tab_Layout);
        mTabLayout.setupWithViewPager(mViewPager);

        return mView;
    }
}
