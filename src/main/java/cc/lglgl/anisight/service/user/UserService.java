package cc.lglgl.anisight.service.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.domain.user.UserRepository;
import cc.lglgl.anisight.dto.CustomResponse;
import cc.lglgl.anisight.utils.CustomResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lgl
 */
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public User saveUser(User user) {
        return userRepository.save(user);
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

    public CustomResponse register(String username, String email, String password, String confirmPassword) {
        try {
            if (!password.equals(confirmPassword)) {
                return CustomResponseFactory.error("两次输入密码不一致");
            }
            if (getUserByUsername(username) != null) {
                return CustomResponseFactory.error("用户名已存在");
            }
            if (getUserByEmail(email) != null) {
                return CustomResponseFactory.error("该邮箱已被注册");
            }

            // 强度检查
            if (username.length() < 4 || username.length() > 16) {
                return CustomResponseFactory.error("用户名长度应在4-16位之间");
            }
            if (password.length() < 6 || password.length() > 16) {
                return CustomResponseFactory.error("密码长度应在6-16位之间");
            }

            // 对密码进行加密
            String cipher = passwordEncoder.encode(password);

            User user = new User(username, cipher, email);
            saveUser(user);
            return CustomResponseFactory.success("注册成功", user2Map(getUserByUsername(username), List.of("id", "username", "email", "role")));
        } catch (Exception e) {
            return CustomResponseFactory.error("注册失败");
        }
    }

}
