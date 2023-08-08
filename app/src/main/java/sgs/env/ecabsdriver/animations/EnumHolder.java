package sgs.env.ecabsdriver.animations;

/**
 * Created by Lenovo on 4/6/2018.
 */

public enum EnumHolder {
    BounceInUp(BounceInUpAnimator.class),
    BounceIn(BounceInAnimator.class),
    Pulse(PulseAnimator.class);

    private final Class animatorClazz;
    EnumHolder(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        }
        catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
