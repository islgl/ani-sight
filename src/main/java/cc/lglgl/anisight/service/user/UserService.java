package cc.lglgl.anisight.service.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lgl
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


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
                    filteredUser.put("role", user.getRole());
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
                "role", user.getRole(),
                "avatar", user.getAvatar()
        );
    }


}
