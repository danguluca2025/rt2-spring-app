package jp.co.sss.crud.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.crud.bean.EmployeeBean;
import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.repository.EmployeeRepository;
import jp.co.sss.crud.util.BeanManager;

@Service
public class SearchForEmployeesByAddressService {
	@Autowired
	EmployeeRepository employeeRepository;
	
	public List<EmployeeBean> execute(String address){
		List<Employee> employees = employeeRepository.findByAddress(address);
		List<EmployeeBean> tempEmployeeBeans = BeanManager.copyEntityListToBeanList(employees);
		
		return tempEmployeeBeans;
	}
}
