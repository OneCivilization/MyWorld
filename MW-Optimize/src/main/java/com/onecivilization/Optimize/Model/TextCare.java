package com.onecivilization.Optimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public class TextCare extends Care {

    private int color;

    public TextCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, String category, long createTime, long achievedTime, long archivedTime, int color) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, STATE_NONE, order, category, createTime, achievedTime, archivedTime);
        this.color = color;
        type = TEXT;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
