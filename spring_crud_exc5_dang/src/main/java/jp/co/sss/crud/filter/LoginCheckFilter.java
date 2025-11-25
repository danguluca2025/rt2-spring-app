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

		//セッション情報を取得(null)
		HttpSession session = request.getSession(false); 
		
		//loginUserを一旦nullで初期化
		EmployeeBean loginUser = null;

		/* 
		 * 1.if(session != null){}
		 * sessionがnullでないことを確認してから属性を取得する
		 * セッション情報はnullではない場合、セッションのゲッターでloginUserを再度取得して
		 * 最初nullになっているBean型のloginUserに返却
		 * 
		 * 2.if(loginUser != null && ...){}
		 * ログイン中 + アクセスしようとするURLはログイン画面(/spring_crud/) 
		 * あるいは (/login)(POSTメソッドで普段URLからアクセスできないが念のため入れておく)である場合
		 * 社員一覧画面にリダイレクトし、処理を終了する(return)
		 * 
		 * 3.if(requestURL.endsWith("/spring_crud/") ||...){}
		 * フィルタ除外対象のURLを許可
		 * ・("/spring_crud/")
		 * ・("/login")
		 * ・("/login")
		 * 処理を終了する(return)
		 * 
		 * 4.if (loginUser == null){}
		 * 未ログイン時の処理
		 * ログインしない状態で社員一覧画面などにアクセスしようとする場合にアクセス制限
		 * 処理を終了する(return)
		 * 
		 * 5. (上のif文に全て当てはまらない場合)
		 * ログイン済みユーザーの通常アクセスを許可
		 * */
		if (session != null) {
		    loginUser = (EmployeeBean) session.getAttribute("loginUser");
		}

		if (loginUser != null && 
		    (requestURL.endsWith("/spring_crud/") || requestURL.endsWith("/login"))) {
		    response.sendRedirect("/spring_crud/list");
		    return;
		}

		if (requestURL.endsWith("/spring_crud/") ||
		        requestURL.endsWith("/login") ||
		        requestURL.endsWith("/login")) {
		    chain.doFilter(request, response);
		    return;
		}

		if (loginUser == null) {
		    response.sendRedirect("/spring_crud/");
		    return;
		} 
		
		chain.doFilter(request, response);
	}
}
