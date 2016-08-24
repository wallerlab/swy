/*package org.wallerlab.swy.service.meta.swarm;

import org.wallerlab.swy.service.meta.MetaInterface;
import org.wallerlab.swy.service.meta.swarm.PsoParticle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import net.sourceforge.jswarm_pso.Swarm;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Lazy;


*//**
 * Use PSO to find optimal solution to problem
 *//*
@Service
@Lazy
@Profile({"unstable"})
//public class ParticleSwarmOptimizer implements MetaInterface{
public class ParticleSwarmOptimizer {

        //Spring Injected
        private static int numberOfIterations;

 
         public ParticleSwarmOptimizer(){
        }

        public void startSearch(String jobName, int jobNumber){

                // Create a swarm (using 'MyParticle' as sample particle and
                // 'MyFitnessFunction' as fitness function)
                Swarm swarm = new Swarm(Swarm .DEFAULT_NUMBER_OF_PARTICLES,
                                new PsoParticle(), new PsoFitnessFunction(jobName, jobNumber));

                // Use neighborhood
                Neighborhood neigh = new Neighborhood1D(
                Swarm.DEFAULT_NUMBER_OF_PARTICLES / 5, true);
                swarm.setNeighborhood(neigh);
                swarm.setNeighborhoodIncrement(0.9);

                // Set position (and velocity) constraints. 
                swarm.setInertia(0.95);
                swarm.setMaxPosition(1);
                swarm.setMinPosition(-1);
                swarm.setMaxMinVelocity(0.1);

                // Optimize 
                for (int i = 0; i < numberOfIterations; i++){
                        swarm.evolve();
                }

                // Print results
                try {
                        System.out.println(swarm.toStringStats());
                        BufferedWriter bout = new BufferedWriter(new FileWriter(
                                        "particle.csv", true));
                        bout.write(swarm.toStringStats());
                        bout.close();
                        System.out.println("File created successfully.");
                        //SET the degrees of freedom to the best found solution!!! most important step
                        agent.solution = swarm.getBestPosition();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
        public static int getNumberOfIterations() {
                return numberOfIterations;
        }

        public static void setNumberOfIterations(int numberOfIterations) {
                ParticleSwarmOptimizer.numberOfIterations = numberOfIterations;
        }
}
*/