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
	
	/**
	 * [管理者専用]
	 * 従業員生年月日による部分一致検索を実行します。
	 * 
	 * 指定された検索文字列を含む生年月日を持つ従業員を検索し
	 * BeanManagerを使用してEmployeeBeanリストに変換して返却します。
	 * 部分一致で行われます。
	 * 
	 * @param birthday1 開始日
	 * @param birthday2 終了日
	 * @return 検索文字列を含む住所を持つEmployeeBeanリスト（従業員ID昇順）。
	 * 			該当する従業員が存在しない場合は空のリストを返却
	 */
	public List<EmployeeBean> execute(Date birthday1, Date birthday2){
		List<Employee> employees = employeeRepository.findByBirthday(birthday1,birthday2);
		List<EmployeeBean> tempEmployeeBeans = BeanManager.copyEntityListToBeanList(employees);
		
		return tempEmployeeBeans;
	}
}
