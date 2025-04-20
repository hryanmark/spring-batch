package config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import entity.Customer;
import lombok.AllArgsConstructor;
import repository.CustomerRepository;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	
	private StepBuilderFactory stepBuilderFactory;
	
	private CustomerRepository customerRepository;
	
	//Read from dataSource (csv, db or other datasource)
	//For reader component to use in a step
	@Bean
	public FlatFileItemReader<Customer> reader(){
		
		FlatFileItemReader itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/customer.csv"));
		itemReader.setName("csvReader"); //name of your reader
		itemReader.setLineMapper(1);
		itemReader.setLineMapper(mapper()); //Skip the first row (usually title headers)
		
		return itemReader;
	}

	private LineMapper<Customer> mapper() {
		DefaultLineMappe<Customer> lineMapper = new DefaultLineMapper<>();


		//setting delimiter of the csv file
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");
		
		
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
	public Step step1() {
		return stepBuilderFactory.get("csv-step").<Customer, Customer> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.taskExecutor(taskExecutor())
				.build(); 
	}
	
	//For job component to use in JobLauncher (controller)
	@Bean
	public Job runJob() {
		return jobBuilderFactory.get("importCustomers") //(importCustomer) name of your job.
				.flow(step1()) //can be multiple flow if more steps required
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















