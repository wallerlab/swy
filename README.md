# Swy-0.1 

The Swy code is capable of performing metaheuristic optimizations of molecular systems.

` git clone `

` cd swy`

` ./gradlew clean build`

To execute:
Java –jar target/libs/swy-0.1.jar input.properties

Take an example input.properties file, e.g.:
examples/inputfiles/aco_dihed_mop_trien.properties

The input.properties file contains the location of the molecular system, e.g: 
example/molecularsystems/FormicAcidDimer.xml 

Therefore, an actual job can be run: 
java –jar swy-0.1.jar  examples/aco_dihed_mop_trien.properties

The resulting structures can be easily viewed in VMD by loading the .xyz files:
analysis/structures/

The convergence behavior of metaheuristics can be followed in:
analysis/visualizable/ 

The documentation can be viewed in a browser by opening docs/index.html 

Initially the environment variable must be set for external codes, e.g.:

`export MOPAC_EXE=’/opt/mopac/MOPAC2012.exe’`


