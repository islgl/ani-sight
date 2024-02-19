package cc.lglgl.anisight.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Singleton class for JWT utility.
 */
@Component
public class JwtUtil {

    private static volatile JwtUtil instance;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    private JwtUtil() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Get the singleton instance of JwtUtil.
     * @return The singleton instance of JwtUtil.
     */
    public static JwtUtil getInstance() {
        if (instance == null) {
            synchronized (JwtUtil.class) {
                if (instance == null) {
                    instance = new JwtUtil();
                }
            }
        }
        return instance;
    }

    // Generate JWT Token
    public String generateToken(int uid) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(uid))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token, int uid) {
        int extractedUid = extractUid(token);
        return (extractedUid == uid && !isTokenExpired(token));
    }

    public int extractUid(String token) {
        return Integer.parseInt(Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expirationDate.before(new Date());
    }
}
