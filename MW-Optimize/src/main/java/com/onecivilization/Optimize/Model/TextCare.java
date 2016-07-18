package com.onecivilization.Optimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public class TextCare extends Care {

    private int color;

    {
        type = TEXT;
    }

    public TextCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, int color) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime);
        this.color = color;
    }

    public TextCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, int color) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime);
        this.color = color;
    }

    public TextCare(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime, int color) {
        super(title, descriptionTitle, description, descriptionLastEditedTime, order, createTime, achievedTime, archivedTime);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
