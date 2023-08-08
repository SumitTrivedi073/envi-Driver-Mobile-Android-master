package sgs.env.ecabsdriver.activity;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.fragment.BookedFragment;


public class DriverTripsActivity extends BaseActivity implements TabLayout.OnTabSelectedListener{

    private static final String TAG = "TripsFragment";

    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_driver_trips);

        ImageView imageViewBack = findViewById(R.id.imageBack);
        imageViewBack = findViewById(R.id.imageBack);
        imageViewBack.setOnClickListener(view -> finish());

        tabLayout =  findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager =  findViewById(R.id.pager);
        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("Booked"));
        /*tabLayout.addTab(tabLayout.newTab().setText("Canceled"));*/
        tabLayout.addOnTabSelectedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("OnPageSelected", " " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: " + state);
            }
        });
    }

    @Override
    protected void refresh() {

    }

    @Override
    protected int setLayoutIfneeded() {
        return 0;
    }

    @Override
    protected int getColor() {
        return 0;
    }

    @Override
    protected void retrivedLocation(Location location) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabUnselected: ");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabReselected:  ");
    }

    private class HomePagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;

        public HomePagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    return new BookedFragment();
              /*  case 1:
                    return new CanceledFragment();*/
            }
            return null;
        }

        public int getCount() {
            return 1;
        }
    }
}
