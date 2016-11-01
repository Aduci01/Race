package com.race2135.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Adam on 2016. 10. 31..
 */
public class Tire {

    Body body;

    public enum Direction{up, down, right, left, stop};
    Direction direction = Direction.stop;

    float maxForwardSpeed = 300 / Main.PPM;
    float maxBackwardSpeed = -20 / Main.PPM;
    float maxDriveForce;
    float maxLateralImpulse;


    public Tire(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(2f / Main.PPM, 3f / Main.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
       /* fixtureDef.filter.categoryBits = Constants.TIRE;
        fixtureDef.filter.maskBits  = Constants.GROUND;*/
        Fixture fixture = body.createFixture(fixtureDef);

        body.setUserData(this);
    }

    public void setValues(float a, float b, float c, float d){
        maxForwardSpeed = a;
        maxBackwardSpeed = b;
        maxDriveForce = c;
        maxLateralImpulse = d;

    }

    public void updateDrive() {
        float desiredSpeed = 0;
        if(direction == Direction.up){
            desiredSpeed = maxForwardSpeed;
        } else if(direction == Direction.down){
            desiredSpeed = maxBackwardSpeed;
        } else {
            return;
        }

        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        float currentSpeed = getForwardVelocity().dot(currentForwardNormal);

        float force = 0;

        if (desiredSpeed > currentSpeed) {
            force = maxDriveForce;
        } else if (desiredSpeed < currentSpeed) {
            force = (-maxDriveForce);
        } else {
            return;
        }
        body.applyForce(
                multiply(force, currentForwardNormal),
                body.getWorldCenter(), true);
    }

     Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1,0));
        return currentRightNormal.scl(currentRightNormal.dot(body.getLinearVelocity()));
    }

    Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        return multiply(
                currentForwardNormal.dot(body.getLinearVelocity()),
                currentForwardNormal);
    }

    public void updateFriction() {
        Vector2 impulse = getLateralVelocity().scl(-body.getMass());

        if (impulse.len() > maxLateralImpulse) {
            impulse = multiply(maxLateralImpulse / impulse.len(), impulse);
        }
        body.applyLinearImpulse(impulse,
                body.getWorldCenter(), true);
        body.applyAngularImpulse(0.1f * body.getInertia()
                * -body.getAngularVelocity(), true);

        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed =currentForwardNormal.nor().len();
        float dragForceMagnitude = -2 * currentForwardSpeed;
        body.applyForce(multiply( dragForceMagnitude,
                currentForwardNormal), body.getWorldCenter(), true);
    }

   public void updateTurn() {
       float desiredTorque = 0;

       switch(direction){
           case left:
               desiredTorque = 15;
               break;
           case right:
               desiredTorque = -15;
               break;
           default:
               return;
       }
       body.applyTorque(desiredTorque, true);
    }



    public static Vector2 multiply(float s, Vector2 a) {
        return new Vector2(s * a.x, s * a.y);
    }

}
