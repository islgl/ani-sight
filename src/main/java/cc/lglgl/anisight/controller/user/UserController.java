package cc.lglgl.anisight.controller.user;

import cc.lglgl.anisight.domain.user.User;
import cc.lglgl.anisight.dto.CustomResponse;
import cc.lglgl.anisight.service.user.UserService;
import cc.lglgl.anisight.utils.CustomResponseFactory;
import cc.lglgl.anisight.utils.JwtUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lgl
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private final UserService userService;

  @Autowired
  private JwtUtil jwtUtil;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public CustomResponse getUsers(
      @RequestParam(value = "uid", required = false) Integer uid,
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "role", required = false) String role,
      @RequestParam(value = "fields", required = false) List<String> fields) {
    List<User> users;

    if (uid != null) {
      users = List.of(userService.getUserByUid(uid));
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

  @GetMapping("/{uid}")
  public CustomResponse getUser(
      @PathVariable int uid,
      @RequestParam(value = "fields", required = false) List<String> fields) {
    User user = userService.getUserByUid(uid);

    if (user == null) {
      return CustomResponseFactory.error("No user found");
    }

    if (fields != null) {
      return CustomResponseFactory.success(
          "Executed successfully",
          userService.user2Map(user, fields));
    }

    return CustomResponseFactory.success(
        "Executed successfully",
        userService.user2Map(user));
  }

  @GetMapping("/{uid}/{field}")
  public CustomResponse getUserField(
      @PathVariable int uid,
      @PathVariable String field) {
    User user = userService.getUserByUid(uid);

    if (user == null) {
      return CustomResponseFactory.error("No user found");
    }

    return CustomResponseFactory.success(
        "Executed successfully",
        userService.getUserField(user, field));
  }

  @PostMapping
  public CustomResponse createUser(User user) {
    try {
      userService.addUser(user);
      return CustomResponseFactory.success(
          "Successfully created user",
          userService.user2Map(user));
    } catch (Exception e) {
      return CustomResponseFactory.error("Failed to create user");
    }
  }

  @PutMapping("/{uid}")
  public CustomResponse updateUser(
      @PathVariable int uid,
      @RequestParam Map<String, String> userInfo) {
    User user = userService.getUserByUid(uid);

    if (user == null) {
      return CustomResponseFactory.error("No user found");
    } else {
      if (userInfo.containsKey("username")) {
        String username = userInfo.get("username");
        if (userService.getUserByUsername(username) != null) {
          return CustomResponseFactory.error("用户名已存在");
        }
        if (!userService.isUsernameValid(username)) {
          return CustomResponseFactory.error("用户名含有非法字符");
        }
        if (username.length() < 4 || username.length() > 16) {
          return CustomResponseFactory.error("用户名长度应在4-16位之间");
        }

        user.setUsername(userInfo.get("username"));
      }
      if (userInfo.containsKey("password")) {
        String password = userInfo.get("password");
        if (password.length() < 6 || password.length() > 16) {
          return CustomResponseFactory.error("密码长度应在6-16位之间");
        }
        user.setPassword(password);
      }
      if (userInfo.containsKey("email")) {
        if (userService.getUserByEmail(userInfo.get("email")) != null) {
          return CustomResponseFactory.error("邮箱已存在");
        }
        if (!userService.isEmailValid(userInfo.get("email"))) {
          return CustomResponseFactory.error("邮箱格式不正确");
        }
        user.setEmail(userInfo.get("email"));
      }
      if (userInfo.containsKey("role")) {
        user.setRole(Integer.parseInt(userInfo.get("role")));
      }
      if (userInfo.containsKey("avatar")) {
        user.setAvatar(userInfo.get("avatar"));
      }
      userService.updateUser(user);
      return CustomResponseFactory.success(
          "Successfully updated user",
          userService.user2Map(user));
    }
  }

  @PutMapping("/{uid}/{field}")
  public CustomResponse updateUserField(
      @PathVariable int uid,
      @PathVariable String field,
      @RequestBody Map<String, String> data) {

    User user = userService.getUserByUid(uid);
    String value = data.get("value");

    if (user == null) {
      return CustomResponseFactory.error("No user found");
    } else {
      switch (field) {
        case "username":
          if (userService.getUserByUsername(value) != null) {
            return CustomResponseFactory.error("用户名已存在");
          }
          if (!userService.isUsernameValid(value)) {
            return CustomResponseFactory.error("用户名含有非法字符");
          }
          if (value.length() < 4 || value.length() > 16) {
            return CustomResponseFactory.error("用户名长度应在4-16位之间");
          }
          user.setUsername(value);
          break;
        case "password":
          if (value.length() < 6 || value.length() > 16) {
            return CustomResponseFactory.error("密码长度应在6-16位之间");
          }
          user.setPassword(value);
          break;
        case "email":
          String verifyCode = data.get("verifyCode");
          if (verifyCode == null || verifyCode.isEmpty()) {
            return CustomResponseFactory.error("请输入验证码");
          }
          if (userService.getUserByEmail(value) != null) {
            return CustomResponseFactory.error("邮箱已存在");
          }
          if (!userService.isEmailValid(value)) {
            return CustomResponseFactory.error("邮箱格式不正确");
          }
          // TODO: 特权验证码，实际上线记得删除！
          if (!verifyCode.equals("admincode")) {
            // 验证码检查
            String trueCode = userService.getVerifyCodeFromCache(value);
            if (trueCode == null) {
              return CustomResponseFactory.error("验证码失效");
            } else if (!trueCode.equals(verifyCode)) {
              return CustomResponseFactory.error("验证码错误");
            } else {
              userService.removeVerifyCodeFromCache(value);
            }
          }

          user.setEmail(value);
          break;
        case "role":
          int roleCode = Integer.parseInt(value);
          if (roleCode != 0 || roleCode != 1) {
            return CustomResponseFactory.error("Invalid role code");
          }
          user.setRole(Integer.parseInt(value));
          break;
        case "avatar":
          if (value == null || value.isEmpty()) {
            return CustomResponseFactory.error("Please provide avatar filename");
          }

          // 如果图片类型不同，删除 OSS 上的旧头像
          if (user.getAvatar() != null && !user.getAvatar().equals(value)) {
            boolean deleteFlag = userService.deleteAvatar(user.getAvatar());
            if (!deleteFlag) {
              System.out.println("Failed to delete old avatar");
              return CustomResponseFactory.error("头像更新失败");
            }
          }

          user.setAvatar(value);
          break;
        default:
          return CustomResponseFactory.error("Invalid field name");
      }
      userService.updateUser(user);
      return CustomResponseFactory.success(
          "Successfully updated user",
          userService.user2Map(user));
    }
  }

  @DeleteMapping
  public CustomResponse deleteUsers(
      @RequestParam(value = "uid", required = false) Integer uid,
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "email", required = false) String email) {
    try {
      if (uid != null) {
        if (userService.getUserByUid(uid) == null) {
          return CustomResponseFactory.error("No user found");
        }
        userService.deleteUserByUid(uid);
        return CustomResponseFactory.success(
            "Successfully deleted user uid = " + uid);
      } else if (username != null) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
          return CustomResponseFactory.error("No user found");
        }
        userService.deleteUserByUid(user.getUid());
        return CustomResponseFactory.success(
            "Successfully deleted user username = " + username);
      } else if (email != null) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
          return CustomResponseFactory.error("No user found");
        }
        userService.deleteUserByUid(user.getUid());
        return CustomResponseFactory.success(
            "Successfully deleted user email = " + email);
      } else {
        userService.deleteUsers();
        return CustomResponseFactory.success("Successfully deleted all users");
      }
    } catch (Exception e) {
      return CustomResponseFactory.error("Failed to delete user");
    }
  }

  @DeleteMapping("/{uid}")
  public CustomResponse deleteUser(@PathVariable int uid) {
    userService.deleteUserByUid(uid);
    return CustomResponseFactory.success(
        "Successfully deleted user uid = " + uid);
  }

  /**
   * 获取邮箱验证码
   *
   * @param email 待验证的邮箱
   * @return 包含请求ID的响应
   */
  @GetMapping("/email-verify")
  public CustomResponse emailVerify(@RequestParam String email) {
    if (email == null || email.isEmpty() || !userService.isEmailValid(email)) {
      return CustomResponseFactory.error("Invalid email");

    }
    String code = userService.sendVerifyCode(email);
    if (code == null) {
      return CustomResponseFactory.error("Failed to send email");
    }
    return CustomResponseFactory.success("Successfully sent email");
  }

  @PostMapping("/register")
  public CustomResponse register(
      @RequestParam String username,
      @RequestParam String password,
      @RequestParam String email,
      @RequestParam String confirmPassword,
      @RequestParam String verifyCode) {
    if (!password.equals(confirmPassword)) {
      return CustomResponseFactory.error("两次输入密码不一致");
    }
    if (userService.getUserByUsername(username) != null) {
      return CustomResponseFactory.error("用户名已存在");
    }
    if (userService.getUserByEmail(email) != null) {
      return CustomResponseFactory.error("该邮箱已被注册");
    }
    if (verifyCode.isEmpty()) {
      return CustomResponseFactory.error("请输入验证码");
    }

    // 强度检查
    if (username.length() < 4 || username.length() > 16) {
      return CustomResponseFactory.error("用户名长度应在4-16位之间");
    }
    if (password.length() < 6 || password.length() > 16) {
      return CustomResponseFactory.error("密码长度应在6-16位之间");
    }
    if (!userService.isUsernameValid(username)) {
      return CustomResponseFactory.error("用户名含有非法字符");
    }
    if (!userService.isEmailValid(email)) {
      return CustomResponseFactory.error("邮箱格式不正确");
    }

    // TODO: 特权验证码，实际上线记得删除！
    if (!"admincode".equals(verifyCode)) {
      // 验证码检查
      String trueCode = userService.getVerifyCodeFromCache(email);
      if (trueCode == null) {
        return CustomResponseFactory.error("验证码失效");
      } else if (!trueCode.equals(verifyCode)) {
        return CustomResponseFactory.error("验证码错误");
      } else {
        userService.removeVerifyCodeFromCache(email);
      }
    }

    User user = new User(username, password, email);

    try {
      User userInDb = userService.addUser(user);
      return CustomResponseFactory.success(
          "注册成功",
          userService.user2Map(userInDb));
    } catch (Exception e) {
      return CustomResponseFactory.error("注册失败");
    }
  }

  @PostMapping("/login")
  public CustomResponse login(
      @RequestParam String usernameOrEmail,
      @RequestParam String password) {
    System.out.println("Login request received: " + usernameOrEmail);
    if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
      return CustomResponseFactory.error("用户名或邮箱不能为空");
    }
    if (password == null || password.isEmpty()) {
      return CustomResponseFactory.error("密码不能为空");
    }
    // 判断是用户名还是邮箱
    User user = null;
    if (usernameOrEmail.contains("@")) {
      user = userService.getUserByEmail(usernameOrEmail);
    } else {
      user = userService.getUserByUsername(usernameOrEmail);
    }
    if (user == null) {
      return CustomResponseFactory.error("用户不存在");
    }

    if (!userService.checkPassword(password, user.getPassword())) {

      return CustomResponseFactory.error("密码错误");
    }

    String avatarUrl = userService.getAvatarUrl(user.getAvatar());
    if (avatarUrl != null) {
      user.setAvatar(avatarUrl);
    }
    String token = jwtUtil.generateToken(user.getUid(), user.getRole());
    Map<String, Object> userInfo = userService.user2Map(user, token);

    return CustomResponseFactory.success("Successfully login", userInfo);
  }

  @PostMapping("/resetpwd")
  public CustomResponse resetPwd(
      @RequestParam String email,
      @RequestParam String newPassword,
      @RequestParam String confirmPassword,
      @RequestParam String verifyCode) {
    if (!newPassword.equals(confirmPassword)) {
      return CustomResponseFactory.error("两次输入密码不一致");
    }
    if (verifyCode.isEmpty()) {
      return CustomResponseFactory.error("请输入验证码");
    }

    if (newPassword.length() < 6 || newPassword.length() > 16) {
      return CustomResponseFactory.error("密码长度应在6-16位之间");
    }

    if (!userService.isEmailValid(email)) {
      return CustomResponseFactory.error("邮箱格式不正确");
    }

    // 验证码检查
    // TODO: 特权验证码，实际上线记得删除！
    if (!verifyCode.equals("admincode")) {
      String trueCode = userService.getVerifyCodeFromCache(email);
      if (trueCode == null) {
        return CustomResponseFactory.error("验证码失效");
      } else if (!trueCode.equals(verifyCode)) {
        return CustomResponseFactory.error("验证码错误");
      } else {
        userService.removeVerifyCodeFromCache(email);
      }
    }

    User user = userService.getUserByEmail(email);
    if (user == null) {
      return CustomResponseFactory.error("用户不存在");
    }

    user.setPassword(userService.encPassword(newPassword));
    userService.updateUser(user);
    return CustomResponseFactory.success("密码修改成功");
  }

  @GetMapping("/token-valid")
  public CustomResponse tokenValidate(@RequestHeader(value = "Authorization", required = false) String token,
      @RequestParam int uid) {
    if (token == null || token.isEmpty()) {
      return CustomResponseFactory.error("Plase provide token in header");
    }
    if (jwtUtil.validateToken(token, uid)) {
      return CustomResponseFactory.success("Token is valid");
    } else {
      return CustomResponseFactory.error("Token is invalid");
    }
  }
}
