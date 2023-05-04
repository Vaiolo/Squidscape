package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class BaseActor extends Actor {
    public BaseActor(float x, float y, Stage s)
    {
        super();
        //Initial position of the character
        setPosition(x,y);
        //Add actor to the stage
        s.addActor(this);
    }
}
