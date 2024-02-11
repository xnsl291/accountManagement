
package zb.accountMangement.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtToken {

    private String accessToken;
    private String refreshToken;

}
