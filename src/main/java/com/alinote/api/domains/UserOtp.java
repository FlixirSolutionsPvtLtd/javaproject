package com.alinote.api.domains;

import com.alinote.api.constants.GlobalConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Setter
@Getter
@NoArgsConstructor
@Document(collection = GlobalConstants.DocumentCollections.COLLECTION_USER_OTP)
public class UserOtp extends Auditable {

    @Id
    private String id;

    private String otp;
    @Field("contact_no")
    private String contactNo;
    @Field("device_id")
    private String deviceId;
    private int attempts = 0;

    UserOtp(String otp, String contactNo) {
        this.otp = otp;
        this.contactNo = contactNo;
    }

    public UserOtp(String otp, String contactNo, String deviceId) {
        this.otp = otp;
        this.contactNo = contactNo;
        this.deviceId = deviceId;
    }
}
