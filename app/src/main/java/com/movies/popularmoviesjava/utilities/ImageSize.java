package com.movies.popularmoviesjava.utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ImageSize {
    public static final List<String> imageSize = new ArrayList<>();

    static {
        imageSize.add("w92");
        imageSize.add("154");
        imageSize.add("w185");
        imageSize.add("w342");
        imageSize.add("w500");
        imageSize.add("w780");
        imageSize.add("original");

    }

    public static String getImageSize(int index) {
        String posterSize = "";
        try {
            posterSize = imageSize.get(index);
        } catch (IndexOutOfBoundsException exception) {
            Log.e("FAILED", "Image size is out of boundary", exception);
        }
        return posterSize;
    }
}
