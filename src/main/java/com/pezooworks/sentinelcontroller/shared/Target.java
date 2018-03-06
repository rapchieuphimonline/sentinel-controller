package com.pezooworks.sentinelcontroller.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 16/12/2017.
 */
public class Target {
    private String id, url;
    private List<String> anchor_ids = new ArrayList<>();
    private boolean finished;

    public Target(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAnchor_ids() {
        return anchor_ids;
    }

    public void setAnchor_ids(List<String> anchor_ids) {
        this.anchor_ids = anchor_ids;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
