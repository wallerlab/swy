package org.wallerlab.swy.dao.flat;

import static org.junit.Assert.*;
import org.junit.Test;

import org.springframework.core.env.PropertySource;

import java.io.IOException;
import java.util.Map;

/**
 * Check if the standard.properties file exists and can be read.
 * 
 * @author t_dres03
 */
public class PropertyFileLinkerTest {

	private PropertyFileLinker propertyFileLinker;
	private PropertySource<Map<String, Object>> propertySource;
	
	@Test
	public void testGetPropertySource() {
		propertyFileLinker = new PropertyFileLinker();
		try {
			propertySource = propertyFileLinker.getPropertySource("standard.properties");
		} catch (IOException e) {
			fail("Standard PropertySource file could not be found.");
			e.printStackTrace();
		}
		assertEquals((String)propertySource.getProperty("swy.xmlMoleculeFile"), "NO_DEFAULT_SPECIFIED");
	}

}
