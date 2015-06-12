package toolbar.pathanim;

import android.view.animation.Interpolator;

/**
 * Created by moon.zhong on 2015/6/12.
 * time : 17:31
 */
public class MyInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        return 2;
    }
}
