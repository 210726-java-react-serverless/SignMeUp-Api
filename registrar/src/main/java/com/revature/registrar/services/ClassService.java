package com.revature.registrar.services;

import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.OpenWindowException;
import com.revature.registrar.exceptions.ResourcePersistenceException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.User;
import com.revature.registrar.repository.ClassModelRepo;
import com.revature.registrar.repository.UserRepository;
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
    private final Logger logger = LogManager.getLogger(ClassService.class);

    public ClassService(ClassModelRepo classRepo) {
        this.classRepo = classRepo;
    }

    /**
     * Gets the class with a given id and returns it
     * @param id
     * @return
     */
    public ClassModel getClassWithId(int id) {
        ClassModel result = classRepo.findById(id);
        if(result == null) {
            logger.error("Invalid ID\n");
            throw new InvalidRequestException("Invalid ID");
        } else {
            return classRepo.findById(id);
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
    public List<ClassModel> getOpenClasses() {
        return classRepo.findOpenClasses();
    }

    /**
     * Deletes a classModel from the database if it exists
     * @param classModel
     * @return
     */
    public boolean delete(ClassModel classModel) {
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
        if(classModel.getName() == null || classModel.getName().trim().equals("")) return false;
        if(classModel.getDescription() == null || classModel.getDescription().trim().equals("")) return false;
        if(classModel.getCapacity() <= 0) return false;
        if(classModel.getCapacity() < classModel.getStudents().size()) return false;
        //Open/Close Windows cannot be before the current time
        if(classModel.getOpenWindow() == null) return false;
        if(classModel.getCloseWindow() == null || classModel.getCloseWindow().getTimeInMillis() <= current.getTimeInMillis() ) return false;
        //Open has to be before the close
        if(classModel.getCloseWindow().getTimeInMillis() <= classModel.getOpenWindow().getTimeInMillis() ) return false;

        if(classModel.getStudents() == null) return false;
        if(classModel.getFaculty() == null) return false;

        //if a duplicate already exists in the db, reject
        if(classRepo.findById(classModel.getId()) != null) {
            logger.error("Duplicate");
            throw new ResourcePersistenceException("Duplicate");
        }
        if(classModel.getOpenWindow().getTimeInMillis() <= current.getTimeInMillis()) throw new OpenWindowException("Window is open");

        return true;
    }

}
