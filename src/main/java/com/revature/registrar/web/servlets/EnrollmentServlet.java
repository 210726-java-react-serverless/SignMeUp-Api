package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.registrar.exceptions.ResourceNotFoundException;
import com.revature.registrar.services.ClassService;
import com.revature.registrar.services.UserService;
import com.revature.registrar.web.dtos.ErrorResponse;
import com.revature.registrar.web.dtos.Principal;
import com.revature.registrar.web.dtos.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class EnrollmentServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ClassService classService;
    private final ObjectMapper mapper;

    public EnrollmentServlet(ClassService classService, ObjectMapper mapper) {
        this.classService = classService;
        this.mapper = mapper;
    }

    /**
     * /registrar/enrollment/user_id=val?class_id=val
     * Enroll the user with the given id into the class with the given classid
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        } if(req.getParameter("id") == null) {
            //What error do we throw???
            String msg = "Invalid endpoint, id parameter required.";
            logger.info(msg);
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (req.getParameter("class_id") == null) {
            //What error do we throw???
            String msg = "Invalid endpoint, class_id parameter required.";
            logger.info(msg);
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (!requestingUser.isAdmin() && (req.getParameter("id") != requestingUser.getId())) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            logger.info(msg);
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        String userIdParam = req.getParameter("id");
        String classIdParam = req.getParameter("class_id");
        if(requestingUser.isAdmin() || (userIdParam == requestingUser.getId())) {
            //We can enroll
            try {
                classService.enroll(userIdParam, classIdParam);
                resp.setStatus(201);
            } catch (ResourceNotFoundException rnfe) {
                String msg = "Resource not found";
                logger.info(msg);
                resp.setStatus(404);
                ErrorResponse errResp = new ErrorResponse(403, msg);
                respWriter.write(mapper.writeValueAsString(errResp));
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(500); // server's fault
                ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later.");
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

    /**
     * /registrar/enrollment/user_id=val?class_id=val
     * Unenroll the user with the given id into the class with the given class_id
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        } if(req.getParameter("id") == null) {
            //What error do we throw???
            String msg = "Invalid endpoint, id parameter required.";
            logger.info(msg);
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (req.getParameter("class_id") == null) {
            //What error do we throw???
            String msg = "Invalid endpoint, class_id parameter required.";
            logger.info(msg);
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (!requestingUser.isAdmin() && (req.getParameter("id") != requestingUser.getId())) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            logger.info(msg);
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        String userIdParam = req.getParameter("id");
        String classIdParam = req.getParameter("class_id");
        if(requestingUser.isAdmin() || (userIdParam == requestingUser.getId())) {
            //We can unenroll
            try {
                classService.unenroll(userIdParam, classIdParam);
                resp.setStatus(201);
            } catch (ResourceNotFoundException rnfe) {
                String msg = "Resource not found";
                logger.info(msg);
                resp.setStatus(404);
                ErrorResponse errResp = new ErrorResponse(403, msg);
                respWriter.write(mapper.writeValueAsString(errResp));
            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(500); // server's fault
                ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later.");
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
}
