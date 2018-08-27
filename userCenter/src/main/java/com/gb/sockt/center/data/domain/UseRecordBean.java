package com.gb.sockt.center.data.domain;

import java.util.List;

/**
 * Created by guobiao on 2018/8/27.
 */

public class UseRecordBean {

    /**
     * useCDZRecordsList : [{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-27 14:59:23","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139851","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 14:33:03","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-27 14:19:55","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139831","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 14:09:48","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"0","endDate":"2018-8-27 13:55:26","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139791","income":"","menoy":"0","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 13:53:22","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-27 13:48:48","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139781","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 13:34:25","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-27 13:25:51","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139761","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 13:15:09","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"2","endDate":"2018-8-27 12:56:46","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139721","income":"","menoy":"4","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 11:07:22","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-27 11:06:54","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139681","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 10:15:04","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-27 10:00:17","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139651","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-27 09:13:13","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"40","endDate":"2018-8-27 09:12:58","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139621","income":"","menoy":"80","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 17:15:24","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"0","endDate":"2018-8-25 17:13:36","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139571","income":"","menoy":"0","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 17:08:14","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"0","endDate":"2018-8-25 16:56:17","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139551","income":"","menoy":"0","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 16:54:42","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"1","endDate":"2018-8-25 16:54:27","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139541","income":"","menoy":"2","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 16:29:05","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"0","endDate":"2018-8-25 16:21:28","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139521","income":"","menoy":"0","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 16:20:36","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"0","endDate":"2018-8-25 16:20:18","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139511","income":"","menoy":"0","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 16:18:28","thirdAdmin":"","thirdRate":0,"userName":"哈哈"},{"adminName":"","belonger":"","deviceName":"CG000003","duration":"0","endDate":"2018-8-25 16:18:16","fifthAdmin":"","fifthRate":0,"finalCompany":"","fourthAdmin":"","fourthRate":0,"id":"139501","income":"","menoy":"0","price":"","primaryCompany":"","profitRate":"0","rate":"2","secendAdmin":"","secendPrice":"","secendRate":"0","sensorRate":"0","startDate":"2018-8-25 16:17:19","thirdAdmin":"","thirdRate":0,"userName":"哈哈"}]
     * pageVO : {"curPage":1,"pageEnd":15,"pageSize":15,"pageStart":0,"totalPages":123,"totalRows":1838}
     */

    private PageVOBean pageVO;
    private List<UseCDZRecordsListBean> useCDZRecordsList;

    public PageVOBean getPageVO() {
        return pageVO;
    }

    public void setPageVO(PageVOBean pageVO) {
        this.pageVO = pageVO;
    }

    public List<UseCDZRecordsListBean> getUseCDZRecordsList() {
        return useCDZRecordsList;
    }

    public void setUseCDZRecordsList(List<UseCDZRecordsListBean> useCDZRecordsList) {
        this.useCDZRecordsList = useCDZRecordsList;
    }

    public static class PageVOBean {
        /**
         * curPage : 1
         * pageEnd : 15
         * pageSize : 15
         * pageStart : 0
         * totalPages : 123
         * totalRows : 1838
         */

        private int curPage;
        private int pageEnd;
        private int pageSize;
        private int pageStart;
        private int totalPages;
        private int totalRows;

        public int getCurPage() {
            return curPage;
        }

        public void setCurPage(int curPage) {
            this.curPage = curPage;
        }

        public int getPageEnd() {
            return pageEnd;
        }

        public void setPageEnd(int pageEnd) {
            this.pageEnd = pageEnd;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageStart() {
            return pageStart;
        }

        public void setPageStart(int pageStart) {
            this.pageStart = pageStart;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(int totalRows) {
            this.totalRows = totalRows;
        }
    }

    public static class UseCDZRecordsListBean {
        /**
         * adminName :
         * belonger :
         * deviceName : CG000003
         * duration : 1
         * endDate : 2018-8-27 14:59:23
         * fifthAdmin :
         * fifthRate : 0
         * finalCompany :
         * fourthAdmin :
         * fourthRate : 0
         * id : 139851
         * income :
         * menoy : 2
         * price :
         * primaryCompany :
         * profitRate : 0
         * rate : 2
         * secendAdmin :
         * secendPrice :
         * secendRate : 0
         * sensorRate : 0
         * startDate : 2018-8-27 14:33:03
         * thirdAdmin :
         * thirdRate : 0
         * userName : 哈哈
         */

        private String adminName;
        private String belonger;
        private String deviceName;
        private String duration;
        private String endDate;
        private String fifthAdmin;
        private int fifthRate;
        private String finalCompany;
        private String fourthAdmin;
        private int fourthRate;
        private String id;
        private String income;
        private String menoy;
        private String price;
        private String primaryCompany;
        private String profitRate;
        private String rate;
        private String secendAdmin;
        private String secendPrice;
        private String secendRate;
        private String sensorRate;
        private String startDate;
        private String thirdAdmin;
        private int thirdRate;
        private String userName;

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }

        public String getBelonger() {
            return belonger;
        }

        public void setBelonger(String belonger) {
            this.belonger = belonger;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getFifthAdmin() {
            return fifthAdmin;
        }

        public void setFifthAdmin(String fifthAdmin) {
            this.fifthAdmin = fifthAdmin;
        }

        public int getFifthRate() {
            return fifthRate;
        }

        public void setFifthRate(int fifthRate) {
            this.fifthRate = fifthRate;
        }

        public String getFinalCompany() {
            return finalCompany;
        }

        public void setFinalCompany(String finalCompany) {
            this.finalCompany = finalCompany;
        }

        public String getFourthAdmin() {
            return fourthAdmin;
        }

        public void setFourthAdmin(String fourthAdmin) {
            this.fourthAdmin = fourthAdmin;
        }

        public int getFourthRate() {
            return fourthRate;
        }

        public void setFourthRate(int fourthRate) {
            this.fourthRate = fourthRate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getMoney() {
            return menoy;
        }

        public void setMoney(String menoy) {
            this.menoy = menoy;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPrimaryCompany() {
            return primaryCompany;
        }

        public void setPrimaryCompany(String primaryCompany) {
            this.primaryCompany = primaryCompany;
        }

        public String getProfitRate() {
            return profitRate;
        }

        public void setProfitRate(String profitRate) {
            this.profitRate = profitRate;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getSecendAdmin() {
            return secendAdmin;
        }

        public void setSecendAdmin(String secendAdmin) {
            this.secendAdmin = secendAdmin;
        }

        public String getSecendPrice() {
            return secendPrice;
        }

        public void setSecendPrice(String secendPrice) {
            this.secendPrice = secendPrice;
        }

        public String getSecendRate() {
            return secendRate;
        }

        public void setSecendRate(String secendRate) {
            this.secendRate = secendRate;
        }

        public String getSensorRate() {
            return sensorRate;
        }

        public void setSensorRate(String sensorRate) {
            this.sensorRate = sensorRate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getThirdAdmin() {
            return thirdAdmin;
        }

        public void setThirdAdmin(String thirdAdmin) {
            this.thirdAdmin = thirdAdmin;
        }

        public int getThirdRate() {
            return thirdRate;
        }

        public void setThirdRate(int thirdRate) {
            this.thirdRate = thirdRate;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
