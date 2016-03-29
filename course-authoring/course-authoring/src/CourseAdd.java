

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CourseAdd
 */
public class CourseAdd extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CourseAdd() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String name = request.getParameter("name"); 
		String code = request.getParameter("code");
		String desc = request.getParameter("desc");
		String domain = request.getParameter("domain");
		String visible = request.getParameter("visible");
		String usr = request.getParameter("usr");
		ConfigManager cm = new ConfigManager(this);
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser, cm.agg_dbpass);
		agg_db.openConnection();
		Integer cid = agg_db.addCourse(name,code,desc,domain,visible,usr);
		boolean outcome = (cid!=null);
		String output = "{outcome: \""+outcome+"\", course: "+getJSON(agg_db,cid,usr)+"}";
		agg_db.closeConnection();
		out.print(output);
	}

	private String getJSON(AggregateDB agg_db, Integer newcid,String usr) {
		ArrayList<String> course = agg_db.getCourse(newcid);
		String output = "{\n";
		output += "      id: \""+course.get(0)+"\", institution: \""+course.get(1)+"\", name:\""+course.get(2)+"\", num:\""+course.get(3)+
				         "\", created: { by: \""+course.get(4)+"\", on: \""+course.get(5)+"\" }, domainId: \""+course.get(6)+
				         "\", groupCount: \""+course.get(7)+"\", isMy: "+(usr.equals(course.get(8)))+", desc: \""+course.get(9)+"\", visible: \""+course.get(10)+"\", ";
		output += "\n      resources: [\n],"; //no resource
		output += "\n      units: [\n]";		//no unit
		output += "\n    }"; 		
		return output;	
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
