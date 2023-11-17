package lac.test.service;

import lac.test.entity.User;
import lac.test.exception.BadRequestException;
import lac.test.model.UserModel;

public interface UserService {
    User getByUserId(Long userId) throws BadRequestException;

    UserModel getByUserIdAndMapToModel(Long userId) throws BadRequestException;

    UserModel create(UserModel model) throws BadRequestException;

    UserModel addMoney(Long userId, Long amount) throws BadRequestException;

    User pay(Long userId, Long amount) throws BadRequestException;

}
