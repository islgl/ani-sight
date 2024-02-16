package cc.lglgl.anisight.controller.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lgl
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<Map<String, Object>> getUsers(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "fields", required = false) List<String> fields
    ) {
        List<User> users;

        if (id != null) {
            users = List.of(userService.getUserById(id));
        } else if (username != null) {
            users = List.of(userService.getUserByUsername(username));
        } else if (email != null) {
            users = List.of(userService.getUserByEmail(email));
        } else if (role != null) {
            int roleCode = "user".equals(role) ? 0 : 1;
            users = userService.getUsersByRole(roleCode);
        } else {
            users = userService.getAllUsers();
        }

        if (users == null) {
            return null;
        }


        List<Map<String, Object>> usersInfo = new ArrayList<>();
        if (fields != null) {
            for (User user : users) {
                usersInfo.add(userService.user2Map(user, fields));
            }
        } else {
            for (User user : users) {
                usersInfo.add(userService.user2Map(user));
            }
        }

        return usersInfo;

    }

    @GetMapping("/{id}")
    public Map<String, Object> getUser(@PathVariable int id,
                                       @RequestParam(value = "fields", required = false) List<String> fields) {
        User user = userService.getUserById(id);

        if (user == null) {
            return null;
        }

        if (fields != null) {
            return userService.user2Map(user, fields);
        }

        return userService.user2Map(user);
    }

}
