package com.onecivilization.MyOptimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public abstract class Care {

    public static final int TEXT = 1;
    public static final int NONPERIODIC = 2;
    public static final int PERIODIC = 3;
    public static final int SUB_PERIODIC = 4;
    public static final int TIMELIMITED_PERIODIC = 5;
    public static final int COMPLEX_PERIODIC = 6;

    public static final int STATE_MINUS = 0;
    public static final int STATE_UNDONE = 1;
    public static final int STATE_NONE = 2;
    public static final int STATE_DONE = 3;
    public static final int STATE_ACHIEVED_UNDONE = 4;
    public static final int STATE_ACHIEVED = 5;

    protected String title = "";
    protected String descriptionTitle = "";
    protected String description = "";
    protected long descriptionLastEditedTime = 0L;
    protected int type = 0;
    protected int order = 0;
    protected long createTime = 0L;
    protected long achievedTime = 0L;
    protected long archivedTime = 0L;

    public Care(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime) {
        this.title = title;
        this.descriptionTitle = descriptionTitle;
        this.description = description;
        this.descriptionLastEditedTime = descriptionLastEditedTime;
        this.order = order;
        this.createTime = createTime;
    }

    public Care(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime) {
        this.title = title;
        this.descriptionTitle = descriptionTitle;
        this.description = description;
        this.descriptionLastEditedTime = descriptionLastEditedTime;
        this.order = order;
        this.createTime = createTime;
        this.achievedTime = achievedTime;
    }

    public Care(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int order, long createTime, long achievedTime, long archivedTime) {
        this.title = title;
        this.descriptionTitle = descriptionTitle;
        this.description = description;
        this.descriptionLastEditedTime = descriptionLastEditedTime;
        this.order = order;
        this.createTime = createTime;
        this.achievedTime = achievedTime;
        this.archivedTime = archivedTime;
    }

    public long getDescriptionLastEditedTime() {
        return descriptionLastEditedTime;
    }

    public void setDescriptionLastEditedTime(long descriptionLastEditedTime) {
        this.descriptionLastEditedTime = descriptionLastEditedTime;
    }

    public String getDescriptionTitle() {
        return descriptionTitle;
    }

    public void setDescriptionTitle(String descriptionTitle) {
        this.descriptionTitle = descriptionTitle;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int increaseOrder() {
        order++;
        return order - 1;
    }

    public int decreaseOrder() {
        order--;
        return order + 1;
    }

    public long getAchievedTime() {
        return achievedTime;
    }

    public void setAchievedTime(long achievedTime) {
        this.achievedTime = achievedTime;
    }

    public long getArchivedTime() {
        return archivedTime;
    }

    public void setArchivedTime(long archivedTime) {
        this.archivedTime = archivedTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAchieved() {
        return !(achievedTime == 0L);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public abstract int getState();

}
