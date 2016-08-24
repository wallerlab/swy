package org.wallerlab.swy.model

import org.wallerlab.swy.model.SearchSpace;
import org.wallerlab.swy.service.conformation.factory.ConformationInterface;
import org.wallerlab.swy.service.energy.EnergyInterface;
import org.wallerlab.swy.service.energy.StructureOptimizer;
import org.wallerlab.swy.service.searchspace.factory.SearchSpaceInterface;
import org.wallerlab.swy.ApplicationContextProvider

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Domain model of the problem to be solved by the meta or hypers.
 */
@Entity
@Profile(["untested"])
public class ProblemRepresentation {
	
	@Id
	@Column(name = "ID")
	private int id
	
	/* the 'problem' */
	private MolecularSystem molecularSystem
	private SearchSpace searchSpace
	/* the evaluation scheme for a problem solution */
	private EnergyInterface energyCalculator
	private ConformationInterface conformationFactory
	private StructureOptimizer structureOptimizer
	
	SearchSpace getSearchSpace() {
		 searchSpace
	}
	
	EnergyInterface getEnergyCalculator() {
		return energyCalculator
	}
	
	StructureOptimizer getStructureOptimizer() {
/*		if (!structureOptimizer) {
			try {
				structureOptimizer = ApplicationContextProvider.getApplicationContext().getBean("structureOptimizer", conformationFactory, ApplicationContextProvider.getApplicationContext().getBean("${Input.externalCode}") )
			} catch(e) {
				// TODO override with singlePoint
				println ("WARNING: the code you chose cannot perform structural optimizations!")
			}
		}*/
		structureOptimizer
	}

	public void setConformationFactory(ConformationInterface conformationFactory) {
		this.conformationFactory = conformationFactory
	}
	
	public ConformationInterface getConformationFactory() {
		return conformationFactory
	}
	
	@Required	
	void setSearchSpace(SearchSpace newValue) {
		searchSpace = newValue
	}
	
	@Required	
	void setMolecularSystem(MolecularSystem newValue) {
		molecularSystem = newValue
	}
	
	@Required
	void setEnergyCalculator(EnergyInterface newValue) {
		energyCalculator = newValue
	}

}
