package config;

import org.springframework.batch.item.ItemProcessor;

import entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer customer) throws Exception {
		//by default, it's writing all customer data
		//you can filter out specific data.
		//sample if customer.getCountry().equals("United States) return customer; //only customer from US is saved.
		return customer;
	}


}
