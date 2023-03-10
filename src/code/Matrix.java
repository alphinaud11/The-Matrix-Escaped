package code;

import java.util.*;

public class Matrix extends SearchProblem {

    public static Matrix matrix;

    public Matrix() {
        super(Arrays.asList("carry", "drop", "takePill", "fly", "up", "down", "left", "right", "kill"));
    }

    @Override
    public boolean goalTest(String state) {
        String[] splittedState = state.split(";");
        String neoX = splittedState[0].split(",")[0];
        String neoY = splittedState[0].split(",")[1];
        String telephoneBoothX = splittedState[8].split(",")[0];
        String telephoneBoothY = splittedState[8].split(",")[1];
        String hostages = splittedState[2];
        String carriedHostages = splittedState[3];
        String mutatedAgents = splittedState[6];

        return neoX.equals(telephoneBoothX) && neoY.equals(telephoneBoothY) && hostages.isEmpty()
                && carriedHostages.isEmpty() && mutatedAgents.isEmpty();
    }

    @Override
    public int pathCost(String state) {
        String[] splittedState = state.split(";");
        int deaths = Integer.parseInt(splittedState[9]);
        int kills = Integer.parseInt(splittedState[10]);

        return (225*deaths) + kills;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Generate grid

    public static String genGrid() {
        String grid = "";
        Random rand = new Random();
        // m,n from 5 to 15
        int m = 5 + rand.nextInt(11);
        int n = 5 + rand.nextInt(11);
        ArrayList<int[]> positions = new ArrayList<>();

        for (int i=0; i<n; i++) {
            for (int j=0; j<m; j++) {
                positions.add(new int[]{i, j});
            }
        }

        // C from 1 to 4
        int c = 1 + rand.nextInt(4);
        int[] neoPosition = positions.remove(rand.nextInt(positions.size()));
        int[] tbPosition = positions.remove(rand.nextInt(positions.size()));

        // hostages from 3 to 10
        int noOfHostages = 3 + rand.nextInt(8);
        StringBuilder hostages = new StringBuilder();
        for (int i=0; i<noOfHostages; i++) {
            int[] hostagePosition = positions.remove(rand.nextInt(positions.size()));
            int hostageDamage = 1 + rand.nextInt(99);
            hostages.append(hostagePosition[0]).append(",").append(hostagePosition[1]).append(",").append(hostageDamage);
            if (i != noOfHostages - 1)
                hostages.append(",");
        }

        // pills from 1 to number of hostages
        int noOfPills = 1 + rand.nextInt(noOfHostages);
        StringBuilder pills = new StringBuilder();
        for (int i=0; i<noOfPills; i++) {
            int[] pillPosition = positions.remove(rand.nextInt(positions.size()));
            pills.append(pillPosition[0]).append(",").append(pillPosition[1]);
            if (i != noOfPills - 1)
                pills.append(",");
        }

        // pads from 1 pair to maximum number of pairs (making sure at least one cell is left for agents)
        int maxNoOfPadPairs = positions.size() / 2;
        if (positions.size() % 2 == 0)
            maxNoOfPadPairs--;
        int noOfPadPairs = 1 + rand.nextInt(maxNoOfPadPairs);
        StringBuilder pads = new StringBuilder();
        for (int i=0; i<noOfPadPairs; i++) {
            int[] padStartPosition = positions.remove(rand.nextInt(positions.size()));
            int[] padFinishPosition = positions.remove(rand.nextInt(positions.size()));
            pads.append(padStartPosition[0]).append(",").append(padStartPosition[1]).append(",").append(padFinishPosition[0]).append(",").append(padFinishPosition[1]).append(",").append(padFinishPosition[0]).append(",").append(padFinishPosition[1]).append(",").append(padStartPosition[0]).append(",").append(padStartPosition[1]);
            if (i != noOfPadPairs - 1)
                pads.append(",");
        }

        // agents from 1 to whatever positions left
        int noOfAgents = 1 + rand.nextInt(positions.size());
        StringBuilder agents = new StringBuilder();
        for (int i=0; i<noOfAgents; i++) {
            int[] agentPosition = positions.remove(rand.nextInt(positions.size()));
            agents.append(agentPosition[0]).append(",").append(agentPosition[1]);
            if (i != noOfAgents - 1)
                agents.append(",");
        }

        grid += m + "," + n + ";" + c + ";" + neoPosition[0] + "," + neoPosition[1] + ";" + tbPosition[0] + "," + tbPosition[1] + ";" + agents.toString() + ";" + pills.toString() + ";" + pads.toString() + ";" + hostages.toString();

        return grid;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // solve matrix

    public String generateInitialState(String grid) {
        String initialState = "";
        String[] splitGrid = grid.split(";");
        // Neo info
        initialState += splitGrid[2] + ",0," + splitGrid[1] + ";";
        // Grid size
        initialState += splitGrid[0] + ";";
        // Hostages
        initialState += splitGrid[7] + ";";
        // Carried hostages
        initialState += ";";
        // Pills
        initialState += splitGrid[5] + ";";
        // Normal agents
        initialState += splitGrid[4] + ";";
        // Mutated agents
        initialState += ";";
        // Pads
        initialState += splitGrid[6] + ";";
        // Telephone booth
        initialState += splitGrid[3] + ";";
        // Deaths
        initialState += "0;";
        // Kills
        initialState += "0";

        matrix.initialState = initialState;
        return initialState;
    }

    public Node makeNode(String state, Node parentNode, String operator, int depth, int pathCost) {
        return new MatrixNode(state, parentNode, operator, depth, pathCost);
    }

    public boolean isApplicable(String state, String operator) {
        String[] splittedState = state.split(";");
        String neoInfo = splittedState[0];
        String gridSize = splittedState[1];
        String hostages = splittedState[2];
        String carriedHostages = splittedState[3];
        String pills = splittedState[4];
        String normalAgents = splittedState[5];
        String mutatedAgents = splittedState[6];
        String pads = splittedState[7];
        String telephoneBooth = splittedState[8];

        switch (operator) {
            case "up": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                if (neoX == 0)
                    return false;
                String[] normalSplit = normalAgents.split(",");
                for (int i = 0; i < normalSplit.length && !normalSplit[i].equals(""); i += 2) {
                    int normalAgentX = Integer.parseInt(normalSplit[i]);
                    int normalAgentY = Integer.parseInt(normalSplit[i + 1]);
                    if (neoX - 1 == normalAgentX && neoY == normalAgentY)
                        return false;
                }
                String[] mutatedSplit = mutatedAgents.split(",");
                for (int i = 0; i < mutatedSplit.length && !mutatedSplit[i].equals(""); i += 2) {
                    int mutatedAgentX = Integer.parseInt(mutatedSplit[i]);
                    int mutatedAgentY = Integer.parseInt(mutatedSplit[i + 1]);
                    if (neoX - 1 == mutatedAgentX && neoY == mutatedAgentY)
                        return false;
                }
                String[] hostagesSplit = hostages.split(",");
                for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                    int hostageX = Integer.parseInt(hostagesSplit[i]);
                    int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                    int hostageDamage = Integer.parseInt(hostagesSplit[i + 2]);
                    if (neoX - 1 == hostageX && neoY == hostageY && hostageDamage >= 98)
                        return false;
                }
                break;
            }
            case "down": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                int n = Integer.parseInt(gridSize.split(",")[1]);
                if (neoX == n - 1)
                    return false;
                String[] normalSplit = normalAgents.split(",");
                for (int i = 0; i < normalSplit.length && !normalSplit[i].equals(""); i += 2) {
                    int normalAgentX = Integer.parseInt(normalSplit[i]);
                    int normalAgentY = Integer.parseInt(normalSplit[i + 1]);
                    if (neoX + 1 == normalAgentX && neoY == normalAgentY)
                        return false;
                }
                String[] mutatedSplit = mutatedAgents.split(",");
                for (int i = 0; i < mutatedSplit.length && !mutatedSplit[i].equals(""); i += 2) {
                    int mutatedAgentX = Integer.parseInt(mutatedSplit[i]);
                    int mutatedAgentY = Integer.parseInt(mutatedSplit[i + 1]);
                    if (neoX + 1 == mutatedAgentX && neoY == mutatedAgentY)
                        return false;
                }
                String[] hostagesSplit = hostages.split(",");
                for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                    int hostageX = Integer.parseInt(hostagesSplit[i]);
                    int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                    int hostageDamage = Integer.parseInt(hostagesSplit[i + 2]);
                    if (neoX + 1 == hostageX && neoY == hostageY && hostageDamage >= 98)
                        return false;
                }
                break;
            }
            case "left": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                if (neoY == 0)
                    return false;
                String[] normalSplit = normalAgents.split(",");
                for (int i = 0; i < normalSplit.length && !normalSplit[i].equals(""); i += 2) {
                    int normalAgentX = Integer.parseInt(normalSplit[i]);
                    int normalAgentY = Integer.parseInt(normalSplit[i + 1]);
                    if (neoX == normalAgentX && neoY - 1 == normalAgentY)
                        return false;
                }
                String[] mutatedSplit = mutatedAgents.split(",");
                for (int i = 0; i < mutatedSplit.length && !mutatedSplit[i].equals(""); i += 2) {
                    int mutatedAgentX = Integer.parseInt(mutatedSplit[i]);
                    int mutatedAgentY = Integer.parseInt(mutatedSplit[i + 1]);
                    if (neoX == mutatedAgentX && neoY - 1 == mutatedAgentY)
                        return false;
                }
                String[] hostagesSplit = hostages.split(",");
                for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                    int hostageX = Integer.parseInt(hostagesSplit[i]);
                    int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                    int hostageDamage = Integer.parseInt(hostagesSplit[i + 2]);
                    if (neoX == hostageX && neoY - 1 == hostageY && hostageDamage >= 98)
                        return false;
                }
                break;
            }
            case "right": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                int m = Integer.parseInt(gridSize.split(",")[0]);
                if (neoY == m - 1)
                    return false;
                String[] normalSplit = normalAgents.split(",");
                for (int i = 0; i < normalSplit.length && !normalSplit[i].equals(""); i += 2) {
                    int normalAgentX = Integer.parseInt(normalSplit[i]);
                    int normalAgentY = Integer.parseInt(normalSplit[i + 1]);
                    if (neoX == normalAgentX && neoY + 1 == normalAgentY)
                        return false;
                }
                String[] mutatedSplit = mutatedAgents.split(",");
                for (int i = 0; i < mutatedSplit.length && !mutatedSplit[i].equals(""); i += 2) {
                    int mutatedAgentX = Integer.parseInt(mutatedSplit[i]);
                    int mutatedAgentY = Integer.parseInt(mutatedSplit[i + 1]);
                    if (neoX == mutatedAgentX && neoY + 1 == mutatedAgentY)
                        return false;
                }
                String[] hostagesSplit = hostages.split(",");
                for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                    int hostageX = Integer.parseInt(hostagesSplit[i]);
                    int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                    int hostageDamage = Integer.parseInt(hostagesSplit[i + 2]);
                    if (neoX == hostageX && neoY + 1 == hostageY && hostageDamage >= 98)
                        return false;
                }
                break;
            }
            case "carry": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                int c = Integer.parseInt(neoInfo.split(",")[3]);
                int carryCount = carriedHostages.split(",")[0].equals("") ? 0 : carriedHostages.split(",").length;
                if (carryCount == c)
                    return false;
                boolean locationMatch = false;
                String[] hostagesSplit = hostages.split(",");
                for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                    int hostageX = Integer.parseInt(hostagesSplit[i]);
                    int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                    if (neoX == hostageX && neoY == hostageY)
                        locationMatch = true;
                }
                return locationMatch;
            }
            case "drop": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                int tbX = Integer.parseInt(telephoneBooth.split(",")[0]);
                int tbY = Integer.parseInt(telephoneBooth.split(",")[1]);
                if (carriedHostages.split(",")[0].equals(""))
                    return false;
                if (!(neoX == tbX && neoY == tbY))
                    return false;
                break;
            }
            case "takePill": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                boolean locationMatch = false;
                String[] pillsSplit = pills.split(",");
                for (int i = 0; i < pillsSplit.length && !pillsSplit[i].equals(""); i += 2) {
                    int pillX = Integer.parseInt(pillsSplit[i]);
                    int pillY = Integer.parseInt(pillsSplit[i + 1]);
                    if (neoX == pillX && neoY == pillY)
                        locationMatch = true;
                }
                return locationMatch;
            }
            case "kill": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                String[] hostagesSplit = hostages.split(",");
                for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                    int hostageX = Integer.parseInt(hostagesSplit[i]);
                    int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                    int hostageDamage = Integer.parseInt(hostagesSplit[i + 2]);
                    if (neoX == hostageX && neoY == hostageY && hostageDamage >= 98)
                        return false;
                }
                String[] normalAgentsSplit = normalAgents.split(",");
                for (int i = 0; i < normalAgentsSplit.length && !normalAgentsSplit[i].equals(""); i += 2) {
                    int normalX = Integer.parseInt(normalAgentsSplit[i]);
                    int normalY = Integer.parseInt(normalAgentsSplit[i + 1]);
                    if ((neoX - 1 == normalX && neoY == normalY) || (neoX + 1 == normalX && neoY == normalY)
                            || (neoX == normalX && neoY - 1 == normalY) || (neoX == normalX && neoY + 1 == normalY))
                        return true;
                }
                String[] mutatedAgentsSplit = mutatedAgents.split(",");
                for (int i = 0; i < mutatedAgentsSplit.length && !mutatedAgentsSplit[i].equals(""); i += 2) {
                    int mutatedX = Integer.parseInt(mutatedAgentsSplit[i]);
                    int mutatedY = Integer.parseInt(mutatedAgentsSplit[i + 1]);
                    if ((neoX - 1 == mutatedX && neoY == mutatedY) || (neoX + 1 == mutatedX && neoY == mutatedY)
                            || (neoX == mutatedX && neoY - 1 == mutatedY) || (neoX == mutatedX && neoY + 1 == mutatedY))
                        return true;
                }
                return false;
            }
            case "fly": {
                int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                boolean locationMatch = false;
                String[] padsSplit = pads.split(",");
                for (int i = 0; i < padsSplit.length && !padsSplit[i].equals(""); i += 4) {
                    int startPadX = Integer.parseInt(padsSplit[i]);
                    int startPadY = Integer.parseInt(padsSplit[i + 1]);
                    if (neoX == startPadX && neoY == startPadY)
                        locationMatch = true;
                }
                return locationMatch;
            }
        }

        return true;
    }

    public String transform(String state) {
        String[] splittedState = state.split(";");
        String neoInfo = splittedState[0];
        StringBuilder hostages = new StringBuilder(splittedState[2]);
        String carriedHostages = splittedState[3];
        String pills = splittedState[4];
        String normalAgents = splittedState[5];
        String mutatedAgents = splittedState[6];
        String transformedState = "";

        String[] neoSplit = neoInfo.split(",");
        transformedState += neoSplit[0] + "," + neoSplit[1] + "," + neoSplit[2] + ";";

        String[] hostagesSplit = hostages.toString().split(",");
        hostages = new StringBuilder();
        for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
            hostages.append(hostagesSplit[i]).append(",").append(hostagesSplit[i + 1]);
            if (i != hostagesSplit.length - 3)
                hostages.append(",");
        }
        transformedState += hostages + ";";

        transformedState += carriedHostages.length() + ";";

        transformedState += pills + ";";

        transformedState += normalAgents + ";";

        transformedState += mutatedAgents;

        return transformedState;
    }

    public boolean isRepeated(String newState) {
        String transformedState = matrix.transform(newState);

        if (matrix.generatedStates.containsKey(transformedState))
            return true;
        else {
            matrix.generatedStates.put(transformedState, true);
            return false;
        }
    }

    public String act(String state, String operator) {
        String[] splittedState = state.split(";");
        String neoInfo = splittedState[0];
        String gridSize = splittedState[1];
        StringBuilder hostages = new StringBuilder(splittedState[2]);
        StringBuilder carriedHostages = new StringBuilder(splittedState[3]);
        StringBuilder pills = new StringBuilder(splittedState[4]);
        StringBuilder normalAgents = new StringBuilder(splittedState[5]);
        StringBuilder mutatedAgents = new StringBuilder(splittedState[6]);
        String pads = splittedState[7];
        String telephoneBooth = splittedState[8];
        int deaths = Integer.parseInt(splittedState[9]);
        int kills = Integer.parseInt(splittedState[10]);

        if (Integer.parseInt(neoInfo.split(",")[2]) == 100)
            return "invalid";

        if (operator.equals("takePill")) {
            int neoX = Integer.parseInt(neoInfo.split(",")[0]);
            int neoY = Integer.parseInt(neoInfo.split(",")[1]);
            String[] pillsSplit = pills.toString().split(",");
            pills = new StringBuilder();
            for (int i = 0; i < pillsSplit.length && !pillsSplit[i].equals(""); i += 2) {
                int pillX = Integer.parseInt(pillsSplit[i]);
                int pillY = Integer.parseInt(pillsSplit[i + 1]);
                if (!(neoX == pillX && neoY == pillY)) {
                    if (pills.length() != 0)
                        pills.append(",");
                    pills.append(pillX).append(",").append(pillY);
                }
            }
            String[] neoInfoSplit = neoInfo.split(",");
            int neoDamage = Math.max(Integer.parseInt(neoInfoSplit[2])-20, 0);
            neoInfo = neoInfoSplit[0] + "," + neoInfoSplit[1] + "," + neoDamage + "," + neoInfoSplit[3];

            String[] hostagesSplit = hostages.toString().split(",");
            hostages = new StringBuilder();
            for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                int hostageDamage = Math.max(Integer.parseInt(hostagesSplit[i+2])-20, 0);
                hostages.append(hostagesSplit[i]).append(",").append(hostagesSplit[i + 1]).append(",").append(hostageDamage);
                if (i != hostagesSplit.length - 3)
                    hostages.append(",");
            }
            String[] carriedHostagesSplit = carriedHostages.toString().split(",");
            carriedHostages = new StringBuilder();
            for (int i = 0; i < carriedHostagesSplit.length && !carriedHostagesSplit[i].equals(""); i++) {
                int carriedHostageDamage = Integer.parseInt(carriedHostagesSplit[i]);
                if (carriedHostageDamage < 100)
                    carriedHostageDamage = Math.max(carriedHostageDamage-20, 0);
                carriedHostages.append(carriedHostageDamage);
                if (i != carriedHostagesSplit.length - 1)
                    carriedHostages.append(",");
            }
        }
        else {

            switch (operator) {
                case "up": {
                    String[] neoInfoSplit = neoInfo.split(",");
                    neoInfo = (Integer.parseInt(neoInfoSplit[0]) - 1) + "," + neoInfoSplit[1] + "," + neoInfoSplit[2] + "," + neoInfoSplit[3];
                    break;
                }
                case "down": {
                    String[] neoInfoSplit = neoInfo.split(",");
                    neoInfo = (Integer.parseInt(neoInfoSplit[0]) + 1) + "," + neoInfoSplit[1] + "," + neoInfoSplit[2] + "," + neoInfoSplit[3];
                    break;
                }
                case "left": {
                    String[] neoInfoSplit = neoInfo.split(",");
                    neoInfo = neoInfoSplit[0] + "," + (Integer.parseInt(neoInfoSplit[1]) - 1) + "," + neoInfoSplit[2] + "," + neoInfoSplit[3];
                    break;
                }
                case "right": {
                    String[] neoInfoSplit = neoInfo.split(",");
                    neoInfo = neoInfoSplit[0] + "," + (Integer.parseInt(neoInfoSplit[1]) + 1) + "," + neoInfoSplit[2] + "," + neoInfoSplit[3];
                    break;
                }
                case "carry": {
                    String[] neoInfoSplit = neoInfo.split(",");
                    int neoX = Integer.parseInt(neoInfoSplit[0]);
                    int neoY = Integer.parseInt(neoInfoSplit[1]);
                    String[] hostagesSplit = hostages.toString().split(",");
                    hostages = new StringBuilder();
                    for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                        int hostageX = Integer.parseInt(hostagesSplit[i]);
                        int hostageY = Integer.parseInt(hostagesSplit[i + 1]);
                        int hostageDamage = Integer.parseInt(hostagesSplit[i + 2]);
                        if (neoX == hostageX && neoY == hostageY) {
                            if (carriedHostages.length() != 0)
                                carriedHostages.append(",");
                            carriedHostages.append(hostageDamage);
                        } else {
                            hostages.append(hostagesSplit[i]).append(",").append(hostagesSplit[i + 1]).append(",").append(hostagesSplit[i + 2]);
                            if (i != hostagesSplit.length - 3)
                                hostages.append(",");
                        }
                    }
                    break;
                }
                case "drop":
                    carriedHostages = new StringBuilder();
                    break;
                case "kill": {
                    int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                    int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                    String[] normalAgentsSplit = normalAgents.toString().split(",");
                    normalAgents = new StringBuilder();
                    for (int i = 0; i < normalAgentsSplit.length && !normalAgentsSplit[i].equals(""); i += 2) {
                        int normalX = Integer.parseInt(normalAgentsSplit[i]);
                        int normalY = Integer.parseInt(normalAgentsSplit[i + 1]);
                        if ((neoX - 1 == normalX && neoY == normalY) || (neoX + 1 == normalX && neoY == normalY)
                                || (neoX == normalX && neoY - 1 == normalY) || (neoX == normalX && neoY + 1 == normalY)) {
                            kills++;
                        } else {
                            if (normalAgents.length() != 0)
                                normalAgents.append(",");
                            normalAgents.append(normalAgentsSplit[i]).append(",").append(normalAgentsSplit[i + 1]);
                        }
                    }
                    String[] mutatedAgentsSplit = mutatedAgents.toString().split(",");
                    mutatedAgents = new StringBuilder();
                    for (int i = 0; i < mutatedAgentsSplit.length && !mutatedAgentsSplit[i].equals(""); i += 2) {
                        int mutatedX = Integer.parseInt(mutatedAgentsSplit[i]);
                        int mutatedY = Integer.parseInt(mutatedAgentsSplit[i + 1]);
                        if ((neoX - 1 == mutatedX && neoY == mutatedY) || (neoX + 1 == mutatedX && neoY == mutatedY)
                                || (neoX == mutatedX && neoY - 1 == mutatedY) || (neoX == mutatedX && neoY + 1 == mutatedY)) {
                            kills++;
                        } else {
                            if (mutatedAgents.length() != 0)
                                mutatedAgents.append(",");
                            mutatedAgents.append(mutatedAgentsSplit[i]).append(",").append(mutatedAgentsSplit[i + 1]);
                        }
                    }
                    String[] neoInfoSplit = neoInfo.split(",");
                    int neoDamage = Math.min(Integer.parseInt(neoInfoSplit[2]) + 20, 100);
                    neoInfo = neoInfoSplit[0] + "," + neoInfoSplit[1] + "," + neoDamage + "," + neoInfoSplit[3];
                    break;
                }
                case "fly": {
                    int neoX = Integer.parseInt(neoInfo.split(",")[0]);
                    int neoY = Integer.parseInt(neoInfo.split(",")[1]);
                    String[] padsSplit = pads.split(",");
                    for (int i = 0; i < padsSplit.length && !padsSplit[i].equals(""); i += 4) {
                        int startPadX = Integer.parseInt(padsSplit[i]);
                        int startPadY = Integer.parseInt(padsSplit[i + 1]);
                        int finishPadX = Integer.parseInt(padsSplit[i + 2]);
                        int finishPadY = Integer.parseInt(padsSplit[i + 3]);
                        if (neoX == startPadX && neoY == startPadY) {
                            neoX = finishPadX;
                            neoY = finishPadY;
                            break;
                        }
                    }
                    neoInfo = neoX + "," + neoY + "," + neoInfo.split(",")[2] + "," + neoInfo.split(",")[3];
                    break;
                }
            }

            // Repeated event
            String[] hostagesSplit = hostages.toString().split(",");
            hostages = new StringBuilder();
            for (int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3) {
                String hostageX = hostagesSplit[i];
                String hostageY = hostagesSplit[i+1];
                int hostageDamage = Integer.parseInt(hostagesSplit[i+2]);
                if (hostageDamage < 98) {
                    hostages.append(hostagesSplit[i]).append(",").append(hostagesSplit[i + 1]).append(",").append(hostageDamage + 2);
                    if (i != hostagesSplit.length - 3)
                        hostages.append(",");
                }
                else {
                    if (mutatedAgents.length() != 0)
                        mutatedAgents.append(",");
                    mutatedAgents.append(hostageX).append(",").append(hostageY);
                    deaths++;
                }
            }
            String[] carriedHostagesSplit = carriedHostages.toString().split(",");
            carriedHostages = new StringBuilder();
            for (int i = 0; i < carriedHostagesSplit.length && !carriedHostagesSplit[i].equals(""); i++) {
                int carriedHostageDamage = Integer.parseInt(carriedHostagesSplit[i]);
                if (carriedHostageDamage < 98)
                    carriedHostageDamage += 2;
                else if (carriedHostageDamage < 100) {
                    carriedHostageDamage = 100;
                    deaths++;
                }
                carriedHostages.append(carriedHostageDamage);
                if (i != carriedHostagesSplit.length - 1)
                    carriedHostages.append(",");
            }
        }
        String newState = neoInfo + ";" + gridSize + ";" + hostages + ";" + carriedHostages + ";" + pills + ";" + normalAgents + ";" + mutatedAgents
                + ";" + pads + ";" + telephoneBooth + ";" + deaths + ";" + kills;

        if (isRepeated(newState))
            return "invalid";
        else
            return newState;
    }

    public int heuristicOne(String state)
    {
        String[] splittedState = state.split(";");
        String neoInfo = splittedState[0];
        String hostages = splittedState[2];
        String carriedHostages = splittedState[3];
        String telephoneBooth = splittedState[8];

        int neoX = Integer.parseInt(neoInfo.split(",")[0]);
        int neoY = Integer.parseInt(neoInfo.split(",")[1]);

        int telephoneX = Integer.parseInt(telephoneBooth.split(",")[0]);
        int telephoneY = Integer.parseInt(telephoneBooth.split(",")[1]);

        //Estimate the number of deaths
        String[] hostagesSplit = hostages.split(",");
        String[] carriedHostagesSplit = carriedHostages.split(",");
        int numOfDeaths = 0;

        for(int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3)
        {
            int distance1 = 0;    //Assume distance between Neo and the hostage is zero
            int distance2 = 1;    //Assume distance between the hostage and the telephone booth is 1
            int distance = distance1 + distance2;

            int hostageDamage = Integer.parseInt(hostagesSplit[i+2]);

            if(hostageDamage + (2*distance + 2) >= 100)     // +2 to account for carry
            {
                numOfDeaths++;
            }
        }

        for(int i = 0; i < carriedHostagesSplit.length && !carriedHostagesSplit[i].equals(""); i++)
        {
            int distanceNeoTB = Math.abs(neoX - telephoneX) + Math.abs(neoY - telephoneY);
            if(distanceNeoTB > 0)
            {
                int distance = 1;       //Assume that the telephone booth is neighbour to Neo

                int hostageDamage = Integer.parseInt(carriedHostagesSplit[i]);

                if(hostageDamage + (2*distance) >= 100)
                {
                    numOfDeaths++;
                }
            }
        }

        return (225*numOfDeaths);
    }

    public int heuristicTwo(String state)
    {
        String[] splittedState = state.split(";");
        String neoInfo = splittedState[0];
        String hostages = splittedState[2];
        String carriedHostages = splittedState[3];
        String mutatedAgents = splittedState[6];
        String telephoneBooth = splittedState[8];

        int neoX = Integer.parseInt(neoInfo.split(",")[0]);
        int neoY = Integer.parseInt(neoInfo.split(",")[1]);

        int telephoneX = Integer.parseInt(telephoneBooth.split(",")[0]);
        int telephoneY = Integer.parseInt(telephoneBooth.split(",")[1]);

        //Estimate the number of deaths and kills
        String[] hostagesSplit = hostages.split(",");
        String[] carriedHostagesSplit = carriedHostages.split(",");
        int numOfDeaths = 0;

        for(int i = 0; i < hostagesSplit.length && !hostagesSplit[i].equals(""); i += 3)
        {
            int distance1 = 0;    //Assume distance between Neo and the hostage is zero
            int distance2 = 1;    //Assume distance between the hostage and the telephone booth is 1
            int distance = distance1 + distance2;

            int hostageDamage = Integer.parseInt(hostagesSplit[i+2]);

            if(hostageDamage + (2*distance + 2) >= 100)     // +2 to account for carry
            {
                numOfDeaths++;
            }
        }

        for(int i = 0; i < carriedHostagesSplit.length && !carriedHostagesSplit[i].equals(""); i++)
        {
            int distanceNeoTB = Math.abs(neoX - telephoneX) + Math.abs(neoY - telephoneY);
            if(distanceNeoTB > 0)
            {
                int distance = 1;       //Assume that the telephone booth is neighbour to Neo

                int hostageDamage = Integer.parseInt(carriedHostagesSplit[i]);

                if(hostageDamage + (2*distance) >= 100)
                {
                    numOfDeaths++;
                }
            }
        }

        int kills = mutatedAgents.split(",").length/2;

        return (225*numOfDeaths) + kills;
    }

    public ArrayList<Node> expand(Node node, List<String> operators, String strategy) {
        ArrayList<Node> expandedNodes = new ArrayList<>();
        for (String operator : operators) {
            if (matrix.isApplicable(node.state, operator)) {
                String newState = matrix.act(node.state, operator);
                if (!newState.equals("invalid")) {
                    switch (strategy) {
                        case "BF": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, node.pathCost + 1);
                            expandedNodes.add(newNode);
                            break;
                        }
                        case "DF": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, node.pathCost - 1);
                            expandedNodes.add(newNode);
                            break;
                        }
                        case "UC": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, matrix.pathCost(newState));
                            expandedNodes.add(newNode);
                            break;
                        }
                        case "GR1": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, matrix.heuristicOne(newState));
                            expandedNodes.add(newNode);
                            break;
                        }
                        case "GR2": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, matrix.heuristicTwo(newState));
                            expandedNodes.add(newNode);
                            break;
                        }
                        case "AS1": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, matrix.pathCost(newState) + matrix.heuristicOne(newState));
                            expandedNodes.add(newNode);
                            break;
                        }
                        case "AS2": {
                            Node newNode = matrix.makeNode(newState, node, operator, node.depth + 1, matrix.pathCost(newState) + matrix.heuristicTwo(newState));
                            expandedNodes.add(newNode);
                            break;
                        }
                    }
                }
            }
        }
        return expandedNodes;
    }

    public String visualize(String state, String operator) {
        String[] splittedState = state.split(";");
        String neoInfo = splittedState[0];
        String gridSize = splittedState[1];
        StringBuilder hostages = new StringBuilder(splittedState[2]);
        StringBuilder pills = new StringBuilder(splittedState[4]);
        StringBuilder normalAgents = new StringBuilder(splittedState[5]);
        StringBuilder mutatedAgents = new StringBuilder(splittedState[6]);
        String pads = splittedState[7];
        String telephoneBooth = splittedState[8];

        int cols = Integer.parseInt(gridSize.split(",")[0]);
        int rows = Integer.parseInt(gridSize.split(",")[1]);

        StringBuilder result = new StringBuilder();

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                boolean empty = true;

                if (Integer.parseInt(neoInfo.split(",")[0]) == i && Integer.parseInt(neoInfo.split(",")[1]) == j) {
                    String s = "Neo(" + Integer.parseInt(neoInfo.split(",")[2]) + ")";
                    result.append("Neo(").append(Integer.parseInt(neoInfo.split(",")[2])).append(")");
                    for (int z=0; z<10-s.length(); z++)
                        result.append(" ");
                    result.append("\t");
                    empty = false;
                }

                else if (Integer.parseInt(telephoneBooth.split(",")[0]) == i && Integer.parseInt(telephoneBooth.split(",")[1]) == j) {
                    result.append("TB        \t");
                    empty = false;
                }

                String[] hostagesSplit = hostages.toString().split(",");
                for (int k = 0; empty && k < hostagesSplit.length && !hostagesSplit[k].equals(""); k += 3) {
                    String hostageX = hostagesSplit[k];
                    String hostageY = hostagesSplit[k+1];
                    String hostageDamage = hostagesSplit[k+2];
                    if (Integer.parseInt(hostageX) == i && Integer.parseInt(hostageY) == j) {
                        String s = "H(" + hostageDamage + ")";
                        result.append("H(").append(hostageDamage).append(")");
                        for (int z=0; z<10-s.length(); z++)
                            result.append(" ");
                        result.append("\t");
                        empty = false;
                        break;
                    }
                }

                String[] pillsSplit = pills.toString().split(",");
                for (int k = 0; empty && k < pillsSplit.length && !pillsSplit[k].equals(""); k += 2) {
                    int pillX = Integer.parseInt(pillsSplit[k]);
                    int pillY = Integer.parseInt(pillsSplit[k + 1]);
                    if (pillX == i && pillY == j) {
                        result.append("P         \t");
                        empty = false;
                        break;
                    }
                }

                String[] normalAgentsSplit = normalAgents.toString().split(",");
                for (int k = 0; empty && k < normalAgentsSplit.length && !normalAgentsSplit[k].equals(""); k += 2) {
                    int normalX = Integer.parseInt(normalAgentsSplit[k]);
                    int normalY = Integer.parseInt(normalAgentsSplit[k + 1]);
                    if (normalX == i && normalY == j) {
                        result.append("A         \t");
                        empty = false;
                        break;
                    }
                }

                String[] mutatedAgentsSplit = mutatedAgents.toString().split(",");
                for (int k = 0; empty && k < mutatedAgentsSplit.length && !mutatedAgentsSplit[k].equals(""); k += 2) {
                    int mutatedX = Integer.parseInt(mutatedAgentsSplit[k]);
                    int mutatedY = Integer.parseInt(mutatedAgentsSplit[k + 1]);
                    if (mutatedX == i && mutatedY == j) {
                        result.append("M         \t");
                        empty = false;
                        break;
                    }
                }

                String[] padsSplit = pads.split(",");
                for (int k = 0; empty && k < padsSplit.length && !padsSplit[k].equals(""); k += 4) {
                    int startPadX = Integer.parseInt(padsSplit[k]);
                    int startPadY = Integer.parseInt(padsSplit[k + 1]);
                    String finishPadX = padsSplit[k + 2];
                    String finishPadY = padsSplit[k + 3];
                    if (startPadX == i && startPadY == j) {
                        String s = "Pad(" + finishPadX + "," + finishPadY + ")";
                        result.append("Pad(").append(finishPadX).append(",").append(finishPadY).append(")");
                        for (int z=0; z<10-s.length(); z++)
                            result.append(" ");
                        result.append("\t");
                        empty = false;
                        break;
                    }
                }

                if (empty)
                    result.append("Empty     \t");

                if (j == cols-1)
                    result.append("\n");
            }
        }

        if (operator != null)
            result.append("\nNeo performed ").append(operator).append(".\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        else
            result.append("\nInitial State.\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        return result.toString();
    }

    public String[] goalBackTrack(Node node) {
        String[] output = new String[2];
        StringBuilder plan = new StringBuilder();
        StringBuilder visuals = new StringBuilder();

        while (node.operator != null) {
            plan.insert(0, node.operator);
            visuals.insert(0, visualize(node.state, node.operator));
            visuals.insert(0, "\n");

            if (node.parentNode.operator != null) {
                plan.insert(0, ",");
            }

            node = node.parentNode;
        }

        output[0] = plan.toString();
        visuals.insert(0, visualize(node.state, null));
        output[1] = visuals.toString();
        return output;
    }

    public String depthLimitedSearch(Node root, int depth, boolean visualize) {
        PriorityQueue<Node> nodes = new PriorityQueue<>(new NodeComparator());
        nodes.add(root);
        int maxIterationDepth = 0;
        while (true) {
            if (nodes.isEmpty())
                return Integer.toString(maxIterationDepth);
            Node node = nodes.poll();
            matrix.chosenNodes++;
            maxIterationDepth = Math.max(maxIterationDepth, node.depth);
            if (matrix.goalTest(node.state)) {
                String[] backTrack = matrix.goalBackTrack(node);
                String plan = backTrack[0];
                String[] splittedState = node.state.split(";");
                String deaths = splittedState[9];
                String kills = splittedState[10];

                if (visualize)
                    System.out.println(backTrack[1]);

                return plan + ";" + deaths + ";" + kills + ";" + matrix.chosenNodes++;
            }
            if (node.depth < depth)
                nodes.addAll(matrix.expand(node, matrix.operators, "DF"));
        }
    }

    public static String solve(String grid, String strategy, boolean visualize) {
        matrix = new Matrix();
        Node root = matrix.makeNode(matrix.generateInitialState(grid), null, null, 0, 0);
        if (strategy.equals("ID")) {
            int depth = 0;
            int maxDepthSoFar = -1;
            while (true) {
                matrix.generatedStates = new HashMap<>();
                String solution = matrix.depthLimitedSearch(root, depth, visualize);
                if (solution.split(";").length > 1)
                    return solution;
                if (Integer.parseInt(solution) > maxDepthSoFar)
                    maxDepthSoFar = Integer.parseInt(solution);
                else if (Integer.parseInt(solution) == maxDepthSoFar)
                    return "No Solution";
                depth++;
            }
        }
        else {
            PriorityQueue<Node> nodes = new PriorityQueue<>(new NodeComparator());
            nodes.add(root);
            while (true) {
                if (nodes.isEmpty())
                    return "No Solution";
                Node node = nodes.poll();
                matrix.chosenNodes++;
                if (matrix.goalTest(node.state)) {
                    String[] backTrack = matrix.goalBackTrack(node);
                    String plan = backTrack[0];
                    String[] splittedState = node.state.split(";");
                    String deaths = splittedState[9];
                    String kills = splittedState[10];

                    if (visualize)
                        System.out.println(backTrack[1]);

                    return plan + ";" + deaths + ";" + kills + ";" + matrix.chosenNodes++;
                }
                nodes.addAll(matrix.expand(node, matrix.operators, strategy));
            }
        }
    }


    public static void main(String[] args) {
        String grid = genGrid();
        String solution = solve(grid, "AS2", true);
        System.out.println("\n" + solution);
        System.out.println("No. of steps: " + solution.split(";")[0].split(",").length);
    }
}
