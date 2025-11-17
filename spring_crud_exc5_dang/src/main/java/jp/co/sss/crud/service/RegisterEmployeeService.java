package jp.co.sss.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.form.EmployeeForm;
import jp.co.sss.crud.repository.EmployeeRepository;
import jp.co.sss.crud.util.BeanManager;

/**
 * 従業員登録サービスクラス。
 * フォームから入力された従業員情報をデータベースに新規登録します。
 * EmployeeFormからEmployeeエンティティへの変換処理も含まれます。
 * 
 * @author SystemShared Co., Ltd.
 * @version 1.0
 * @since 1.0
 */
@Service
public class RegisterEmployeeService {

	/**
	 * 従業員データアクセス用リポジトリ。
	 * Spring DIによって自動注入されます。
	 */
	//TODO ここに記述
	@Autowired
	EmployeeRepository employeeRepository;

	/**
	 * 新規従業員を登録します。
	 * 
	 * EmployeeFormに格納された従業員情報をEmployeeエンティティに変換し、
	 * データベースに新規レコードとして保存します。
	 * 変換処理はBeanManager.copyFormToEntityメソッドを使用して行います。
	 * 
	 * @param employeeForm 登録する従業員情報を格納したフォームオブジェクト
	 */
	//TODO ここに記述
	public void execute(EmployeeForm employeeForm) {
		/*教科書p212のように、Employeeオブジェクト生成しBeanUtilsでコピーする方法なら最後まで書かなくてはならない
		 * Date formatDateや
		 * Department情報まで
		*BeanManagerクラスのcopyFormToEntityメソッドは代わりにやってくれるので
		*必要なのはemployeeオブジェクトにそれぞれの情報を値代入し、リポジトリのsaveメソッドで登録する
		*左辺は登録する際にあってもなくても良いが、更新などする際に必要なのでキープ
		**/
		Employee employee = BeanManager.copyFormToEntity(employeeForm);
		employee = employeeRepository.save(employee);
	}

}