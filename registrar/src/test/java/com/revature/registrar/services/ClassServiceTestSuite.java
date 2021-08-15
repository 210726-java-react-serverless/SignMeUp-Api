package com.revature.registrar.services;

import com.revature.registrar.exceptions.InvalidRequestException;
import com.revature.registrar.exceptions.OpenWindowException;
import com.revature.registrar.models.ClassModel;
import com.revature.registrar.models.Faculty;
import com.revature.registrar.models.Student;
import com.revature.registrar.repository.ClassModelRepo;
import org.junit.*;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ClassServiceTestSuite {
    ClassService sut; //SUT = System Under Test
    ClassModelRepo mockClassRepo;

    /*
    common junit4 annotations
        - @BeforeClass
        - @AfterClass
        - @Before
        - @After
        - @Test
        - @Ignore
     */

    @BeforeClass //runs before all test case; runs only once
    public static void setUpSuite() {

    }

    @AfterClass //runs after all test cases; runs only once
    public static void tearDownSuite() {

    }

    @Before // runs before each test case
    public void beforeEachTest() {
        mockClassRepo = Mockito.mock(ClassModelRepo.class);
        sut = new ClassService(mockClassRepo);
    }

    @After // runs after each test case
    public void afterEachTest() {
        sut = null;
    }

    @Test (expected = OpenWindowException.class)
    public void isValid_throwsOpenWindowException_givenValidUser() {
        ///AAA - Arrange, Act, Assert

        Calendar open = Calendar.getInstance();
        Date d = new Date(open.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        Set<Faculty> fac = new HashSet<>();
        Set<Student> stu = new HashSet<>();

        // Arrange expectations
        boolean expected = true;
        ClassModel valid = new ClassModel("valid", "valid", 2, open, close, fac);


        // Act
        boolean actual = sut.isValid(valid); //This method is private... need to mock

        //Assert
        Assert.assertEquals("Expected user to be considered valid", expected, actual);
    }

    @Test
    public void register_returnsSuccessfully_whenGivenValidUser() {
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        Set<Faculty> fac = new HashSet<>();
        Set<Student> stu = new HashSet<>();

        ClassModel expected = new ClassModel("valid", "valid", 2, open, close, fac);
        ClassModel valid = new ClassModel("valid", "valid", 2, open, close, fac);


        when(mockClassRepo.save(any())).thenReturn(expected);

        // Act
        ClassModel actual = sut.register(valid);

        // Assert
        Assert.assertEquals(expected.getId(), actual.getId());
        verify(mockClassRepo, times(1)).save(any());
    }

    @Test (expected = InvalidRequestException.class)
    public void register_throwsInvalidRequestException_whenGivenInvalidClassModel() {
        // Arrange
        Calendar curr = Calendar.getInstance();
        Date d = new Date(curr.getTimeInMillis() + 10000);
        Calendar open = new Calendar.Builder()
                .setInstant(d)
                .build();
        d = new Date(curr.getTimeInMillis() + 100000);
        Calendar close = new Calendar.Builder()
                .setInstant(d)
                .build();
        Set<Faculty> fac = new HashSet<>();
        Set<Student> stu = new HashSet<>();

        ClassModel invalid = new ClassModel("", "valid", 2, open, close, fac);


        when(mockClassRepo.save(any())).thenReturn(invalid);

        // Act
        ClassModel actual = sut.register(invalid);

        // Assert
        verify(mockClassRepo, times(0)).save(any());
    }
}
