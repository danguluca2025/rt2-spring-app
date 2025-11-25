package jp.co.sss.crud.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.crud.bean.EmployeeBean;

@Component
public class AccountCheckFilter extends HttpFilter {
	@Override
	public void doFilter(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		//URLをリクエストし取得する
		String requestURL = request.getRequestURI();
		
		/*静的情報を許可
		 * .htmlファイルや.cssファイルなど
		 * 許可しないとアクセス不可
		 * */
		if (requestURL.indexOf("/html/") != -1 ||
				requestURL.indexOf("/css/") != -1 ||
				requestURL.indexOf("/img/") != -1 ||
				requestURL.indexOf("/js/") != -1) {
			chain.doFilter(request, response);
			return;
		}

		/*ログイン関係処理のフィルタ除外
		 * ("/")トップ画面：除外しないとログインできない
		 * ("/login")ログイン成功画面：ログインしても進めない（/listに移動できない）
		 * ("/logout")ログアウト処理をできない（invalidate()メソッドを実行せず、ログアウトできない）
		 * ("/list/empName")社員名検索
		 * ("/list/deptId")部署名検索
		 * */
		if (requestURL.endsWith("/") ||
				requestURL.endsWith("/login") ||
				requestURL.endsWith("/logout") ||
				requestURL.endsWith("/list") ||
				requestURL.endsWith("/list/empName") ||
				requestURL.endsWith("/list/deptId")) {
			chain.doFilter(request, response);
			return;
		}

		/* session セッション情報を取得
		 * 
		 * loginUser  session内から"loginUser"属性を持つ情報を取得。
		 * EmployeeBean  loginUser ログイン時に入力したユーザ情報によって
		 * そのユーザ丸ごとの情報を一旦取得するため型はEmployeeBeanとなる
		 * 
		 * checkAuthority  ユーザ情報からゲッターでauthority情報を取得
		 */
		HttpSession session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		Integer checkAuthority = (Integer) loginUser.getAuthority();

		/* ログイン中のempId情報とアクセスする（しようとしている）URLのパラメータ部分を比較するため
		 * のつづき
		 * loginUserからログイン中のユーザId(empId)を取得する
		 * */
		Integer checkEmpId = loginUser.getEmpId();

		
		/*
		 * ログイン中ユーザの権限情報を確認
		 * case 1(一般ユーザ)：
		 * ＊ログイン・ログアウト・ログイン画面
		 * ＊（制限あり）社員一覧・社員名検索・部署名検索
		 * ＊自分自身のアップデート画面
		 * 以外すべてのアクセスはアクセス不可
		 * 
		 * case 2(管理者):アクセスの制限なし
		 * 
		 * */
		switch (checkAuthority) {
		case 1:
			/*
			 * authority == 1
			 * 
			 * if文
			 * 1.アップデート画面にアクセスする場合
			 * ・URLからパラメータを取得(empId)、String型のempIdStringに代入
			 * 		(2.ネストif)・empIdStringはnullではないこと ＋ 空
			 * 		Integerに変換（Integer.valueOf）
			 * 			(3.さらにネストif)・ログインユーザのempId情報(checkEmpId)
			 * 			とempIdIntは一致するかどうかチェック
			 * 				一致する場合はアクセス付与し、ここで処理終了(return;)
			 * 				一致しない場合はアクセス不可にし、アラート＋ログイン画面にリダイレクト
			 * 				＋ログインセッションを終了
			 * 1.のelse(アップデート画面以外にアクセスする)：アクセスを付与する
			 * 
			 * */
			if (requestURL.contains("/update/input")) {
				String empIdString = request.getParameter("empId");

				if (empIdString != null && !empIdString.isEmpty()) {
					Integer empIdInt = Integer.valueOf(empIdString);
					if (empIdInt.equals(checkEmpId)) { 
						chain.doFilter(request, response);
						return;
					} else {
						String redirectWithAlert = "/spring_crud/" + "?showAlert=true";
						response.sendRedirect(redirectWithAlert);
						session.invalidate();
					}
				}

			} else {
				chain.doFilter(request, response);
				return;
			}
			break;
			
		/*
		 * authority == 2
		 * 管理者ログイン：アクセスの制限なし
		 * */
		case 2:
			chain.doFilter(request, response);
			break;
		}
		return;
	}
}
