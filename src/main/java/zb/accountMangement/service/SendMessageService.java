package zb.accountMangement.service;

import static zb.accountMangement.common.type.RedisTime.PHONE_VALID;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zb.accountMangement.common.util.RedisUtil;
import zb.accountMangement.dto.SmsVerificationInfo;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendMessageService {

    private final RedisUtil redisUtil;

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.secret-key}")
    private String secretKey;

    @Value("${provider.phone-number}")
    private String senderPhoneNumber;
    private final int VERIFY_CODE_LEN = 5;

    /**
     * 핸드폰 인증 문자 발송
     * @param phoneNumber 핸드폰번호
     */
    public void sendVerificationMessage(String phoneNumber) {
        String verificationCode = RandomStringUtils.random(VERIFY_CODE_LEN, false, true);
        //TODO: 로그인 후, 토큰 받아와서 REDIS에 토큰정보도 함께 저장
        Message coolsms = new Message(apiKey, secretKey);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);
        params.put("from", senderPhoneNumber);
        params.put("type", "SMS");
        params.put("text", "핸드폰 인증 메세지 \n 인증번호는 [" + verificationCode + "] 입니다.");
        params.put("app_version", "test app 1.2");

        try {
            coolsms.send(params);
            //TODO : setMsgVerificationInfo 사용해서 토큰 정보도 함꼐 저장
//            redisUtil.setMsgVerificationInfo(token, phoneNumber, verificationCode, PHONE_VALID.getTime());
            redisUtil.setData(phoneNumber,verificationCode, PHONE_VALID.getTime());
        } catch (CoolsmsException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 토큰정보 + 인증번호가 일치하는지 확인
     *
     * @param smsVerificationInfo - token, phoneNumber,inputCode
     * @return boolean
     */
    public boolean verifyCode(SmsVerificationInfo smsVerificationInfo) {
        //TODO : getMsgVerificationInfo 사용해서 토큰 정보 맞는지 확인. (인증번호 + 토큰 일치해야함)
        SmsVerificationInfo info = redisUtil.getMsgVerificationInfo(senderPhoneNumber);
        return info.getVerificationCode().equals(smsVerificationInfo.getVerificationCode());
    }
}
