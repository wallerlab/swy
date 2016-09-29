# Swy

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.wallerlab/swy/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.wallerlab/swy)
[ ![Download](https://api.bintray.com/packages/wallerlab/release-candidates/swy/images/download.svg) ](https://bintray.com/wallerlab/release-candidates/swy/_latestVersion)
[![Apache License](http://img.shields.io/badge/license-APACHE2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/wallerlab/swy.svg?branch=master)](https://travis-ci.org/wallerlab/swy)
[![Coverage Status](https://coveralls.io/repos/github/wallerlab/swy/badge.svg?branch=master)](https://coveralls.io/github/wallerlab/swy?branch=master)
[![Join the chat at https://gitter.im/wallerlab/swy](https://badges.gitter.im/wallerlab/swy.svg)](https://gitter.im/wallerlab/swy?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**A Hybrid Metaheuristic Approach for Non-Local Optimization of Molecular Systems** 

Thomas Dresselhaus, Jack Yang, Sadhana Kumbhar, Mark P. Waller
J. Chem. Theory Comput., (2013) 9, 2137–2149. 

DOI:10.1021/ct301079m


#Quick Start 

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

`export MOPAC_EXE=/opt/mopac/MOPAC2012.exe`


