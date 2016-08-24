package org.wallerlab.swy.dao

import org.wallerlab.swy.model.MolecularSystem

/**
* Interface to switch between the xml, jdbc, flat...etc.
*
*/
interface SwyMoleculeDao {

	 MolecularSystem readIn();
		
	 void writeOut(String location);
	
}
