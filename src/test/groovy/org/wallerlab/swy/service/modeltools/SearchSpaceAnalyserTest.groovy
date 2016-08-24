package org.wallerlab.swy.service.modeltools;

import spock.lang.*;

public class SearchSpaceAnalyserTest extends Specification{
	
	private double pointA
	private double pointB
	private double searchSpaceSize
	private double distance
	private boolean periodic
	
	@Unroll
	def "Distance between #pointA and #pointB is #distance; is periodic: #periodic" () {
		
		expect:
		SearchSpaceAnalyser.distanceInSearchSpace(pointA, pointB, periodic, searchSpaceSize).round(8) == distance
		
		where:
		pointA	|	pointB	|	searchSpaceSize	|	distance	|	periodic
		0.0		|	0.0		|	1.0				|	0.0			|	true
		0.0		|	0.0		|	1.0				|	0.0			|	false
		0.0		|	0.9		|	1.0				|	0.1			|	true
		0.0		|	0.9		|	1.0				|	-0.9		|	false
		-0.4	|	0.3		|	2.5				|	-0.7		|	true
		0.3		|	-0.4	|	2.5				|	0.7			|	true
		-0.4	|	0.3		|	2.5				|	-0.7		|	false
		0.3		|	-0.4	|	2.5				|	0.7			|	false
		0.9		|	0.0		|	1.0				|	-0.1		|	true
		0.0		|	-0.9	|	1.0				|	-0.1		|	true
		
	}
}
