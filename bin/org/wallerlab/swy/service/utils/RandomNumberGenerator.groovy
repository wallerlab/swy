package org.wallerlab.swy.service.utils

import org.springframework.stereotype.Service

@Service
class RandomNumberGenerator {

	private Random numberGenerator = new Random(System.nanoTime());

	/**
	 * @return a new random double between 0 and 1.
	 */
	public double getNewDouble(){
		numberGenerator.nextDouble()
	}
}
