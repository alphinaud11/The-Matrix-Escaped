package code;

import java.util.HashMap;
import java.util.List;

public abstract class SearchProblem {

    public List<String> operators;
    public String initialState;
    public HashMap<String, Boolean> generatedStates;
    public int chosenNodes;
    public abstract boolean goalTest(String state);
    public abstract int pathCost(String state);

    public SearchProblem(List<String> operators) {
        this.operators = operators;
        initialState = "";
        generatedStates = new HashMap<>();
        chosenNodes = 0;
    }

}
