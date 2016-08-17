package com.onecivilization.MyOptimize.Model;

import java.util.Calendar;
import java.util.Date;

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
    private String analysis = "";
    private String solution = "";
    private int rank = 0;
    private int order = 0;
    private long createTime = 0L;
    private long solvedTime = 0L;
    private long archivedTime = 0L;

    public Problem(String title, String description, int rank, int order, long createTime) {
        this.title = title;
        this.description = description;
        this.rank = rank;
        this.order = order;
        this.createTime = createTime;
    }

    public Problem(String title, String description, String analysis, String solution, int rank, int order, long createTime, long solvedTime) {
        this.title = title;
        this.description = description;
        this.analysis = analysis;
        this.solution = solution;
        this.rank = rank;
        this.order = order;
        this.createTime = createTime;
        this.solvedTime = solvedTime;
    }

    public Problem(String title, String description, String analysis, String solution, int rank, long createTime, long solvedTime, long archivedTime) {
        this.title = title;
        this.description = description;
        this.analysis = analysis;
        this.solution = solution;
        this.rank = rank;
        this.order = 0;
        this.createTime = createTime;
        this.solvedTime = solvedTime;
        this.archivedTime = archivedTime;
    }

    public int getExistedDays() {
        Calendar calendar = Calendar.getInstance();
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int yearNow = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(createTime);
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        int days = (int) (new Date(yearNow, monthNow, dayNow).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
        return days + 1;
    }

    public int getDays() {
        Calendar calendar = Calendar.getInstance();
        if (isSolved()) {
            calendar.setTimeInMillis(solvedTime);
        }
        int dayNow = calendar.get(Calendar.DAY_OF_YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int yearNow = calendar.get(Calendar.YEAR);
        calendar.setTimeInMillis(createTime);
        int yearCreated = calendar.get(Calendar.YEAR);
        int monthCreated = calendar.get(Calendar.MONTH);
        int dayCreated = calendar.get(Calendar.DAY_OF_YEAR);
        int days = (int) (new Date(yearNow, monthNow, dayNow).getTime() - new Date(yearCreated, monthCreated, dayCreated).getTime()) / 86400000;
        return days + 1;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int decreaseOrder() {
        order--;
        return order + 1;
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

    public boolean isSolved() {
        return solvedTime != 0;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRankOrder() {
        if (isSolved()) return 0;
        return rank;
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

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public long getArchivedTime() {
        return archivedTime;
    }

    public void setArchivedTime(long archivedTime) {
        this.archivedTime = archivedTime;
    }
}
