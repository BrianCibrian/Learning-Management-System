package entityClasses;

/*******
 * <p> Title: StudentScore Class </p>
 * * <p> Description: This class represents a specific grade assigned to a student for a 
 * specific grading parameter. It links the parameter details (ID and Name) with the 
 * actual score earned by the student. </p>
 * * @author Daniel Ortiz Figueroa
 */

public class StudentScore {
    private String parameterName;
    private int parameterId;
    private double score;
    private double maxScore;

    
    /*******
     * <p> Method: StudentScore(String parameterName, int parameterId, double score, double maxScore) </p>
     * * <p> Description: Constructor to initialize a new StudentScore object. </p>
     * * @param parameterName The name of the grading category.
     * @param parameterId The unique ID of the grading category.
     * @param score The actual score earned by the student.
     * @param maxScore The maximum possible score for this category.
     */
    public StudentScore(String parameterName, int parameterId, double score, double maxScore) {
        this.parameterName = parameterName;
        this.parameterId = parameterId;
        this.score = score;
        this.maxScore = maxScore;
    }
    //Retrieves the name of the grading parameter associated with this score.
    public String getParameterName() { return parameterName; }
    //Retrieves the ID of the grading parameter associated with this score. 
    public int getParameterId() { return parameterId; }
    //Retrieves the numeric score earned by the student.
    public double getScore() { return score; }
    //Updates the numeric score for this entry.
    public void setScore(double score) { this.score = score; }
    //Retrieves the maximum possible score for this category.
    public double getMaxScore() { return maxScore; }
}