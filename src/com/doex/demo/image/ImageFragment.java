
package com.doex.demo.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.doex.demo.R;

public class ImageFragment extends Fragment implements OnClickListener {

    ImageView imgBtn;
    ImageView big;
    ImageView middle;
    ImageView small;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_view, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();
        big = (ImageView) activity.findViewById(R.id.big);
        big.setOnClickListener(this);
        middle = (ImageView) activity.findViewById(R.id.middle);
        middle.setOnClickListener(this);
        small = (ImageView) activity.findViewById(R.id.small);
        small.setOnClickListener(this);
    }

    private Bitmap getBitmpWithFactory(int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap m = BitmapFactory.decodeResource(getResources(), R.drawable.badge_1_1_big, options);
        options.inJustDecodeBounds = false;
        int be = options.outHeight / height;
        options.inSampleSize = 3;

        m = BitmapFactory.decodeResource(getResources(), R.drawable.badge_1_1_big, options);
        // options.inPreferredConfig = Bitmap.Config.RGB_565;
        // options.inDensity = getResources().getDisplayMetrics().densityDpi;
        // options.inTargetDensity =
        // getResources().getDisplayMetrics().densityDpi;
        // options.outHeight = height;
        // options.outWidth = width;
        // options.inSampleSize = 1;
        // Bitmap newbmp = BitmapFactory.decodeResource(getResources(),
        // R.drawable.badge_1_1_big,
        // options);
        return m;
    }

    private Bitmap getBitmp(Bitmap bitmap, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap newbmp = BitmapFactory.decodeResource(getResources(), R.drawable.badge_1_1_big,
                options);
        return newbmp;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

        // load the origial Bitmap
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
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap.Config config = Bitmap.Config.RGB_565;

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        // return new BitmapDrawable(resizedBitmap);
        return resizedBitmap;

    }

    @Override
    public void onClick(View v) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.badge_1_1_big);
        Bitmap bit = null;
        bit = resizeImage(bitmap, 88, 104);
        switch (v.getId()) {
            case R.id.big:
//                 bit = resizeImage(bitmap, 150, 178);
                big.setImageBitmap(bit);
            case R.id.middle:
//                bit = resizeImage(bitmap, 90, 96);
                middle.setImageBitmap(bit);
                break;
            case R.id.small:
                bit = resizeImage(bitmap, 45, 45);
                small.setImageBitmap(bit);
                break;
        }
    }

}
