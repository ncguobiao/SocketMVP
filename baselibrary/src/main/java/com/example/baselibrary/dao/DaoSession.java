package com.example.baselibrary.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.example.baselibrary.dao.model.DeviceUseRecords;

import com.example.baselibrary.dao.DeviceUseRecordsDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig deviceUseRecordsDaoConfig;

    private final DeviceUseRecordsDao deviceUseRecordsDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        deviceUseRecordsDaoConfig = daoConfigMap.get(DeviceUseRecordsDao.class).clone();
        deviceUseRecordsDaoConfig.initIdentityScope(type);

        deviceUseRecordsDao = new DeviceUseRecordsDao(deviceUseRecordsDaoConfig, this);

        registerDao(DeviceUseRecords.class, deviceUseRecordsDao);
    }
    
    public void clear() {
        deviceUseRecordsDaoConfig.clearIdentityScope();
    }

    public DeviceUseRecordsDao getDeviceUseRecordsDao() {
        return deviceUseRecordsDao;
    }

}
