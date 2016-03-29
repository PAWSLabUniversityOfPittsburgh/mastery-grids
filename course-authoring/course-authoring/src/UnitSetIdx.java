

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UnitSetIdx
 */
public class UnitSetIdx extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UnitSetIdx() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		int idx = Integer.parseInt(request.getParameter("idx"));
		String cid = request.getParameter("course_id");
		String uid = request.getParameter("unit_id");
		int idxDelta = Integer.parseInt(request.getParameter("idxDelta"));
		ConfigManager cm = new ConfigManager(this); 
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser, cm.agg_dbpass);
		agg_db.openConnection();
		boolean outcome = agg_db.swapUnit(cid,uid,idx,idxDelta);	
		agg_db.closeConnection();
		String output = "{outcome: \""+outcome+"\", courseId: \""+cid+"\", unitId: \""+uid+"\", idx: \""+idx+"\", idxDelta: \""+idxDelta+"\"}";
		out.print(output);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
