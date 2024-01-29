package zb.accountMangement.common.util;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import zb.accountMangement.member.dto.SmsVerificationInfo;

@Service
@RequiredArgsConstructor
public class RedisUtil {
  private final RedisTemplate<String, Object> redisTemplate;
  private final StringRedisTemplate stringRedisTemplate;

  public String getData(String key){
    ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
    return valueOperations.get(key);
  }

  public void setData(String key, String value, long duration) {
    ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
    valueOperations.set(key,value,duration, TimeUnit.MINUTES);
  }

  public void deleteData(String key){
    stringRedisTemplate.delete(key);
  }

  public void setMsgVerificationInfo(String key, String phone, String verificationCode,long duration) {
    SmsVerificationInfo info = new SmsVerificationInfo();
    info.setPhoneNumber(phone);
    info.setVerificationCode(verificationCode);

    HashOperations<String, String, SmsVerificationInfo> valueOperations = redisTemplate.opsForHash();
    valueOperations.put(key, "info", info);

    redisTemplate.expire(key, duration, TimeUnit.MINUTES);
  }

  public SmsVerificationInfo getMsgVerificationInfo(String key) {
    HashOperations<String, String, SmsVerificationInfo> valueOperations = redisTemplate.opsForHash();
    return valueOperations.get(key, "info");
  }

  public void deleteMsgVerificationInfo(String key){
    redisTemplate.delete(key);
  }


}
