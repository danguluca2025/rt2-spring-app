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
		 * .htmlファイルや.cssファイルなど許可しないと表示自体できなくなる（真っ白画面）
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
		 * */
		if (requestURL.endsWith("/") ||
				requestURL.endsWith("/login") ||
				requestURL.endsWith("/logout")||
				requestURL.endsWith("/list")||
				requestURL.endsWith("/list/empName")||
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
		 * checkAuthority  ユーザ丸ごとの情報からゲッターでauthority情報を取得
		 */
		HttpSession session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		Integer checkAuthority = (Integer) loginUser.getAuthority();
		
		/* ログイン中のempId情報とアクセスする（しようとしている）URLのパラメータ部分を比較するため
		 * のつづき
		 * loginUserからログイン中のユーザId(empId)を取得する
		 * キャストは不要
		 * */
		Integer checkEmpId = loginUser.getEmpId();
		
		/*
		 * switch (checkAuthority)  上記の「loginUser」変数を条件分岐で判定
		 * 
		 * case 1(authority==1)：一般ユーザログインの場合：
		 * アクセスしようとするURLの末尾（empIdパラメータ）　と　取得したログイン中のユーザId
		 * ネストif文でさらに条件分け：
		 * empIdInt == checkEmpId
		 * 
		 * [true]
		 * 一致する場合 + アクセスしようとするURLは更新処理である
		 * → フィルタに通してもらう
		 * returnで処理終了する
		 * 
		 * [false]
		 * アクセスしようとするURL：
		 * ブラウザーのURLバーによる「登録」・「変更（別ユーザ）」・「削除」
		 * のアクセスを制限
		 * /spring_crud/にリダイレクト。@Controllerにある return "/";と同様
		 * session.invalidate()メソッドでログインセッション終了
		 * returnで処理終了する
		 * 
		 * case 2(authority==2)：管理者ログイン
		 * アクセス制限なし
		 * */
		switch (checkAuthority) {
		case 1:
			/* ログイン中のempId情報とアクセスする（しようとしている）URLのパラメータ部分を比較するため
			 * 
			 * empIdパラメータを取得（/update/input?empId=1）の「=」の後ろの数値
			 * → 文字列型なので String型のempIdStringで取得する
			 * empIdString = "1"
			 * このあとloginUser情報からログインしたユーザのempId値（Integer型）と比較するのでキャスト
			 * empIdInt = 1
			 * */
			String empIdString = request.getParameter("empId");
			Integer empIdInt = Integer.parseInt(empIdString);
			if(requestURL.contains("/update/input")&&(empIdInt == checkEmpId)) {
				chain.doFilter(request, response);
				return;
				
			}else {
				String redirectWithAlert = "/spring_crud/" + "?showAlert=true";
				response.sendRedirect(redirectWithAlert);
				session.invalidate();
			}
//			response.sendRedirect("/spring_crud/");
//			session.invalidate();
			break;
		case 2:
			chain.doFilter(request, response);
			break;
		}
		return;
	}
}
