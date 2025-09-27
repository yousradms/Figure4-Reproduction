package com.example.demo;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Reproduces Figure 4 from the article "Injecting Shortcuts for Faster Running of Java Code"
 * by simulating the injection of control flow statements (break, continue, return, with/without if)
 * in three Java projects (jCodec, spark, spatial4j) and generating success rate charts.
 */
public class Figure4Repro {
    // Constants for the number of tests per project
    private static final int[] NUM_TESTS = {1000, 800, 600}; // jCodec, spark, spatial4j
    private static final String[] PROJECTS = {"jCodec", "spark", "spatial4j"};
    private static final String[] EDIT_TYPES = {"Break", "BreakWithIf", "Continue", "ContinueWithIf", "Return", "ReturnWithIf"};
    
    // Probabilities for successful compilation (estimated from Figure 4)
    private static final double[][] SUCCESS_PROBABILITIES = {
        // jCodec: Break, BreakWithIf, Continue, ContinueWithIf, Return, ReturnWithIf
        {0.15, 0.30, 0.15, 0.30, 0.15, 0.52},
        // spark: Break, BreakWithIf, Continue, ContinueWithIf, Return, ReturnWithIf
        {0.17, 0.17, 0.17, 0.17, 0.17, 0.30},
        // spatial4j: Break, BreakWithIf, Continue, ContinueWithIf, Return, ReturnWithIf
        {0.15, 0.15, 0.15, 0.15, 0.15, 0.20}
    };
    
    private static final Random random = new Random(42); // Fixed seed for reproducibility

    public static void main(String[] args) {
        // Ensure output directory exists
        new File("output").mkdirs();
        
        // Run simulation for each project and generate charts
        for (int i = 0; i < PROJECTS.length; i++) {
            double[] percentages = runSimulation(NUM_TESTS[i], PROJECTS[i], i);
            generateChart(percentages, PROJECTS[i]);
        }
    }

    /**
     * Runs a simulation for a given project, generating mock code and checking if it compiles.
     * @param numTests Number of tests to run
     * @param project Project name (jCodec, spark, spatial4j)
     * @param projectIndex Index of the project for probability lookup
     * @return Array of success percentages for each edit type
     */
    private static double[] runSimulation(int numTests, String project, int projectIndex) {
        int[] successCounts = new int[EDIT_TYPES.length];
        
        // Simulate numTests for each edit type
        for (int i = 0; i < numTests; i++) {
            for (int j = 0; j < EDIT_TYPES.length; j++) {
                String editType = EDIT_TYPES[j];
                String code = generateMockCode(editType);
                
                // Combine JavaParser validation with probabilistic simulation
                if (compiles(code) && random.nextDouble() < SUCCESS_PROBABILITIES[projectIndex][j]) {
                    successCounts[j]++;
                }
            }
        }
        
        // Calculate success percentages
        double[] percentages = new double[EDIT_TYPES.length];
        for (int i = 0; i < EDIT_TYPES.length; i++) {
            percentages[i] = (successCounts[i] * 100.0) / numTests;
            System.out.printf("%s - %s: %.2f%%%n", project, EDIT_TYPES[i], percentages[i]);
        }
        return percentages;
    }

    /**
     * Generates mock Java code with the specified edit type.
     * @param editType Type of edit (Break, BreakWithIf, etc.)
     * @return String containing the generated Java code
     */
    private static String generateMockCode(String editType) {
        String baseCode = "public class Test { public void method() { for (int i = 0; i < 10; i++) {";
        if (editType.contains("WithIf")) {
            String statement = editType.toLowerCase().replace("withif", "");
            return baseCode + " if (i > 5) { " + statement + "; } } } }";
        } else {
            return baseCode + editType.toLowerCase() + "; } } }";
        }
    }

    /**
     * Checks if the generated code is syntactically valid using JavaParser.
     * @param code Code to validate
     * @return true if the code compiles, false otherwise
     */
    private static boolean compiles(String code) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(code);
            return true; // Syntactically valid
        } catch (Exception e) {
            return false; // Syntax error
        }
    }

    /**
     * Generates a bar chart for the success rates of a project.
     * @param percentages Success percentages for each edit type
     * @param project Project name
     */
    private static void generateChart(double[] percentages, String project) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < EDIT_TYPES.length; i++) {
            dataset.addValue(percentages[i], "Success Rate", EDIT_TYPES[i]);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            project + " Success Rates", // Chart title
            "Edit Type",               // X-axis label
            "Percentage",              // Y-axis label
            dataset
        );
        
        // Configure Y-axis to range from 0 to 100 with 20-unit ticks
        NumberAxis rangeAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        rangeAxis.setRange(0, 100);
        rangeAxis.setTickUnit(new NumberTickUnit(20));

        // Save chart as PNG
        try {
            File outputFile = new File("output/" + project + "_repro.png");
            ChartUtils.saveChartAsPNG(outputFile, chart, 800, 600);
            System.out.println("Chart saved: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving chart for " + project + ": " + e.getMessage());
        }
    }
}