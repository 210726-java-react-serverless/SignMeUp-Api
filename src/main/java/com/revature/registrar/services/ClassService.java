package com.revature.registrar.services;

import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.OpenWindowException;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.exceptions.ResourcePersistenceException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import com.revature.registrar.repository.ClassModelRepo;
import com.revature.registrar.repository.UserRepository;
import com.revature.registrar.web.dtos.ClassModelDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Middle man between Page and Database logic. Handles general business logic and wrapper functions
 * to expose the ClassModelRepository
 */
public class ClassService {
    private final ClassModelRepo classRepo;
    private final UserService userService;
    private final Logger logger = LogManager.getLogger(ClassService.class);

    public ClassService(ClassModelRepo classRepo, UserService userService) {
        this.classRepo = classRepo;
        this.userService = userService;
    }

    /**
     * Gets the class with a given id and returns it
     * @param id
     * @return
     */
    public ClassModel getClassWithId(String id) {
        ClassModel result = classRepo.findById(id);
        if(result == null) {
            logger.error("Invalid ID\n");
            throw new InvalidRequestException("Invalid ID");
        } else {
            return classRepo.findById(id);
        }
    }

    /**
     * Enrolls a user in a course
     * @param user_id
     * @param class_id
     */
    public void enroll(String user_id, String class_id) {
        ClassModel classModel = null;
        try {
            classModel = getClassWithId(class_id);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }

        Student curr = (Student)userService.getUserWithId(user_id);
        if(curr.isInClasses(classModel)) {
            logger.info(user_id + " is already enrolled in " + class_id);
            System.out.println("ALREADY ENROLLED");
            return;
        }

        classModel.addStudent(curr);
        curr.addClass(classModel);

        //Need to persist these changes to the db with UPDATE
        update(classModel);
        userService.update(curr);
    }

    /**
     * Unenrolls a user from a course
     * classService.update(classModel) should be run afterwards to ensure the classdb is updated
     * @param user_id
     * @param class_id
     */
    public void unenroll(String user_id, String class_id) {
        ClassModel classModel = null;
        Student curr = null;
        try {
            classModel = getClassWithId(class_id);
            curr = (Student)userService.getUserWithId(user_id);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }

        if(canUnenroll(curr, classModel)) {
            classModel.removeStudent(curr);
            curr.removeClass(classModel);
            update(classModel);
            userService.update(curr);
        }
    }

    private boolean canUnenroll(Student user, ClassModel classModel) {
        if(!user.isInClasses(classModel)) {
            logger.error("Cannot unenroll from a class that they are not enrolled in\n");
            throw new InvalidRequestException("Student cannot unenroll from a class that they are not enrolled in");
        }

        Calendar current = Calendar.getInstance();
        boolean openOkay = classModel.getOpenWindow() < current.getTimeInMillis();
        boolean closeOkay = classModel.getCloseWindow() > current.getTimeInMillis();
        if(openOkay && closeOkay) {
            return true;
        } else {
            logger.error("Cannot unenroll from a class outside of the Registration Window\n");
            throw new InvalidRequestException("Cannot unenroll from a class outside of the Registration Window");
        }
    }

    /**
     * Refreshes the data of a classModel instance with fresh data from the database
     * @param classModel
     * @return
     */
    public ClassModel refresh(ClassModel classModel) {
        return classRepo.findById(classModel.getId());
    }

    /**
     * Retrieves a list of classes where the current date lies between the openDate and closeDate
     * @return
     */
    public List<ClassModelDTO> getOpenClasses() {
        return classRepo.findOpenClasses();
    }

    /**
     * Deletes a classModel from the database if it exists
     * @param classModel
     * @return
     */
    public boolean delete(ClassModel classModel) {
        userService.deleteClassFromAll(classModel);
        return classRepo.deleteById(classModel.getId());
    }

    /**
     * Updates the fields of a given classModel in the database with new fields
     * @param classModel
     * @return
     */
    public boolean update(ClassModel classModel) {
        try {
            if (!isValid(classModel)) {
                logger.error("Invalid classModel data provided\n");
                throw new InvalidRequestException("Invalid classModel data provided");
            }
        } catch (ResourcePersistenceException rpe) {
            logger.info("Updating existing resource");
        } catch (OpenWindowException owe) {
            logger.info("Updating existing resource");
        }

        userService.updateClassForAll(classModel);

        return classRepo.update(classModel);
    }

    /**
     * Validates user input and stores the classModel in the database if it is valid
     * @param classModel
     * @return
     * @throws RuntimeException
     */
    public ClassModel register(ClassModel classModel) throws RuntimeException{
        if(!isValid(classModel)) {
            logger.error("Invalid classModel data provided\n");
            throw new InvalidRequestException("Invalid classModel data provided");
        }
        //pass validated user to UserRepository
        System.out.println("In Register and Saving");
        classRepo.save(classModel);
        return classModel;
    }


    /**
     * Returns true if a classModel instance is "valid".
     * - Must contain no empty string values
     * - Capacity must be a positive integer greater than the size of the students set
     * - Open and Close windows must be greater than the current time
     * - Open window must happen before the Close window
     * - An element with this id must not exist in the db
     * @param classModel
     * @return
     */
    public boolean isValid(ClassModel classModel) {
        if(classModel == null) {
            return false;
        }

        Calendar current = Calendar.getInstance();
        System.out.println("in isValid");

        if(classModel.getStudents() == null) return false;
        if(classModel.getFaculty() == null) return false;

        if(classModel.getName() == null || classModel.getName().trim().equals("")) return false;

        if(classModel.getDescription() == null || classModel.getDescription().trim().equals("")) return false;
        if(classModel.getCapacity() <= 0) return false;
        //Below line will cause error if no
        System.out.println("Before get students in isValid");
        if(classModel.getCapacity() < classModel.getStudents().size()) return false;
        System.out.println("After get students in isValid");
        //Open/Close Windows cannot be before the current time
        if(classModel.getOpenWindow() <= 0) return false;
        if(classModel.getCloseWindow() <= 0 || classModel.getCloseWindow() <= current.getTimeInMillis() ) return false;
        //Open has to be before the close
        if(classModel.getCloseWindow() <= classModel.getOpenWindow() ) return false;

        //if a duplicate already exists in the db, reject
        if(classRepo.findById(classModel.getId()) != null) {
            System.out.println("Duplicate class in db");
            logger.error("Duplicate");
            throw new ResourcePersistenceException("Duplicate");
        }

        if(classModel.getOpenWindow() <= current.getTimeInMillis()) throw new OpenWindowException("Window is open");

        System.out.println("After isValid");

        return true;
    }

}
