package com.revature.registrar.web.dtos;

import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.web.dtos.minis.ClassModelMini;

import java.util.HashSet;
import java.util.Set;

public class FacultyDTO extends UserDTO {

    private Set<ClassModelMini> classes = new HashSet<>();

    public FacultyDTO(Faculty subject) {
        super(subject);
        for (ClassModel classModel : subject.getClasses()) {
            classes.add(new ClassModelMini(classModel));
        }

    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + this.getId() +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", isFaculty=" + "false" +
                '}';
    }
}
