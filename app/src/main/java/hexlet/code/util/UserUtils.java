package hexlet.code.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import hexlet.code.model.User;
import hexlet.code.repository.UsersRepository;

@Component
public class UserUtils {
    @Autowired
    private UsersRepository usersRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
