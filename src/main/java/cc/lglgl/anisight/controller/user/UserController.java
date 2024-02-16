package cc.lglgl.anisight.controller.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.dto.CustomResponse;
import cc.lglgl.anisight.service.user.UserService;
import cc.lglgl.anisight.utils.CustomResponseFactory;
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
    public CustomResponse getUsers(
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
            return CustomResponseFactory.error("No user found");
        }

        List<Map<String, Object>> usersInfo = new ArrayList<>();
        try {
            if (fields != null) {
                for (User user : users) {
                    usersInfo.add(userService.user2Map(user, fields));
                }
            } else {
                for (User user : users) {
                    usersInfo.add(userService.user2Map(user));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CustomResponseFactory.success("Executed successfully", usersInfo);

    }

    @GetMapping("/{id}")
    public CustomResponse getUser(@PathVariable int id,
                                  @RequestParam(value = "fields", required = false) List<String> fields) {
        User user = userService.getUserById(id);

        if (user == null) {
            return CustomResponseFactory.error("No user found");
        }

        if (fields != null) {
            return CustomResponseFactory.success("Executed successfully", userService.user2Map(user, fields));
        }

        return CustomResponseFactory.success("Executed successfully", userService.user2Map(user));
    }

    @GetMapping("/{id}/{field}")
    public CustomResponse getUserField(@PathVariable int id, @PathVariable String field) {
        User user = userService.getUserById(id);

        if (user == null) {
            return CustomResponseFactory.error("No user found");
        }

        return CustomResponseFactory.success("Executed successfully", userService.getUserField(user, field));
    }

    @PostMapping
    public CustomResponse createUser(User user) {
        try {
            userService.saveUser(user);
            return CustomResponseFactory.success("Successfully created user",
                    userService.user2Map(user));
        } catch (Exception e) {
            return CustomResponseFactory.error("Failed to create user");
        }
    }

    @PutMapping("/{id}")
    public CustomResponse updateUser(@PathVariable int id,
                                     @RequestParam Map<String, String> userInfo) {
        User user = userService.getUserById(id);

        if (user == null) {
            return CustomResponseFactory.error("No user found");
        } else {
            if (userInfo.containsKey("username")) {
                user.setUsername(userInfo.get("username"));
            }
            if (userInfo.containsKey("password")) {
                user.setPassword(userInfo.get("password"));
            }
            if (userInfo.containsKey("email")) {
                user.setEmail(userInfo.get("email"));
            }
            if (userInfo.containsKey("role")) {
                user.setRole(Integer.parseInt(userInfo.get("role")));
            }
            if (userInfo.containsKey("avatar")) {
                user.setAvatar(userInfo.get("avatar"));
            }
            userService.updateUser(user);
            return CustomResponseFactory.success("Successfully updated user",
                    userService.user2Map(user));
        }
    }

    @PutMapping("/{id}/{field}")
    public CustomResponse updateUserField(@PathVariable int id, @PathVariable String field, @RequestBody String value) {
        User user = userService.getUserById(id);

        if (user == null) {
            return CustomResponseFactory.error("No user found");
        } else {
            switch (field) {
                case "username":
                    user.setUsername(value);
                    break;
                case "password":
                    user.setPassword(value);
                    break;
                case "email":
                    user.setEmail(value);
                    break;
                case "role":
                    user.setRole(Integer.parseInt(value));
                    break;
                case "avatar":
                    user.setAvatar(value);
                    break;
                default:
                    return CustomResponseFactory.error("Invalid field");
            }
            System.out.println(user);
            userService.updateUser(user);
            return CustomResponseFactory.success("Successfully updated user",
                    userService.user2Map(user));
        }
    }

    @DeleteMapping
    public CustomResponse deleteUsers() {
        userService.deleteUsers();
        return CustomResponseFactory.success("Successfully deleted all users");
    }

    @DeleteMapping("/{id}")
    public CustomResponse deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return CustomResponseFactory.success("Successfully deleted user id = " + id);
    }

}
