package ua.andoroid.firstneuralnetwork.neuralnetwork;

public class Matrix {
    public int rows,cols;
    public float[][] data;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[rows][cols];
    }

    public static Matrix fromArray(float[] arr) {
        Matrix x = new Matrix(arr.length,1);
        for(int i=0;i<x.rows;i++) {
            x.data[i][0] = arr[i];
        }
        return x;
    }

    public static Matrix subtract(Matrix a, Matrix b) {
        if (a.rows != b.rows || a.cols != b.cols) {
            System.out.println("Columns and Rows of A must match Columns and Rows of B.");
            throw new IllegalArgumentException();
        }

        Matrix ret = new Matrix(a.rows,a.cols);
        for(int i=0;i<ret.rows;i++) {
            for(int j = 0;j<ret.cols;j++) {
                ret.data[i][j] = a.data[i][j]-b.data[i][j];
            }
        }
        return ret;
    }

    public float[] toArray() {
        float[] arr = new float[rows*cols];
        int k = 0;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                arr[k] = data[i][j];
                k++;
            }
        }
        return arr;
    }

    public void randomize() {
        for(int i=0;i<this.rows;i++) {
            for(int j=0;j<this.cols;j++) {
                data[i][j] =  (float) Math.random() * 2 - 1;
            }
        }
    }

    public void add(Matrix matrix) {
        for(int i=0;i<this.rows;i++) {
            for(int j = 0;j<this.cols;j++) {
                this.data[i][j] += matrix.data[i][j];
            }
        }
    }

    public void add(float n) {
        for(int i=0;i<this.rows;i++) {
            for(int j = 0;j<this.cols;j++) {
                this.data[i][j] += n;
            }
        }
    }

    public void takeSigmoid() {
        for(int i=0;i<this.rows;i++) {
            for(int j=0;j<this.cols;j++) {
                this.data[i][j] = sigmoid(this.data[i][j]);
            }
        }
    }

    public Matrix takeDSigmoid() {
        Matrix matrix = new Matrix(this.rows, this.cols);
        for(int i=0;i<this.rows;i++) {
            for(int j=0;j<this.cols;j++) {
                matrix.data[i][j] = dsigmoid(this.data[i][j]);
            }
        }
        return matrix;
    }

    public static Matrix transpose(Matrix matrix) {
        Matrix ret = new Matrix(matrix.cols, matrix.rows);
        for(int i=0;i<ret.rows;i++) {
            for(int j=0;j<ret.cols;j++) {
                ret.data[i][j] = matrix.data[j][i];
            }
        }
        return ret;
    }



    public static Matrix multiply(Matrix a, Matrix b) {

        if (a.cols != b.rows) {
            throw new IllegalArgumentException("A:Rows: " + a.cols + " did not match B:Columns " + b.rows + ".");
        }
        Matrix c = new Matrix(a.rows,b.cols);

        for (int i = 0; i < a.rows; i++) { // aRow
            for (int j = 0; j < b.cols; j++) { // bColumn
                for (int k = 0; k < a.cols; k++) { // aColumn
                    c.data[i][j] += a.data[i][k] * b.data[k][j];
                }
            }
        }

        int h = 0;

        return c;
    }

    public void multiply(Matrix n) {
        if (this.rows != n.rows || this.cols != n.cols) {
            throw new IllegalArgumentException("Columns and Rows of A must match Columns and Rows of B.");
        }

        for(int i=0;i<rows;i++) {
            for(int j=0;j<cols;j++) {
                data[i][j] *= n.data[i][j];
            }
        }
    }

    public void multiply(float n) {
        for (int i=0;i<this.rows;i++) {
            for(int j=0;j<this.cols;j++) {
                this.data[i][j] *= n;
            }
        }
    }

    public static float sigmoid(float x) {
        return (float)(1/(1+Math.exp(-x)));
    }

    public static float dsigmoid(float sig) {
        return sig*(1-sig);
    }
}
