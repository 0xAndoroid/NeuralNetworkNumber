package ua.andoroid.firstneuralnetwork;

import ua.andoroid.firstneuralnetwork.neuralnetwork.NeuralNetwork;

import java.io.File;

public class Statements {
    public static File AI_FILE = new File("ai.json");
    public static String TRAINING_DATA_PREFERENCES_NAME ="ua.andoroid.number.testdata";

    public static int width = 32;
    public static int height = 32;


    public Main main;
    public NeuralNetwork nn;

    public Statements(Main main) {
        this.main = main;
        this.nn = new NeuralNetwork(width*height,64,64,10, AI_FILE);
    }

}
