package entityClasses;

/*******
 * <p> Title: GradingParameter Class </p>
 * * <p> Description: This class represents a grading criterion or category within the system
 * ("Participation", "Homework"). It holds the unique identifier, the display name, 
 * and the maximum possible score (weight) for that category. </p>
 * * @author Daniel Ortiz Figueroa
 */
public class GradingParameter {
    private int id;
    private String name;
    private double maxScore; // or weight

    /*******
     * <p> Method: GradingParameter(int id, String name, double maxScore) </p>
     * * <p> Description: Constructor to initialize a new GradingParameter object. </p>
     * * @param id The unique identifier for this parameter.
     * @param name The display name of the grading category.
     * @param maxScore The maximum score or weight assigned to this category.
     */
    public GradingParameter(int id, String name, double maxScore) {
        this.id = id;
        this.name = name;
        this.maxScore = maxScore;
    }

    //Retrieves the unique ID of this grading parameter.
    public int getId() { return id; }
    //Retrieves the name of the grading category.
    public String getName() { return name; }
    //Updates the name of the grading category.
    public void setName(String name) { this.name = name; }
    //Retrieves the maximum possible score for this category.
    public double getMaxScore() { return maxScore; }
    //Updates the maximum possible score for this category.
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }
    
    /*******
     * <p> Method: String toString() </p>
     * * <p> Description: Returns a string representation of the parameter, useful for 
     * debugging or simple display purposes ("Participation (20.0)"). </p>
     * * @return A formatted string containing the name and max score.
     */
    @Override
    public String toString() { return name + " (" + maxScore + ")"; }
}