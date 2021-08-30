package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.registrar.exceptions.*;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import com.revature.registrar.services.ClassService;
import com.revature.registrar.services.UserService;
import com.revature.registrar.web.dtos.ClassModelDTO;
import com.revature.registrar.web.dtos.UserDTO;
import com.revature.registrar.web.dtos.ErrorResponse;
import com.revature.registrar.web.dtos.Principal;
import com.revature.registrar.web.dtos.minis.ClassModelMini;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ClassServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ClassService classService;
    private final UserService userService;
    private final ObjectMapper mapper;

    public ClassServlet(ClassService classService, UserService userService, ObjectMapper mapper) {
        this.classService = classService;
        this.userService = userService;
        this.mapper = mapper;
    }


    /**
     * /registrar/classes: Gets all classes
     * /registrar/classes/id: Get the class with the given id
     * /registrar/classes?user_id=val: Get the classes for the user with the given user_id
     * /
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter respWriter = resp.getWriter();

        Principal requestingUser = (Principal) req.getAttribute("principal");

        String userIdParam = req.getParameter("id");

        //TODO: User needs to be logged in to view classes?
        if(requestingUser==null){
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }
        if(userIdParam != null) {
            //We are doing a find specific user.
            if (userIdParam.equals(requestingUser.getId())) {

                List<ClassModelDTO> classes;

                try {
                    classes = userService.getAllClassesOfUser(userIdParam);

                    resp.setStatus(200);

                    respWriter.write(mapper.writeValueAsString(classes));

                } catch (ResourceNotFoundException rnfe){
                    resp.setStatus(404);
                    ErrorResponse errResp = new ErrorResponse(404, rnfe.getMessage());
                    respWriter.write(mapper.writeValueAsString(errResp));
                } catch (Exception e){
                    resp.setStatus(500);
                    e.printStackTrace();
                    ErrorResponse errResp = new ErrorResponse(500, e.getMessage());
                    respWriter.write(mapper.writeValueAsString(errResp));
                }

            } else {
                String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
                logger.info(msg);
                resp.setStatus(403);
                ErrorResponse errResp = new ErrorResponse(403, msg);
                respWriter.write(mapper.writeValueAsString(errResp));
            }
            return;
        }

        //We want to find all
        try {
            List<ClassModelDTO> foundClasses = classService.getOpenClasses();
            respWriter.write(mapper.writeValueAsString(foundClasses));
        } catch (ResourceNotFoundException rnfe) {
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, rnfe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500); // server's fault
            ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later.");
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        resp.setStatus(200);

        return;
    }

    /**
     * /registrar/classes: Create a new class as the logged in user
     * /registrar/classes?user_id=val: Create a new class for Faculty member with id=val. Only works for ADMIN
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter = resp.getWriter();

        Principal requestingUser = (Principal) req.getAttribute("principal");

        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }
        User reqUser = userService.getUserWithId(requestingUser.getId());
        if(!reqUser.isFaculty()){
            //Then requesting user is not faculty
            String msg = "Must be faculty to create a class.";
            logger.info(msg);
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        resp.setContentType("application/json");

        Faculty facultyUser = (Faculty) reqUser;
        
        ClassModel classModel = mapper.readValue(req.getInputStream(), ClassModel.class);

        System.out.println(classModel);

        classModel.setId("hash");

        classModel.addFaculty(facultyUser);

        try {
            //Adds class to classCollection
            classService.register(classModel);

            //Add the class to the faculty member that created it

            facultyUser.addClass(classModel);

            //Update said faculty
            userService.update(facultyUser);


            logger.info("New class created!\n" + classModel.toString());
            resp.setStatus(201);
            respWriter.write(mapper.writeValueAsString(classModel.toString()));

        } catch(InvalidRequestException ire) {
            logger.error(ire.getStackTrace() + "\n");

            String msg = "Invalid request";
            System.out.println(msg);
            logger.error(msg);
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch(OpenWindowException owe){
            logger.error(owe.getStackTrace() + "\n");

            String msg = "Open window error! Opening date needs to be in the future";
            logger.error(msg);
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
        }
        catch(Exception e) {
            logger.error(e.getStackTrace() + "\n");

            String msg = "Unexpected error has occurred.";
            logger.error(msg);
            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
        }

        return;
    }

    /**
     * /registrar/classes/id: Update the class with the given id
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        PrintWriter respWriter = resp.getWriter();

        Principal requestingUser = (Principal) req.getAttribute("principal");

        String id = req.getParameter("id");

        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }
        try {
            User reqUser = userService.getUserWithId(requestingUser.getId());
            if (!reqUser.isFaculty()) {
                //Then requesting user is not faculty
                String msg = "Must be faculty to create a class.";
                logger.info(msg);
                resp.setStatus(403);
                ErrorResponse errResp = new ErrorResponse(403, msg);
                respWriter.write(mapper.writeValueAsString(errResp));
                return;
            }
            ClassModel oldClass = classService.getClassWithId(id);

            ClassModelMini classModelMini = mapper.readValue(req.getInputStream(), ClassModelMini.class);

            classModelMini.setId(id);
            classModelMini.setName(oldClass.getName());

            ClassModel newClass = new ClassModel(classModelMini);

            newClass.setFaculty(oldClass.getFaculty());

            //Remove or Add a fac member
            String facUsername = classModelMini.getFaculty();

            if(facUsername!=null){
                Set<Faculty> facList = oldClass.getFaculty();
                User addOrRemoveUser = userService.getUserWithUsername(facUsername);


                boolean removeUser = true;
                for(Faculty f: facList)
                    if(f.getUsername().equals(facUsername)) {
                        newClass.removeFac((Faculty) addOrRemoveUser);
                        removeUser = false;
                    }

                if(removeUser && addOrRemoveUser.isFaculty()){
                    newClass.addFaculty((Faculty)addOrRemoveUser);
                }

            }
            else {
                //Notify user that faculty member was not found so was not add/removed

            }

            newClass.setStudents(oldClass.getStudents());
            classService.update(newClass);

            resp.setStatus(201);
            respWriter.write(mapper.writeValueAsString(classModelMini));
        }
        catch(InvalidRequestException ire){
            String msg = "Given resource was invalid. " + ire.getMessage();
            logger.error(ire.getMessage());
            ErrorResponse errResp = new ErrorResponse(400, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            resp.setStatus(400);
        }catch(ResourceNotFoundException rnfe){
            String msg = "Resource was not found. "+rnfe.getMessage();
            logger.error(msg);
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
        }
    }

    /**
     * /registrar/classes/id: Delete the class with the given id
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //receive an id from req for deleting class
        resp.setContentType("application/json");
        PrintWriter respWriter = resp.getWriter();
        String id = req.getParameter("id");
        ClassModel classModel = null;

        Principal requestingUser = (Principal) req.getAttribute("principal");

        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }
        if(requestingUser.isFaculty() ){

            try {
                Faculty requestingFac = (Faculty)userService.getUserWithId(requestingUser.getId());
                if(!requestingFac.isInClasses(id) && !requestingUser.isAdmin()){
                    String msg = "Class is not listed as taught by requesting faculty member. Deletion not allowed.";
                    logger.info(msg);
                    resp.setStatus(405);
                    ErrorResponse errResp = new ErrorResponse(405, msg);
                    respWriter.write(mapper.writeValueAsString(errResp));
                    return;
                }
                classModel = classService.getClassWithId(id);

                classService.delete(classModel);

                ClassModelDTO returnClass = new ClassModelDTO(classModel);

                resp.setStatus(200);
                respWriter.write(mapper.writeValueAsString(returnClass));

            } catch (ResourceNotFoundException rnfe) {
                respWriter.write("Failed to retrieve resource with given ID.");
                logger.error("Resource with given ID was not found in DB.",rnfe);
                resp.setStatus(404);
            } catch (Exception e) {
                respWriter.write("Unexpected error has occurred.");
                logger.error("Unexpected error has occurred.", e);
                resp.setStatus(500);
            }

        } else{
            //requesting user is not faculty
            String msg = "Requesting user is not faculty. Deletion not allowed.";
            respWriter.write(msg);
            logger.info(msg);
            resp.setStatus(405);
        }
    }
}
