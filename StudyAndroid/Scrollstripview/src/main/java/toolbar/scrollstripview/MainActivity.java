package toolbar.scrollstripview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    static List<PagerItem> mPagerItems = new ArrayList<>() ;
    static {
        mPagerItems.add(new PagerItem("周四06-12","今天"));
        mPagerItems.add(new PagerItem("周五06-13","周五06-13"));
        mPagerItems.add(new PagerItem("周六06-14","周六06-14"));
        mPagerItems.add(new PagerItem("周日06-15","周日06-15"));
        mPagerItems.add(new PagerItem("周一06-16","今天"));
        mPagerItems.add(new PagerItem("周二06-17","今天"));
        mPagerItems.add(new PagerItem("周三06-18","今天"));
        mPagerItems.add(new PagerItem("周四06-19","今天"));
        mPagerItems.add(new PagerItem("周五06-20","今天"));
        mPagerItems.add(new PagerItem("周六06-21","今天"));
        mPagerItems.add(new PagerItem("周日06-22","今天"));
        mPagerItems.add(new PagerItem("周一06-23","今天"));
        mPagerItems.add(new PagerItem("周二06-24","今天"));
    }

    private ViewPager mViewPager ;

    private SlidingTabLayout mLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.id_view_pager) ;
        mViewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager()));
        mLayout = (SlidingTabLayout) findViewById(R.id.id_sliding_view) ;
        mLayout.setViewPager(mViewPager);
    }

    public class FragmentPageAdapter extends FragmentPagerAdapter {

        private Fragment mContentFragment ;
        private Map<Integer, Fragment> mFragmentMap = new HashMap<>() ;

        public FragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mPagerItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            mContentFragment = mFragmentMap.get(position);{
                if (mContentFragment == null){
                    mContentFragment =  mPagerItems.get(position).createFragment(position);
                    mFragmentMap.put(position,mContentFragment) ;
                }
            }
            return mContentFragment ;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagerItems.get(position).getTitle();
        }

    }
}
