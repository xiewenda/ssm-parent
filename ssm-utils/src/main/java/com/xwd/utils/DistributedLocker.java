package com.xwd.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by lishaoyan on 2015/5/13
 * <p>
 * It implements the distribute locker to lock the global resource across server. The acquirer should complete
 * the task in the timeout and release the lock explicitly. If it fails to complete the task, the lock will be
 * released after the time is out.
 * <p>
 * The locker solution refers "http://redis.io/topics/distlock". See the link for the details.
 */
@Component
public class DistributedLocker {
    private final static Logger logger = LoggerFactory.getLogger(DistributedLocker.class);

    private final static String DISTRI_LOCK_KEY_PREFIX = "distri_locker_";

    private final int MAX_LOCKER_TIME = 3600 * 6;   // seconds

    @Autowired
    private StringRedisTemplate redis;

    /**
     * acquire the distribute locker
     *
     * @param name    the locker name
     * @param timeout the timeout (seconds, 1~3600). After that, the locker will be released
     * @return the locker id if the locker is acquired or null
     */
    public String lock(String name, int timeout) {
        if (timeout < 1 || timeout > MAX_LOCKER_TIME) {
            logger.error("invalid timeout. It must be between 1 and 3600");
            return null;
        }

        String key = getLockerKey(name);
        if (redis.opsForValue().get(key) != null) {
            logger.debug("Failed to acquire the locker '{}' because it's locked by other.", name);

            // process the expire exception case
            Long expireSeconds = redis.getExpire(key);
            if (expireSeconds == -1) {
                logger.warn("reset the timeout of bad distribute locker '{}'", key);
                redis.expire(key, timeout, TimeUnit.SECONDS);
            }

            return null;
        }

        try {
            String lockId = UUID.randomUUID().toString();
            if (redis.opsForValue().setIfAbsent(key, lockId)) {
                redis.expire(key, timeout, TimeUnit.SECONDS);
                logger.debug("obtained distributed locker '{} ({})'", name, lockId);
                return lockId;
            } else {
                logger.debug("Failed to create the locker key '{}' because it exists.", key);
                return null;
            }
        } catch (Exception e) {
            logger.warn("exception in locking: {}", e);
            return null;
        }
    }

    /**
     * unlock the global lock
     *
     * @param name     the locker name
     * @param lockerId the locker id returned by lock()
     */
    public void unlock(String name, String lockerId) {
        String key = getLockerKey(name);
        String value = redis.opsForValue().get(key);

        if (value == null) {
            logger.info("the locker '{}' doesn't exists", name);
            return;
        }

        if (!value.equals(lockerId)) {
            logger.warn("invalid locker id!");
            return;
        }

        redis.delete(key);
        logger.info("the locker '{}' is unlocked", name);
    }

    public boolean isLocked(String name) {
        return redis.opsForValue().get(getLockerKey(name)) != null;
    }

    /**
     * update the locker expire time to (now + timeout)
     *
     * @param lockerName     the locker's name
     * @param timeoutSeconds the timeout seconds
     * @return true if success or null if the locker doesn't exist and failed to update
     */
    public boolean update(String lockerName, int timeoutSeconds) {
        String key = getLockerKey(lockerName);
        if (redis.opsForValue().get(key) != null) {
            redis.expire(key, timeoutSeconds, TimeUnit.SECONDS);
            return true;
        } else {
            logger.debug("the locker '{}' doesn't exist", lockerName);
            return false;
        }
    }

    private String getLockerKey(String name) {
        return DISTRI_LOCK_KEY_PREFIX + "_" + name;
    }
}
