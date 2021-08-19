package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.registrar.services.ClassService;
import com.revature.registrar.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EnrollmentServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final ClassService classService;
    private final ObjectMapper mapper;

    public EnrollmentServlet(ClassService classService, ObjectMapper mapper) {
        this.classService = classService;
        this.mapper = mapper;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
