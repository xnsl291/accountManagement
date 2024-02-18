package zb.accountMangement.common.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zb.accountMangement.common.error.exception.InvalidTokenException;
import zb.accountMangement.common.type.ErrorCode;
import zb.accountMangement.common.util.RedisUtil;
import zb.accountMangement.member.type.RoleType;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
            Claims claims = getClaimsFromToken(token);
            if(!claims.getExpiration().before(new Date())) // 토큰 만료 여부
                throw new InvalidTokenException(ErrorCode.EXPIRED_TOKEN);
            return true;
        } catch (Exception e) {
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHORIZATION_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getPhoneNumber(String token) {
        return getClaimsFromToken(token).get("phoneNumber",String.class);
    }

    public  Long getId(String token){
        return Long.valueOf(getClaimsFromToken(token).getId());
    }

    public String getRoles(String token) {
        return getClaimsFromToken(token).get("roles", String.class);
    }

    public Long getExpirationInSeconds(String token){
        return getClaimsFromToken(token).getExpiration().getTime();
    }
}
