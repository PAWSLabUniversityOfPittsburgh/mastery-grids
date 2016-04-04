

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CourseClone
 */
public class CourseClone extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CourseClone() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  //courseClone_cb({ outcome: true, courseId: c.id, course: { id: "" + (new Date()).getTime(), institution: c.institution, name: name, num: c.num, date: c.date, created: c.created, domainId: c.domainId, isMy: true, units: [], resources: [] }});

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String cid = request.getParameter("course_id");
		String name = request.getParameter("name"); //name of the cloned course
		String usr = request.getParameter("usr");
		ConfigManager cm = new ConfigManager(this); 
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser, cm.agg_dbpass);
		agg_db.openConnection();
		Integer newcid = agg_db.cloneCourse(cid,name,usr);
		boolean outcome = (newcid!=null);
		String output = "{outcome: \""+outcome+"\", course: "+getJSON(agg_db,newcid,usr)+"}";//course is list so it does not need quotation
		agg_db.closeConnection();
		out.print(output);
	}

	private String getJSON(AggregateDB agg_db, Integer newcid, String usr) {
		ArrayList<String> course = agg_db.getCourse(newcid);
	    //each element of course is an ordered list of {id,institution,name,num,year,term,by,on,domainId,creator_id}
		String output = "\n    {\n";
		output += "      id: \""+course.get(0)+"\", institution: \""+course.get(1)+"\", name:\""+course.get(2)+"\", num:\""+course.get(3)+
		         "\", created: { by: \""+course.get(4)+"\", on: \""+course.get(5)+"\" }, domainId: \""+course.get(6)+
		         "\", groupCount: \""+course.get(7)+"\", isMy: "+(usr.equals(course.get(8)))+", desc: \""+course.get(9)+"\", visible: \""+course.get(10)+"\", ";		
		//fetch all resources of the course
		//each element of the resource is an ordered list of {id,name};
		ArrayList<ArrayList<String>> resourceList = agg_db.getResource(course.get(0));
		output += "\n      resources: [";
		for (ArrayList<String> resource : resourceList)
		{
			output += "\n        { id: \""+resource.get(0)+"\", name: \""+resource.get(1)+"\", "+getJSONProviderIds(agg_db,resource.get(0))+" },";
		}
		if (resourceList.isEmpty() == false)
			output = output.substring(0,output.length()-1); //ignoring the last comma
		output += "\n      ],";
		
		//fetch all unit in the course
		ArrayList<ArrayList<String>> unitList = agg_db.getUnits(course.get(0));
		//each unit element is an ordered list with {id,name}
		output += "\n      units: [";
		for (ArrayList<String> unit : unitList)
		{
			output += "\n        { id: \""+unit.get(0)+"\", name: \""+unit.get(1)+"\", "+getJSONunitActivity(agg_db,unit.get(0))+" },";
		}
		if (unitList.isEmpty() == false)
		    output = output.substring(0,output.length()-1); //ignoring the last comma
		output += "\n      ]";
		
		output += "\n    }"; 		
		return output;	
	}

	private String getJSONunitActivity(AggregateDB agg_db, String unit) 
	{
		//e.g.: activityIds: { ex: ["1"], qz: ["2"] } 
		String output = " activityIds: { ";
		HashMap<String,ArrayList<String>> resActList = agg_db.getResourceActivity(unit);
		//each element of actList contains {key:resource_id, value:activityList of that resource}
		for (String res : resActList.keySet())
		{
			output += res + ": ["+getActString(resActList.get(res))+"], ";
		
		}
		if (resActList.isEmpty() == false)
			output = output.substring(0, output.length()-2); //ignoring the last comma
		output += " }";
		return output;
	}
	
	private String getActString(ArrayList<String> actList) {
		String text = "";
		for (String act : actList)
		{
			text += "\""+act+"\",";
		}
		if (actList.isEmpty() == false)
			text = text.substring(0,text.length()-1);//for ignoring the last comma
		return text;
	}
	
	private String getJSONProviderIds(AggregateDB agg_db, String resource) {
		String output =  " providerIds: [";
		ArrayList<String> providerList = agg_db.getProviders(resource);
		//each element of provider contains {providerId}
		for (String prov : providerList)
		{
			output += " \""+prov+"\",";
		}
		if (providerList.isEmpty() == false)
			output = output.substring(0,output.length()-1); //ignoring the last comma
		output += "]";
		return output;
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
