

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
/**
 * Servlet implementation class GetData
 */
public class GetData extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public GetData() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		String user = request.getParameter("usr"); // get the user id from service url
		String group = request.getParameter("grp"); //get the group of the user from the service url
		if (group == null)
		{
			group = "";
		}
		ConfigManager cm = new ConfigManager(this); 
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser, cm.agg_dbpass);
		agg_db.openConnection();
		String data = "";
		try {
			data = getUserJSON(user,group, agg_db);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		agg_db.closeConnection();
		String output = "{outcome:\"true\", data:"+data+"}"; // data does not need to be in quotation since it is a list not a single value
		out.print(output);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	// this method returns the json which is the same as data.js
	private String getUserJSON(String user, String group, AggregateDB agg_db) throws JSONException, IOException
	{
		String output = "{\n";
		output += getJSONMetaData(user,group) + ",\n";
		output += getJSONCourse(agg_db,user)+ ",\n";
		output += getJSONActivities(agg_db)+ ",\n";
		output += getJSONProviders(agg_db)+ ",\n";
		output += getJSONAuthors(agg_db)+ ",\n";
		output += getJSONDomains(agg_db);
		output += "\n}";
		return output;
	}

	private String getJSONCourse(AggregateDB agg_db,String user) {
		String output = "  courses: [";
		ArrayList<ArrayList<String>> courseList = agg_db.getCourses();
	    //each element of course is an ordered list of {id,institution,name,num,year,term,by,on,domainId,creator_id}
		for (ArrayList<String> course : courseList)
		{
			output += "\n    {\n";
			output += "      id: \""+course.get(0)+"\", institution: \""+course.get(1)+"\", name:\""+course.get(2)+"\", num:\""+course.get(3)+
			         "\", created: { by: \""+course.get(4)+"\", on: \""+course.get(5)+"\" }, domainId: \""+course.get(6)+
			         "\", groupCount: \""+course.get(7)+"\", isMy: "+(user.equals(course.get(8)))+", desc: \""+course.get(9)+"\", visible: \""+course.get(10)+"\", ";			
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
			
			output += "\n    },"; 
		}
		if (courseList.isEmpty() == false)
			output = output.substring(0,output.length()-1); //ignoring the last comma
		output += "\n  ]";
		return output;	}

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

	private String getJSONActivities(AggregateDB agg_db) throws IOException, JSONException 
	{	    
		String output = "  activities: [\n";
		ArrayList<ArrayList<String>> activityList = agg_db.getActivities(); 
		//each provider element is an ordered list containing {id,providerId,name,authorId,url,domain,tags}; tags are the topics(comma separated text)
		for (ArrayList<String> act : activityList) 
		{
			output += "    { id: \""+act.get(0)+"\", providerId: \""+act.get(1)+"\", name: \""+act.get(2)+"\", authorId: \""+act.get(3)+"\", url: \""+act.get(4)+"\", domain: \""+act.get(5)+"\", "+getActTags(act.get(6))+" },\n"; 
		}
		if (activityList.isEmpty() == false)
			output = output.substring(0, output.length()-2); // this is for ignoring the last comma
		output += "\n  ]";
		return output;
	}

	private String getActTags(String text) {
		String tags = "tags: [";
		if (text != null){
			String[] list = text.split(",");
			for (String t : list)
			{
				tags += " \"" + t + "\"" + ",";
			}
			if (list.length != 0)
				tags = tags.substring(0, tags.length()-1); // this is for ignoring the last comma character in the tags list 
		}
		tags += "]";
		return tags;
	}

	private String getJSONProviders(AggregateDB agg_db) {
		String output = "  providers: [\n";
		ArrayList<ArrayList<String>> providerList = agg_db.getProviders(); 
		//each provider element is an ordered list containing {id,name}
		for (ArrayList<String> prov : providerList) 
		{
			output += "    { id: \""+prov.get(0)+"\", name: \""+prov.get(1)+"\", domainId: \""+prov.get(2)+"\" },\n"; 
		}
		if (providerList.isEmpty() == false)
			output = output.substring(0, output.length()-2); // this is for ignoring the last comma character in the domains list
		output += "\n  ]";
		return output;
	}

	private String getJSONAuthors(AggregateDB agg_db) {
		String output = "  authors: [\n";
		ArrayList<ArrayList<String>> authorList = agg_db.getCourseAuthors(); 
		//each author element is an ordered list containing {id,name}
		for (ArrayList<String> auth : authorList) 
		{
			output += "    { id: \""+auth.get(0)+"\", name: \""+auth.get(1)+"\" },\n"; 
		}
		if (authorList.isEmpty() == false)
			output = output.substring(0, output.length()-2); // this is for ignoring the last comma character in the domains list
		output += "\n  ]";
		return output;
	}

	private String getJSONDomains(AggregateDB agg_db) throws JSONException, IOException{
		String output = "  domains: [\n";
		ArrayList<ArrayList<String>> domainList = agg_db.getDomains(); 
		//each domain element is an ordered list containing {id,name}
		for (ArrayList<String> dom : domainList) 
		{
			output += "    { id: \""+dom.get(0)+"\", name: \""+dom.get(1)+"\" },\n"; 
		}
		if (domainList.isEmpty() == false)
			output = output.substring(0, output.length()-2); //this is for ignoring the last comma 
		output += "\n  ]";
		return output;
	}

	private String getJSONMetaData(String user,String group) {
		String output = "  meta: {\n    grp: \""+group+"\",\n    usr: \""+user+"\"\n  }";
		return output;
	}

}
