package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;
import com.revature.registrar.services.UserService;
import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.exceptions.ResourcePersistenceException;
import com.revature.registrar.web.dtos.UserDTO;
import com.revature.registrar.web.dtos.ErrorResponse;
import com.revature.registrar.web.dtos.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class UserServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final UserService userService;
    private final ObjectMapper mapper;

    public UserServlet(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    /**
     * /registrar/users: Get all users in the system
     * /registrar/users/id: Get the user with the specified id
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // Get the session from the request, if it exists (do not create one)
        HttpSession session = req.getSession(false);

        // If the session is not null, then grab the auth-user attribute from it
        Principal requestingUser = (session == null) ? null : (Principal) session.getAttribute("auth-user");

        String userIdParam = req.getParameter("id");

        // Check to see if there was a valid auth-user attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } if(userIdParam!=null) {
            //We are doing a find specific user.
            if(requestingUser.isAdmin() || (userIdParam.equals(requestingUser.getId()))) {

                //Return the User
                UserDTO user = new UserDTO(userService.getUserWithId(userIdParam));
                respWriter.write(mapper.writeValueAsString(user));
            } else {
                String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
                logger.info(msg);
                resp.setStatus(403);
                ErrorResponse errResp = new ErrorResponse(403, msg);
                respWriter.write(mapper.writeValueAsString(errResp));
                return;
            }
            return;
        } else if (!requestingUser.isAdmin()) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            logger.info(msg);
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        //We want to find all
        try {
            List<UserDTO> users = userService.findAll();
            respWriter.write(mapper.writeValueAsString(users));
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
    }

    /**
     * /registrar/users: Register a new user in the system
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            org.apache.commons.io.IOUtils.copy(req.getInputStream(), baos);

            byte[] body = baos.toByteArray();
            //Jerry Rigged work around
            User newUser = mapper.readValue(body, User.class);

            if(newUser.isFaculty()) {
                newUser = mapper.readValue(body, Faculty.class);
            } else {
                newUser = mapper.readValue(body, Student.class);
            }


            Principal principal = new Principal(userService.register(newUser)); // after this, the newUser should have a valid id
            String payload = mapper.writeValueAsString(principal);
            respWriter.write(payload);
            resp.setStatus(201);

        } catch (InvalidRequestException | MismatchedInputException e) {
            e.printStackTrace();
            resp.setStatus(400); // client's fault
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (ResourcePersistenceException rpe) {
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500); // server's fault
        }
    }

    /**
     * /registrar/users/id: Update the user with the given id
     * TODO: STRETCH GOAL
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
     * /registrar/users/id: Delete the user with the given id
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    //*****************
    //TODO: Make sure classes with user remove the user if they are deleted (REF INT.)
    //*****************
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // If the session is not null, then grab the auth-user attribute from it
        Principal requestingUser = (session == null) ? null : (Principal) session.getAttribute("auth-user");

        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (!requestingUser.isAdmin()) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            logger.info(msg);
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }


        //Delete based on entered id or current user?

        String userIdParam = req.getParameter("id");
        User user = userService.getUserWithId(userIdParam);

        if(userService.deleteUser(user))
            respWriter.write("User successfully deleted.");
        else
            respWriter.write("User was not deleted.");

        resp.setStatus(200);

        return;
    }
}
