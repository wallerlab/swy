package org.wallerlab.swy.service.batch

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile;

/** 
 *  JobParameters needed to carry out different jobs 
 */
@Configuration
@Profile(["unstable"])
public class SwyBatchJobParameters {

	@Value("#{swy.run.cycles}")
	private int runCycles

	@Value("#{swy.hyper.cycles}")
	private int hyperCycles

	@Value("#{swy.meta.cycles}")
	private int metaCycles

	@Value("#{swy.run.dao}")
	private int runDao

	@Value("#{swy.hyper.dao}")
	private int hyperDao

	@Value("#{swy.meta.dao}")
	private int metaDao

	@Value("#{swy.agent.dao}")
	private int agentDao

	@Bean
	public JobParameters runParams(){
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("fail","false")
				.addString("Stage","run")
				.addInteger('Cycles',$runCycles)
				.addString('Dao',$runDao)
				.toJobParameters();
	}


	@Bean
	public JobParameters metaParams(){
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("fail","false")
				.addString("Stage","meta")
				.addInteger('Cycles',$metaCycles)
				.addString('Dao',$metaDao)
				.toJobParameters();
	}

	@Bean
	public JobParameters agentParams(){
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("fail","false")
				.addString("Stage","agent")
				.addString('Dao',$agentDao)
				.toJobParameters();
	}
}

