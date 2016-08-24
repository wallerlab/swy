package org.wallerlab.swy.aop;

import org.wallerlab.swy.dao.flat.CSVWriter;
import org.wallerlab.swy.dao.flat.XYZWriter;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.AfterReturning

/**
 * Some analysis tools using the AOP paradigm. At the moment this class
 * creates files to store information of a running calculation. These
 * can further be analysed manually.
 */
@Aspect
@Component
@Profile(["untested"])
// TODO switches for the output level are needed.
public class AnalysisAspect {
	
	File analysisMainDir
	File structureDir
	File visualizableDataDir
	
	File iterationBestXyz
	File globalBestXyz
	File pheromoneFile
//	File particleFile
	
	/**
	 * The AnalysisAspect default constructor sets up a directory structure for files that
	 * can be investigated to see what the program does.
	 * No file is created here, but variables to find the right files are set.
	 * Files that already exist are cleared.
	 */
	public AnalysisAspect() {
		/*
		 * directories
		 */
		analysisMainDir = new File("analysis")
		analysisMainDir.mkdir()
		structureDir = new File("structures", analysisMainDir)
		structureDir.mkdir()
		visualizableDataDir = new File("visualizable", analysisMainDir)
		visualizableDataDir.mkdir()
		/*
		 * file pointers
		 * 
		 */
		iterationBestXyz = new File("iterationBest.xyz", structureDir)
		if (iterationBestXyz.exists()) {
			iterationBestXyz.write("")
		}
		globalBestXyz = new File("globalBest.xyz", structureDir)
		if (globalBestXyz.exists()) {
			globalBestXyz.write("")
		}
		pheromoneFile = new File("pheromones.dat", visualizableDataDir)
		if (pheromoneFile.exists()) {
			pheromoneFile.write("")
		}
//		particleFile = new File("particles.dat", visualizableDataDir)
//		if (particleFile.exists()) {
//			particleFile.write("")
//		}
	}
	
	/*
	 * Structure output
	 */
	// giving total package/class paths now...maybe switch to import?
	/**
	 * After a new set of agents is generated and calculated the structures
	 * corresponding to the solutions of the best agents are written.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.meta.MetaInterface#getBetterAgents()}
	 */
	@After("execution(org.wallerlab.swy.model.main.Agent[] getBetterAgents())")
	public void analysisAfterGetBetterAgents(JoinPoint jp) {
		if (jp.getTarget().getIterationBestAgent()) {
			org.wallerlab.swy.service.conformation.factory.ConformationInterface conformationFactory \
			= jp.getTarget().getProblemRepresentation().getConformationFactory()
			if (conformationFactory) {
				double[] iterationBestSolution = jp.getTarget().getIterationBestAgent().lastReasonableSolution
				double[] globalBestSolution = jp.getTarget().getGlobalBestAgent().lastReasonableSolution
				XYZWriter.writeXYZ(iterationBestXyz, conformationFactory.getMolecularSystem(), \
					conformationFactory.newConformation(iterationBestSolution), \
					"Energy=${jp.getTarget().getIterationBestAgent().lastReasonableEnergy}  iterationBestSolution")
				XYZWriter.writeXYZ(globalBestXyz, conformationFactory.getMolecularSystem(), \
					conformationFactory.newConformation(globalBestSolution), \
					"Energy=${jp.getTarget().getGlobalBestAgent().lastReasonableEnergy}  globalBestSolution")
			}
		}
	}
	
