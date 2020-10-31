package ua.andoroid.firstneuralnetwork.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ua.andoroid.firstneuralnetwork.Statements;

public class AskScreen implements Screen {
    private Statements st;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private int[][] plane;
    private float width;
    private float height;
    private int askingTimes;

    public AskScreen(Statements st) {
        this.st = st;
    }

    @Override
        public void show() {
        stage = new Stage();
        shapeRenderer = new ShapeRenderer();
        plane = new int[Statements.height][Statements.width];
        askingTimes = 0;

        Skin skin = new Skin(Gdx.files.internal("skin/skin.json"));
        final Label textField = new Label("Input smth", skin);
        textField.setBounds(10,90,100, 30);
        textField.setVisible(true);

        final TextButton clearButton = new TextButton("Clear",skin);
        clearButton.setBounds(166,90,100, 30);
        clearButton.setVisible(true);
        clearButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                for(int i=0;i<plane.length;i++) for(int j=0;j<plane[i].length;j++) plane[i][j] = 0;
                textField.setText("Input smth");
            }
        });

        final TextButton askButton = new TextButton("Ask",skin);
        askButton.setBounds(10,50,100, 30);
        askButton.setVisible(true);
        askButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                askingTimes++;
                float[] give = new float[plane.length*plane[0].length];
                int count = 0;
                for(int i=0;i<plane.length;i++) for(int j=0;j<plane[i].length;j++) {
                    give[count] = plane[i][j];
                    count++;
                }

                float[] res = st.nn.predict(give);
                int maxIndex = 0;
                float max = 0;
                for(int i=0;i<10;i++) {
                    if(res[i] > max) {
                        maxIndex = i;
                        max = res[i];
                    }
                }
                for(int i=0;i<10;i++) {
                    if(i==maxIndex) Gdx.app.log("Asking #"+askingTimes,i+" -  "+res[i] + " - Answer");
                    else Gdx.app.log("Asking #"+askingTimes,i+" -  "+res[i]);
                }
                textField.setText("It is "+maxIndex);
            }
        });

        final TextButton exitButton = new TextButton("Exit",skin);
        exitButton.setBounds(166,10,100, 30);
        exitButton.setVisible(true);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                Gdx.app.exit();
            }
        });

        final TextButton goToLearningScreenButton = new TextButton("Learn",skin);
        goToLearningScreenButton.setBounds(10,10,100, 30);
        goToLearningScreenButton.setVisible(true);
        goToLearningScreenButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                st.main.setScreen(new LearnScreen(st));
            }
        });

        final TextButton incorrectButton = new TextButton("Incorrect",skin);
        incorrectButton.setBounds(166,50,100, 30);
        incorrectButton.setVisible(true);
        incorrectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent e, float x, float y){
                st.main.setScreen(new LearnScreen(st,plane));
            }
        });

        width = 256f/Statements.width ;
        height = 256f/Statements.height;

        stage.addActor(askButton);
        stage.addActor(clearButton);
        stage.addActor(textField);
        stage.addActor(exitButton);
        stage.addActor(goToLearningScreenButton);
        stage.addActor(incorrectButton);


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1,1, 1,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int x0=10,y0=140;
        for(int i=0;i<plane.length;i++) {
            for(int j=0;j<plane[i].length;j++) {
                if(plane[i][j] == 1) {
                    shapeRenderer.setColor(0,0,0,1);
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
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    private void update(float dt) {
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            int x = (int)((Gdx.input.getX()-10)/width);
            int y = (int)((Gdx.graphics.getHeight()-Gdx.input.getY()-140)/height);
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
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
