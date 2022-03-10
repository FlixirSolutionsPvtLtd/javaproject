package com.alinote.api.repository;

import com.alinote.api.domains.UserOtp;
import org.springframework.data.mongodb.repository.*;

public interface UserOtpRepository extends MongoRepository<UserOtp, String> {

    UserOtp findByContactNoAndDeviceId(String mobileNo, String deviceId);

    @DeleteQuery
    void deleteByCreatedTsBefore(long timeToClearOtpAttemptsBefore);
}
