package org.wallerlab.swy.model

import org.springframework.context.annotation.Profile;
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
* Domain model for the molecular system.
*/
@Entity
@Profile(["untested"])
public class MolecularSystem {
	
	@Id
	@Column(name = "ID")
	int id
	
	public int molecularSystemId
	public int numberOfMolecules;
	public int[] numberOfAtomsInMolecule
	public int numberOfAtomsTotal
	public int[] numberOfBondsInMolecule
	
	public ArrayList atomLabel = []
	
	/*
	 * TODO check the type!
	 */
	public ArrayList<double[]> xCoordInitial = new ArrayList()
	public ArrayList<double[]> yCoordInitial = new ArrayList()
	public ArrayList<double[]> zCoordInitial = new ArrayList()
	
	/** Bond list
	 * 0th dimension: Molecule index
	 * First dimension (i): Atom number from which the bond is
	 * Second dimension (j): Which bond of atom i is looked at
	 * Value (k): The number of the atom to which atom i is connected via bond j
	 * The list is redundant (bonds go into both directions)
	 * The list is sorted by the atom number of the bond partner k
	 * Example: bonds[0][9][2] = 17 means: atom 9 of molecule 0 
	 * is connected to atom 17 (of course of the same molecule).
	 * It is the 2nd(+1) bond of atom 9.
	 */
	public ArrayList bonds = new ArrayList()
	
	/** Ring list
	 * 0th dimension: molecule index
	 * first dimension (i): ring index
	 * second dimension: this is a list of all atomIndexes which belong to the ring i
	 * Example: rings[0][2][3] = 7 means: ring 2 of molecule 0 contains atom 7.
	 * It is the 3rd(+1) atom of this ring.
	 */
	public ArrayList rings = new ArrayList()
	
	/**
	 * List of turnable bonds
	 * 0th dimension (x): Molecule index
	 * First dimension (i): Just the bond index
	 * Second dimension (j): The i-th bond goes from atom turnableBonds[x][i][0] to turnableBonds[x][i][1]
	 * Example: turnableBonds[0][2] = [5,7] means: the 2nd(+1) bond in molecule 0 is between atoms 5 and 7.
	 */
	// The following outcommented definition does not work, because numberOfBonds is not known when
	// MolecularSystem is constructed. Same in atomsToTurn[][], ...
	// public static int[][][] turnableBonds = new int[numberOfMolecules][numberOfBonds][]
	
	public ArrayList turnableBonds = new ArrayList()
	
	/* Like above list. Innermost dimension: angle between atoms ...[0], ...[1] and ...[2] */
	public ArrayList bendableAngles = new ArrayList()
	/* like the turnableBonds list */
	public ArrayList stretchableBonds = new ArrayList()
	
	/**
	 * List of atom numbers that need to be moved to create a turn around a certain bond.
	 * 0th dimension: Molecule index
	 * First dimension (i): Just the bond index
	 * Second dimension (j): the list of atoms that move by turning around bond i
	 * Example: atomsToTurn[0][3][4] = 9 means: atom 9 needs to be adjusted whenever the 3rd(+1)
	 * bond of molecule 0 is twisted. It is the 4th(+1) atom that needs to be adjusted.
	 */
   /* public static int[][][] atomsToTurn = new int[numberOfMolecules][numberOfBonds][] */
   public ArrayList atomsToTurn = new ArrayList()
   public ArrayList atomsToBend = new ArrayList()
   private ArrayList atomsToMove = new ArrayList()
}
