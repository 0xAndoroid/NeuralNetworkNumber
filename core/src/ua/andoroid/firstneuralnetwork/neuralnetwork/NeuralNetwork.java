package ua.andoroid.firstneuralnetwork.neuralnetwork;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class NeuralNetwork {
    public float learning_rate;

    public int input_nodes;
    public int hidden1_nodes;
    public int hidden2_nodes;
    public int output_nodes;

    public Matrix weights_ih;
    public Matrix weights_hh;
    public Matrix weights_ho;
    public Matrix bias_h1;
    public Matrix bias_h2;
    public Matrix bias_o;


    private File file;

    public NeuralNetwork(int input_nodes, int hidden1_nodes, int hidden2_nodes, int output_nodes, File file) {
        this.file = file;
        this.input_nodes = input_nodes;
        this.hidden1_nodes = hidden1_nodes;
        this.hidden2_nodes = hidden2_nodes;
        this.output_nodes = output_nodes;

        this.weights_ih = new Matrix(this.hidden1_nodes, this.input_nodes);
        this.weights_hh = new Matrix(this.hidden2_nodes, this.hidden1_nodes);
        this.weights_ho = new Matrix(this.output_nodes, this.hidden2_nodes);

        this.bias_h1 = new Matrix(this.hidden1_nodes, 1);
        this.bias_h2 = new Matrix(this.hidden2_nodes, 1);
        this.bias_o = new Matrix(this.output_nodes, 1);
        this.setLearningRate();
        this.load();
    }

    public float[] predict(float[] input_array) {
        Matrix inputs = Matrix.fromArray(input_array);
        Matrix hidden1 = Matrix.multiply(this.weights_ih, inputs);
        hidden1.add(this.bias_h1);
        hidden1.takeSigmoid();
        Matrix hidden2 = Matrix.multiply(this.weights_hh,hidden1);
        hidden2.add(this.bias_h2);
        hidden2.takeSigmoid();
        Matrix output = Matrix.multiply(this.weights_ho, hidden2);
        output.add(this.bias_o);
        output.takeSigmoid();
        return output.toArray();
    }

    public void setLearningRate() {
        this.learning_rate = 0.1f;
    }

    public void setLearningRate(int learning_rate) {
        this.learning_rate = learning_rate;
    }

    public void train(float[] input_array, float[] target_array) {
        Matrix inputs = Matrix.fromArray(input_array);
        Matrix hidden1 = Matrix.multiply(this.weights_ih, inputs);
        hidden1.add(this.bias_h1);
        hidden1.takeSigmoid();
        Matrix hidden2 = Matrix.multiply(this.weights_hh,hidden1);
        hidden2.add(bias_h2);
        hidden2.takeSigmoid();
        Matrix outputs = Matrix.multiply(this.weights_ho, hidden2);
        outputs.add(this.bias_o);
        outputs.takeSigmoid();
        Matrix targets = Matrix.fromArray(target_array);
        Matrix output_errors = Matrix.subtract(targets, outputs);
        Matrix gradients = outputs.takeDSigmoid();
        gradients.multiply(output_errors);
        gradients.multiply(this.learning_rate);
        Matrix hidden2_T = Matrix.transpose(hidden2);
        Matrix weight_ho_deltas = Matrix.multiply(gradients, hidden2_T);
        this.weights_ho.add(weight_ho_deltas);
        this.bias_o.add(gradients);
        Matrix who_t = Matrix.transpose(this.weights_ho);
        Matrix hidden2_errors = Matrix.multiply( who_t, output_errors);
        Matrix hidden2_gradient = hidden2.takeDSigmoid();
        hidden2_gradient.multiply(hidden2_errors);
        hidden2_gradient.multiply(this.learning_rate);
        Matrix hidden1_T = Matrix.transpose(hidden1);
        Matrix weight_hh_deltas = Matrix.multiply(hidden2_gradient,hidden1_T);
        this.weights_hh.add(weight_hh_deltas);
        this.bias_h2.add(hidden2_gradient);
        Matrix who1_t = Matrix.transpose(this.weights_hh);
        Matrix hidden1_errors = Matrix.multiply(who1_t,hidden2_errors);
        Matrix hidden1_gradient = hidden1.takeDSigmoid();
        hidden1_gradient.multiply(hidden1_errors);
        hidden1_gradient.multiply(this.learning_rate);
        Matrix inputs_T = Matrix.transpose(inputs);
        Matrix weight_ih_deltas = Matrix.multiply(hidden1_gradient, inputs_T);
        this.weights_ih.add(weight_ih_deltas);
        this.bias_h1.add(hidden1_gradient);
    }

    public void save()  throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter writer = new FileWriter(file);
        gson.toJson(this, writer);
        writer.flush();
        writer.close();
    }

    public void load() {
        Gson gson = new Gson();
        try{
            NeuralNetwork dd = gson.fromJson(new FileReader(file), this.getClass());
            this.bias_h1 = dd.bias_h1;
            this.bias_h2 = dd.bias_h2;
            this.bias_o = dd.bias_o;
            this.weights_ih = dd.weights_ih;
            this.weights_ho = dd.weights_ho;
            this.weights_hh = dd.weights_hh;
            this.learning_rate = dd.learning_rate;
        } catch (Exception ex) {
            System.out.println("Cannot find file, randomizing data");
            weights_hh.randomize();
            weights_ho.randomize();
            weights_ih.randomize();
            bias_h2.randomize();
            bias_h1.randomize();
            bias_o.randomize();
        }
    }

}
