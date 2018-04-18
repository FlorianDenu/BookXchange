package com.denuinc.bookxchange.ui.common;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.databinding.CategoryItemBinding;
import com.denuinc.bookxchange.utils.Objects;
import com.denuinc.bookxchange.vo.Category;

/**
 * Created by Florian on 3/25/2018.
 */

public class CategoryAdapter extends DataBoundListAdapter<Category, CategoryItemBinding> {

    private final DataBindingComponent dataBindingComponent;
    private final CategoryClickCallback categoryClickCallback;
    private ImageView oldImageView;
    private TextView oldTextView;

    public CategoryAdapter(DataBindingComponent dataBindingComponent, CategoryClickCallback categoryClickCallback) {
        this.dataBindingComponent = dataBindingComponent;
        this.categoryClickCallback = categoryClickCallback;
    }

    @Override
    protected CategoryItemBinding createBinding(ViewGroup parent) {
        CategoryItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.category_item, parent, false, dataBindingComponent);
        binding.imageCategory.setSelected(false);
        binding.getRoot().setOnClickListener(v -> {
            Category category = binding.getCategory();
            if (category != null && categoryClickCallback != null) {
                if (oldImageView != null && oldTextView != null) {
                    oldImageView.setSelected(false);
                    oldTextView.setSelected(false);
                }
                oldImageView = binding.imageCategory;
                oldTextView = binding.tvCategoryDescription;
                binding.imageCategory.setSelected(true);
                binding.tvCategoryDescription.setSelected(true);
                categoryClickCallback.onClick(category);
            }
        });
        return binding;
    }

    @Override
    protected void bind(CategoryItemBinding binding, Category category) {
        binding.setCategory(category);
    }

    @Override
    protected boolean areItemsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.getDescriptionId(), newItem.getDescriptionId()) &&
                Objects.equals(oldItem.getImageId(), newItem.getImageId());
    }


    @Override
    protected boolean areContentsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.getDescriptionId(), newItem.getDescriptionId());
    }

    public interface CategoryClickCallback {
        void onClick(Category category);
    }
}
