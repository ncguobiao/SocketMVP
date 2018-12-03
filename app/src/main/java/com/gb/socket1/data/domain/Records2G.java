package com.gb.socket1.data.domain;

/**
 * Created by guobiao on 2018/8/14.
 */

public class Records2G {

    /**
     * useTemporary : {"deviceName":"CE000149-10","endDate":"","endMessage":"","id":"TEM1000000165hTstPBj","imei":"865533035338296","money":"","orderType":"0","rate":"2","startDate":"2018-8-13 16:36:39","userId":"USR1000000009zLWisgM"}
     * deviceCz : {"batteryQuantity":"","batteryType":"","batteryVoltage":"","chargeQuantity":"","chargeTime":"0","deviceGsmId":"GSM1000000047zFs9evI","deviceName":"CE000149-10","errorCode":"00","fullTime":"","id":"CZ1000001666Q4DNTWS9","imei":"865533035338296","number":"10","outputCurrent":"0.0","outputVoltage":"0.0","remainingTime":"","status":"0","sumQuantity":"0.0","temperature":"0.0","updateDate":"2018-08-14 10:04:06.0"}
     */

    private UseTemporaryBean useTemporary;
    private DeviceCzBean deviceCz;

    public UseTemporaryBean getUseTemporary() {
        return useTemporary;
    }

    public void setUseTemporary(UseTemporaryBean useTemporary) {
        this.useTemporary = useTemporary;
    }

    public DeviceCzBean getDeviceCz() {
        return deviceCz;
    }

    public void setDeviceCz(DeviceCzBean deviceCz) {
        this.deviceCz = deviceCz;
    }

    public static class UseTemporaryBean {
        /**
         * deviceName : CE000149-10
         * endDate :
         * endMessage :
         * id : TEM1000000165hTstPBj
         * imei : 865533035338296
         * money :
         * orderType : 0
         * rate : 2
         * startDate : 2018-8-13 16:36:39
         * userId : USR1000000009zLWisgM
         */

        private String deviceName;
        private String endDate;
        private String endMessage;
        private String id;
        private String imei;
        private String money;
        private String orderType;
        private String rate;
        private String startDate;
        private String userId;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getEndMessage() {
            return endMessage;
        }

        public void setEndMessage(String endMessage) {
            this.endMessage = endMessage;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class DeviceCzBean {
        /**
         * batteryQuantity :
         * batteryType :
         * batteryVoltage :
         * chargeQuantity :
         * chargeTime : 0
         * deviceGsmId : GSM1000000047zFs9evI
         * deviceName : CE000149-10
         * errorCode : 00
         * fullTime :
         * id : CZ1000001666Q4DNTWS9
         * imei : 865533035338296
         * number : 10
         * outputCurrent : 0.0
         * outputVoltage : 0.0
         * remainingTime :
         * status : 0
         * sumQuantity : 0.0
         * temperature : 0.0
         * updateDate : 2018-08-14 10:04:06.0
         */

        private String batteryQuantity;
        private String batteryType;
        private String batteryVoltage;
        private String chargeQuantity;
        private String chargeTime;
        private String deviceGsmId;
        private String deviceName;
        private String errorCode;
        private String fullTime;
        private String id;
        private String imei;
        private String number;
        private String outputCurrent;
        private String outputVoltage;
        private String remainingTime;
        private String status;
        private String sumQuantity;
        private String temperature;
        private String updateDate;

        public String getBatteryQuantity() {
            return batteryQuantity;
        }

        public void setBatteryQuantity(String batteryQuantity) {
            this.batteryQuantity = batteryQuantity;
        }

        public String getBatteryType() {
            return batteryType;
        }

        public void setBatteryType(String batteryType) {
            this.batteryType = batteryType;
        }

        public String getBatteryVoltage() {
            return batteryVoltage;
        }

        public void setBatteryVoltage(String batteryVoltage) {
            this.batteryVoltage = batteryVoltage;
        }

        public String getChargeQuantity() {
            return chargeQuantity;
        }

        public void setChargeQuantity(String chargeQuantity) {
            this.chargeQuantity = chargeQuantity;
        }

        public String getChargeTime() {
            return chargeTime;
        }

        public void setChargeTime(String chargeTime) {
            this.chargeTime = chargeTime;
        }

        public String getDeviceGsmId() {
            return deviceGsmId;
        }

        public void setDeviceGsmId(String deviceGsmId) {
            this.deviceGsmId = deviceGsmId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getFullTime() {
            return fullTime;
        }

        public void setFullTime(String fullTime) {
            this.fullTime = fullTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getOutputCurrent() {
            return outputCurrent;
        }

        public void setOutputCurrent(String outputCurrent) {
            this.outputCurrent = outputCurrent;
        }

        public String getOutputVoltage() {
            return outputVoltage;
        }

        public void setOutputVoltage(String outputVoltage) {
            this.outputVoltage = outputVoltage;
        }

        public String getRemainingTime() {
            return remainingTime;
        }

        public void setRemainingTime(String remainingTime) {
            this.remainingTime = remainingTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSumQuantity() {
            return sumQuantity;
        }

        public void setSumQuantity(String sumQuantity) {
            this.sumQuantity = sumQuantity;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }
    }
}
