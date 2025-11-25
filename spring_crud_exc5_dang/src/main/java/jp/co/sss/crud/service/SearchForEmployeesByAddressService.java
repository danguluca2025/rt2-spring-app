package jp.co.sss.crud.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	/**
	 * [管理者専用]
	 * 従業員住所による部分一致検索を実行します。
	 * 
	 * 指定された検索文字列を含む住所を持つ従業員を検索し
	 * BeanManagerを使用してEmployeeBeanリストに変換して返却します。
	 * 検索は大文字小文字を区別し、部分一致で行われます。
	 *          
	 * @param addressList 住所。複数選択可能
	 * @return 検索文字列を含む住所を持つEmployeeBeanリスト（従業員ID昇順）。
	 * 			該当する従業員が存在しない場合は空のリストを返却
	 */
	public List<EmployeeBean> execute(List<String> addressList) {
		List<Employee> employees = employeeRepository.findByAddressInOrderByEmpId(addressList);
		List<EmployeeBean> tempEmployeeBeans = BeanManager.copyEntityListToBeanList(employees);

		return tempEmployeeBeans;
	}
	
	/**
	 * 従業員住所（重複除く）すべてを検索
	 * 
	 * 全件検索処理をし、alEmployeesリストに返却。
	 * listビューに出さないのでBeanに渡さない。
	 * 
	 * @return 最後はallEmployeesリストから重複されている住所を除き、返却
	 */
	public Set<String> findAllUniqueAddresses() {
		List<Employee> allEmployees = employeeRepository.findAll();
		return allEmployees.stream().map(Employee::getAddress).collect(Collectors.toSet());
	}

}
