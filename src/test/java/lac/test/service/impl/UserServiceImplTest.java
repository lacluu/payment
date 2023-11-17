package lac.test.service.impl;

import lac.test.Main;
import lac.test.entity.User;
import lac.test.exception.BadRequestException;
import lac.test.model.UserModel;
import lac.test.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest(classes = Main.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private final Long userId = 1L;

    @Test
    public void testGetByUserId() throws BadRequestException {
        // Mock data
        User user = new User("username", 0L);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //run function test
        User result = userService.getByUserId(userId);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals("username", result.getUsername());
        Assertions.assertEquals(0L, result.getBalance());
    }

    @Test
    public void testCreate_Success() throws BadRequestException {
        // Mock data
        UserModel userModel = new UserModel();
        userModel.setUsername("username");
        userModel.setBalance(0L);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        //run function test
        UserModel result = userService.create(userModel);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.getBalance());
    }

    @Test
    public void testUserPay_Success() throws BadRequestException {
        // Mock data
        User user = new User("username", 100_000L);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        //run function test
        User result = userService.pay(userId, 40_000L);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals(60_000L, result.getBalance());
    }


    @Test
    public void testUserPay_FailWithCaseAmountPaymentMoreThanBalanceUser() {
        // Mock data
        User user = new User("username", 100_000L);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        //assert result with expected
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.pay(userId, 200_000L);
        });
    }

    @Test
    public void testUserAddMoney_Success() throws BadRequestException {
        // Mock data
        User user = new User("username", 150_000L);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        //run function test
        UserModel result = userService.addMoney(userId, 40_000L);

        //assert result with expected
        Assertions.assertNotNull(result);
        Assertions.assertEquals(190_000L, result.getBalance());
    }

    @Test
    public void testUserAddMoney_FailWithCaseAmountLessThanZero() {
        // Mock data
        User user = new User("username", 150_000L);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        //assert result with expected
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.addMoney(userId, -10_000L);
        });
    }

}
