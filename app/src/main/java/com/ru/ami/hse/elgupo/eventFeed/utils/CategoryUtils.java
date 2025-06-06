package com.ru.ami.hse.elgupo.eventFeed.utils;

import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.dataclasses.Category;

public class CategoryUtils {
    public static int categoryColor(Category category) {
        int categoryId = category.getId();
        if (categoryId == Category.CINEMA.getId()) {
            return R.color.purple_500;
        } else if (categoryId == Category.SPORT.getId()) {
            return R.color.blue_500;
        } else if (categoryId == Category.EXHIBITION.getId()) {
            return R.color.green_700;
        } else if (categoryId == Category.CONCERTS.getId()) {
            return R.color.orange_500;
        } else if (categoryId == Category.THEATER.getId()) {
            return R.color.blue_400;
        } else {
            return R.color.grey_600;
        }
    }
}
