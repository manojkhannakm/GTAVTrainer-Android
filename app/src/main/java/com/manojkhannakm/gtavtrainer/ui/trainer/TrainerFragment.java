package com.manojkhannakm.gtavtrainer.ui.trainer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manojkhannakm.gtavtrainer.R;

/**
 * @author Manoj Khanna
 */

public class TrainerFragment extends Fragment {

    private static final int VIEW_PAGER_PAGE_COUNT = 1;
    private static final int[] VIEW_PAGER_PAGE_TITLE_RES_IDS = new int[]{
//            R.string.player_view_pager_trainer,
//            R.string.weapon_view_pager_trainer,
//            R.string.vehicle_view_pager_trainer,
            R.string.map_view_pager_trainer
    };

    public static TrainerFragment newInstance() {
        return new TrainerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager_trainer);
        viewPager.setAdapter(new ViewPagerAdapter());

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout_main);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        return view;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
//                case 0:
//                    return HelpFragment.newInstance();
//
//                case 1:
//                    return HelpFragment.newInstance();
//
//                case 2:
//                    return HelpFragment.newInstance();

                case 0:
                    return MapFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return VIEW_PAGER_PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(VIEW_PAGER_PAGE_TITLE_RES_IDS[position]);
        }

    }

}
