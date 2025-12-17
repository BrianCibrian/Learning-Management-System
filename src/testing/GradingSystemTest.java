package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.User;
import entityClasses.GradingParameter;
import entityClasses.StudentScore;

import java.util.List;

/*******
 * <p> Title: GradingSystemTest Class </p>
 * * <p> Description: This class performs JUnit 5 testing on the Grading System.
 * It validates the functionality of the Entity classes and the 
 * Database operations. </p>
 * * @author Daniel Ortiz Figueroa
 */
public class GradingSystemTest {

    // Reference to the database instance used for testing
    private Database db;
    
    // A fake/test user object used to simulate student interactions in tests
    private User testStudent;

    /*******
     * <p> Method: setUp() </p>
     * * <p> Description: This method runs before each test execution. It establishes a fresh 
     * connection to the database. It checks if the test student already exists and 
     * registers them only if they are missing.
     * This ensures that every test starts with a known state without unique constraint violations.</p>
     * */
    @BeforeEach
    public void setUp() throws Exception {
        // 1. Initialize connection
        db = new Database();
        db.connectToDatabase();
        
        // 2. Create a fake/test student object for testing
        testStudent = new User("testStudent", "password", "Test", "M", "Student", "Test", "test@asu.edu", false, false, true);
        
        // 3. Check if user exists
        if (!db.doesUserExist(testStudent.getUserName())) {
            db.register(testStudent);
        }
    }

    /*******
     * <p> Method: tearDown() </p>
     * * <p> Description: This method runs after each test execution. It closes the database 
     * connection to ensure resource management.</p>
     * */
    @AfterEach
    public void tearDown() throws Exception {
        // Close connection
        db.closeConnection();
    }

    /*******
     * <p> Method: testAddAndRetrieveGradingParameter() </p>
     * * <p> Description: Tests the ability to insert a new Grading Parameter into the database
     * and successfully retrieve it using the getGradingParameters() list method.</p>
     * */
    @Test
    public void testAddAndRetrieveGradingParameter() {
        // 1. Add a new parameter
        db.addGradingParameter("Projects", 50.0);

        // 2. Retrieve list
        List<GradingParameter> params = db.getGradingParameters();
        
        // 3. Find our new parameter
        boolean found = false;
        for (GradingParameter p : params) {
            if (p.getName().equals("Projects") && p.getMaxScore() == 50.0) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The new grading parameter should exist in the database");
    }

    /*******
     * <p> Method: testUpdateGradingParameter() </p>
     * * <p> Description: Tests the ability to update an existing Grading Parameter's name 
     * and weight (max score) in the database.</p>
     * */
    @Test
    public void testUpdateGradingParameter() {
        // 1. Add parameter
        db.addGradingParameter("Old Name", 10.0);
        List<GradingParameter> params = db.getGradingParameters();
        int idToEdit = -1;
        
        // Find the ID (use the last added to ensure we get the one we just made)
        for(GradingParameter p : params) {
            if(p.getName().equals("Old Name")) idToEdit = p.getId();
        }

        // 2. Update it
        db.updateGradingParameter(idToEdit, "New Name", 20.0);

        // 3. Verify
        params = db.getGradingParameters();
        boolean verified = false;
        for(GradingParameter p : params) {
            if(p.getId() == idToEdit) {
                assertEquals("New Name", p.getName());
                assertEquals(20.0, p.getMaxScore());
                verified = true;
            }
        }
        assertTrue(verified, "Parameter update should be reflected in DB");
    }

    /*******
     * <p> Method: testAssignAndRetrieveStudentGrade() </p>
     * * <p> Description: Tests the ability to assign a specific score to a student for a 
     * specific parameter and retrieve that score accurately.</p>
     * */
    @Test
    public void testAssignAndRetrieveStudentGrade() {
        // 1. Create a parameter to grade against
        db.addGradingParameter("Homework 1", 100.0);
        
        // Get the ID of that parameter
        List<GradingParameter> params = db.getGradingParameters();
        // Safely get the ID of the parameter we just added
        int paramId = 0;
        for(GradingParameter p : params) {
            if(p.getName().equals("Homework 1")) paramId = p.getId();
        }

        // 2. Assign a grade (INSERT/MERGE)
        db.updateStudentScore(testStudent.getUserName(), paramId, 85.5);

        // 3. Retrieve scores for student
        List<StudentScore> scores = db.getStudentScores(testStudent.getUserName());

        // 4. Verify
        boolean gradeFound = false;
        for (StudentScore ss : scores) {
            if (ss.getParameterId() == paramId) {
                assertEquals(85.5, ss.getScore(), 0.01);
                assertEquals(100.0, ss.getMaxScore(), 0.01);
                gradeFound = true;
            }
        }
        assertTrue(gradeFound, "The assigned grade should be retrievable");
    }

    /*******
     * <p> Method: testUpdateExistingGrade() </p>
     * * <p> Description: Tests the merge logic. It verifies that if a grade is assigned
     * to a student who already has a grade for that parameter, the existing record is updated
     * rather than a duplicate record being created.</p>
     * */
    @Test
    public void testUpdateExistingGrade() {
        // 1. Setup parameter and initial grade
        db.addGradingParameter("Midterm", 100.0);
        List<GradingParameter> params = db.getGradingParameters();
        int paramId = 0;
        for(GradingParameter p : params) {
            if(p.getName().equals("Midterm")) paramId = p.getId();
        }

        db.updateStudentScore(testStudent.getUserName(), paramId, 50.0); // Initial Grade

        // 2. Update the grade (Should replace, not duplicate)
        db.updateStudentScore(testStudent.getUserName(), paramId, 95.0); // Correction

        // 3. Verify
        List<StudentScore> scores = db.getStudentScores(testStudent.getUserName());
        
        int count = 0;
        double actualScore = 0;
        for (StudentScore ss : scores) {
            if (ss.getParameterId() == paramId) {
                count++;
                actualScore = ss.getScore();
            }
        }

        assertEquals(1, count, "There should be only one score entry per parameter per student");
        assertEquals(95.0, actualScore, 0.01, "The score should be updated to the new value");
    }

    /*******
     * <p> Method: testDeleteParameterCascades() </p>
     * * <p> Description: Tests the referential integrity of the database. It verifies that
     * deleting a Grading Parameter automatically removes all associated student scores for 
     * that parameter.</p>
     * */
    @Test
    public void testDeleteParameterCascades() {
        // Scenario: If we delete "Temp Category", all student grades for it should disappear
        
        // 1. Setup
        db.addGradingParameter("Temp Category", 10.0);
        int paramId = 0;
        for(GradingParameter p : db.getGradingParameters()) {
            if(p.getName().equals("Temp Category")) paramId = p.getId();
        }
        
        db.updateStudentScore(testStudent.getUserName(), paramId, 10.0);
        
        // Ensure it exists
        assertFalse(db.getStudentScores(testStudent.getUserName()).isEmpty());

        // 2. Delete the parameter
        db.deleteGradingParameter(paramId);

        // 3. Verify the grade is gone
        List<StudentScore> scores = db.getStudentScores(testStudent.getUserName());
        final int deletedId = paramId;
        boolean found = scores.stream().anyMatch(ss -> ss.getParameterId() == deletedId);
        
        assertFalse(found, "Deleting a grading parameter should remove associated student scores");
    }
}