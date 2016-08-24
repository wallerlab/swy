package org.wallerlab.swy.service.batch

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 *  Launch a batch of Jobs.
 *  
 */
@Lazy
@Service("swyBatchLauncher")
@Profile(["unstable"])
public class SwyBatchLauncher {

//	@Autowired
	private Job job
	
	@Autowired
	private SimpleJobLauncher simpleJobLauncher

	public void batchJobStart(JobParameters jobParameters){
		simpleJobLauncher.run(job,jobParameters)
	}
}

