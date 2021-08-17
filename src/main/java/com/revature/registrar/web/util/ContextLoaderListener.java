package com.revature.registrar.web.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.revature.registrar.repository.ClassModelRepo;
import com.revature.registrar.repository.UserRepository;
import com.revature.registrar.services.ClassService;
import com.revature.registrar.util.MongoClientFactory;
import com.revature.registrar.services.UserService;
import com.revature.registrar.util.PasswordUtils;
import com.revature.registrar.web.servlets.AuthServlet;
import com.revature.registrar.web.servlets.ClassServlet;
import com.revature.registrar.web.servlets.HealthCheckServlet;
import com.revature.registrar.web.servlets.UserServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("It's aliiiiive!!!");

        //MongoClient mongoClient = MongoClientFactory.getInstance().getConnection();
        PasswordUtils passwordUtils = new PasswordUtils();
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

        UserRepository userRepo = new UserRepository();
        ClassModelRepo classRepo = new ClassModelRepo();
        UserService userService = new UserService(userRepo, passwordUtils);
        ClassService classService = new ClassService(classRepo, userService);

        HealthCheckServlet healthCheckServlet = new HealthCheckServlet();
        UserServlet userServlet = new UserServlet(userService, mapper);
        ClassServlet classServlet = new ClassServlet(classService, mapper);
        AuthServlet authServlet = new AuthServlet(userService, mapper);

        ServletContext servletContext = sce.getServletContext();
        servletContext.addServlet("UserServlet", userServlet).addMapping("/users/*");
        servletContext.addServlet("ClassServlet", classServlet).addMapping("/classes/*");
        servletContext.addServlet("AuthServlet", authServlet).addMapping("/auth");
        servletContext.addServlet("HealthCheckServlet", healthCheckServlet).addMapping("/health");

        configureLogback(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Goodbye, cruel world!!!");
        MongoClientFactory.getInstance().cleanUp();
    }

    private void configureLogback(ServletContext servletContext) {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator logbackConfig = new JoranConfigurator();
        logbackConfig.setContext(loggerContext);
        loggerContext.reset();

        String logbackConfigFilePath = servletContext.getRealPath("") + File.separator + servletContext.getInitParameter("logback-config");

        try {
            logbackConfig.doConfigure(logbackConfigFilePath);
        } catch (JoranException e) {
            e.printStackTrace();
            System.out.println("An unexpected exception occurred. Unable to configure Logback.");
        }

    }

}
