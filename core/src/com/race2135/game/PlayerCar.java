package com.race2135.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Adam on 2016. 11. 01..
 */
public class PlayerCar {

    Body body;
    Array<Tire> tires = new Array<Tire>();
    RevoluteJoint leftJoint, rightJoint;

    String input = "";

    public PlayerCar(World world) {
        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(new Vector2(3, 3));

        body = world.createBody(bodyDef);
        body.setAngularDamping(3);

        Vector2[] vertices = new Vector2[8];

        vertices[0] = new Vector2(1.5f / Main.PPM * 4, 0);
        vertices[1] = new Vector2(3/ Main.PPM * 4, 2.5f/ Main.PPM * 4);
        vertices[2] = new Vector2(2.8f/ Main.PPM * 4, 5.5f/ Main.PPM * 4);
        vertices[3] = new Vector2(1/ Main.PPM * 4, 10/ Main.PPM * 4);
        vertices[4] = new Vector2(-1/ Main.PPM * 4, 10/ Main.PPM * 4);
        vertices[5] = new Vector2(-2.8f/ Main.PPM * 4, 5.5f/ Main.PPM * 4);
        vertices[6] = new Vector2(-3/ Main.PPM * 4, 2.5f/ Main.PPM * 4);
        vertices[7] = new Vector2(-1.5f/ Main.PPM * 4, 0/ Main.PPM * 4);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.1f;

        body.createFixture(fixtureDef);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.localAnchorB.setZero();

        float maxForwardSpeed = 250 / Main.PPM;
        float maxBackwardSpeed = -40 / Main.PPM;
        float backTireMaxDriveForce = 300 / Main.PPM;
        float frontTireMaxDriveForce = 500 / Main.PPM;
        float backTireMaxLateralImpulse = 0f;
        float frontTireMaxLateralImpulse = 0f;

        Tire tire = new Tire(world);
        tire.setValues(maxForwardSpeed, maxBackwardSpeed,
                backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(-3/ Main.PPM * 4, 0.75f/ Main.PPM * 4);
        world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        tire.setValues(maxForwardSpeed, maxBackwardSpeed,
                backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(3/ Main.PPM * 4, 0.75f/ Main.PPM * 4);
        world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        tire.setValues(maxForwardSpeed, maxBackwardSpeed,
                frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(-3/ Main.PPM * 4, 8.5f/ Main.PPM * 4);
        leftJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        tire.setValues(maxForwardSpeed, maxBackwardSpeed,
                frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set(3/ Main.PPM * 4, 8.5f/ Main.PPM * 4);
        rightJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);
    }

    public void update() {
        inputHandler();
        /*for (Tire tire : tires) {
            tire.updateFriction();
        }*/
        for (Tire tire : tires) {
            tire.updateDrive();
        }

        float lockAngle = 35 * Main.DEGTORAD;
        float turnSpeedPerSec = 160 * Main.DEGTORAD;
        float turnPerTimeStep = turnSpeedPerSec / 60.0f;
        float desiredAngle = 0;

        if(input == "left"){
            desiredAngle = lockAngle;
        } else if(input == "right"){
            desiredAngle = -lockAngle;
        }

        float angleNow = leftJoint.getJointAngle();
        float angleToTurn = desiredAngle - angleNow;
        angleToTurn = Math.max(-turnPerTimeStep, Math.min(angleToTurn, turnPerTimeStep));
        float newAngle = angleNow + angleToTurn;

        leftJoint.setLimits(newAngle, newAngle);
        rightJoint.setLimits(newAngle, newAngle);
    }

    private void inputHandler() {
        input = "";
        for (Tire tire : tires) {
            if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                tire.direction = Tire.Direction.up;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                tire.direction = Tire.Direction.down;
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                tire.direction = Tire.Direction.right;
                input = "right";
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                tire.direction = Tire.Direction.left;
                input = "left";
            } else tire.direction = Tire.Direction.stop;
        }
    }
}
