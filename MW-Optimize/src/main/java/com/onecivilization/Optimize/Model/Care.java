package com.onecivilization.Optimize.Model;

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

    public static final int STATE_NONE = 0;
    public static final int STATE_DONE = 1;
    public static final int STATE_UNDONE = 2;
    public static final int STATE_LOCKED = 3;

    protected String title = "";
    protected String descriptionTitle = "";
    protected String description = "";
    protected long descriptionLastEditedTime = 0L;
    protected int type = 0;
    protected int state = 0;
    protected int order = 0;
    protected String category = "";
    protected long createTime = 0L;
    protected long achievedTime = 0L;
    protected long archivedTime = 0L;

    public Care(String title, String descriptionTitle, String description, long descriptionLastEditedTime, int state, int order, String category, long createTime, long achievedTime, long archivedTime) {
        this.title = title;
        this.descriptionTitle = descriptionTitle;
        this.description = description;
        this.descriptionLastEditedTime = descriptionLastEditedTime;
        this.state = state;
        this.order = order;
        this.category = category;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getAchievedTime() {
        return achievedTime;
    }

    public void setAchievedTime(long achievedTime) {
        this.achievedTime = achievedTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return !(archivedTime == 0L);
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

    public void setType(int type) {
        this.type = type;
    }
}
