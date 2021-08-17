package com.revature.registrar.web.dtos.minis;

import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;

import java.util.Calendar;
import java.util.Set;

//ClassModelDTO without reference to Users
//Need this because ClassModelDTO -> FacultyDTO -> ClassModelDTO -> ...
public class ClassModelMini {
    private int id;
    private String name;
    private int capacity;
    private String description;
    private Calendar openWindow;
    private Calendar closeWindow;


    public ClassModelMini(ClassModel subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.capacity = subject.getCapacity();
        this.description = subject.getDescription();
        this.openWindow = subject.getOpenWindow();
        this.closeWindow = subject.getCloseWindow();
    }

}
