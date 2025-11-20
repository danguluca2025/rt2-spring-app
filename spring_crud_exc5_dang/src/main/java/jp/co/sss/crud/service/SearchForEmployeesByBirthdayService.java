package jp.co.sss.crud.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.crud.bean.EmployeeBean;
import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.repository.EmployeeRepository;
import jp.co.sss.crud.util.BeanManager;

@Service
public class SearchForEmployeesByBirthdayService {
	@Autowired
	EmployeeRepository employeeRepository;
	
	public List<EmployeeBean> execute(Date birthday1, Date birthday2){
		List<Employee> employees = employeeRepository.findByBirthday(birthday1,birthday2);
		List<EmployeeBean> tempEmployeeBeans = BeanManager.copyEntityListToBeanList(employees);
		
		return tempEmployeeBeans;
	}
}
