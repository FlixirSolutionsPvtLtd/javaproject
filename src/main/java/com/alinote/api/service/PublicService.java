package com.alinote.api.service;

import com.alinote.api.enums.*;
import com.alinote.api.exception.ServicesException;
import com.alinote.api.model.GoogleHandleResponseDTO;
import com.alinote.api.model.OtpDTO;
import com.alinote.api.model.ResetPasswordDTO;
import com.alinote.api.model.common.BaseWrapper;

import java.util.Map;

public interface PublicService {


	BaseWrapper sendOtp(OtpDTO value, OtpTarget target, Source source) throws ServicesException;

	BaseWrapper verifyOtp(OtpDTO request, String target, Source source) throws ServicesException;

	BaseWrapper resetPassword(ResetPasswordDTO request) throws ServicesException;

    BaseWrapper getStaticContent(StaticContentSource source) throws ServicesException;
}
