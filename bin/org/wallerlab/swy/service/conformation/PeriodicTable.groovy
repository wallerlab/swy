package org.wallerlab.swy.service.conformation

/**
 * Static information about chemistry, which is normally found
 * on the PSE.
 * 
 * @author t_dres03
 */
class PeriodicTable {

	/**
	 * @param atomName The one- or two-letter string identifying the atom
	 * (not a force field like atom type, just the chemical symbol)
	 * @return The mass of the atom in g/mol
	 */
	static double getMass(String atomName) {
		// TODO complete it; error checking?!?
		// TDOD Must be a map!!!!
		switch (atomName) {
			case "H":
				return 1.008
				break
			case "C":
				return 12.011
				break
			case "N":
				return 14.007
				break
			case "O":
				return 15.999
				break
			case "S":
				return 32.06
				break
			case "P":
				return 30.974
				break
			case "F":
				return 18.998
				break
			case "Cl":
				return 35.45
				break
			case "Br":
				return 79.904
				break
			case "I":
				return 126.90
				break
			case "Li":
				return 6.94
				break
			case "Na":
				return 22.990
				break
			case "K":
				return 39.098
				break
			case "Ca":
				return 40.078
				break
			case "B":
				return 10.81
				break
			case "Cu":
				return 63.546
				break
			case "Ag":
				return 107.87
				break
			case "Au":
				return 196.97
				break
			case "Zn":
				return 65.38
				break
			case "Cd":
				return 112.41
				break
			case "Hg":
				return 200.59
				break
			case "Be":
				return 9.0122
				break
			case "Mg":
				return 24.305
				break
			case "Sr":
				return 87.62
				break
			case "Ba":
				return 137.33
				break
			case "Rb":
				return 85.468
				break
			case "Cs":
				return 132.91
				break
		}
	}
}
