

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ResProvAdd
 */
public class ResProvAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResProvAdd() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String cid = request.getParameter("course_id");
		String resid = request.getParameter("res_id");
		String provid = request.getParameter("prov_id");
		ConfigManager cm = new ConfigManager(this);
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser, cm.agg_dbpass);
		agg_db.openConnection();
		boolean outcome = agg_db.addResProvider(resid,provid);	
		agg_db.closeConnection();
		String output = "{outcome: \""+outcome+"\", courseId: \""+cid+"\", resId: \""+resid+"\", provId: \""+provid+"\"}";
		out.print(output);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
