package jp.co.sss.crud.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.crud.bean.EmployeeBean;
import jp.co.sss.crud.form.EmployeeForm;
import jp.co.sss.crud.service.SearchForEmployeesByEmpIdService;
import jp.co.sss.crud.service.UpdateEmployeeService;
import jp.co.sss.crud.util.BeanManager;

@Controller
public class UpdateController {

	@Autowired
	SearchForEmployeesByEmpIdService searchForEmployeesByEmpIdService;

	@Autowired
	UpdateEmployeeService updateEmployeeService;

	/**
	 * 社員情報の変更内容入力画面を出力
	 * 
	 * list.html「変更ボタン」から画面遷移
	 * 	@param empId　社員IDも含めて送る
	 *            
	 * @param model
	 *            モデル
	 * @return 遷移先のビュー
	 * @throws ParseException 
	 */
	@RequestMapping(path = "/update/input", method = RequestMethod.GET)
	public String inputUpdate(Integer empId,
			@ModelAttribute EmployeeForm employeeForm, Model model) {
		//list.htmlからempId情報を受け取る
		//EmployeeBeanのオブジェクトを生成し、初期値はnullとする
		EmployeeBean employeeBean = null;

		/*主キー検索の検索クラスのメソッドに上のempIdを引数として渡し、メソッドを実行
		 * 実行結果を生成したemployeeBeanオブジェクトに代入
		 * 
		 * */
		//TODO SearchForEmployeesByEmpIdService完成後にコメントを外す
		employeeBean = searchForEmployeesByEmpIdService.execute(empId);

		/*BeanManagerのcopyBeantoFormメソッドで上記取得した情報を
		 * @ModelAttributeのフォームスコープemployeeFormに値を渡し
		 * リクエストスコープでビュー「update_input」に渡す
		 * */
		employeeForm = BeanManager.copyBeanToForm(employeeBean);
		model.addAttribute("employeeForm", employeeForm);

		return "update/update_input";
	}

	/**
	 * 社員情報の変更確認画面を出力
	 *
	 *hidden属性でフォームスコープの情報を取得
	 *
	 * @param employeeForm
	 *            変更対象の社員情報
	 * @param model
	 *            モデル
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/update/check", method = RequestMethod.POST)
	public String checkUpdate(@Valid @ModelAttribute EmployeeForm employeeForm, BindingResult result,
			Model model, HttpSession session, HttpServletRequest request) {

		if(result.hasErrors()) {
			return "update/update_input";
		}else {
			return "update/update_check";
		}

	}

	/**
	 * 変更内容入力画面に戻る
	 * 「戻る」ボタンを押すと繊維元のビューに戻る(update_input)
	 *
	 * @param employeeForm 変更対象の社員情報
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/update/back", method = RequestMethod.POST)
	public String backInputUpdate(@ModelAttribute EmployeeForm employeeForm) {
		return "update/update_input";
	}

	/**
	 * 社員情報の変更
	 *hidden属性で情報を引き継がれ（引数として）
	 *Updateサービスのexecuteメソッドを実行
	 * @param employeeForm
	 *            変更対象の社員情報
	 * @return 暗黙的に遷移先のビューへ
	 */
	@RequestMapping(path = "/update/complete", method = RequestMethod.POST)
	public String completeUpdate(EmployeeForm employeeForm) {

		//TODO UpdateEmployeeService完成後にコメントを外す
		updateEmployeeService.execute(employeeForm);

		return "redirect:/update/complete";
	}

	/**
	 * 完了画面の表示
	 * 「完了しましたよ」のようなお知らせという役割をする
	 * 
	 * @return 遷移先ビュー
	 */
	@RequestMapping(path = "/update/complete", method = RequestMethod.GET)
	public String completeUpdate() {
		return "update/update_complete";
	}

}
