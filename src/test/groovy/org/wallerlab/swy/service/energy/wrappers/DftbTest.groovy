package org.wallerlab.swy.service.energy.wrappers

import spock.lang.*

class DftbTest extends Specification{

	private DftbPlus dftbPlus
		
	@Unroll
	def "Test that DftbPlus executable String is set." () {
			
		given:
		dftbPlus = new DftbPlus()
		
		expect:
		dftbPlus.exeString
	}
	
	@Unroll
	def "Test that DftbPlus is available on current system." () {
			
		given:
		dftbPlus = new DftbPlus()
		
		expect:
		dftbPlus.exeString
	}	
	
}
