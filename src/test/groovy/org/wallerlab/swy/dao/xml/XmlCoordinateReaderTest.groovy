package org.wallerlab.swy.dao.xml

import org.springframework.core.io.ClassPathResource
import org.wallerlab.swy.model.MolecularSystem
import spock.lang.*

class XmlCoordinateReaderTest extends Specification{

    private XmlCoordinateReader xmlCoordinateReader
	
    private MolecularSystem molecularSystem

    @Unroll
    def "Test to read in the difluorobutadien xml file" () {
		
	given:
	String path = new ClassPathResource("molecularsystems/Difluorobutadien.xml").getFile().getPath()
	xmlCoordinateReader = new XmlCoordinateReader()
	xmlCoordinateReader.setInputLocation(path)
	molecularSystem = xmlCoordinateReader.readIn()
	for (int atom=0; atom < 10; atom++) {
		molecularSystem.xCoordInitial[0][atom] = molecularSystem.xCoordInitial[0][atom].round(4)
		molecularSystem.yCoordInitial[0][atom] = molecularSystem.yCoordInitial[0][atom].round(4)
		molecularSystem.zCoordInitial[0][atom] = molecularSystem.zCoordInitial[0][atom].round(4)
	}
		
	expect:
	molecularSystem.numberOfMolecules == 1
	molecularSystem.xCoordInitial == [[-2.9799, -4.0482, -2.302, -1.2336, -2.3073, -3.0692, -4.1376, -2.3913, -1.323, -3.0639]]
	molecularSystem.yCoordInitial == [[-0.2508, -0.2506, 0.9226, 0.9224, -1.4198, 2.2561, 2.2563, 3.4295, 3.4293, 4.5985]]
	molecularSystem.zCoordInitial == [[-0.0099, 0.0499, -0.0268, -0.0865, -0.0686, 0.0402, 0.0999, 0.0233, -0.0365, 0.082]]
	molecularSystem.bonds == [[[1, 2, 4], [0], [0, 3, 5], [2], [0], [2, 6, 7], [5], [5, 8, 9], [7], [7]]]
	molecularSystem.atomLabel == [["C", "H", "C", "H", "F", "C", "H", "C", "H", "F"]]
	molecularSystem.numberOfAtomsInMolecule == [10]
	molecularSystem.numberOfBondsInMolecule == [18]
		
	cleanup:
	xmlCoordinateReader = null
	molecularSystem = null
    }

    @Unroll
    def "Test to read in the formaldehyde dimer xml file" () {
	
	given:
	String path = new ClassPathResource("molecularsystems/FormaldehydeDimer.xml").getFile().getPath()
	xmlCoordinateReader = new XmlCoordinateReader()
	xmlCoordinateReader.setInputLocation(path)
	molecularSystem = xmlCoordinateReader.readIn()
			
	expect:
	molecularSystem.numberOfMolecules == 2
	molecularSystem.bonds == [[[1, 2, 3], [0], [0], [0]], [[1, 2, 3], [0], [0], [0]]]
	molecularSystem.atomLabel == [["C", "O", "H", "H"], ["C", "O", "H", "H"]]
	molecularSystem.numberOfAtomsInMolecule == [4, 4]
	molecularSystem.numberOfBondsInMolecule == [6, 6]
	
	cleanup:
	xmlCoordinateReader = null
	molecularSystem = null
    }
	
    @Unroll
    def "Test to read in the formic acid dimer xml file" () {
	given:
	String path = new ClassPathResource("molecularsystems/FormicAcidDimer.xml").getFile().getPath()
	xmlCoordinateReader = new XmlCoordinateReader()
	xmlCoordinateReader.setInputLocation(path)
	molecularSystem = xmlCoordinateReader.readIn()
	
	for (int atom=0; atom < 5; atom++) {
		molecularSystem.xCoordInitial[0][atom] = molecularSystem.xCoordInitial[0][atom].round(4)
		molecularSystem.xCoordInitial[1][atom] = molecularSystem.xCoordInitial[1][atom].round(4)
	}
		
	expect:
	molecularSystem.numberOfMolecules == 2
	molecularSystem.xCoordInitial == [[-1.9348, -1.4785, -1.1913, -3.0278, -0.4306], [1.9348, 1.4785, 1.1913, 3.0278, 0.4306]]
	molecularSystem.bonds == [[[1, 2, 3], [0, 4], [0], [0], [1]], [[1, 2, 3], [0, 4], [0], [0], [1]]]
	molecularSystem.atomLabel == [["C", "O", "O", "H", "H"], ["C", "O", "O", "H", "H"]]
	molecularSystem.numberOfAtomsInMolecule == [5, 5]
	molecularSystem.numberOfBondsInMolecule == [8, 8]

	cleanup:
	xmlCoordinateReader = null
	molecularSystem = null
    }

}
