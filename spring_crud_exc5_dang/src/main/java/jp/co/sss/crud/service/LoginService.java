package jp.co.sss.crud.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.sss.crud.bean.EmployeeBean;
import jp.co.sss.crud.bean.LoginResultBean;
import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.form.LoginForm;
import jp.co.sss.crud.repository.EmployeeRepository;

/**
 * ログイン認証処理を行うサービスクラス。
 * 従業員IDとパスワードを用いて認証を行い、ログイン結果を返却します。
 * 認証が成功した場合は従業員情報を含むログイン結果を、
 * 失敗した場合はエラーメッセージを含むログイン結果を返却します。
 * 
 * @author SystemShared Co., Ltd.
 * @version 1.0
 * @since 1.0
 */
@Service
public class LoginService {

	/**
	 * 従業員データアクセス用リポジトリ。
	 * Spring DIによって自動注入されます。
	 */
	//TODO ここに記述
	@Autowired
	EmployeeRepository employeeRepository;

	/**
	 * ログイン認証処理を実行します。
	 * 
	 * 入力された従業員IDとパスワードを用いてデータベース検索を行い、
	 * 該当する従業員情報が存在するかを確認します。
	 * <ul>
	 * <li>認証成功：従業員情報を含むLoginResultBeanを返却</li>
	 * <li>認証失敗：エラーメッセージを含むLoginResultBeanを返却</li>
	 * </ul>
	 * 
	 * @param loginForm ログイン情報（従業員ID、パスワード）を格納したフォームオブジェクト
	 * @return LoginResultBean ログイン認証結果
	 *         <ul>
	 *         <li>成功時：LoginResultBean.succeedLogin(従業員エンティティ)の結果</li>
	 *         <li>失敗時：LoginResultBean.failLogin(エラーメッセージ)の結果</li>
	 *         </ul>
	 */
	//TODO ここに記述
	public LoginResultBean execute(LoginForm loginForm) {
		Integer id = loginForm.getEmpId();
		String pass = loginForm.getEmpPass();
		Employee employee = employeeRepository.findByEmpIdAndEmpPass(id, pass);
		LoginResultBean loginBean = null;
		if(employee!=null) {
			EmployeeBean empBean = new EmployeeBean();
			BeanUtils.copyProperties(employee, empBean);
			empBean.setDeptId(employee.getDepartment().getDeptId());
			loginBean = LoginResultBean.succeedLogin(empBean);
		}else {
			String errMsg = "社員ID、またはパスワードが間違っています。";
			loginBean = LoginResultBean.failLogin(errMsg);
		}
		return loginBean;
	}

}