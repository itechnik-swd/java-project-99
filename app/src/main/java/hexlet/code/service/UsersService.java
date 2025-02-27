package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getUserById(long id) {
        return userMapper.map(usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found")));
    }

    public UserDTO createUser(UserCreateDTO userData) {
        return userMapper.map(usersRepository.save(userMapper.map(userData)));
    }

    public UserDTO updateUser(long id, UserUpdateDTO userData) {
        var user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(userData, user);
        return userMapper.map(usersRepository.save(user));
    }

    public void deleteUser(long id) {
        var user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        usersRepository.delete(user);
    }
}
