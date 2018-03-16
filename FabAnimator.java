
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

class FabAnimator {

    private static final String TAG = FabAnimator.class.getSimpleName();
    private static final long FAB_ANIMATION_DURATION = 200;
    private FloatingActionButton fab;
    private ViewGroup container;
    private boolean isAnimating;

    private Rect fabRect = new Rect();
    private Rect containerRect = new Rect();

    FabAnimator(FloatingActionButton fab, ViewGroup container) {
        this.fab = fab;
        this.container = container;
    }

    boolean expand(final OnAnimationEndListener onAnimationEndListener) {
        if (isAnimating) {
            return false;
        }
        isAnimating = true;
        refreshViewRects();

        float translationX = getTranslationXForExpand();
        float translationY = getTranslationYForExpand();

        animate(translationX, translationY, new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animator, int x, int y) {
                onAnimationEndListener.onAnimationEnd(animator, x, y);
                fab.setVisibility(View.INVISIBLE);
            }
        }, new AccelerateInterpolator(1.25f), new LinearInterpolator());

        return true;
    }

    boolean collapse(final OnAnimationEndListener onAnimationEndListener) {
        if (isAnimating) {
            return false;
        }
        isAnimating = true;
        refreshViewRects();

        fab.setVisibility(View.VISIBLE);

        float translationX = getTranslationXCollapse();
        float translationY = getTranslationYCollapse();

        animate(translationX, translationY, onAnimationEndListener, new DecelerateInterpolator(), new DecelerateInterpolator(0.5f));

        return true;
    }

    private void animate(float translationX, float translationY, final OnAnimationEndListener onAnimationEndListener,
                         Interpolator xInterpolator, Interpolator yInterpolator) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(fab, "translationX", translationX);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(fab, "translationY", translationY);

        xAnimator.setInterpolator(xInterpolator);
        yAnimator.setInterpolator(yInterpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(xAnimator).with(yAnimator);
        animatorSet.setDuration(FAB_ANIMATION_DURATION);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                int[] fabCenter = getFabCenter();
                onAnimationEndListener.onAnimationEnd(animation, fabCenter[0], fabCenter[1]);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    private float getTranslationXForExpand() {
        return (containerRect.left - fabRect.left + container.getWidth() / 1.5f - fab.getWidth() / 2f);
    }

    private float getTranslationYForExpand() {
        return (containerRect.top - fabRect.top + container.getHeight() / 2f - fab.getHeight() / 2f);
    }

    private float getTranslationXCollapse() {
        return 0;
    }

    private float getTranslationYCollapse() {
        return 0;
    }

    private void refreshViewRects() {
        fab.getGlobalVisibleRect(fabRect);
        container.getGlobalVisibleRect(containerRect);
    }

    int[] getFabCenter() {
        int[] center = new int[2];
        refreshViewRects();
        center[0] = fabRect.left - containerRect.left + fab.getWidth() / 2;
        center[1] = fabRect.top - containerRect.top + fab.getHeight() / 2;
        return center;
    }


    interface OnAnimationEndListener {
        void onAnimationEnd(Animator animator, int x, int y);
    }
}
