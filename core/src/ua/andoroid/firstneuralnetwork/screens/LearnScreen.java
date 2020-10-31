package ua.andoroid.firstneuralnetwork.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import javafx.util.Pair;
import ua.andoroid.firstneuralnetwork.Statements;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class LearnScreen implements Screen {
    private Statements st;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private float[][] plane;
    private int width;
    private int height;

    private List<Pair<float[][],Integer>> learnedTypes;

    public LearnScreen(Statements st) {
        this.st = st;

        plane = new float[Statements.width][Statements.height];
    }

    public LearnScreen(Statements st, int[][] plane) {
        this.st = st;
        this.plane = new float[Statements.width][Statements.height];

        for(int i=0;i<plane.length;i++) {
            for(int j=0;j<plane[i].length;j++) {
                this.plane[i][j] = plane[i][j];
            }
        }

    }

    @Override
    public void show() {
        stage = new Stage();
        shapeRenderer = new ShapeRenderer();
        learnedTypes = new ArrayList<>();

        Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));
        final TextField textField = new TextField("", skin);
        textField.setBounds(10,90,100, 30);
        textField.setVisible(true);

        final TextButton clearButton = new TextButton("Clear",skin);
        clearButton.setBounds(166,90,100, 30);
        clearButton.setVisible(true);
        clearButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                for(int i=0;i<plane.length;i++) for(int j=0;j<plane[i].length;j++) plane[i][j] = 0;
            }
        });

        final TextButton submitButton = new TextButton("Done",skin);
        submitButton.setBounds(10,50,100, 30);
        submitButton.setVisible(true);
        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                if(textField.getText().isEmpty()) return;
                float[][] s = new float[plane.length][plane[0].length];
                for(int i=0;i<s.length;i++) for(int j=0;j<s[i].length;j++) s[i][j] = plane[i][j];
                learnedTypes.add(new Pair<>(s,Integer.parseInt(textField.getText())));
                for(int i=0;i<plane.length;i++) for(int j=0;j<plane[i].length;j++) plane[i][j] = 0;
                textField.setText("");
            }
        });

        final TextButton saveButton = new TextButton("Save all",skin);
        saveButton.setBounds(166,50,100, 30);
        saveButton.setVisible(true);
        saveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                Preferences testSamples = Gdx.app.getPreferences(Statements.TRAINING_DATA_PREFERENCES_NAME);
                int current_amount = testSamples.getInteger("current_amount",0);
                for(Pair<float[][],Integer> lt : learnedTypes) {
                    for(int i=0;i<lt.getKey().length;i++) {
                        for(int j=0;j<lt.getKey()[i].length;j++) {
                            testSamples.putFloat("sample:"+current_amount+" x:"+i+" y:"+j,lt.getKey()[i][j]);
                        }
                    }
                    testSamples.putInteger("sample:"+current_amount+" answer",lt.getValue());
                    current_amount++;
                }
                testSamples.putInteger("current_amount",current_amount);
                testSamples.flush();
            }
        });

        final TextButton teachButton = new TextButton("Teach",skin);
        teachButton.setBounds(10,10,100, 30);
        teachButton.setVisible(true);
        teachButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                List<Pair<float[][],Integer>> recognizedTypes = new ArrayList<>();
                Preferences testSamples = Gdx.app.getPreferences(Statements.TRAINING_DATA_PREFERENCES_NAME);
                int current_amount = testSamples.getInteger("current_amount",0);
                for(int k = 0;k<current_amount;k++) {
                    float[][] n = new float[32][32];
                    for(int i=0;i<n.length;i++) {
                        for(int j=0;j<n[i].length;j++) {
                            n[i][j] = testSamples.getFloat("sample:"+k+" x:"+i+" y:"+j);
                        }
                    }
                    Integer answer = testSamples.getInteger("sample:"+k+" answer");
                    recognizedTypes.add(new Pair<>(n,answer));
                }

                if(recognizedTypes.size() >= 1) {
                    Collections.shuffle(recognizedTypes);
                    for (Pair<float[][], Integer> recognizedType : recognizedTypes)
                        st.nn.train(getInputArray(recognizedType.getKey()), getTargetArray(recognizedType.getValue()));
                    try {
                        st.nn.save();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        final TextButton goToAskScreenButton = new TextButton("Ask",skin);
        goToAskScreenButton.setBounds(166,10,100, 30);
        goToAskScreenButton.setVisible(true);
        goToAskScreenButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                st.main.setScreen(new AskScreen(st));
            }
        });

        width = 8;
        height = 8;

        stage.addActor(submitButton);
        stage.addActor(clearButton);
        stage.addActor(textField);
        stage.addActor(saveButton);
        stage.addActor(teachButton);
        stage.addActor(goToAskScreenButton);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClearColor(1,1, 1,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int x0=10,y0=140;
        for(int i=0;i<plane.length;i++) {
            for(int j=0;j<plane[i].length;j++) {
                if(plane[i][j] != 0) {
                    shapeRenderer.setColor(0,0,0,plane[i][j]);
                    shapeRenderer.rect(x0+i*width,y0+j*height,width,height);
                } else  {
                    shapeRenderer.setColor(1,1,1,1);
                    shapeRenderer.rect(x0+i*width,y0+j*height,width,height);
                }
            }
        }
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void update(float dt) {
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int x = (Gdx.input.getX()-10)/width;
            int y = (Gdx.graphics.getHeight()-Gdx.input.getY()-140)/height;
            if(x >= 0 && x < Statements.width && y>=0 && y< Statements.height && Gdx.input.getX()-10 >=0 && Gdx.graphics.getHeight()-Gdx.input.getY()-50 >=0){
                if(x >= 1 && x <= Statements.width-2 && y >= 1 && y <= Statements.height-2) {
                    plane[x][y] = 1;
                    plane[x+1][y] = 1;
                    plane[x][y+1] = 1;
                    plane[x-1][y] = 1;
                    plane[x][y-1] = 1;
                }
            }

        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private float[] getTargetArray(int target) {
        float[] ret = new float[10];
        for(int i=0;i<ret.length;i++) {
            if(i==target) ret[i] = 1;
            else ret[i] = 0;
        }
        return ret;
    }

    private float[] getInputArray(float[][] n) {
        float[] ret = new float[n.length*n[0].length];
        int k = 0;
        for (int i=0;i<n.length;i++) {
            for(int j=0;j<n[i].length;j++) {
                ret[k] = n[i][j];
                k++;
            }
        }
        return ret;
    }
}
