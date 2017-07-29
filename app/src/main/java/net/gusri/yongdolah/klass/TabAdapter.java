package net.gusri.yongdolah.klass;

import android.graphics.drawable.Icon;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.gusri.yongdolah.chatgroup.CGFragment;
import net.gusri.yongdolah.fragments.UserListFragment;

import java.util.ArrayList;

/**
 * Created by ghost on 4/1/17.
 */

public class TabAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Fragment> mFragment;
    /*private final ArrayList<String> mTitle;*/


    public TabAdapter(FragmentManager fm) {
        super(fm);
        mFragment = new ArrayList<Fragment>(2);
        mFragment.add(new UserListFragment());
        mFragment.add(new CGFragment());

        /*mTitle = new ArrayList<String>(mFragment.size());
        mTitle.add(getString(R.string.Tab1));
        mTitle.add(Resources.getSystem().getString(R.string.Tab2));*/
    }


    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
     //   return mTitle.get(position);
        switch (position){
            case 0:
                return "User";
            case 1:
                return "Chat";
        }
        return null;
    }

}
