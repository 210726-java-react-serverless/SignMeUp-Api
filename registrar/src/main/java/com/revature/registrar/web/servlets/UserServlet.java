package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // Get the session from the request, if it exists (do not create one)
        HttpSession session = req.getSession(false);

        // If the session is not null, then grab the auth-user attribute from it
        Principal requestingUser = (session == null) ? null : (Principal) session.getAttribute("auth-user");

        // Check to see if there was a valid auth-user attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (!requestingUser.getUsername().equals("wsingleton")) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            logger.info(msg);
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        String userIdParam = req.getParameter("id");

        try {

            if (userIdParam == null) {
                List<UserDTO> users = userService.findAll();
                respWriter.write(mapper.writeValueAsString(users));
            } else {
                UserDTO user = new UserDTO(userService.getUserWithId(Integer.parseInt(userIdParam)));
                respWriter.write(mapper.writeValueAsString(user));
            }

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            User newUser = mapper.readValue(req.getInputStream(), User.class);
            Principal principal = new Principal(userService.register(newUser)); // after this, the newUser should have a new id
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
}
