import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Solve {

    private static ArrayList<Integer> getInputs(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            int logicGates = Integer.parseInt(line.split(" ")[0]);
            int finalNode = Integer.parseInt(line.split(" ")[1]);
            br.close();

            ArrayList<Integer> inputs = new ArrayList<>();
            inputs.add(logicGates);
            inputs.add(finalNode);
            return inputs;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private static Dictionary<Integer, String> readInput(String filePath) {
        try {
            Dictionary<Integer, String> logicEquations = new Hashtable<>();
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                String gateType = tokens[0];
                int[] inputNodes = new int[tokens.length - 2];
                int outputNode = Integer.parseInt(tokens[tokens.length - 1]);

                for (int i = 1; i < tokens.length - 1; i++) {
                    inputNodes[i - 1] = Integer.parseInt(tokens[i]);
                }

                String logicString = tseytinTransformation(gateType, inputNodes, outputNode);

                logicEquations.put(outputNode, logicString);
            }
            br.close();

            return logicEquations;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }

    private static String tseytinTransformation(String gateType, int[] inputNodes, int outputNode) {
        String equation = "";
        switch (gateType) {
            case "NOT":
                equation = "( " + inputNodes[0] + " | " + outputNode + " ) & ( ~" + inputNodes[0] + " | ~" + outputNode + " )";
                break;
            case "AND":
                equation = andEquationFormatter(inputNodes, outputNode);
                break;
            case "OR":
                equation = orEquationFormatter(inputNodes, outputNode);
                break;
            default:
                System.out.println("Error: Invalid gate type");
                System.exit(1);
        }

        return equation;
    }

    private static String andEquationFormatter(int[] inputNodes, int outputNode) {
        String equation = "(";

        for (int i = 0; i < inputNodes.length; i++) {
            equation += "~" + inputNodes[i] + " | ";
        }
        equation += outputNode + ") & ";

        for (int i = 0; i < inputNodes.length; i++) {
            equation += "(" + inputNodes[i] + " | ~" + outputNode + ") & ";
        }
        equation = equation.substring(0, equation.length() - 3);

        return equation;
    }

    private static String orEquationFormatter(int[] inputNodes, int outputNode) {
        String equation = "(";

        for (int i = 0; i < inputNodes.length; i++) {
            equation += inputNodes[i] + " | ";
        }
        equation += "~" + outputNode + ") & ";

        for (int i = 0; i < inputNodes.length; i++) {
            equation += "(~" + inputNodes[i] + " | " + outputNode + ") & ";
        }
        equation = equation.substring(0, equation.length() - 3);

        return equation;
    }

    private static String formatEquation(String clause) {
        clause = clause.replaceAll("\\(", "");
        clause = clause.replaceAll("\\)", "");
        clause = clause.replaceAll("\\|", "");
        clause = clause.replaceAll("~", "-");
        clause = clause.replaceAll("  ", " ");

        if (clause.charAt(0) == ' ') {
            clause = clause.substring(1);
        }

        return clause;
    }

    private static int getNrOfClauses(Dictionary<Integer, String> equations) {
        int nrOfClauses = 0;
        Enumeration<Integer> k = equations.keys();
        while (k.hasMoreElements()) {
            int key = k.nextElement();
            String equation = equations.get(key);
            String[] clauses = equation.split(" & ");

            for (String clause : clauses) {
                nrOfClauses++;
            }
        }

        return nrOfClauses;
    }

    private static void writeOutput(String filePath, Dictionary<Integer, String> equations, int nrOfInputs, int finalNode) {
        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            int nrOfClauses = getNrOfClauses(equations) + 1;
            bw.write("p cnf " + nrOfInputs + " " + nrOfClauses + "\n");

            Enumeration<Integer> k = equations.keys();
            while (k.hasMoreElements()) {
                int key = k.nextElement();
                String equation = equations.get(key);
                String[] clauses = equation.split(" & ");

                for (String clause : clauses) {
                    clause = formatEquation(clause);
                    bw.write(clause + " 0\n");
                }
            }

            bw.write(finalNode + " 0\n");
            bw.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java Solve <input_file> <output_file>");
            System.exit(1);
        }

        int nrOfInputs = getInputs(args[0]).get(0);
        int finalNode = getInputs(args[0]).get(1);
        Dictionary<Integer, String> equations = readInput(args[0]);
        if (equations == null) {
            System.out.println("Error: Could not read input file");
            System.exit(1);
        }

        writeOutput(args[1], equations, finalNode, finalNode);

    }
}