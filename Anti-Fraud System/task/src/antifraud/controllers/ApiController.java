package antifraud.controllers;

import antifraud.JpaRepository;
import antifraud.models.Access;
import antifraud.models.Role;
import antifraud.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiController {

    @Autowired
    JpaRepository jdbcRepository;

    @Autowired
    PasswordEncoder encoder;

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getUserId());
        map.put("name", user.getName());
        map.put("username", user.getUsername());
        map.put("role", user.getRole());
        return map;
    }

    @PostMapping("/api/auth/user")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {

        try {
            if (user.getName().isEmpty() || user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(true);
        //  user.setUsername(user.getUsername().toLowerCase());
        long id = jdbcRepository.addUser(user);
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Map<String, Object> map = convertUserToMap(user);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<Map<String, Object>>> list() {
        List<User> list = jdbcRepository.findAll();
        List<Map<String, Object>> lstResult = new ArrayList<>();
        for (User user : list) {
            lstResult.add(convertUserToMap(user));
        }
        return new ResponseEntity<>(lstResult, HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String username) {
        if (jdbcRepository.deleteUser(username)) {
            Map<String, String> map = Map.of("username", username, "status", "Deleted successfully!");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/api/auth/role")
    public ResponseEntity<Map<String, Object>> role(@RequestBody User user) {

        if (user.getRole() == Role.ADMINISTRATOR) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User usr = jdbcRepository.getUser(user.getUsername());
        if (usr == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (usr.getRole() == user.getRole()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        usr.setRole(user.getRole());
        jdbcRepository.changeRole(usr);
        return new ResponseEntity<>(convertUserToMap(usr), HttpStatus.OK);


    }

    @PutMapping("/api/auth/access")
    public ResponseEntity<Map<String, String>> access(@RequestBody Access access) {
        if (jdbcRepository.access(access)) {
            if (access.getOperation() == Access.AccessEnum.LOCK) {
                return new ResponseEntity<>(Map.of("status", "User " + access.getUsername() + " locked!"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("status", "User " + access.getUsername() + " unlocked!"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
