package org.wallerlab.swy.service.energy.wrappers;

import spock.lang.*

import org.wallerlab.swy.model.Coordinates
import org.wallerlab.swy.model.MolecularSystem
import org.wallerlab.swy.service.energy.wrappers.Mopac;

class MopacTest extends Specification{
	
	private Mopac mopac
	
	private MolecularSystem molecularSystem
	private Coordinates coordinates
/**	
	@Unroll
	def "Verify that Mopac executable String is set." () {
		
		given:
		mopac = new Mopac()
				
		expect:
		System.getenv("MOPAC_EXE")
		mopac.exeString
		
		cleanup:
		mopac = null
	}
	
	@Unroll
	def "Calculate H2." () {
		
		given:
		mopac = new Mopac()
		mopac.method = "PM6-DH+"
		mopac.charge = 0
		mopac.multi = 1
		molecularSystem = new MolecularSystem()
		molecularSystem.atomLabel = [["H", "H"]]
		molecularSystem.numberOfMolecules = 1
		molecularSystem.numberOfAtomsInMolecule =[2]
		mopac.setMolecularSystem(molecularSystem)
		coordinates = new Coordinates()
		coordinates.xCoord = [[0.0, 0.7638]]
		coordinates.yCoord = [[0.0, 0.0]]
		coordinates.zCoord = [[0.0, 0.0]]
		
		expect:
		mopac.getSinglePointEnergy(coordinates).round(5) == -28.12848
		
		cleanup:
		mopac = null
		molecularSystem = null
		coordinates = null
	}
*/
}
