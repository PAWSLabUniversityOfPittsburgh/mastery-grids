

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UnitGetLst
 */
public class UnitGetLst extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UnitGetLst() {
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
		ConfigManager cm = new ConfigManager(this); 
		AggregateDB agg_db = new AggregateDB(cm.agg_dbstring, cm.agg_dbuser, cm.agg_dbpass);
		agg_db.openConnection();
		ArrayList<ArrayList<String>> units = agg_db.getUnits(cid);	
		boolean outcome = (units.size() != 0);
		String output = "{outcome: \""+outcome+"\", units: "+getJSON(agg_db,units)+"}";//units is list so it does not need quotation
		agg_db.closeConnection();
		out.print(output);
	}

	private String getJSON(AggregateDB agg_db,ArrayList<ArrayList<String>> unitList) {
		//each unit element is an ordered list with {id,name}
		String output = "\n      [";
		for (ArrayList<String> unit : unitList)
		{
			output += "\n        { id: \""+unit.get(0)+"\", name: \""+unit.get(1)+"\", "+getJSONunitActivity(agg_db,unit.get(0))+" },";
		}
		if (unitList.isEmpty() == false)
		    output = output.substring(0,output.length()-1); //ignoring the last comma
		output += "\n      ]";
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
