package lac.test.controller;

import lac.test.exception.BadRequestException;
import lac.test.model.UserModel;
import lac.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public UserModel getUserInformation(@PathVariable(name = "userId") Long userId) {
        try {
            return userService.getByUserIdAndMapToModel(userId);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping
    public UserModel create(@RequestBody UserModel userModel) {
        try {
            return userService.create(userModel);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/{userId}/balance/{amount}")
    public UserModel addMoney(@PathVariable(name = "userId") Long userId, @PathVariable(name = "amount") Long amount) {
        try {
            return userService.addMoney(userId, amount);
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
