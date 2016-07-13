package com.onecivilization.Optimize.Model;

/**
 * Created by CGZ on 2016/7/8.
 */
public class Problem {

    public static final int LOW = 1;
    public static final int NORMAL = 2;
    public static final int HIGH = 3;
    public static final int EXTRA_HIGH = 4;
    private String title = "";
    private String description = "";
    private String cause = "";
    private String solution = "";
    private String category = "";
    private int priority = 0;
    private int order = 0;
    private boolean isSolved = false;
    private boolean isArchived = false;
    private long createTime = 0L;
    private long solvedTime = 0L;

    {
        createTime = System.currentTimeMillis();
    }

    public Problem(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public long getSolvedTime() {
        return solvedTime;
    }

    public void setSolvedTime(long solvedTime) {
        this.solvedTime = solvedTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
