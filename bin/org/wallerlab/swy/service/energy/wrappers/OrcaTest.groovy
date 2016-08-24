package org.wallerlab.swy.service.energy.wrappers;

import spock.lang.Specification;
import spock.lang.Unroll;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spock.lang.*
import org.wallerlab.swy.service.energy.wrappers.Orca;

/**
* Test the Orca interface 
*/
class OrcaTest extends Specification{
	
	private Orca orca
	
/*	@Unroll
	def "Verify that Mopac executable String is set." () {
	given:	
	orca = new Orca()
	
	expect:
	orca.exeString
	}*/
	
	/*@Before
	public void setUp() {
		Input.method  = 'am1'
		Input.charge  = 0
		Input.multi   = 1 
		//Methane
		MolecularSystem.numberOfMolecules = 1
		MolecularSystem.numberOfAtomsInMolecule = [5]
		MolecularSystem.xCoordInitial = [[0.257, 0.257, 0.771, 0.771,-0.771]]
		MolecularSystem.yCoordInitial = [[-0.363, 0.727,-0.727, -0.727,-0.727 ]]
		MolecularSystem.zCoordInitial = [[0.0, 0.0, 0.890,-0.890, 0.0]]
	
	}
	
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testCreateInputOrca(){
	orca.createOrcaInput()	
	//assert (Swy.inp Exists!!)
	}
	
	@Test
	public void testExecuteOrca(){
		executeOrca()
	
	
	}
	@Test
	public void testCleanUpOrca(){
	cleanUpOrca()
	}*/
}