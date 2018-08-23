package com.example.baselibrary.dao.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.jetbrains.annotations.NotNull;

/**
 * Created by guobiao on 2018/8/16.
 */

@Entity()
public class DeviceUseRecords {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
//    @Index(unique = true)
    private String userId;

    private String deviceId;

    private String time;

    private String startDate;
    private String endDate;
    @Generated(hash = 1008431706)
    public DeviceUseRecords(Long id, @NotNull String userId, String deviceId,
            String time, String startDate, String endDate) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
        this.time = time;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Generated(hash = 478299690)
    public DeviceUseRecords() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getStartDate() {
        return this.startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return this.endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "DeviceUseRecords{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", time='" + time + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
