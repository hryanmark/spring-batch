package com.hryanmark.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.hryanmark.springbatch.entity.Customer;
import com.hryanmark.springbatch.repository.CustomerRepository;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SpringBatchConfig {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	//Read from dataSource (csv, db or other datasource)
	//For reader component to use in a step
	@Bean
	public FlatFileItemReader<Customer> reader(){
		
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csvReader"); //name of your reader
		itemReader.setLinesToSkip(1);//Skip the first row (usually title headers)
		itemReader.setStrict(false);
		itemReader.setLineMapper(mapper()); 
		
		return itemReader;
	}

	private LineMapper<Customer> mapper() {
		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();


		//setting delimiter of the csv file
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
		lineTokenizer.setStrict(false);
		
		
		//Mapping fields read from csv to Customer entity
		BeanWrapperFieldSetMapper<Customer> fieldMapper = new BeanWrapperFieldSetMapper<>();
		fieldMapper.setTargetType(Customer.class);
		
		
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldMapper);
		
		return lineMapper;
	}
	
	//For processor component to use in a step
	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}
	
	//For writer component to use in a step
	@Bean
	public RepositoryItemWriter<Customer> writer() {
		
		RepositoryItemWriter<Customer> itemWriter = new RepositoryItemWriter<>();
		itemWriter.setRepository(customerRepository);
		itemWriter.setMethodName("save"); //method name from repository
		
		return itemWriter;
	}
	
	//(csv-step) name of your step. chunk(10) process 10 records at a time
	//For step component to use in a job
	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager transManager) {
		return new StepBuilder("csv-step", jobRepository)
				.<Customer, Customer> chunk(10, transManager)
				.reader(reader())
				.processor(processor())
				.writer(writer())
//				.taskExecutor(taskExecutor())
				.build(); 
	}
	
	//For job component to use in JobLauncher (controller)
	@Bean
	public Job runJob(JobRepository jobRepository, PlatformTransactionManager transManager) {
		return new JobBuilder("importCustomers", jobRepository) //(importCustomer) name of your job.
				.flow(step1(jobRepository, transManager)) //can be multiple flow if more steps required
				.end()
				.build();
	}
	
	//TO run job asynchronously (faster process than synchronous)
	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10); //execute 10 records concurrently (multi-threading)
		
		return asyncTaskExecutor;
	}
	
}