	/*
	 * TODO it should be generally possible to monitor several mataheuristics at the same time ,not only
	 * in this case.
	 * 
	 * For the parameterControl case.....
	 */
	/**
	 * In an automatic parameter search calculation the {@link #analysisAfterGetBetterAgents(JoinPoint) normal structure output}
	 * is not used. Instead the best structures of all controlled
	 * heuristics are written, indexed with an id number for the
	 * heuristic.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.energy.HyperHeuristicLoopback#getEnergy(double...)}
	 */
	@After("execution(double org.wallerlab.swy.service.energy.HyperHeuristicLoopback.getEnergy(*))")
	public void analysisAfterHyperHeuristicGetEnergy(JoinPoint jp) {
		int index=jp.getTarget().getIndex()
		org.wallerlab.swy.service.meta.ControllableMeta controllableMeta \
		= jp.getTarget().getMetaInstances().get(index)
		if (controllableMeta.getIterationBestAgent() && !controllableMeta.isConverged()) {
			org.wallerlab.swy.service.conformation.factory.ConformationInterface conformationFactory \
				= controllableMeta.getProblemRepresentation().getConformationFactory()
			if (conformationFactory) {
				double[] iterationBestSolution = controllableMeta.getIterationBestAgent().lastReasonableSolution
				double[] globalBestSolution = controllableMeta.getGlobalBestAgent().lastReasonableSolution
				XYZWriter.writeXYZ(new File(index+"iterationBest.xyz", structureDir), \
					conformationFactory.getMolecularSystem(), \
					conformationFactory.newConformation(iterationBestSolution), \
					"Energy=${controllableMeta.getIterationBestAgent().lastReasonableEnergy}  iterationBestSolution")
				XYZWriter.writeXYZ(new File(index+"globalBest.xyz", structureDir), \
					conformationFactory.getMolecularSystem(), \
					conformationFactory.newConformation(globalBestSolution), \
					"Energy=${controllableMeta.getGlobalBestAgent().lastReasonableEnergy}  globalBestSolution")
			}
		}
	}

	
	/*
	 * Other outputs, e.g. those that can be used for google charts
	 */
	/**
	 * This method produces output files containing the pheromone levels
	 * of the best solution of the just completed metaCycle in an ant
	 * colony optimization.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.meta.ant.AntColonyInterface#getBetterAgents()}
	 */
	@After("execution(* org.wallerlab.swy.service.meta.ant.AntColonyInterface+.getBetterAgents())")
	public void analysisAfterACORunNextMetaCycle(JoinPoint jp) {
		if (jp.getTarget().getIterationBestAgent()) {
			double[][] tau = jp.getTarget().antColony.tau
			double[] iterationBestSolution = jp.getTarget().getIterationBestAgent().lastReasonableSolution
			double[] pheromonesToStore = new double[iterationBestSolution.size()]
			for (int dimension in 0..iterationBestSolution.size()-1) {
				pheromonesToStore[dimension] = \
				 tau[dimension][jp.getTarget().antColony.searchSpaceDiscretiser.toDiscreteSpace(dimension, iterationBestSolution[dimension])] \
				 / jp.getTarget().antColony.tauMax
			}
			CSVWriter.writeCSVLine(pheromoneFile, pheromonesToStore)
		}
	}
	
	/*
	 * all pheromones output writer. USE ONLY IF YOU REEEALLY WANT TO; LOTS OF DATA AND WON'T BE ERASED
	 */
	/**
	 * This method writes out the whole pheromone information
	 * of a running ant colony optimization after the just completed
	 * metaCycle.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.meta.ant.AntColonyInterface#getBetterAgents()}
	 */
	@After("execution(* org.wallerlab.swy.service.meta.ant.AntColonyInterface+.getBetterAgents())")
	public void analysisAfterACORunNextMetaCycleALLPHERO(JoinPoint jp) {
		double[][] tau = jp.getTarget().antColony.tau
		for (int dimension in 0..tau.size()-1) {
			File thisPheroFile = new File("phero${dimension}.dat", visualizableDataDir)
			CSVWriter.writeCSVLine(thisPheroFile, tau[dimension])
		}
	}

	/*
	 * Particle positions
	 */
	/**
	 * This method writes out all current particle positions of
	 * a particle swarm optimization after the just completed
	 * metaCycle.
	 * 
	 * @AnnotatedMethod
	 * {@link org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmInterface#getBetterAgents()}
	 */
	@After("execution(* org.wallerlab.swy.service.meta.studentPSO.ParticleSwarmInterface+.getBetterAgents())")
	public void analysisAfterStudentPSORunNextMetaCycle(JoinPoint jp) {
		for (int particleIndex in 0..jp.getTarget().getNumberOfAgents()-1) {
			File thisParticleFile = new File("particle${particleIndex}.dat", visualizableDataDir)
			CSVWriter.writeCSVLine(thisParticleFile, jp.getTarget().getParticleSwarm().getParticles()[particleIndex].position)
		}
	}

}
