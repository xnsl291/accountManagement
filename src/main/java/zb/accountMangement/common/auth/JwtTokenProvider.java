package zb.accountMangement.common.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zb.accountMangement.common.error.exception.InvalidTokenException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.common.util.RedisUtil;
import zb.accountMangement.member.type.RoleType;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.access-token-seconds}")
    private Long accessTokenExpirationTimeInSeconds;

    @Value("${jwt.expiration.refresh-token-seconds}")
    private Long refreshTokenExpirationTimeInSeconds;

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final String AUTHORIZATION_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_KEY = "Access-Token";
    public static final String REFRESH_TOKEN_KEY = "Refresh-Token";

    private final RedisUtil redisUtil;


    public String generateAccessToken(Long id, String phoneNumber, RoleType authority) {
        return generateToken(id, phoneNumber, authority.toString(), accessTokenExpirationTimeInSeconds );
    }

    public String generateRefreshToken(Long id, String phoneNumber, RoleType authority) {
        return generateToken(id, phoneNumber, authority.toString(), refreshTokenExpirationTimeInSeconds );
    }

    public String generateToken(Long id, String phoneNumber, String authority, Long expirationTimeInSeconds ){
        Date expiration = new Date(System.currentTimeMillis() + expirationTimeInSeconds);

        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", phoneNumber);
        claims.put("authority", authority);
        claims.put("id", id.toString());

        return Jwts.builder()
                .setSubject(phoneNumber)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void saveRefreshToken(String phoneNumber, String token){
        redisUtil.setData("RT:"+phoneNumber , token, refreshTokenExpirationTimeInSeconds);
    }

    public void deleteToken(String phoneNumber) {
        String rtKey = "RT:"+phoneNumber;
        if (redisUtil.getData(rtKey)!=null)
            redisUtil.deleteData(getPhoneNumber(rtKey));
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getRawToken(token));

            if(!claims.getBody().getExpiration().before(new Date())) // 토큰 만료 여부
                throw new InvalidTokenException(ErrorCode.EXPIRED_TOKEN);
            return true;
        } catch (Exception e) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getRawToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
        return token.replace(AUTHORIZATION_PREFIX, "");
    }

    public String getPhoneNumber(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getRawToken(token)).getBody()
                .get("phoneNumber")
                .toString();
    }

    public  Long getId(String token){
        return Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getRawToken(token)).getBody().getId());
    }

    public String getAuthority(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getRawToken(token)).getBody()
                .get("authority")
                .toString();
    }

    public Long getExpirationInSeconds(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(getRawToken(token)).getBody()
                .getExpiration().getTime();
    }

}
