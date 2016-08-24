package org.wallerlab.swy.dao.scp

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * Secure Copy Class for transferring files between
 * remote-webserver and local-compute node
 * 
 * Please  accesses the  launch properties file for destination,  password, etc...
 */
//@Repository
@Profile(["unstable"])
class ScpImpl {

	@Value('${batch.scp.password}')
	private String password
	
	@Value('${batch.scp.localDir}')
	private String localDir
	
	@Value('${batch.scp.file}')
	private String file
	
	
	/**
	 * Get the xml (or similar) structure file.
	 */
	void getFile(){
		ant.scp(
				trust:"true",
				file:"$file",
				todir:"$localDir",
				password:"$password",
				verbose:true
				)
	}
}
