package sgs.env.ecabsdriver.animations;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by Lenovo on 4/6/2018.
 */

public class PulseAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleY", 1, 1.1f, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 1, 1.1f, 1)
        );
    }
}
