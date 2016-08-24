package org.wallerlab.swy.service.batch

import org.springframework.context.annotation.Profile;
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager

import javax.sql.DataSource
import org.apache.commons.dbcp.BasicDataSource

//TODO move to BatchConfig

@Configuration
@Profile(["unstable"])
class BatchConfigurationOld {

	@Value('${swy.jdbc.driver}')
	private String driverClassName

	@Value('${swy.jdbc.url}')
	private String driverUrl

	@Value('${swy.jdbc.user}')
	private String driverUsername

	@Value('${swy.jdbc.password}')
	private String driverPassword

//	@Autowired
//	@Qualifier('jobRepository')
	private JobRepository jobRepository
	
	//Which datasource to use?
	@Bean
	DataSource dataSource() {
		new BasicDataSource(
				driverClassName: driverClassName,
				url: driverUrl,
				username: driverUsername,
				password: driverPassword
		)
	}

	@Bean
	SimpleJobLauncher jobLauncher() {
		new SimpleJobLauncher(jobRepository: jobRepository)
	}

	@Bean
	PlatformTransactionManager transactionManager() {
		new DataSourceTransactionManager(dataSource())
	}
}
