import java.lang.IndexOutOfBoundsException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/** This list represents a flight plan for space travel
  * useThrusters contains, for each iteration, whether we use the thrusters or not (true if we use the thrusters, false if not)
  * correctAngle contains, for each iteration, whether we corrected the angle or not and in which direction (0 if no correction, -1 if the angle is reduced, +1 if the angle is increased)
  */
public class FlightPlan {
    private List<Boolean> useThrusters;         //this list represents the plan of the use of thrusters during the travel
    private List<Integer> correctAngle;         //this list represents the plan of the angle correction during the travel
    //these two lists should have the same length (or size() as they are Lists) at all times

    /** Default constructor with no parameters
      * Initializes the useThrusters and correctAngle variables
      */
    public FlightPlan () {
        useThrusters = new ArrayList<Boolean>();
        correctAngle = new ArrayList<Integer>();
    }

    /** Fully parametric constructor
      * Initializes the useThrusters and correctAngle variables,
      * then copies the content from the parameter lists into these variables
      *
      * @param thrusterPlan the initial value for useThrusters
      * @param angleCorrectionPlan the initial value for correctAngle
      */
    public FlightPlan (List<Boolean> thrusterPlan, List<Integer> angleCorrectionPlan) {
        this();

        //Make sure that the two lists have the same size
        assert(thrusterPlan.size() == angleCorrectionPlan.size());

        //Copy the elemens from thrusterPlan into useThrusters
        for (int i = 0; i < thrusterPlan.size(); i ++) {
            useThrusters.add(thrusterPlan.get(i));
        }

        //Copy the elements from angleCorrection into correctAngle
        for (int i = 0; i < angleCorrectionPlan.size(); i ++) {
            correctAngle.add(angleCorrectionPlan.get(i));
        }
    }

    /** Additional constructor
      * Only one parameter, a FlightPlan from which we will copy the useThrusers and correctAngle lists
      *
      * @param other, the example flight plan from which we should copy the variables' content
      */
    public FlightPlan (FlightPlan other) {
        this(other.useThrusters, other.correctAngle);
    }

    /** Adds a new iteration to the flightPlan
      *
      * @param useThrustersAtIteration the value for useThrusters at the new iteration
      * @param correctAngleAtIteration the value for correctAngle at the new iteration
      */
    public void addIteration (boolean useThrustersAtIteration, int correctAngleAtIteration) {
        useThrusters.add(useThrustersAtIteration);
        correctAngle.add(correctAngleAtIteration);
    }

    /** Should return the item at entry index "iteration" if possible
      */
    public boolean getUseThrusters (int iteration) {
        return useThrusters.get(iteration);
    }

    /** Should return the int at entry index "iteration" if possible
      */
    public int getCorrectAngle (int iteration) {
        return correctAngle.get(iteration);
    }

    /** Sets the value of useThrusters and correctAngle at a given iteration
      *
      * @param iterationIndex the number of the iteration we want to modify
      * @param useThrustersAtIteration the new value of useThrusters of the given iteration
      * @param correctAngleAtIteration the new value of correctAngle of the given iteration
      */
    public void setIteration (int iterationIndex, boolean useThrustersAtIteration, int correctAngleAtIteration) {
        setUseThrusters(iterationIndex, useThrustersAtIteration);
        setCorrectAngle(iterationIndex, correctAngleAtIteration);
    }

    /** Sets the value of useThrusters and correctAngle at a given iteration
      *
      * @param iterationIndex the number of the iteration we want to modify
      * @param useThrustersAtIteration the new value of useThrusters of the given iteration
      */
    public void setUseThrusters (int iterationIndex, boolean useThrustersAtIteration) {
        useThrusters.set(iterationIndex, useThrustersAtIteration);
    }

    /** Sets the value of correctAngle at a given iteration
      *
      * @param iterationIndex the number of the iteration we want to modify
      * @param correctAngleAtIteration the new value of correctAngle of the given iteration
      */
    public void setCorrectAngle (int iterationIndex, int correctAngleAtIteration) {
        correctAngle.set(iterationIndex, correctAngleAtIteration);
    }

    /** Returns the number of iterations in the plan
      * It should be the size of useThrusters and correctAngle (which should both have the same length)
      */
    public int getPlanLength() {
        return useThrusters.size();
    }

    @Override
    public String toString () {
        return "FlightPlan[useThrusters=" + Arrays.toString(useThrusters.toArray()) + ", \n    correctAngle=" + Arrays.toString(correctAngle.toArray()) + "]";
    }
}
