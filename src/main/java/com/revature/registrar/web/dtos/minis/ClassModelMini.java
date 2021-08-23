package com.revature.registrar.web.dtos.minis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;

import java.util.Calendar;
import java.util.Set;

//ClassModelDTO without reference to Users
//Need this because ClassModelDTO -> FacultyDTO -> ClassModelDTO -> ...
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassModelMini {
    private String id;
    private String name;
    private int capacity;
    private String description;
    private long openWindow;
    private long closeWindow;


    public ClassModelMini(ClassModel subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.capacity = subject.getCapacity();
        this.description = subject.getDescription();
        this.openWindow = subject.getOpenWindow();
        this.closeWindow = subject.getCloseWindow();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getOpenWindow() {
        return openWindow;
    }

    public void setOpenWindow(long openWindow) {
        this.openWindow = openWindow;
    }

    public long getCloseWindow() {
        return closeWindow;
    }

    public void setCloseWindow(long closeWindow) {
        this.closeWindow = closeWindow;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
