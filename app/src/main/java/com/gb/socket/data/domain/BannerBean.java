package com.gb.socket.data.domain;

import java.util.List;

/**
 * Created by guobiao on 2018/8/8.
 */

public class   BannerBean {


    /**
     * pageVO : {"curPage":1,"pageSize":10,"totalRows":3,"totalPages":1,"pageStart":0,"pageEnd":10}
     * returnJson : [{"alias":"","aliasFlag":"","createDate":"2017-12-27 20:48:31.0","createUser":"","deleteFlag":"No","deleteFlagFlag":"","fileName":"1514378911608.jpg","id":"6","path":"https://www.sensorte.com/picture/advertisement/1514378911608.jpg","type":"","typeFlag":"","updateDate":"2017-12-27 20:48:51.0","updateUser":""},{"alias":"","aliasFlag":"","createDate":"2017-12-27 20:48:23.0","createUser":"","deleteFlag":"No","deleteFlagFlag":"","fileName":"1514378903137.jpg","id":"5","path":"https://www.sensorte.com/picture/advertisement/1514378903137.jpg","type":"","typeFlag":"","updateDate":"2017-12-27 20:48:49.0","updateUser":""},{"alias":"","aliasFlag":"","createDate":"2017-12-27 20:48:14.0","createUser":"","deleteFlag":"No","deleteFlagFlag":"","fileName":"1514378894311.jpg","id":"4","path":"https://www.sensorte.com/picture/advertisement/1514378894311.jpg","type":"","typeFlag":"","updateDate":"2017-12-27 20:48:46.0","updateUser":""}]
     */

    private PageVOBean pageVO;
    private List<ReturnJsonBean> returnJson;

    public PageVOBean getPageVO() {
        return pageVO;
    }

    public void setPageVO(PageVOBean pageVO) {
        this.pageVO = pageVO;
    }

    public List<ReturnJsonBean> getReturnJson() {
        return returnJson;
    }

    public void setReturnJson(List<ReturnJsonBean> returnJson) {
        this.returnJson = returnJson;
    }

    public static class PageVOBean {
        /**
         * curPage : 1
         * pageSize : 10
         * totalRows : 3
         * totalPages : 1
         * pageStart : 0
         * pageEnd : 10
         */

        private int curPage;
        private int pageSize;
        private int totalRows;
        private int totalPages;
        private int pageStart;
        private int pageEnd;

        public int getCurPage() {
            return curPage;
        }

        public void setCurPage(int curPage) {
            this.curPage = curPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(int totalRows) {
            this.totalRows = totalRows;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getPageStart() {
            return pageStart;
        }

        public void setPageStart(int pageStart) {
            this.pageStart = pageStart;
        }

        public int getPageEnd() {
            return pageEnd;
        }

        public void setPageEnd(int pageEnd) {
            this.pageEnd = pageEnd;
        }
    }

    public static class ReturnJsonBean {
        /**
         * alias :
         * aliasFlag :
         * createDate : 2017-12-27 20:48:31.0
         * createUser :
         * deleteFlag : No
         * deleteFlagFlag :
         * fileName : 1514378911608.jpg
         * id : 6
         * path : https://www.sensorte.com/picture/advertisement/1514378911608.jpg
         * type :
         * typeFlag :
         * updateDate : 2017-12-27 20:48:51.0
         * updateUser :
         */

        private String alias;
        private String aliasFlag;
        private String createDate;
        private String createUser;
        private String deleteFlag;
        private String deleteFlagFlag;
        private String fileName;
        private String id;
        private String path;
        private String type;
        private String typeFlag;
        private String updateDate;
        private String updateUser;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAliasFlag() {
            return aliasFlag;
        }

        public void setAliasFlag(String aliasFlag) {
            this.aliasFlag = aliasFlag;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getDeleteFlag() {
            return deleteFlag;
        }

        public void setDeleteFlag(String deleteFlag) {
            this.deleteFlag = deleteFlag;
        }

        public String getDeleteFlagFlag() {
            return deleteFlagFlag;
        }

        public void setDeleteFlagFlag(String deleteFlagFlag) {
            this.deleteFlagFlag = deleteFlagFlag;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTypeFlag() {
            return typeFlag;
        }

        public void setTypeFlag(String typeFlag) {
            this.typeFlag = typeFlag;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }
    }
}
