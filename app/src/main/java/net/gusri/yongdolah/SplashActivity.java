package net.gusri.yongdolah;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private LinearLayout dotsLayout;
    private int[] mLayouts;
    private TextView[] dots;
    private ViewPagerAdapter mViewPagerAdapter;
    private Button mInSplashBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_splash);
        mViewPager = (ViewPager) findViewById(R.id.View_Pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layouts_Dots);
        mInSplashBtn = (Button) findViewById(R.id.btn_InSplash);

        mLayouts = new int[]
                {
                        R.layout.splash_page2,
                        R.layout.splash_page3,
                        R.layout.splash_page4
                };

        addBottomDots(0);
        ChangeBarColor();
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(viewListener);

        mInSplashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InSplashIntent = new Intent(SplashActivity.this, LoginActivity.class);
                InSplashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(InSplashIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == mLayouts.length - 1) {
            } else {
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void addBottomDots(int CurrentPage) {
        dots = new TextView[mLayouts.length];
        int[] colorActive = getResources().getIntArray(R.array.dot_active);
        int[] colorInactive = getResources().getIntArray(R.array.dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(colorInactive[CurrentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[CurrentPage].setTextColor(colorActive[CurrentPage]);
    }

    private void ChangeBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Informasi! ...")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Apakah Anda ingin keluar dari Applikasi? ...")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Tidak", null).show();
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mInflater;

        @Override
        public java.lang.Object instantiateItem(ViewGroup container, int position) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = mInflater.inflate(mLayouts[position], container, false);
            container.addView(v);
            return v;
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }
    }
}


