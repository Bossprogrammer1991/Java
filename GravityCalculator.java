/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gravitycalculator;

/**
 *
 * @author Eric
 */
public class GravityCalculator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double gravity = -9.81; // Earth's gravity in m/s^2

double initialVelocity = 0.0;

double fallingTime = 10.0;

double initialPosition = 0.0;

double finalPosition = 0.5 * gravity * fallingTime * fallingTime + 
        initialVelocity * fallingTime + initialPosition;

 System.out.println("The object's position after " + fallingTime +

" seconds is " + finalPosition + " m.");
    }
}
