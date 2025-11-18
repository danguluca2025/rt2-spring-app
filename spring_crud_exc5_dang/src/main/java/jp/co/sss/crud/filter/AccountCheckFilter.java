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
				requestURL.endsWith("/list")) {
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
		 * 
		 * switch (checkAuthority)  上記の「loginUser」変数を条件分岐で判定
		 * case 1(authority==1)：一般ユーザログインの場合：
		 * /listより先に進めたい（登録・変更・削除）ときにアクセス制限（ブラウザーのURLバーによるアクセスも含む）
		 * /spring_crud/にリダイレクト。@Controllerにある return "/";と同様
		 * session.invalidate()メソッドでログインセッション終了
		 * 
		 * case 2(authority==2)：管理者ログイン
		 * アクセス制限なし
		 * */
		HttpSession session = request.getSession();
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		Integer checkAuthority = (Integer) loginUser.getAuthority();
		
		switch (checkAuthority) {
		case 1:
			response.sendRedirect("/spring_crud/");
			session.invalidate();
			break;
		case 2:
			chain.doFilter(request, response);
			break;
		}
		return;
		
		
	}
}
