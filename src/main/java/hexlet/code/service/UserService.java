package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.UnprocessableEntityException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getUserById(long id) {
        return userMapper.map(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found!")));
    }

    public UserDTO createUser(UserCreateDTO userData) {
        return userMapper.map(userRepository.save(userMapper.map(userData)));
    }

    public UserDTO updateUser(long id, UserUpdateDTO userData) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found!"));
        userMapper.update(userData, user);
        return userMapper.map(userRepository.save(user));
    }

    public void deleteUser(long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found!"));
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            throw new UnprocessableEntityException("Can't delete user, because it has tasks");
        }
    }
}
