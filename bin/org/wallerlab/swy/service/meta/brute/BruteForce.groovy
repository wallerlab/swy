//package org.wallerlab.swy.service.meta.brute
//
//import org.springframework.context.annotation.Profile;
//import org.springframework.context.annotation.Lazy
//
//import org.wallerlab.swy.ApplicationContextProvider
//import org.wallerlab.swy.model.SearchSpace
//import org.wallerlab.swy.model.ProblemRepresentation
//import org.wallerlab.swy.service.conformation.factory.ConformationInterface;
//import org.wallerlab.swy.service.energy.ModellingInterface;
//import org.wallerlab.swy.service.meta.AbstractMeta
//import org.wallerlab.swy.service.meta.MetaInterface;
//import org.wallerlab.swy.service.meta.brute.StepFactory
//import org.wallerlab.swy.service.meta.brute.BruteForce.StepResult
//import org.wallerlab.swy.service.meta.brute.SolutionGenerator
//import org.wallerlab.swy.service.searchspace.factory.SearchSpaceInterface;
//
//import org.springframework.stereotype.Service
//import org.springframework.context.annotation.Scope
//import org.springframework.context.annotation.Lazy
//
//
//
///**
//* A brute force Impl for testing and benchmarking
//*
//*/
//@Lazy
//@Service("Brute Force")
//@Profile(["unstable"])
//class BruteForce extends AbstractMeta {
//
//	// TODO Not yet in a working state.
//	
//	private SolutionGenerator solutionGenerator
//	
//	public BruteForce(String scrDir, String outDir, ProblemRepresentation problemRepresentation) {
//		super(scrDir, outDir, problemRepresentation)
//		solutionGenerator = new SolutionGenerator();
//	}
//
//	public int startSearch(int runNumber){
//
//		solutionGenerator.init()
//		// The first solution is generated upon initialization
//		// TODO the jobNumber .....
//		stepCompletionService.submit(new StepFactory(0))
//
//		while(solutionGenerator.thereAreMoreSolutions()){
//			while (stepsWorking >= poolSize) {
//				parseStepResult()
//				stepsDone++
//				stepsWorking--
//			}
//			solutionGenerator.getNextSolution();
//			stepCompletionService.submit(new StepFactory(0))
//			stepsSent++
//			stepsWorking++
//		}
//
//		final int stepsLeft = stepsSent - stepsDone;
//		// All jobs are at least started
//		for (int k = 0; k < stepsLeft; k++) {
//			// Get the results for the remaining jobs
//			parseStepResult()
//		}
//		return 0
//	}
//
//	/* Dummy class to exchange the relevant data in parallel usage. */
//	static class StepResult {
//		double energy
//		public StepResult( double energy) {
//			super();
//			this.energy = energy;
//		}
//	}
//	
//	private void parseStepResult() {
//		StepResult stepResult = stepCompletionService.take().get()
//		// TODO what to do with the results??
//	}
//
//	@Override
//	protected void collectParameterNames() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void afterPropertiesSet() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int runNextMetaCycle() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//}
//
//
//
