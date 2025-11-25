package jp.co.sss.crud.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.crud.bean.EmployeeBean;
import jp.co.sss.crud.service.SearchAllEmployeesService;
import jp.co.sss.crud.service.SearchForEmployeesByAddressService;
import jp.co.sss.crud.service.SearchForEmployeesByBirthdayService;
import jp.co.sss.crud.service.SearchForEmployeesByDepartmentService;
import jp.co.sss.crud.service.SearchForEmployeesByEmpNameService;

@Controller
public class ListController {

	@Autowired
	SearchAllEmployeesService searchAllEmployeesService;

	@Autowired
	SearchForEmployeesByEmpNameService searchForEmployeesByEmpNameService;

	@Autowired
	SearchForEmployeesByDepartmentService searchForEmployeesByDepartmentService;

	@Autowired
	SearchForEmployeesByAddressService searchForEmployeesByAddressService;

	@Autowired
	SearchForEmployeesByBirthdayService searchForEmployeesByBirthdayService;

	/**
	 * 社員情報を全件検索した結果を出力
	 * [社員一覧リスト用]
	 * 
	 * リスト初期化（null）
	 * リクエストのゲッター(getSession)でセッション情報取得
	 * Beanオブジェクト生成し、取得したセッション情報を代入
	 * authority情報（ユーザ権限）を取得
	 * 
	 * switch文：場合分け
	 * case 1（一般ユーザ）:一般ユーザ全員のレコードを検索し、ビューに表示
	 * case 2（管理者）:従業員全員のレコードを検索し、ビューに表示
	 * 
	 * [サイドバー用]住所検索の選択肢は検索語も常時表示し、検索対象を選択済として表示
	 * 住所による従業員検索をし、重複を除いた住所情報をSet<String>型オブジェクトに代入し、モデルでビューに返却
	 * 
	 * 検索対象を選択済として表示
	 * 
	 * @param model モデル
	 * @param session セッション
	 * @param request リクエスト
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public String findAll(Model model, HttpSession session, HttpServletRequest request) {

		List<EmployeeBean> allEmployeeList = null;

		session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		Integer checkAuthority = (Integer) loginUser.getAuthority();

		switch (checkAuthority) {
		case 1:
			allEmployeeList = searchAllEmployeesService.executeGeneral();
			model.addAttribute("employees", allEmployeeList);
			break;
		case 2:
			// Authority 2: Admin/Manager
			allEmployeeList = searchAllEmployeesService.execute();
			model.addAttribute("employees", allEmployeeList);
			break;
		}

		Set<String> uniqueAddresses = searchForEmployeesByAddressService.findAllUniqueAddresses();
		model.addAttribute("uniqueAddresses", uniqueAddresses);

		model.addAttribute("selectedAddresses", new ArrayList<String>());

		return "list/list";
	}

	/**
	 * 社員情報を社員名検索した結果を出力
	 * case 1（一般ユーザ）：一般ユーザ同士のみ検索可能
	 * case 2（管理者）：当てはまるレコードをすべて検索可能
	 * 
	 * @param empName 検索対象の社員名
	 * @param model モデル
	 * @param session セッション情報を取得するため
	 * @param request HttpServletRequestのgetSessionメソッドを呼び出すため
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list/empName", method = RequestMethod.GET)
	public String findByEmpName(String empName, Model model,
			HttpSession session, HttpServletRequest request) {

		List<EmployeeBean> searchByEmpNameList = null;

		session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		Integer checkAuthority = (Integer) loginUser.getAuthority();

		switch (checkAuthority) {
		case 1:
			searchByEmpNameList = searchForEmployeesByEmpNameService.executeGeneral(empName);
			model.addAttribute("employees", searchByEmpNameList);
			break;
		case 2:
			searchByEmpNameList = searchForEmployeesByEmpNameService.execute(empName);
			model.addAttribute("employees", searchByEmpNameList);
			break;
		}
		return "list/list";
	}

	/**
	 * 社員情報を部署ID検索した結果を出力
	 * 一般ユーザが管理者検索してもビューに表示しない
	 * 
	 * case 1（一般ユーザ）：一般ユーザ同士のみ検索可能
	 * case 2（管理者）：当てはまるレコードをすべて検索可能
	 * 
	 * @param deptId　検索対象の部署ID
	 * @param model モデル
	 * @param session
	 * @param request
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list/deptId", method = RequestMethod.GET)
	public String findByDeptId(Integer deptId, Model model,
			HttpSession session, HttpServletRequest request) {

		List<EmployeeBean> searchByDepartmentList = null;

		session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		Integer checkAuthority = (Integer) loginUser.getAuthority();

		switch (checkAuthority) {
		case 1:
			searchByDepartmentList = searchForEmployeesByDepartmentService.executeGeneral(deptId);
			model.addAttribute("employees", searchByDepartmentList);
			break;
		case 2:
			searchByDepartmentList = searchForEmployeesByDepartmentService.execute(deptId);
			model.addAttribute("employees", searchByDepartmentList);
			break;
		}
		return "list/list";
	}

	/**
	 * [管理者専用]
	 * 社員情報を住所検索した結果を出力
	 * 
	 * 住所検索する際にチェックする選択肢は一つも選択されない場合、
	 * NullPointerExceptionを防ぐため、nullではなく空のArrayListを返却する
	 * nullはだめ、空のリストで初期化してOK
	 * 
	 * [従業員一覧ビュー]
	 * 選択された住所情報で当てはまる従業員情報を
	 * EmployeeBeanリスト型searchByAdressListに返却し
	 * 遷移ビューに表示
	 * 
	 * [サイドバーの住所選択肢]
	 * 同じサービスにあるメソッドを実行し
	 * 重複除き住所リストを取得し
	 * Set<String>型uniqueAddressesリストに返却
	 * モデルでサイドバーに表示
	 * 
	 * [検索処理を実行後]
	 * 実行された検索処理に選らばれた選択肢をチェック済状態で取り出し、
	 * 重複除き全件を表示
	 * 
	 * @param addressList
	 * @param model
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list/address", method = RequestMethod.GET)
	public String findByAddress(
			@RequestParam(name = "address", required = false) 
			List<String> addressList, Model model) {
		
		if (addressList == null) {
			addressList = new ArrayList<>();
		}

		List<EmployeeBean> searchByAdressList = searchForEmployeesByAddressService.execute(addressList);
		model.addAttribute("employees", searchByAdressList);

		Set<String> uniqueAddresses = searchForEmployeesByAddressService.findAllUniqueAddresses();
		model.addAttribute("uniqueAddresses", uniqueAddresses);

		model.addAttribute("selectedAddresses", addressList);

		return "list/list";
	}

	/**
	 * [管理者専用]
	 * 生年月日の範囲検索
	 * 
	 * Bean型リストを初期化（null）
	 * サービスのメソッドを実行し、Beanリストに返却
	 * モデルでニューに表示
	 * 
	 * @param birthday1 開始日
	 * @param birthday2 終了日
	 * @param model モデル
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list/birthday", method = RequestMethod.GET)
	public String findByBirthday(Date birthday1, Date birthday2, Model model) {
		List<EmployeeBean> searchByBirthdayList = null;

		searchByBirthdayList = searchForEmployeesByBirthdayService.execute(birthday1, birthday2);

		model.addAttribute("employees", searchByBirthdayList);
		return "list/list";
	}

}
