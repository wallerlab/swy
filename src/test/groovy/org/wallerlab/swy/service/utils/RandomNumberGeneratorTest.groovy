
package org.wallerlab.swy.service.utils;
import spock.lang.*
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class RandomNumberGeneratorTest extends Specification {

	def "Test random number to get a Double"(){
	
	given:
	def rand = new RandomNumberGenerator()
	
	expect:
		 rand.getNewDouble()
	0 <= rand.getNewDouble()
	1 >= rand.getNewDouble()
	rand.getNewDouble() != rand.getNewDouble()
	}
}
