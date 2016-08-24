package org.wallerlab.swy.service.energy.wrappers;

import static org.junit.Assert.*
import org.junit.Test

import spock.lang.*

class GaussianTest extends Specification{
		
	def methaneXml ="""<molecule name="METHANE">
	<atoms>
	  <atom symbol="C" x="0.257" y="-0.363" z="0.000"/>
	  <atom symbol="H" x="0.257" y="0.727" z="0.000"/>
	  <atom symbol="H" x="0.771" y="-0.727" z="0.890"/>
	  <atom symbol="H" x="0.771" y="-0.727" z="-0.890"/>
	  <atom symbol="H" x="-0.771" y="-0.727" z="0.000"/>
	</atoms>
  </molecule>"""
	
	 
	
	void testMemberVariables(){				
		assert ('AM1' == gausisan.hamiltonian)
		assert  (0    == gaussian.charge)
		assert  (1    == gaussian.multi) 
	}
	
	
	void  testInputCreate() {
		//write methane molecule
		assert input.exists()
		assert input.canRead()
	}
		
	void testMethaneExecute(){
	}
	
	void testResultGrep(){
		//Gaussian 03:  Mac32-G03RevC.02
		assert (-0.012695 == soutarray[0].toFloat())	
	}
	
}
