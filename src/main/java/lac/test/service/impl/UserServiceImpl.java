package lac.test.service.impl;

import lac.test.entity.User;
import lac.test.exception.BadRequestException;
import lac.test.model.UserModel;
import lac.test.repository.UserRepository;
import lac.test.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserModel create(UserModel model) throws BadRequestException {
        String username = model.getUsername();
        if (Strings.isBlank(username)) {
            throw new BadRequestException("Bad request with empty username");
        }

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            throw new BadRequestException("duplicate user with username: " + username);
        }

        User user = new User(username, 0L);

        userRepository.save(user);

        return getUserModel(user);
    }

    @Override
    @Transactional
    public User pay(Long userId, Long amount) throws BadRequestException {
        User user = getByUserId(userId);

        if (user.getBalance() < amount) {
            throw new BadRequestException("Cannot update balance: amount > current balance");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);
        return user;
    }

    @Override
    public User getByUserId(Long userId) throws BadRequestException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Cannot find user with ID: " + userId));
    }

    @Override
    public UserModel getByUserIdAndMapToModel(Long userId) throws BadRequestException {
        User user = getByUserId(userId);
        return getUserModel(user);
    }

    private static UserModel getUserModel(User user) {
        return new UserModel(user.getId(), user.getUsername(), user.getBalance());
    }

    @Override
    public UserModel addMoney(Long userId, Long amount) throws BadRequestException {

        if (amount <= 0) {
            throw new BadRequestException("Bad request: Amount cannot be negative");
        }

        User user = getByUserId(userId);

        Long currentBalance = user.getBalance();
        user.setBalance(currentBalance + amount);
        userRepository.save(user);

        return getUserModel(user);
    }
}
