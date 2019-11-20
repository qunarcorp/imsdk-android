package com.qunar.im.base.jsonbean;

import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码级日志
 */
public class LogInfo {
    private long threadId;//线程id
    private String threadName;//线程名称
    private boolean isMainThread;//是否祝线程
    private long reportTime;//日志上报时间

    private String describtion;//日志描述
    private String eventId;
    private String currentPage;//当前页面
    private String type;//日志类型
    private String subType;//子类型

    private long costTime;//耗时
    private String method;
    private String url;
    private String methodParams;//参数json串
    private String response;
    private List<SQL> sql = new ArrayList<>();

    public static class SQL{
        public String content;//sql串
        public long time;//sql耗时
        public String args;
    }

    public LogInfo(){
        this("","","");
    }

    public LogInfo(String type){
        this(type,"");
    }

    public LogInfo(String type, String subType){
        this(type,subType,"");
    }

    public LogInfo(String type, String subType, String describtion){
        this(type,subType,"",describtion,"");
    }

    public LogInfo(String type, String subType, String eventId,String describtion,String currentPage){
        this.type = type;
        this.subType = subType;
        this.eventId = eventId;
        this.describtion = describtion;
        this.currentPage = currentPage;
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        this.isMainThread = Looper.getMainLooper().getThread() == Thread.currentThread();
        this.reportTime = System.currentTimeMillis();
    }

    public LogInfo type(String type){
        this.type = type;
        return this;
    }

    public LogInfo subType(String subType){
        this.subType = subType;
        return this;
    }

    public LogInfo eventId(String eventId){
        this.eventId = eventId;
        return this;
    }

    public LogInfo describtion(String describtion){
        this.describtion = describtion;
        return this;
    }

    public LogInfo currentPage(String currentPage){
        this.currentPage = currentPage;
        return this;
    }

    public LogInfo costTime(long costTime){
        this.costTime = costTime;
        return this;
    }

    public LogInfo method(String method){
        this.method = method;
        return this;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isMainThread() {
        return isMainThread;
    }

    public void setMainThread(boolean mainThread) {
        isMainThread = mainThread;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<SQL> getSql() {
        return sql;
    }

    public void setSql(List<SQL> sql) {
        this.sql = sql;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }
}
