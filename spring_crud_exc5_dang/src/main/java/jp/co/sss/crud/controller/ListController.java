package jp.co.sss.crud.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	 *
	 * @param model モデル
	 * @return 遷移先のビュー
	 * @throws ParseException 
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
			allEmployeeList = searchAllEmployeesService.execute();
			model.addAttribute("employees", allEmployeeList);
			break;
		}
		return "list/list";
	}

	/**
	 * 社員情報を社員名検索した結果を出力
	 *
	 * @param empName 検索対象の社員名
	 * @param model モデル
	 * @return 遷移先のビュー
	 * @throws ParseException 
	 */
	@RequestMapping(path = "/list/empName", method = RequestMethod.GET)
	public String findByEmpName(String empName, Model model) {

		List<EmployeeBean> searchByEmpNameList = null;

		//TODO SearchForEmployeesByEmpNameService完成後にコメントを外す
		searchByEmpNameList = searchForEmployeesByEmpNameService.execute(empName);

		model.addAttribute("employees", searchByEmpNameList);
		return "list/list";
	}

	/**
	 * 社員情報を部署ID検索した結果を出力
	 *
	 * @param deptId 検索対象の部署ID
	 * @param model モデル
	 * @return 選先のビュー
	 * @throws ParseException 
	 */
	@RequestMapping(path = "/list/deptId", method = RequestMethod.GET)
	public String findByDeptId(Integer deptId, Model model) {

		List<EmployeeBean> searchByDepartmentList = null;

		//TODO SearchForEmployeesByDepartmentService完成後にコメントを外す
		searchByDepartmentList = searchForEmployeesByDepartmentService.execute(deptId);

		model.addAttribute("employees", searchByDepartmentList);
		return "list/list";
	}
	
	@GetMapping("/list/address")
	public String findByAddress(String address, Model model) {
		List<EmployeeBean> searchByAdressList = null;

		//TODO SearchForEmployeesByDepartmentService完成後にコメントを外す
		searchByAdressList = searchForEmployeesByAddressService.execute(address);

		model.addAttribute("employees", searchByAdressList);
		return "list/list";
	}
	@GetMapping("/list/birthday")
	public String findByBirthday(
			Date birthday1, 
			Date birthday2, Model model) {
		List<EmployeeBean> searchByBirthdayList = null;
		
		//TODO SearchForEmployeesByDepartmentService完成後にコメントを外す
		searchByBirthdayList = searchForEmployeesByBirthdayService.execute(birthday1,birthday2);
		
		model.addAttribute("employees", searchByBirthdayList);
		return "list/list";
	}
	
}
