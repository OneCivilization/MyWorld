package com.onecivilization.Optimize.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.onecivilization.Optimize.R;

/**
 * Created by CGZ on 2016/7/9.
 */
public class GridColorPicker extends GridView {

    private int selectedItemPosition = -1;
    private int selectedColor = -1;
    private ImageView selectedView;
    private boolean isEnabled = true;
    private int[] colors;

    private int imageResource;
    private int lockedImageResource;

    {
        colors = new int[]{R.color.color_picker_1,
                R.color.color_picker_2,
                R.color.color_picker_3,
                R.color.color_picker_4,
                R.color.color_picker_5,
                R.color.color_picker_6,
                R.color.color_picker_7,
                R.color.color_picker_8,
                R.color.color_picker_9,
                R.color.color_picker_10,
                R.color.color_picker_11,
                R.color.color_picker_12
        };
        for (int i = 0; i < colors.length; i++) {
            colors[i] = getResources().getColor(colors[i]);
        }
        setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    public GridColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GridColorPicker, defStyleAttr, 0);
        try {
            imageResource = typedArray.getResourceId(R.styleable.GridColorPicker_pickedImage, R.drawable.picked);
            lockedImageResource = typedArray.getResourceId(R.styleable.GridColorPicker_lockedImage, R.drawable.picked_locked);
        } finally {
            typedArray.recycle();
        }
        setAdapter(new GridColorPickerAdapter());
        setOnItemClickListener(new MyOnItemClickListener());
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        if (isEnabled) {
            if (!this.isEnabled) {
                if (selectedView != null) {
                    selectedView.setImageResource(imageResource);
                }
                setOnItemClickListener(new MyOnItemClickListener());
                this.isEnabled = true;
            }
        } else {
            if (selectedView != null) {
                selectedView.setImageResource(lockedImageResource);
            }
            setOnItemClickListener(null);
            this.isEnabled = false;
        }
    }

    public void setColors(int[] colors) {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = getResources().getColor(colors[i]);
        }
        this.colors = colors;
        ((BaseAdapter) getAdapter()).notifyDataSetChanged();
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    @Override
    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    @Override
    public void setSelection(int position) {
        selectedItemPosition = position;
        if (selectedView != null) {
            selectedView.setImageResource(0);
        }
        View view;
        if ((view = getChildAt(selectedItemPosition)) != null && (selectedView = (ImageView) view.findViewById(R.id.color_picker_imageView)) != null) {
            selectedView.setImageResource(imageResource);
        }
        selectedColor = colors[position];
        selectedItemPosition = position;
    }

    public void setSelectionByColor(int selectedColor) {
        for (int position = 0; position < colors.length; position++) {
            if (colors[position] == selectedColor) {
                setSelection(position);
                break;
            }
        }
    }

    private class GridColorPickerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return colors.length;
        }

        @Override
        public Object getItem(int position) {
            return colors[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.item_color_picker, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.color_picker_imageView);
                GradientDrawable drawable = (GradientDrawable) imageView.getBackground();
                drawable.setColor(colors[position]);
                if (position == selectedItemPosition) {
                    if (isEnabled) {
                        imageView.setImageResource(imageResource);
                    } else {
                        imageView.setImageResource(lockedImageResource);
                    }
                    selectedView = imageView;
                    selectedColor = colors[position];
                }
            } else {
                view = convertView;
            }
            return view;
        }
    }

    private class MyOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (selectedView != null) {
                selectedView.setImageResource(0);
            }
            if (selectedItemPosition == 0) {
                View container;
                if ((container = getChildAt(0)) != null && (selectedView = (ImageView) container.findViewById(R.id.color_picker_imageView)) != null) {
                    selectedView.setImageResource(0);
                }
            }
            selectedView = (ImageView) view.findViewById(R.id.color_picker_imageView);
            selectedView.setImageResource(imageResource);
            selectedColor = colors[position];
            selectedItemPosition = position;
        }
    }

}
