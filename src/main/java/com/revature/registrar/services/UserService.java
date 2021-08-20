package com.revature.registrar.services;

import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.exceptions.ResourcePersistenceException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import com.revature.registrar.repository.UserRepository;
import com.revature.registrar.util.PasswordUtils;
import com.revature.registrar.web.dtos.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Middle man between Page and Database logic. Handles general business logic and wrapper functions
 * to expose the UserRepository
 */
public class UserService {
    private final UserRepository userRepo;
    private ClassService classService;
    private final Logger logger = LogManager.getLogger(UserService.class);

    private final PasswordUtils passUtil;


    public UserService(UserRepository userRepo, PasswordUtils passUtil) {
        this.userRepo = userRepo;
        this.passUtil = passUtil;
    }

    public void setClassService(ClassService classService) {
        this.classService = classService;
    }

    /**
     * Wrapper for userRepo.update
     * @param user
     * @return
     */
    public boolean update(User user) {
        try{
            if(!isValid(user)) {
                logger.error("Invalid user data provided\n");
                throw new InvalidRequestException("Invalid user data provided");
            }
        } catch (ResourcePersistenceException rpe) {
            logger.info("Updating existing resource");
        }
        return userRepo.update(user);
    }


    /**
     * Retieves the user with the given id
     * @param id
     * @return
     */
    public User getUserWithId(String id) {
        User result = userRepo.findById(id);
        if(result == null) {
            logger.error("Invalid ID\n");
            throw new ResourceNotFoundException();
        } else {
            return result;
        }
    }

    /**
     * Deletes a classModel from the classes field of all Users
     * @param classModel
     * @return
     * @throws RuntimeException
     */
    public boolean deleteClassFromAll(ClassModel classModel) throws RuntimeException {
        List<User> users = userRepo.findWithClass(classModel.getId());
        for(User user : users) {
            if (user.isFaculty()) {
                Faculty fac = (Faculty) user;
                fac.removeClass(classModel);
            } else {
                Student stu = (Student) user;
                stu.removeClass(classModel);
            }
            update(user);
        }

        return true;
    }

    /**
     * Refreshes the data of a User instance with fresh data from the database
     * @param user
     * @return
     */
    //Refresh classModel with complete information
    public User refresh(User user) {
        return userRepo.findById(user.getId());
    }

    /**
     * Validates user input, and stores in the UserRepo if valid
     * Returns stored User
     * @param user
     * @return
     * @throws RuntimeException
     */
    //Validate user input, store in UserRepo and return AppUser with repo_id
    public User register(User user) throws RuntimeException{
        if(!isValid(user)) {
            logger.error("Invalid user data provided\n");
            throw new InvalidRequestException("Invalid user data provided");
        }

        String encryptedPassword = passUtil.generateSecurePassword(user.getPassword());
        user.setPassword(encryptedPassword);
        //pass validated user to UserRepository
        userRepo.save(user);

        return user;
    }

    /**
     * Returns the User associated with a given username and password
     * @param username
     * @param password
     * @return
     */
    public User login(String username, String password) {

        String encryptedPassword = passUtil.generateSecurePassword(password);
        User user = userRepo.findUserByCredentials(username, encryptedPassword);
        return user;
    }

    /**
     * Returns a list of UserDTOs stored in db
     * @return
     */
    public List<UserDTO> findAll() {
        return userRepo.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns true if a user instance is "valid".
     * - Must contain no empty string values
     * - An element with this id must not exist in the db
     * @param user
     * @return
     */
    public boolean isValid(User user) {
        if(user == null) {
            return false;
        }
        if(user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
        if(user.getLastName() == null || user.getLastName().trim().equals("")) return false;
        if(user.getPassword() == null || user.getPassword().trim().equals("")) return false;
        if(user.getEmail() == null || user.getEmail().trim().equals("")) return false;
        if(user.getUsername() == null || user.getUsername().trim().equals("")) return false;

        //if a duplicate already exists in the db, reject
        if(userRepo.findById(user.getId()) != null) {
            logger.error("Duplicate");
            throw new ResourcePersistenceException("Duplicate");
        }

        return true;
    }

    public boolean deleteUser(User user) {
        //Unenroll student from all classes
        if(user.isFaculty()) {
            for(ClassModel classModel : ((Faculty) user).getClasses()) {
                classService.unenroll(user.getId(), classModel.getId());
            }
        } else {
            for(ClassModel classModel : ((Student) user).getClasses()) {
                classService.unenroll(user.getId(), classModel.getId());
            }
        }

        return userRepo.deleteById(user.getId());
    }
}
