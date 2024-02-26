package cc.lglgl.anisight.service.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.domain.user.UserRepository;
import cc.lglgl.anisight.utils.EmailUtil;
import cc.lglgl.anisight.utils.OssUtil;
import cc.lglgl.anisight.utils.UidUtil;
import com.aliyun.dm20151123.models.SingleSendMailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

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

    @Qualifier("verifyCodeCacheManager")
    @Autowired
    private CacheManager verifyCodeCacheManager;

    @Qualifier("userCacheManager")
    @Autowired
    private CacheManager userCacheManager;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Read
    @Cacheable(value = "USER", key = "#uid", unless = "#result==null", cacheManager = "userCacheManager")
    public User getUserByUid(int uid) {
        return userRepository.findByUid(uid);
    }

    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        userCacheManager.getCache("USER").put(user.getUid(), user);
        return user;
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        userCacheManager.getCache("USER").put(user.getUid(), user);
        return user;
    }

    public List<User> getUsersByRole(int role) {
        return userRepository.findAllByRole(role);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Create
    @CachePut(value = "USER", key = "#user.uid", unless = "#result==null")
    public User addUser(User user) {
        user.setUid(UidUtil.generateUid(userRepository.count()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    // Update
    @CachePut(value = "USER", key = "#user.uid", unless = "#result==null")
    public User updateUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @CacheEvict(value = "USER", key = "#uid", cacheManager = "userCacheManager")
    public void deleteUserByUid(int uid) {
        User user = userRepository.findByUid(uid);
        userRepository.delete(user);
    }

    public void deleteUsers() {
        // 清理所有缓存
        userCacheManager.getCache("USER").clear();
        userRepository.deleteAll();
    }

    public Map<String, Object> user2Map(User user, List<String> fields) {
        Map<String, Object> filteredUser = new HashMap<>();
        for (String field : fields) {
            switch (field) {
                case "uid":
                    filteredUser.put("uid", user.getUid());
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
                "uid", user.getUid(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole() == 0 ? "User" : "Administrator",
                "avatar", user.getAvatar());
    }

    public Map<String, Object> user2Map(User user, String token) {
        return Map.of(
                "uid", user.getUid(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole() == 0 ? "User" : "Administrator",
                "avatar", user.getAvatar(),
                "token", token);
    }

    public Object getUserField(User user, String field) {
        switch (field) {
            case "uid":
                return user.getUid();
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

    @CachePut(value = "VERIFYCODE", key = "#email", unless = "#result==null", cacheManager = "verifyCodeCacheManager")
    public String sendVerifyCode(String email) {
        String code = generateCode(6);
        try {
            SingleSendMailResponse response = EmailUtil.sendEmail(
                    0,
                    email,
                    "AniSight 邮箱验证",
                    "欢迎使用AniSight！您的验证码是：" + code + "。验证码有效期为5分钟。");
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

    public boolean isUsernameValid(String username) {
        String specialCharacters = "!@#$%^&*()_+{}|:<>?`-=[]\\;',./~";
        for (char c : username.toCharArray()) {
            if (specialCharacters.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public boolean checkPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public String getAvatarUrl(int uid) {
        try{
            List<String> images=new OssUtil().listImages("avatar",String.valueOf(uid));
            String imageName=images.get(0);
            String avatarUrl=new OssUtil().getImgUrl(imageName);
            return avatarUrl;
        } catch (Exception e) {
            return null;
        }
    }

}
