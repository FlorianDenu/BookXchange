package com.denuinc.bookxchange.ui;

import android.content.Context;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.binding.FragmentDataBindingComponent;
import com.denuinc.bookxchange.databinding.CategoriesLayoutBinding;
import com.denuinc.bookxchange.ui.common.CategoryAdapter;
import com.denuinc.bookxchange.ui.common.CenterLayoutManager;
import com.denuinc.bookxchange.utils.AutoClearedValue;
import com.denuinc.bookxchange.vo.Categories;
import com.denuinc.bookxchange.vo.Category;

/**
 * Created by Florian on 3/25/2018.
 */

public class CategoriesFragment extends Fragment implements CategoryAdapter.CategoryClickCallback {

    private DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private AutoClearedValue<CategoryAdapter> adapter;
    private AutoClearedValue<CategoriesLayoutBinding> binding;
    private OnCategorieSelectedListener onCategorieSelectedListener;
    private Categories categories;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CategoriesLayoutBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.categories_layout, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onCategorieSelectedListener = (OnCategorieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CategoryAdapter categoryAdapter = new CategoryAdapter( dataBindingComponent, this);
        binding.get().recyclerViewCategories.setAdapter(categoryAdapter);
        adapter = new AutoClearedValue<>(this, categoryAdapter);
        initRecyclerView();
    }

    private void initRecyclerView() {
        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        CenterLayoutManager layoutManager = new CenterLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.get().recyclerViewCategories.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(binding.get().recyclerViewCategories);
        binding.get().recyclerViewCategories.setOnFlingListener(snapHelper);
        this.categories = new Categories();
        binding.get().setCategories(categories);
        adapter.get().replace(categories.getCategories());
        binding.get().executePendingBindings();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(Category category) {
        onCategorieSelectedListener.categorySelected(category);
        int position = categories.getCategories().indexOf(category);
        binding.get().recyclerViewCategories.smoothScrollToPosition(position);
    }

}

interface OnCategorieSelectedListener {
    void categorySelected(Category category);
}


