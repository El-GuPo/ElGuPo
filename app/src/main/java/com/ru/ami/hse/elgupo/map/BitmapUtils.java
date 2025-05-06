package com.ru.ami.hse.elgupo.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.ru.ami.hse.elgupo.ElGupoApplication;

public class BitmapUtils {
    public static Bitmap createBitmapFromVector(Context context, int art) {
        if (context == null) {
            return null;
        }

        try {
            Drawable drawable = ContextCompat.getDrawable(ElGupoApplication.getAppContext(), art);
            if (drawable == null) {
                return null;
            }
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            Log.e("BitmapUtils", "Error creating bitmap", e);
            return null;
        }
    }
}
