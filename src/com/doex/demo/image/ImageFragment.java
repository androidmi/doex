
package com.doex.demo.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.doex.demo.R;

public class ImageFragment extends Fragment {

    private ImageView mLeftImage;
    private ImageView mRightImage;
    private ImageView mBottomRightImage;
    private ImageView mBottomLeftImage;

    String file = "/mnt/sdcard/xxx.png";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_view, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();
        mLeftImage = (ImageView) activity.findViewById(R.id.left);
        mRightImage = (ImageView) activity.findViewById(R.id.right);
        mBottomRightImage = (ImageView) activity.findViewById(R.id.bottom_right);
        mBottomLeftImage = (ImageView) activity.findViewById(R.id.bottom_left);
        init();
    }

    private void init() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.badge_1_2_middle);
        Bitmap fileBitmap = BitmapFactory.decodeFile(file);
        Bitmap map = resizeImage(fileBitmap, bitmap.getWidth(), bitmap.getHeight());
        mLeftImage.setImageBitmap(map);

        Bitmap leftBottom = Bitmap.createScaledBitmap(fileBitmap, 100,
                100, true);
        Bitmap rightBottom = resizeImage(fileBitmap, 100, 100);
        mBottomLeftImage.setImageBitmap(leftBottom);
        mBottomRightImage.setImageBitmap(rightBottom);
    }

    /**
     * the same as Bitmap.createScaledBitmap(fileBitmap, 100, 100, true);
     * 
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    private Bitmap resizeImage(Bitmap bitmap, int w, int h) {

        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        return resizedBitmap;

    }

}
