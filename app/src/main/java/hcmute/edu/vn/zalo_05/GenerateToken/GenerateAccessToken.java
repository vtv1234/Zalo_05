package hcmute.edu.vn.zalo_05.GenerateToken;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenerateAccessToken {
    public static void main(String[] args) {
        /*
        String access_token = genAccessToken("SKBe3kjKlx9EbIhbgmEBciRKFshrB0QMR", "NDZzYXJ4d1hKRmhWMmJzYmdicXh5eDFlRUFEZ3FJRmg=", 2592000 );*/

        /*System.out.println(access_token);*/
        System.out.println("hello world");
    }

    public static String genAccessToken(String keySid, String keySecret, int expireInSecond, String userId) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(keySecret);

            Map<String, Object> headerClaims = new HashMap<String, Object>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = (long) (System.currentTimeMillis()) + expireInSecond * 1000;

            String token = JWT.create().withHeader(headerClaims)
                    .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                    .withClaim("iss", keySid)
                    .withExpiresAt(new Date(exp))
                    .withClaim("userId",userId)
                    .sign(algorithmHS);

            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
