
package com.doex.demo.graphics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doex.demo.R;

public class GraphicsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.graphics_view, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView colorView = (TextView) getActivity().findViewById(R.id.color);
        TextView styleView = (TextView) getActivity().findViewById(R.id.style);
        colorView.setText(Html.fromHtml(getString(R.string.colorText)));
        styleView.setText(Html.fromHtml(getString(R.string.styleText)));
        
    }
}
