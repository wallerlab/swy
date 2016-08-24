package org.wallerlab.swy.model

import org.wallerlab.swy.model.SearchSpace
import org.wallerlab.swy.service.meta.ControllableMeta;

import org.springframework.context.annotation.Profile;

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Domain model for Hyperheuristic search spaces.
 */
@Entity
@Table(name = "HYPER_SEARCH_SPACE")
@Profile(["untested"])
public class ParameterControlSearchSpace extends SearchSpace {

	int expectationValueForCalculationsPerMetaCycle
}
