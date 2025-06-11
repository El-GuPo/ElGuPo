package com.ru.ami.hse.elgupo.dataclasses;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Category implements Serializable {
    private final String title;
    private final Integer id;

    public Category(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public static Category CINEMA = new Category("Кино", 1011);
    public static Category SPORT = new Category("Спорт", 1001);
    public static Category EXHIBITION = new Category("Выставки", 8);
    public static Category CONCERTS = new Category("Концерты", 11);
    public static Category THEATER = new Category("Театр", 7);
    public static Category NOCATEGORY = new Category("Без категории", -1);

    public static Category[] CATEGORIES = {CINEMA, SPORT, EXHIBITION, CONCERTS, THEATER};

    public static Category getCategoryById(int id) {
        for (Category category : CATEGORIES) {
            if (id == category.id) {
                return category;
            }
        }
        return NOCATEGORY;
    }

    public int hashCode() {
        return id.hashCode();
    }
}
