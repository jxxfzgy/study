package toolbar.scrollstripview;

import android.support.v4.app.Fragment;

/**
 * Created by moon.zhong on 2015/5/25.
 */
public class PagerItem {

    private final CharSequence mTitle;

    private final String mContent;

    public PagerItem(String mContent, CharSequence mTitle) {
        this.mContent = mContent;
        this.mTitle = mTitle;
    }

    public Fragment createFragment(int position) {
            return ContentFragment.newInstance(mContent);
    }

    public CharSequence getTitle() {
        return mContent;
    }
}
