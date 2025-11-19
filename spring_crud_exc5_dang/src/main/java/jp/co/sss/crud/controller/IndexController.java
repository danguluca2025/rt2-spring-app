package jp.co.sss.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.crud.bean.LoginResultBean;
import jp.co.sss.crud.form.LoginForm;
import jp.co.sss.crud.service.LoginService;

@Controller
public class IndexController {

	@Autowired
	LoginService loginService;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String index(@ModelAttribute LoginForm loginForm) {
		return "index";
	}

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, Model model,
			HttpSession sesson) {

		String path = "index";

		//TODO LoginServiceが完成後にコメントを外す
		LoginResultBean loginResultBean = loginService.execute(loginForm);
		//入力チェック＞エラーなったらログインに戻る(return文で処理終了)
		if (result.hasErrors()) {
			return path;
		}

		/*入力チェックする際にエラーがなかったら
		 *ログインバリデーションチェック（データベースに存在するかどうか）
		 *の処理を実行する
		 *
		 *〇login判定「isLoginメソッド」はtrue（入力したempIdとempPassが存在し一致する）の場合
		 *empIdとempPassに当てはまる情報を取得し
		 *セッションスコープでloginUser属性にそういった情報を送る
		 *list.htmlビューに画面遷移
		 */
		if (loginResultBean.isLogin()) {
			sesson.setAttribute("loginUser", loginResultBean.getLoginUser());
			path = "redirect:/list";
			/*
			 *✖empIdとempPassは存在しないか一致しないの場合は
			 *エラーメッセージを出力し
			 *index（ログイン画面）ビューにリダイレクトし
			 *エラーメッセージを表示
			 */
		} else {
			model.addAttribute("errMessage", loginResultBean.getErrorMsg());
		}

		return path;

	}

	@RequestMapping(path = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		
		session.invalidate();
		return "redirect:/";
	}

}
