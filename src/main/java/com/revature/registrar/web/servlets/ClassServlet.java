package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.User;
import com.revature.registrar.services.ClassService;
import com.revature.registrar.services.UserService;
import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.exceptions.ResourcePersistenceException;
import com.revature.registrar.web.dtos.ClassModelDTO;
import com.revature.registrar.web.dtos.UserDTO;
import com.revature.registrar.web.dtos.ErrorResponse;
import com.revature.registrar.web.dtos.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        HttpSession session = req.getSession(false);

        Principal requestingUser = (session == null) ? null : (Principal) session.getAttribute("auth-user");

        String userIdParam = req.getParameter("id");

        //TODO: User needs to be logged in to view classes?
        if(session==null){
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }
        if(userIdParam != null) {
            //We are doing a find specific user.
            if (requestingUser.isAdmin() || (userIdParam == requestingUser.getId())) {

                ClassModelDTO foundClass = new ClassModelDTO(classService.getClassWithId(userIdParam));
                respWriter.write(mapper.writeValueAsString(foundClass));
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
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500); // server's fault
            ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later.");
            respWriter.write(mapper.writeValueAsString(errResp));
        }
        resp.setContentType("application/json");
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

        //Need to validate info in class model is correct
        ClassModel classModel = mapper.readValue(req.getInputStream(), ClassModel.class);

        try {
            //Adds class to classCollection
            classService.register(classModel);
            //Add the class to the faculty member that created it
            ((Faculty) userService.getCurrUser()).addClass(classModel);
            //Update said faculty
            userService.update(userService.getCurrUser());
            logger.info("New class created!\n" + classModel.toString());

        } catch(Exception e) {
            logger.error("Invalid class details");
            logger.error(e.getStackTrace() + "\n");
            System.out.println("Invalid credentials");
        }

        PrintWriter respWriter = resp.getWriter();
        //resp.setContentType("application/json");
        resp.setStatus(200);
        respWriter.write("POST Endpoint works");
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
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        //resp.setContentType("application/json");
        resp.setStatus(200);
        respWriter.write("PUT Endpoint works");
        return;
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
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        //resp.setContentType("application/json");
        resp.setStatus(200);
        respWriter.write("DELETE Endpoint works");
        return;
    }

}
