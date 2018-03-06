package com.pezooworks.sentinelcontroller.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 16/12/2017.
 */
public class HumanProfile {
    private String ip;
    private double webview_width, webview_height, num_pageviews, min_num_action, max_num_action, min_time_delay_schedule_ms
            , max_time_delay_schedule_ms, min_time_interval_schedule_ms, max_time_interval_schedule_ms, clickAdsRate, minSkipAdsAction
            , maxSkipAdsAction, minPercentX, maxPercentX, minPercentY, maxPercentY;

    private String userAgent;
    private List<String> humanActionList = new ArrayList<>();
    private List<String> adsIdList = new ArrayList<>();
    private List<String> directAdsList = new ArrayList<>();
    private List<String> shortenAdsList = new ArrayList<>();
    private List<String> shortenAdsSkipIdList = new ArrayList<>();
    private List<Target> targets = new ArrayList<>();
    private List<Cookies> cookies = new ArrayList<>();

    public HumanProfile(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public double getWebview_width() {
        return webview_width;
    }

    public void setWebview_width(double webview_width) {
        this.webview_width = webview_width;
    }

    public double getWebview_height() {
        return webview_height;
    }

    public void setWebview_height(double webview_height) {
        this.webview_height = webview_height;
    }

    public double getNum_pageviews() {
        return num_pageviews;
    }

    public void setNum_pageviews(double num_pageviews) {
        this.num_pageviews = num_pageviews;
    }

    public double getMin_num_action() {
        return min_num_action;
    }

    public void setMin_num_action(double min_num_action) {
        this.min_num_action = min_num_action;
    }

    public double getMax_num_action() {
        return max_num_action;
    }

    public void setMax_num_action(double max_num_action) {
        this.max_num_action = max_num_action;
    }

    public double getMin_time_delay_schedule_ms() {
        return min_time_delay_schedule_ms;
    }

    public void setMin_time_delay_schedule_ms(double min_time_delay_schedule_ms) {
        this.min_time_delay_schedule_ms = min_time_delay_schedule_ms;
    }

    public double getMax_time_delay_schedule_ms() {
        return max_time_delay_schedule_ms;
    }

    public void setMax_time_delay_schedule_ms(double max_time_delay_schedule_ms) {
        this.max_time_delay_schedule_ms = max_time_delay_schedule_ms;
    }

    public double getMin_time_interval_schedule_ms() {
        return min_time_interval_schedule_ms;
    }

    public void setMin_time_interval_schedule_ms(double min_time_interval_schedule_ms) {
        this.min_time_interval_schedule_ms = min_time_interval_schedule_ms;
    }

    public double getMax_time_interval_schedule_ms() {
        return max_time_interval_schedule_ms;
    }

    public void setMax_time_interval_schedule_ms(double max_time_interval_schedule_ms) {
        this.max_time_interval_schedule_ms = max_time_interval_schedule_ms;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public List<Cookies> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookies> cookies) {
        this.cookies = cookies;
    }

    public List<String> getHumanActionList() {
        return humanActionList;
    }

    public void setHumanActionList(List<String> humanActionList) {
        this.humanActionList = humanActionList;
    }

    public double getClickAdsRate() {
        return clickAdsRate;
    }

    public void setClickAdsRate(double clickAdsRate) {
        this.clickAdsRate = clickAdsRate;
    }

    public List<String> getAdsIdList() {
        return adsIdList;
    }

    public void setAdsIdList(List<String> adsIdList) {
        this.adsIdList = adsIdList;
    }

    public List<String> getDirectAdsList() {
        return directAdsList;
    }

    public void setDirectAdsList(List<String> directAdsList) {
        this.directAdsList = directAdsList;
    }

    public List<String> getShortenAdsList() {
        return shortenAdsList;
    }

    public void setShortenAdsList(List<String> shortenAdsList) {
        this.shortenAdsList = shortenAdsList;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public double getMinSkipAdsAction() {
        return minSkipAdsAction;
    }

    public void setMinSkipAdsAction(double minSkipAdsAction) {
        this.minSkipAdsAction = minSkipAdsAction;
    }

    public double getMaxSkipAdsAction() {
        return maxSkipAdsAction;
    }

    public void setMaxSkipAdsAction(double maxSkipAdsAction) {
        this.maxSkipAdsAction = maxSkipAdsAction;
    }

    public double getMinPercentX() {
        return minPercentX;
    }

    public void setMinPercentX(double minPercentX) {
        this.minPercentX = minPercentX;
    }

    public double getMaxPercentX() {
        return maxPercentX;
    }

    public void setMaxPercentX(double maxPercentX) {
        this.maxPercentX = maxPercentX;
    }

    public double getMinPercentY() {
        return minPercentY;
    }

    public void setMinPercentY(double minPercentY) {
        this.minPercentY = minPercentY;
    }

    public double getMaxPercentY() {
        return maxPercentY;
    }

    public void setMaxPercentY(double maxPercentY) {
        this.maxPercentY = maxPercentY;
    }

    public List<String> getShortenAdsSkipIdList() {
        return shortenAdsSkipIdList;
    }

    public void setShortenAdsSkipIdList(List<String> shortenAdsSkipIdList) {
        this.shortenAdsSkipIdList = shortenAdsSkipIdList;
    }
}
