package org.wallerlab.swy.service.searchspace.factory

import org.wallerlab.swy.model.ParameterControlSearchSpace
import org.wallerlab.swy.model.ProblemRepresentation
import org.wallerlab.swy.service.meta.ControllableMeta;

import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Service

import groovy.util.slurpersupport.GPathResult

/**
 * Creates a searchSpace for parameter optimisation. Search space
 * borders are read in from an xml file (data/parameterSettings.xml)
 * for all parameters an algorithm (which's parameters are to be
 * optimised) has.
 * 
 * @NeededProperties
 * <b><i><font color=purple>double</font></i> swy.service.searchSpace.parameterGranularity</b> -
 * Similar to the normal 'granularity' of the other searchSpaceInterface implementations (e.g.
 * {@link org.wallerlab.swy.service.searchspace.factory.BondRotation}).<br>
 * <b>default:</b> <i>0.001</i><br><br>
 * 
 * @author t_dres03
 */
@Profile(["untested"])
public class HyperHeuristicSearch implements SearchSpaceInterface {

	private ParameterControlSearchSpace searchSpace
	
	private final GPathResult parameterSettings = new XmlSlurper().parse(new File("data/parameterSettings.xml"))
	private List<String> parameterNames
	
	private double parameterGranularity
	
	public HyperHeuristicSearch() {
	}
	
	public ParameterControlSearchSpace generateSearchSpace() {

		searchSpace = new ParameterControlSearchSpace()

		searchSpace.caption = parameterNames
		searchSpace.numberOfDimensions = parameterNames.size()
		
		searchSpace.startValues = new double[searchSpace.numberOfDimensions]
		searchSpace.endValues = new double[searchSpace.numberOfDimensions]
		searchSpace.stepSize = new double[searchSpace.numberOfDimensions]
		searchSpace.periodic = new boolean[searchSpace.numberOfDimensions]
		
		for (int parameterIndex=0; parameterIndex < searchSpace.numberOfDimensions; parameterIndex++) {
			searchSpace.startValues[parameterIndex] \
				= parameterSettings.getProperty(parameterNames[parameterIndex]).startValue.toDouble()
			searchSpace.endValues[parameterIndex] \
				= parameterSettings.getProperty(parameterNames[parameterIndex]).endValue.toDouble()
			searchSpace.stepSize[parameterIndex] \
				= StepSizeCalculator.calculateStepSize(searchSpace.startValues[parameterIndex], \
					searchSpace.endValues[parameterIndex], parameterGranularity)
			searchSpace.periodic[parameterIndex] \
				= parameterSettings.getProperty(parameterNames[parameterIndex]).periodic.toBoolean()
		}
		
		int numberOfAgentsIndex = parameterNames.indexOf("numberOfAgents")
		if (numberOfAgentsIndex == -1 ) {
			// TODO this needs to be somehow unfixed.
			searchSpace.expectationValueForCalculationsPerMetaCycle = 10
		} else {
		searchSpace.expectationValueForCalculationsPerMetaCycle \
			= searchSpace.startValues[numberOfAgentsIndex] \
			+ 0.5*(searchSpace.endValues[numberOfAgentsIndex] - searchSpace.startValues[numberOfAgentsIndex])
		}

		for (int parameterIndex=0; parameterIndex < searchSpace.numberOfDimensions; parameterIndex++) {
			searchSpace.stepSize[parameterIndex] /= searchSpace.expectationValueForCalculationsPerMetaCycle
		}

		return searchSpace
	}
	
	@Required
	public void setParameterNames(List<String> parameterNames) {
		this.parameterNames = parameterNames
	}
	
	@Value('${swy.service.searchSpace.parameterGranularity}')
	public void setParameterGranularity(double parameterGranularity) {
		this.parameterGranularity = parameterGranularity
	}
}
