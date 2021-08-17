package com.revature.registrar.web.dtos;

import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.models.User;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ClassModelDTO {
    private int id;
    private String name;
    private int capacity;
    private String description;
    private Calendar openWindow;
    private Calendar closeWindow;

    private Set<UserDTO> students = new HashSet<>();
    private Set<UserDTO> faculty = new HashSet<>(); //Could have multiple faculty members per class

    public ClassModelDTO(ClassModel subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.capacity = subject.getCapacity();
        this.description = subject.getDescription();
        this.openWindow = subject.getOpenWindow();
        this.closeWindow = subject.getCloseWindow();

        //Convert Users into UserDTOs, we don't care about their classes
        for (Student stu : subject.getStudents()) {
            students.add(new UserDTO(stu));
        }

        for (Faculty fac : subject.getFaculty()) {
            faculty.add(new UserDTO(fac));
        }
    }

}
