package com.personalassistant.views.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.personalassistant.R;
import com.personalassistant.adapter.WalkThroughAdapter;
import com.personalassistant.enities.WalkThrough;
import com.personalassistant.utils.CircleIndicatorView;

import java.util.ArrayList;

public class WalkThroughActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private Context mContext;
    private WalkThroughAdapter mWalkThroughAdapter;
    private ArrayList<WalkThrough> walkThroughModelArrayList = new ArrayList<>();
    private ImageView mPreviousIV, mNextIV;
    private TextView mGetStartedTV;
    private CircleIndicatorView mCircleIndicatorView;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough_layout);
        setStatusBackgroundColor();
        mContext = WalkThroughActivity.this;
        initViews();
        mWalkThroughAdapter = new WalkThroughAdapter(mContext, getDataList(walkThroughModelArrayList));
        mViewPager.setAdapter(mWalkThroughAdapter);
        mCircleIndicatorView.setPageIndicators(walkThroughModelArrayList.size());
    }

    private void initViews() {
        mPreviousIV = findViewById(R.id.action_previous_iv);
        mNextIV = findViewById(R.id.action_next_iv);
        mGetStartedTV = findViewById(R.id.get_started_tv);
        mViewPager = findViewById(R.id.walk_through_pager);
        mCircleIndicatorView = findViewById(R.id.circle_indicator_view);
        mViewPager.addOnPageChangeListener(this);
        mGetStartedTV.setOnClickListener(this);
        mPreviousIV.setOnClickListener(this);
        mNextIV.setOnClickListener(this);
        hideFinish(false);
        fadeOut(mPreviousIV, false);


    }

    private void setStatusBackgroundColor() {
        try {
            View decor = getWindow().getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_white_1000));
            } else {
                decor.setSystemUiVisibility(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<WalkThrough> getDataList(ArrayList<WalkThrough> walkThroughModelArrayList) {
        walkThroughModelArrayList.add(new WalkThrough("Manage Expense", getString(R.string.str_expense_manager_content), R.drawable.ic_walkthrough_one));
        walkThroughModelArrayList.add(new WalkThrough("Manage Budgets", getString(R.string.str_manage_budgets_content), R.drawable.ic_walkthrough_two));
        walkThroughModelArrayList.add(new WalkThrough("Manage Report", getString(R.string.str_manage_report_content), R.drawable.ic_walkthrough_three));
        return walkThroughModelArrayList;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int firstPagePosition = 0;
        int lastPagePosition = mWalkThroughAdapter.getCount() - 1;
        mCircleIndicatorView.setCurrentPage(position);

        if (position == lastPagePosition) {
            fadeOut(mCircleIndicatorView);
            showFinish();
            fadeOut(mNextIV);
            fadeIn(mPreviousIV);
        } else if (position == firstPagePosition) {
            fadeOut(mPreviousIV);
            fadeIn(mNextIV);
            hideFinish();
            fadeIn(mCircleIndicatorView);
        } else {
            fadeIn(mCircleIndicatorView);
            hideFinish();
            fadeIn(mPreviousIV);
            fadeIn(mNextIV);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        boolean isInFirstPage = mViewPager.getCurrentItem() == 0;
        boolean isInLastPage = mViewPager.getCurrentItem() == mWalkThroughAdapter.getCount() - 1;
        if (i == R.id.get_started_tv && isInLastPage) {
            onFinishButtonPressed();
        } else if (i == R.id.action_previous_iv && !isInFirstPage) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        } else if (i == R.id.action_next_iv && !isInLastPage) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        }
    }

    //Animation start
    private void hideFinish(boolean delay) {

        long duration = 0;
        if (delay) {
            duration = 250;
        }

        this.mGetStartedTV.animate().translationY(this.mGetStartedTV.getBottom() + dpToPixels(100, this)).setInterpolator(new AccelerateInterpolator()).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                mGetStartedTV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }

    private void fadeOut(View v) {
        fadeOut(v, true);
    }

    private void fadeOut(final View v, boolean delay) {

        long duration = 0;
        if (delay) {
            duration = 300;
        }

        if (v.getVisibility() != View.GONE) {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setDuration(duration);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(fadeOut);
        }
    }

    private void showFinish() {
        mGetStartedTV.setVisibility(View.VISIBLE);
        this.mGetStartedTV.animate().translationY(0 - dpToPixels(5, this)).setInterpolator(new DecelerateInterpolator()).setDuration(500).start();
    }

    private void fadeIn(final View v) {

        if (v.getVisibility() != View.VISIBLE) {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(300);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    v.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(fadeIn);
        }
    }

    public float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    private void hideFinish() {
        hideFinish(true);
    }

    //end
    private void onFinishButtonPressed() {
        startActivity(new Intent(WalkThroughActivity.this, LoginManagementActivity.class));
        overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
        finish();
    }
}
