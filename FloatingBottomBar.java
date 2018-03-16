
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class FloatingBottomBar extends FrameLayout implements View.OnClickListener,
        View.OnLongClickListener {

    private static final String TAG = FloatingBottomBar.class.getSimpleName();
    private static final long REVEAL_DURATION = 200;

    private boolean isExpanded;
    private boolean isAnimating;
    private FloatingActionButton fab;
    private FabAnimator fabAnimator;
    private LinearLayout contentView;

    public FloatingBottomBar(Context context) {
        this(context, null);
    }

    public FloatingBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FloatingBottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);

        contentView = new LinearLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(layoutParams);
        contentView.setBackgroundColor(ContextCompat.getColor(context, R.color.color));
        contentView.setVisibility(GONE);
        contentView.setElevation(context.getResources().getDimension(R.dimen.toolbar_elevation));
        contentView.setClipChildren(false);
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        });
        addView(contentView);
    }

    @Override
    public void onClick(View v) {

    }



    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void attachFab(FloatingActionButton fab) {
        this.fab = fab;
        fabAnimator = new FabAnimator(fab, this);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });
    }

    private void expand() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;

        fabAnimator.expand(new FabAnimator.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animator, int cx, int cy) {
                isAnimating = false;
                isExpanded = true;

                int finalRadius = Math.max(getWidth(), getHeight());

                contentView.setVisibility(VISIBLE);
                Animator anim = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, fab.getWidth() / 2, finalRadius);
                Log.d(TAG, "Animator: " + (anim instanceof ValueAnimator));
                anim.setDuration(REVEAL_DURATION);
                anim.start();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    private void collapse() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;

        int[] fabCenter = fabAnimator.getFabCenter();

        int initialRadius = getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(contentView, fabCenter[0], fabCenter[1], initialRadius, fab.getWidth() / 2);
        anim.setDuration(REVEAL_DURATION);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                contentView.setVisibility(View.GONE);
                fabAnimator.collapse(new FabAnimator.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animator animator, int x, int y) {
                        isAnimating = false;
                        isExpanded = false;
                    }
                });
            }
        });

        anim.start();
    }
}
