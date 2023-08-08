package sgs.env.ecabsdriver.animations;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by Lenovo on 4/9/2018.
 */

public class BounceInUpAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "translationY", target.getMeasuredHeight(), -30, 10, 0),
                ObjectAnimator.ofFloat(target, "alpha", 0, 1, 1, 1)
        );
    }

}

