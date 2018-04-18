package com.denuinc.bookxchange.vo;

import com.denuinc.bookxchange.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian on 3/25/2018.
 */

public class Categories {

    private List<Category> categories;

    public Categories() {
        this.categories = new ArrayList<>();
        categories.add(new Category(R.drawable.ic_whatshot_selector, R.string.cat_all, "All"));
        categories.add(new Category(R.drawable.ic_art_selector, R.string.cat_art_and_photo, "subject:art"));
        categories.add(new Category(R.drawable.ic_biographies_selector, R.string.cat_bio_and_memoirs, "subject:biography"));
        categories.add(new Category(R.drawable.ic_businesse_selector, R.string.cat_bus_and_invest, "subject:business"));
        categories.add(new Category(R.drawable.ic_comic_selector, R.string.cat_comics_and_novels, "subject:novel"));
        categories.add(new Category(R.drawable.ic_child_selector, R.string.cat_child, "subject:children"));
        categories.add(new Category(R.drawable.ic_cooking_selector, R.string.cat_food, "subject:cooking"));
        categories.add(new Category(R.drawable.ic_history_selector, R.string.cat_history, "subject:history"));
        categories.add(new Category(R.drawable.ic_fiction_selector, R.string.cat_lit_and_fiction, "subject:fiction"));
        categories.add(new Category(R.drawable.ic_mystery_selector, R.string.cat_myst_and_suspense, "subject:mystery"));
        categories.add(new Category(R.drawable.ic_science_fiction_selector, R.string.cat_sci_fi_and_fantasy, "subject:science+fiction"));
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
