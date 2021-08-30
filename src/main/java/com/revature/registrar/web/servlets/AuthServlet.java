package com.revature.registrar.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.registrar.services.UserService;
import com.revature.registrar.exceptions.AuthenticationException;
import com.revature.registrar.web.dtos.Credentials;
import com.revature.registrar.web.dtos.ErrorResponse;
import com.revature.registrar.web.dtos.Principal;
import com.revature.registrar.web.util.security.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(AuthServlet.class);
    private final UserService userService;
    private final ObjectMapper mapper;
    private final TokenGenerator tokenGenerator;

    public AuthServlet(UserService userService, ObjectMapper mapper, TokenGenerator tokenGenerator) {
        this.userService = userService;
        this.mapper = mapper;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            Credentials creds = mapper.readValue(req.getInputStream(), Credentials.class);
            Principal principal = new Principal(userService.login(creds.getUsername(), creds.getPassword()));

            System.out.println(mapper.writeValueAsString(principal));

            String payload = mapper.writeValueAsString(principal);

            respWriter.write(payload);

            String token = tokenGenerator.createToken(principal);

            resp.setHeader(tokenGenerator.getJwtConfig().getHeader(),token);

            logger.info("**** Authenticated as "+ principal.getUsername() + " ****");

        } catch (AuthenticationException ae) {
            resp.setStatus(401); // user's fault
            ErrorResponse errResp = new ErrorResponse(401, ae.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        }  catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500); // server's fault
            respWriter.write(e.getMessage());
            ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later. ");
        }

    }
}
