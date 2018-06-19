package com.example.zimzik.budget;

import android.content.Context;

public class Helper {
    public static String[] getLocatedMonth(Context context) {
        return new String[]{context.getString(R.string.january),
                context.getString(R.string.february),
                context.getString(R.string.march),
                context.getString(R.string.april),
                context.getString(R.string.may),
                context.getString(R.string.june),
                context.getString(R.string.july),
                context.getString(R.string.august),
                context.getString(R.string.september),
                context.getString(R.string.october),
                context.getString(R.string.november),
                context.getString(R.string.december)};
    }
}
