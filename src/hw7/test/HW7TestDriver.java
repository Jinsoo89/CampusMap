package hw7.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import hw5.Edge;
import hw5.Graph;
import hw7.MarvelPaths2;


/**
 * This class implements a testing driver which reads test scripts
 * from files for your graph ADT and improved MarvelPaths application
 * using Dijkstra's algorithm.
 **/
public class HW7TestDriver {


    public static void main(String args[]) {
        try {
            if (args.length > 1) {
                printUsage();
                return;
            }

            HW7TestDriver td;

            if (args.length == 0) {
                td = new HW7TestDriver(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
            } else {

                String fileName = args[0];
                File tests = new File(fileName);

                if (tests.exists() || tests.canRead()) {
                    td = new HW7TestDriver(new FileReader(tests), new OutputStreamWriter(System.out));
                } else {
                    System.err.println("Cannot read from " + tests.toString());
                    printUsage();
                    return;
                }
            }

            td.runTests();

        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    private static void printUsage() {
        System.err.println("Usage:");
        System.err.println("to read from a file: java hw5.test.HW5TestDriver <name of input script>");
        System.err.println("to read from standard in: java hw5.test.HW5TestDriver");
    }

    /** String -> Graph: maps the names of graphs to the actual graph **/
    private final Map<String, Graph<String, Double>> graphs = 
            new HashMap<String, Graph<String, Double>>();
    private final PrintWriter output;
    private final BufferedReader input;

    /**
     * @requires r != null && w != null
     *
     * @effects Creates a new HW5TestDriver which reads command from <tt>r</tt> and
     *          writes results to <tt>w</tt>.
     **/
    public HW7TestDriver(Reader r, Writer w) {
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    /**
     * @effects Executes the commands read from the input and writes results to the
     *          output
     * @throws IOException
     *             if the input or output sources encounter an IOException
     **/
    public void runTests() throws IOException {
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            if ((inputLine.trim().length() == 0) || (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if (st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<String>();
                    while (st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            if (command.equals("CreateGraph")) {
                createGraph(arguments);
            } else if (command.equals("AddNode")) {
                addNode(arguments);
            } else if (command.equals("AddEdge")) {
                addEdge(arguments);
            } else if (command.equals("ListNodes")) {
                listNodes(arguments);
            } else if (command.equals("ListChildren")) {
                listChildren(arguments);
            } else if (command.equals("LoadGraph")) {
                loadGraph(arguments);
            } else if (command.equals("FindPath")) {
                findPath(arguments);
            } else {
                output.println("Unrecognized command: " + command);
            }
        } catch (Exception e) {
            output.println("Exception: " + e.toString());
        }
    }

    private void createGraph(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {

        graphs.put(graphName, new Graph<String, Double>());
        output.println("created graph " + graphName);
    }

    private void addNode(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to addNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {

        Graph<String, Double> graph = graphs.get(graphName);
        graph.addNode(nodeName);
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if (arguments.size() != 4) {
            throw new CommandException("Bad arguments to addEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        Double edgeLabel = Double.parseDouble(arguments.get(3));

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName, Double edgeLabel) {

        Graph<String, Double> graph = graphs.get(graphName);
        graph.addEdge(parentName, childName, edgeLabel);
        output.println("added edge " + String.format("%.3f", edgeLabel) + " from " + parentName + " to " + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to listNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {

        Graph<String, Double> graph = graphs.get(graphName);
        String str = graphName + " contains:";

        Set<String> nodeList = new TreeSet<String>(graph.getNodes());
        for (String node : nodeList) {
            str += " " + node;
        }

        output.println(str);
    }

    private void listChildren(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to listChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {

        Graph<String, Double> graph = graphs.get(graphName);
        String str = "the children of " + parentName + " in " + graphName + " are:";

        Set<Edge<String, Double>> edges = new TreeSet<Edge<String, Double>>(
                new Comparator<Edge<String, Double>>() {
            public int compare(Edge<String, Double> edge1, Edge<String, Double> edge2) {
                if (!(edge1.getChild().equals(edge2.getChild()))) {
                    return edge1.getChild().compareTo(edge2.getChild());
                }
                
                if (!edge1.getLabel().equals(edge2.getLabel())) {
                    return edge1.getLabel().compareTo(edge2.getLabel());
                }
                
                return 0;
            }
        });
        
        for (Edge<String, Double> edge : graph.getEdges(parentName)) {
            if (edge.getChild().equals(parentName)) {
                continue;
            }
            edges.add(edge);
        }
        
        for (Edge<String, Double> edge : edges) {
            str += " " + edge.getChild() + String.format("(%.3f)", edge.getLabel());
        }

        output.println(str);
    }

    private void loadGraph(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to findPath: " + arguments);
        }
        String graphName = arguments.get(0);
        String fileName = arguments.get(1);

        loadGraph(graphName, fileName);
    }

    private void loadGraph(String graphName, String fileName) {
        fileName = "src/hw7/data/" + fileName;
        Graph<String, Double> graph = MarvelPaths2.buildGraph(fileName);
        graphs.put(graphName, graph);

        output.println("loaded graph " + graphName);
    }

    private void findPath(List<String> arguments) {
        if (arguments.size() != 3) {
            throw new CommandException("Bad arguments to findPath: " + arguments);
        }
        String graphName = arguments.get(0);
        // unknown error in reading string. 
        // read space as underline so, convert it back
        String parentName = arguments.get(1).replace("_", " ");
        String childName = arguments.get(2).replace("_", " ");

        findPath(graphName, parentName, childName);
    }

    private void findPath(String graphName, String parentName, String childName) {
        Graph<String, Double> graph = graphs.get(graphName);

        if ((!graph.containsNode(parentName)) && (!graph.containsNode(childName))) {
            output.println("unknown character " + parentName);
            output.println("unknown character " + childName);
        } else if (!graph.containsNode(parentName)) {
            output.println("unknown character " + parentName);
        } else if (!graph.containsNode(childName)) {
            output.println("unknown character " + childName);
        } else {
            List<Edge<String, Double>> paths = MarvelPaths2.findMinCostPath(
                    graph, parentName, childName);

            // sublisting to exclude parentNode to parentNode itself
            paths = paths.subList(1, paths.size());
            String str = "path from " + parentName + " to " + childName + ":";
            String curFrom = parentName;
            Double curCost = 0.0;
            
            if (paths == null) {
                str += "\n" + "no path found";
            } else {
                for (Edge<String, Double> edge : paths) {
                    str += "\n" + curFrom + " to " + edge.getChild() +
                            " with weight " + String.format("%.3f", edge.getLabel() - curCost);
                    curFrom = edge.getChild();
                    curCost = edge.getLabel();
                }
                str += "\n" + "total cost: " + String.format("%.3f", curCost);
            }
            output.println(str);
        }
    }

    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}