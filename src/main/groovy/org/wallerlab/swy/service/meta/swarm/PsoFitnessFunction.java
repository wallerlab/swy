/*package org.wallerlab.swy.service.meta.swarm;

import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.service.conformation.factory.ConformationInterface;
import org.wallerlab.swy.service.conformation.factory.SupramolecularConformation;
import org.wallerlab.swy.service.energy.ModellingInterface;

import java.util.Random;
import net.sourceforge.jswarm_pso.FitnessFunction;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;


@Service
@Lazy
@Profile({"unstable"})
public class PsoFitnessFunction extends FitnessFunction {
	
		MetaInterface conf = new SupramolecularConformation();
		
		SearchSpace searchSpace; 
	
	*//** Default constructor *//*
	public PsoFitnessFunction(String jobName, int jobNumber) {
		super(false); // Minimize  is false; true is maximize
		}

		public double evaluate(double[] position) {
		double fitness = 0;
	try{
		// read the particles values into the current conformer
		for (int i=0; i< searchSpace.numberOfDimensions;i++){
				agent.lastReasonableSolution[i] = position[i];
		}
		conf.newConformation();
		extCode.computeEnergy(jobName, jobNumber);
		fitness =Conformation.energy;
	   }
		catch(Exception e) {
		e.printStackTrace();
	   }
	   return fitness;
	  }
  }*/