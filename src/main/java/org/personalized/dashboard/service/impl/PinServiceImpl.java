package org.personalized.dashboard.service.impl;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.personalized.dashboard.dao.api.ActivityDao;
import org.personalized.dashboard.dao.api.PinDao;
import org.personalized.dashboard.model.Activity;
import org.personalized.dashboard.model.ActivityType;
import org.personalized.dashboard.model.EntityType;
import org.personalized.dashboard.model.Pin;
import org.personalized.dashboard.service.api.PinService;
import org.personalized.dashboard.utils.auth.SessionManager;
import org.personalized.dashboard.utils.generator.ActivityGenerator;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sudan on 3/4/15.
 */
@Repository
public class PinServiceImpl implements PinService {

    private final PinDao pinDao;
    private final SessionManager sessionManager;
    private final ActivityGenerator activityGenerator;
    private final ActivityDao activityDao;

    @Inject
    public PinServiceImpl(PinDao pinDao, SessionManager sessionManager, ActivityGenerator
            activityGenerator, ActivityDao activityDao) {
        this.pinDao = pinDao;
        this.sessionManager = sessionManager;
        this.activityGenerator = activityGenerator;
        this.activityDao = activityDao;
    }

    @Override
    public String createPin(Pin pin) {
        String pinId = pinDao.create(pin, sessionManager.getUserIdFromSession());
        Activity activity = activityGenerator.generate(ActivityType.CREATED, EntityType.PIN,
                pinId, pin.getName());
        activityDao.add(activity, sessionManager.getUserIdFromSession());
        return pinId;
    }

    @Override
    public Pin getPin(String pinId) {
        return pinDao.get(pinId, sessionManager.getUserIdFromSession());
    }

    @Override
    public Long updatePin(Pin pin) {
        Long modifiedCount = pinDao.update(pin, sessionManager.getUserIdFromSession());
        if (modifiedCount > 0) {
            Activity activity = activityGenerator.generate(ActivityType.UPDATED, EntityType.PIN,
                    pin.getPinId(), pin.getName());
            activityDao.add(activity, sessionManager.getUserIdFromSession());
        }
        return modifiedCount;
    }

    @Override
    public void deletePin(String pinId) {
        Long deletedCount = pinDao.delete(pinId, sessionManager.getUserIdFromSession());
        if (deletedCount > 0) {
            Activity activity = activityGenerator.generate(ActivityType.DELETED, EntityType.PIN,
                    pinId, StringUtils.EMPTY);
            activityDao.add(activity, sessionManager.getUserIdFromSession());
        }
    }

    @Override
    public Long countPins() {
        return pinDao.count(sessionManager.getUserIdFromSession());
    }

    @Override
    public List<Pin> fetchPins(int limit, int offset) {
        return pinDao.get(limit, offset, sessionManager.getUserIdFromSession());
    }
}
