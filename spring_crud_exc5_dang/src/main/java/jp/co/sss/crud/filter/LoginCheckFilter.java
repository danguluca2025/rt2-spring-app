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
public class LoginCheckFilter extends HttpFilter {
	@Override
	public void doFilter(
			HttpServletRequest request,
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
				requestURL.endsWith("/login")||
				requestURL.endsWith("/logout")) {
			chain.doFilter(request, response);
			return;
		}
		
		//セッション情報を取得
		HttpSession session = request.getSession();
		//取得した情報から"loginUser"属性が持つ情報をEmployeeBean型の「loginUser」変数に取り出す
		EmployeeBean loginUser = (EmployeeBean) session.getAttribute("loginUser");
		
		//上記の「loginUser」変数を条件分岐で判定
		if (loginUser == null) {
			/*nullの場合
			 *@Controllerクラスではないのでreturn "/"できないので 
			 *代わりにresponseクラスのsendRedirectを使用しURLバインドし移動させる
			 * */
			response.sendRedirect("/spring_crud/");
			return;
		} else {
			chain.doFilter(request, response);
			return;
		}
	}
}
