package org.wallerlab.swy.dao.flat

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile(["unstable"])
class TurbomoleCoord {

	String input;
	def Integer i, j, numberAtoms
	def dummy=[]

	public int getTurbomoleCoords(){
		
		List infile = new File("${input}").readLines()
		numberAtoms = 0
		j = 0
		for (i=0; i<infile.size(); i++){
			j++
			if (infile[i].stripIndent()=="\$coord"){
				break
			}
		}
		for (i=j; i<infile.size(); i++){
			if (infile[i].stripIndent()=="\$end"){
				break
			}else{
				dummy=infile[i].stripIndent().split()
				numberAtoms += 1
				atomLabel[i-j+numberAtomsTotal] = dummy[3].toString()
				xCoord[i-j+numberAtomsTotal] = dummy[0].toDouble()
				yCoord[i-j+numberAtomsTotal] = dummy[1].toDouble()
				zCoord[i-j+numberAtomsTotal] = dummy[2].toDouble()
			}
		}
		if (numberAtoms==0){
			log.error("Error while reading TurboMole coord file: ${input}")
		}else{
			numberAtomsTotal += numberAtoms
			return numberAtoms
		}
	}
}


