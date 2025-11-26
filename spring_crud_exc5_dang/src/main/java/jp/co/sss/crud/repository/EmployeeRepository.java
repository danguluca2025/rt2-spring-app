package jp.co.sss.crud.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.sss.crud.entity.Department;
import jp.co.sss.crud.entity.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Integer>{
	/**
	 * [ログイン機能用]
	 * 社員ID％パスワード検索
	 * @param empId
	 * @param empPass
	 * @return 部分一致内容をlistビューに反映
	 */
	Employee findByEmpIdAndEmpPass(Integer empId,String empPass);
	
	
	/**
	 * [管理者用]
	 * 社員一覧、全件検索
	 * 主キーの昇順並べ
	 * @return 部分一致内容をlistビューに反映
	 */
	List<Employee> findAllByOrderByEmpId();
	
	/**
	 * [一般ユーザ制限用]
	 * 
	 * 全件検索にauthority==1(一般ユーザ)の従業員全員のレコードを取り出す
	 * 
	 * 主キー昇順並べ
	 * @param authority 権限
	 * @return 部分一致内容をlistビューに反映
	 */
	List<Employee> findByAuthorityOrderByEmpId(Integer authority);
	
	
	/**
	 * [管理者用]
	 * 社員名検索
	 * @param empName 社員名
	 * @return 部分一致内容をlistビューに反映
	 */
	List<Employee> findByEmpNameContainingOrderByEmpId(String empName);
	
	/**
	 * [一般ユーザ用]
	 * 社員名検索
	 * @Query でバインドし、あいまい検索を実行
	 * 
	 * 一般ユーザのみ検索、主キー昇順並べ
	 * 
	 * @param empName
	 * @return 部分一致内容をlistビューに反映
	 */
	@Query("SELECT e FROM Employee e WHERE e.empName LIKE %:empName% AND e.authority = 1 ORDER BY e.empId")
	List<Employee> findByEmpNameGeneral(String empName);

	
	/**
	 * [管理者用]
	 * 部署ID検索
	 * 
	 * [引数の説明]
	 * Employeeエンティティには部署情報がないため
	 * （Department departmentから直接deptIdを取得できない）
	 * 引数をDepartment departmentと記述
	 * 
	 * @param department
	 * @return 部分一致内容をlistビューに反映
	 */
	List<Employee> findByDepartmentOrderByEmpId(Department department);
	
	/**
	 * [一般ユーザ用]
	 * 部署名検索
	 * 
	 * 一般ユーザのみ検索、主キー昇順並べ
	 * @param department
	 * @param authority 場合分け
	 * @return 部分一致内容をlistビューに反映
	 */
	List<Employee> findByDepartmentAndAuthorityOrderByEmpId(Department department,Integer authority);
	
	/**
	 * [管理者-専用]
	 * 住所検索
	 * 複数選択検索
	 * 
	 * @param addressList 住所リスト
	 * @return 部分一致内容をlistビューに反映
	 */
	List<Employee> findByAddressInOrderByEmpId(List<String> addressList);
	
	/**
	 * [管理者-専用]
	 * 生年月日の範囲検索
	 * 開始日＞終了日の全て当てはまる従業員レコードを取り出す
	 * 
	 * @param birthday1 開始日
	 * @param birthday2 終了日
	 * @return 部分一致内容をlistビューに反映
	 */
	@Query("SELECT e FROM Employee e WHERE e.birthday BETWEEN :birthday1 AND :birthday2 ORDER BY e.empId")
	List<Employee> findByBirthday(Date birthday1, Date birthday2);
}
