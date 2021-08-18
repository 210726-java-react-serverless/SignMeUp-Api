package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.registrar.models.User;
import com.revature.registrar.services.ClassService;
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

public class ClassServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ClassService classService;
    private final ObjectMapper mapper;

    public ClassServlet(ClassService classService, ObjectMapper mapper) {
        this.classService = classService;
        this.mapper = mapper;
    }


    /**
     * /registrar/classes: Gets all classes
     * /registrar/classes/id: Get the class with the given id
     * /registrar/classes?user_id=val: Get the classes for the user with the given user_id
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        //resp.setContentType("application/json");
        resp.setStatus(200);
        respWriter.write("GET Endpoint works");
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
        System.out.println(req.getAttribute("filtered"));
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
