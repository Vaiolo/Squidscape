package com.mygdx.game;
import java.io.FilenameFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class BaseActor extends Actor {
    private Animation<TextureRegion> animation; // the Animation class is generic so we have to specify which type we want to use
    private float elapsedTime;
    private boolean animationPaused;
    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float maxSpeed;
    private float deceleration;
    private static Rectangle worldBounds;

    public BaseActor(float x, float y, Stage s)
    {
        super();
        //Initial position of the character
        setPosition(x,y);
        //Add actor to the stage
        s.addActor(this);

        //Initialize physics data for movement
        velocityVec = new Vector2(0,0);
        accelerationVec = new Vector2(0,0);
        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;
    }

    public void setAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
        TextureRegion region = animation.getKeyFrame(0);
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        setSize(width, height);
        setOrigin(width/2, height/2);
    }

    public void setAnimationPaused(boolean pause){
        animationPaused = pause;
    }

    @Override
    // automatically updates the elapsed time when act is called
    public void act(float dt){
        super.act(dt);
        
        if(!animationPaused)
            elapsedTime += dt; // incremented by the amount of time passed since the last loop
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        if ( animation != null && isVisible() )
            batch.draw( animation.getKeyFrame(elapsedTime),
            getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation() );
       }

       // method to load the animation as individual files of frames
    public Animation<TextureRegion> loadAnimationFromFiles(String[] fileNames,
        float frameDuration, boolean loop)
    {
        int fileCount = fileNames.length;
        Array<TextureRegion> textureArray = new Array<TextureRegion>();
        for (int n = 0; n < fileCount; n++)
            {
            String fileName = fileNames[n];
            Texture texture = new Texture( Gdx.files.internal(fileName) );
            texture.setFilter( TextureFilter.Linear, TextureFilter.Linear );
            textureArray.add( new TextureRegion( texture ) );
            }
        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);
        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        if (animation == null)
            setAnimation(anim);
        return anim;
    }
    // using a method to load the animation from a spritesheet
    public void loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop){
        Texture texture = new Texture(Gdx.files.internal(fileName), true);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        int frameHeight = texture.getHeight() / rows;
        int frameWidth = texture.getWidth() / cols;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight); // splits the spritesheet into the individual frames
        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                textureArray.add(temp[r][c]); // adds to the array each frame of the spritesheet
        
    }

    // method to load a single image that technically counts as a one-frame-animation
    public Animation<TextureRegion> loadTexture(String fileName){
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    public boolean isAnimationFinished()
    {
        return animation.isAnimationFinished(elapsedTime);
    }

    public static void setWorldBounds(float width, float height)
    {
        worldBounds = new Rectangle( 0,0, width, height );
    }

    public static void setWorldBounds(BaseActor ba)
    {
        setWorldBounds( ba.getWidth(), ba.getHeight() );
    }

    public void boundToWorld() {
        //check left edge
        if (getX() < 0)
            setX(0);
        //check right edge
        if (getX() + getWidth() > worldBounds.width)
            setX(worldBounds.width - getWidth());
        //check bottom edge
        if (getY() < 0)
            setY(0);
        //check top edge
        if (getY() + getHeight() > worldBounds.height)
            setY (worldBounds.height - getHeight());
    }

    //Methods for Speed/Movement
    public void setSpeed(float speed)
    {
        //if length is zero, motion angle is zero degrees
        if(velocityVec.len() == 0)
        {
            velocityVec.set(speed, 0);
        } else {
            velocityVec.setLength(speed);
        }
    }

    public float getSpeed()
    {
        return velocityVec.len();
    }

    public void setMotionAngle(float angle)
    {
        velocityVec.setAngleDeg(angle);
    }

    public float getMotionAngle()
    {
        return velocityVec.angleDeg();
    }

    public boolean isMoving()
    {
        return (getSpeed() > 0);
    }

    public void setAcceleration(float acc)
    {
        acceleration = acc;
    }
    public void accelerateAtAngle(float angle)
    {
        accelerationVec.add( new Vector2(acceleration, 0).setAngleDeg(angle) );
    }
    public void accelerateForward()
    {
        accelerateAtAngle( getRotation() );
    }

    public void setMaxSpeed(float ms)
    {
        maxSpeed = ms;
    }
    public void setDeceleration(float dec)
    {
        deceleration = dec;
    }
    public void applyPhysics(float dt)
    {
        // apply acceleration
        velocityVec.add( accelerationVec.x * dt, accelerationVec.y * dt );

        float speed = getSpeed();

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0)
            speed -= deceleration * dt;

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);

        // update velocity
        setSpeed(speed);

        // apply velocity
        moveBy( velocityVec.x * dt, velocityVec.y * dt );

        // reset acceleration
        accelerationVec.set(0,0);
    }



}
