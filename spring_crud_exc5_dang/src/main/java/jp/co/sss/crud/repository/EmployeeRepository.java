package jp.co.sss.crud.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.sss.crud.entity.Department;
import jp.co.sss.crud.entity.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Integer>{
	/**
	 * ログイン機能用
	 * 社員ID％パスワード検索
	 * @param empId
	 * @param empPass
	 * @return
	 */
	Employee findByEmpIdAndEmpPass(Integer empId,String empPass);
	
	
	/**
	 * 全件検索、主キーの昇順並べ
	 * @return
	 */
	List<Employee> findAllByOrderByEmpIdAsc();
	
	/**
	 * 一般ユーザ用社員一覧リスト
	 * 一般ユーザのみ検索、主キー昇順並べ
	 * @param authority
	 * @return
	 */
	List<Employee> findByAuthorityOrderByEmpIdAsc(Integer authority);
	
	
	/**社員名検索
	 * @param empName
	 * @return
	 */
	List<Employee> findByEmpNameContaining(String empName);

	
	/**部署ID検索
	 * Employeeエンティティには部署情報がないため
	 * （Department departmentから直接deptIdを取得できない）
	 * 一旦引数をDepartment departmentと記述
	 * @param department
	 * @return
	 */
	List<Employee> findByDepartment(Department department);
	
	List<Employee> findByAddress(String address);
	
	@Query("SELECT e FROM Employee e WHERE e.birthday BETWEEN :birthday1 AND :birthday2")
	List<Employee> findByBirthday(Date birthday1, Date birthday2);

}
