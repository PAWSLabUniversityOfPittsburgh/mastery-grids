

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UnitAdd
 */
public class UnitAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UnitAdd() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// $call("GET", "UnitAdd?course_id=" + state.curr.course.id + "&name=" +
		// name, null, function (res) { unitAdd_cb(res); }, true, false);
		// unitAdd_cb({ outcome: true, courseId: state.curr.course.id, unit: {
		// id: "" + (new Date()).getTime(), name: name } });
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String cid = request.getParameter("course_id");
		String name = request.getParameter("name");
		String usr = request.getParameter("usr");
		ConfigManager cm = new ConfigManager(this); 
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser,cm.agg_dbpass);
		agg_db.openConnection();
		Integer uid = agg_db.addUnit(cid, name, usr);
		boolean outcome = (uid != null);
		agg_db.closeConnection();
		String output = "{ outcome: \"" + outcome + "\", courseId: \"" + cid+ "\", unit: { id: \"" + uid + "\", name: \"" + name + "\"}}";
		out.print(output);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
