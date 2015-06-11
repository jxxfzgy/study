package toolbar.scrollstripview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by moon.zhong on 2015/5/25.
 */
public class ContentFragment extends Fragment {
    private final static String KEY_TITLE = "title";



    public static ContentFragment newInstance(CharSequence department) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, department);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        TextView mTitle = (TextView) view.findViewById(R.id.id_show_text);
        mTitle.setText(bundle.getString(KEY_TITLE));
    }



}
