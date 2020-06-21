# Swy

[ ![Download](https://api.bintray.com/packages/wallerlab/release-candidates/swy/images/download.svg) ](https://bintray.com/wallerlab/release-candidates/swy/_latestVersion)
[![Apache License](http://img.shields.io/badge/license-APACHE2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/wallerlab/swy.svg?branch=master)](https://travis-ci.org/wallerlab/swy)
[![Coverage Status](https://coveralls.io/repos/github/wallerlab/swy/badge.svg?branch=master)](https://coveralls.io/github/wallerlab/swy?branch=master)

## A Hybrid Metaheuristic Approach for Non-Local Optimization of Molecular Systems

Thomas Dresselhaus, Jack Yang, Sadhana Kumbhar, Mark P. Waller
J. Chem. Theory Comput., (2013) 9, 2137–2149. 

DOI:10.1021/ct301079m


# Quick Start 

` git clone https://github.com/wallerlab/swy.git`

` cd swy`

` ./gradlew clean build`

To execute:

`Java –jar target/libs/swy-0.1.jar input.properties`

Take an example input.properties file, e.g.:
`examples/inputfiles/aco_dihed_mop_trien.properties`

The input.properties file contains the location of the molecular system, e.g: 

`example/molecularsystems/FormicAcidDimer.xml `

Therefore, an actual job can be run: 

`java –jar swy-0.1.jar  examples/aco_dihed_mop_trien.properties`

The resulting structures can be easily viewed in VMD by loading the .xyz files:

`analysis/structures/`

The convergence behavior of metaheuristics can be followed in:

`analysis/visualizable/ `

The documentation can be viewed in a browser by opening docs/index.html 

Initially the environment variable must be set for external codes, e.g.:

`export MOPAC_EXE=/opt/mopac/MOPAC2016.exe`


# Example:

[![DEMO](https://cloud.githubusercontent.com/assets/13583117/18966537/e079ab50-8681-11e6-9145-52bd1a4ad6c4.png)](https://www.youtube.com/watch?v=B9fMx7RRP_Y)
