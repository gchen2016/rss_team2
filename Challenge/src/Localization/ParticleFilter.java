package Localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import Challenge.Fiducial;
import Challenge.GrandChallengeMap;

/**
 * ParticleFilter is part of the Localization module. It represents a particle
 * filter with RobotParticle objects as the particles.
 * 
 * It is based off of Thrun's Python implementation from his Udacity course
 * Artifical Intelligence for Robotics.
 *
 */
public class ParticleFilter {
    //    The number of particles used in the particle filter
    private final int numParticles;
    private ArrayList<RobotParticle> particles = new ArrayList<RobotParticle>();

    //    Map Fields
    private GrandChallengeMap map;
    private java.awt.geom.Rectangle2D.Double worldRect;

    //    Map Dimensions
    private double worldWidth;
    private double worldHeight;
    private double botLeftX;
    private double botLeftY;
    
    //    Noise Variables
    private double transNoise;
    private double rotNoise;
    private double sensorNoise;
    private Fiducial[] fiducials;

    /**
     * Creates a Particle filter with the provided parameters.
     * @param numParticles the number of particles 
     * @param map the map of the environment
     * @param transNoise the translational noise (std dev of translational measurements)
     * @param rotNoise the rotational noise (std dev of rotation measurements)
     * @param sensorNoise the sensor noise (std dev of sensor measurements)
     */
    public ParticleFilter(int numParticles, GrandChallengeMap map, 
            double transNoise, double rotNoise, double sensorNoise) {
        this.numParticles = numParticles;
        this.map = map;
        this.transNoise = transNoise;
        this.rotNoise = rotNoise;
        this.sensorNoise = sensorNoise;
        this.worldRect = map.getWorldRect();
        this.fiducials = map.getFiducials();
        this.worldWidth = worldRect.getWidth();
        this.worldHeight = worldRect.getHeight();
        this.botLeftX = worldRect.getMinX();
        this.botLeftY = worldRect.getMinY();
        
        //        Creating the particles
        for (int i = 0; i < numParticles; i++) {
            particles.add(new RobotParticle(fiducials, worldWidth, worldHeight, 
                    botLeftX, botLeftY, transNoise, rotNoise, sensorNoise));
        }
    }

    /**
     * Updates the position of each particle.
     * @param translation the translational distance the robot has moved (in meters)
     * @param rotation the rotational distance the robot has moved (in radians)
     */
    protected void motionUpdate(double translation, double rotation) {
        for (RobotParticle particle : particles) {
            particle.motionUpdate(translation, rotation);
        }
    }

    /**
     * Computes the probability that each particle is at it's current position given the provided measurements
     * @param measuredFiducials the indices of the fiducials that have measurements 
     * (corresponding to the indices of the fiducials in the field of GrandChallengeMap)
     * @param measuredDists the measured distances to the fiducials
     */
    protected void measurementUpdate(ArrayList<Integer> measuredFiducials, HashMap<Integer, Double> measuredDists) {
        //        Determining the probabilities that each of the particles measured the input measurements
        ArrayList<Double> measurementProbs = new ArrayList<Double>();
        for (RobotParticle particle : particles) {
            measurementProbs.add(particle.measurementProb(measuredFiducials, measuredDists));
        }
        
        System.out.println("Measurement Update");
        for (Double measurementProb : measurementProbs) {
            System.out.print(measurementProb + " ");
        }

        particles = resampleParticles(measurementProbs);
    }

    /**
     * Resamples the particles with the provided measurement probabilities using a resampling wheel.
     * See https://www.udacity.com/course/viewer#!/c-cs373/l-48704330/e-48748082/m-48740082
     * @param measurementProbs the probabilities that each of the particles 
     * @return the resampled particles
     */
    protected ArrayList<RobotParticle> resampleParticles(ArrayList<Double> measurementProbs){
        ArrayList<RobotParticle> resampledParticles = new ArrayList<RobotParticle>();

        int index = (int) (Math.random() * numParticles);
        double beta = 0.0;

        double maxProb = Collections.max(measurementProbs);
        for (int i = 0; i < numParticles; i++) {
            beta += (Math.random()*2*maxProb);
            while (measurementProbs.get(index) < beta) {
                beta -= measurementProbs.get(index);
                index = (index + 1) % numParticles;
            }
            resampledParticles.add(particles.get(index));
        }
        return resampledParticles;
    }

    public ArrayList<RobotParticle> getParticles() {
        return particles;
    }

    public void printParticles() {
        System.out.println("ParticleFilter with [numParticles=" + numParticles + ",  transNoise="
                + transNoise + ", rotNoise="
                + rotNoise + ", sensorNoise=" + sensorNoise + "]");
        for (RobotParticle particle : particles) {
            System.out.println(particle.toString());
        }
    }
    
    @Override
    public String toString() {
        String stringRep = "ParticleFilter with [numParticles=" + numParticles + ",  transNoise="
                + transNoise + ", rotNoise="
                + rotNoise + ", sensorNoise=" + sensorNoise + "]";
        
        for (RobotParticle particle : particles) {
            stringRep = stringRep + particle.toString();
        }
        
        return stringRep;
    }
}