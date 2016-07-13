package com.onecivilization.Optimize.CustomView;

import android.content.Context;
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
public class GridColorPickerView extends GridView {

    private ImageView selectedView;
    private int selectedColor = -1;
    private int selectedItemPosition = -1;
    private int imageResource = R.drawable.picked;
    private int[] colors;

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
                R.color.color_picker_12,
        };

        setAdapter(new GridColorPickerAdapter());

        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedView != null) {
                    selectedView.setImageResource(0);
                }
                selectedView = (ImageView) view.findViewById(R.id.color_picker_imageView);
                selectedView.setImageResource(imageResource);
                selectedColor = getResources().getColor(colors[position]);
                selectedItemPosition = position;
            }
        });

    }

    public GridColorPickerView(Context context) {
        super(context);
    }

    public GridColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColors(int[] colors) {
        this.colors = colors;
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
                drawable.setColor(getResources().getColor(colors[position]));
                if (position == selectedItemPosition) {
                    imageView.setImageResource(imageResource);
                    selectedView = imageView;
                    selectedColor = getResources().getColor(colors[position]);
                }
            } else {
                view = convertView;
            }
            return view;
        }
    }

}
