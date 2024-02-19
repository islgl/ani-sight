package cc.lglgl.anisight.service.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.domain.user.UserRepository;
import cc.lglgl.anisight.utils.EmailUtil;
import com.aliyun.dm20151123.models.SingleSendMailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author lgl
 */
@Service
@CacheConfig(cacheNames = "user")
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager verifyCodeCacheManager;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Read
    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRole(int role) {
        return userRepository.findByRole(role);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Create
    @CachePut(value = "USER", key = "#user.username", unless = "#result==null")
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return userRepository.findByUsername(user.getUsername());
    }

    // Update
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // Delete
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        userRepository.delete(user);
    }

    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        userRepository.delete(user);
    }

    public void deleteUsers() {
        userRepository.deleteAll();
    }

    public Map<String, Object> user2Map(User user, List<String> fields) {
        Map<String, Object> filteredUser = new HashMap<>();
        for (String field : fields) {
            switch (field) {
                case "id":
                    filteredUser.put("id", user.getId());
                    break;
                case "username":
                    filteredUser.put("username", user.getUsername());
                    break;
                case "email":
                    filteredUser.put("email", user.getEmail());
                    break;
                case "role":
                    filteredUser.put("role", user.getRole() == 0 ? "User" : "Administrator");
                    break;
                case "avatar":
                    filteredUser.put("avatar", user.getAvatar());
                    break;
                default:
                    break;
            }
        }
        return filteredUser;
    }

    public Map<String, Object> user2Map(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole() == 0 ? "User" : "Administrator",
                "avatar", user.getAvatar()
        );
    }

    public Object getUserField(User user, String field) {
        switch (field) {
            case "id":
                return user.getId();
            case "username":
                return user.getUsername();
            case "email":
                return user.getEmail();
            case "role":
                return user.getRole();
            case "avatar":
                return user.getAvatar();
            default:
                return null;
        }
    }

    public String generateCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Cacheable(value = "VERIFYCODE", key = "#email", unless = "#result==null", cacheManager = "verifyCodeCacheManager")
    public String sendVerifyCode(String email) {
        String code = generateCode(6);
        try {
            SingleSendMailResponse response = EmailUtil.sendEmail(
                    0,
                    email,
                    "AniSight 邮箱验证",
                    "欢迎使用AniSight！您的验证码是：" + code + "。验证码有效期为5分钟。"
            );
            if (response != null) {
                return code;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getVerifyCodeFromCache(String email) {
        return verifyCodeCacheManager.getCache("VERIFYCODE").get(email, String.class);
    }

    public void removeVerifyCodeFromCache(String email) {
        verifyCodeCacheManager.getCache("VERIFYCODE").evict(email);
    }

}
