import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AggregateDB extends dbInterface{
	DateFormat dateFormat;
	Date date;
	public AggregateDB(String connurl, String user, String pass){
		super(connurl, user, pass); 
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
	}	

	/*
	 * @Return: a list of lists. The second list is an ordered list {name,desc} for each domain
	 */
	public ArrayList<ArrayList<String>> getDomains() throws JSONException, IOException {
		ArrayList<ArrayList<String>> domainList = new ArrayList<ArrayList<String>>();
		try{
			stmt = conn.createStatement();
			String query = "select name,`desc` from ent_domain;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				ArrayList<String> dom = new ArrayList<String>();
				dom.add(rs.getString(1));
				dom.add(rs.getString(2));
				domainList.add(dom);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return domainList;
	}
	

	/*
	 * @Return: a list of lists. The second list is an ordered list {id,name} for each author
	 */
	public ArrayList<ArrayList<String>> getCourseAuthors() {
		ArrayList<ArrayList<String>> authorList = new ArrayList<ArrayList<String>>();
		try{
			stmt = conn.createStatement();
			//TODO check the finizlied fields in the ent_creator table
			String query = "select creator_id,creator_name from ent_creator;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				ArrayList<String> auth = new ArrayList<String>();
				auth.add(rs.getString(1));
				auth.add(rs.getString(2));
				authorList.add(auth);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return authorList;
	}

	/*
	 * @Return: a list of lists. The second list is an ordered list {id,name} for each provider
	 */
	public ArrayList<ArrayList<String>> getProviders() {
		ArrayList<ArrayList<String>> providerList = new ArrayList<ArrayList<String>>();
		try{
			stmt = conn.createStatement();
			String query = "select p.provider_id,p.name,pd.domain_name"
							+ " from ent_provider p, rel_provider_domain pd"
							+ " where p.provider_id = pd.provider_id;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				ArrayList<String> prov = new ArrayList<String>();
				prov.add(rs.getString(1));
				prov.add(rs.getString(2));
				prov.add(rs.getString(3));
				providerList.add(prov);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return providerList;
	}
	
	//TODO groupCount added to protocol + term and year is removed from protocol, needs change in processing the information in UI
	/*
	 * @Return: a list of lists. The second list is an ordered list {id,institution_code,name,course_code,creator_name,creation_date,domainId,groupCount,creator_id; 
	 * tags are created based on the available concepts inside the activity
	 * all available courses are returned.
	 */
	public ArrayList<ArrayList<String>> getCourses() {
		ArrayList<ArrayList<String>> courseList = new ArrayList<ArrayList<String>>();
		Map<Integer,Integer> courseGroupMap = new HashMap<Integer,Integer>();
		try{
            stmt = conn.createStatement();			
			String query = " select c.course_id, count(*)"
							+ " from ent_course c, ent_creator cr, ent_group g"
							+ " where c.creator_id = cr.creator_id and g.course_id = c.course_id"
							+ " group by course_id;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				courseGroupMap.put(rs.getInt(1),rs.getInt(2));
			}
			this.releaseStatement(stmt, rs);			
			stmt = conn.createStatement();			
			query = " select c.course_id,cr.affiliation_code, c.course_name, c.course_code, cr.creator_name,c.creation_date,c.domain,cr.creator_id,c.desc,c.visible"
							+ " from ent_course c, ent_creator cr"
							+ " where c.creator_id = cr.creator_id"
							+ " order by c.domain asc, cr.affiliation_code asc, c.course_name asc;";
			rs = stmt.executeQuery(query);	
			int count=0,cid;
			while(rs.next()){
				ArrayList<String> course = new ArrayList<String>();
				cid = rs.getInt(1);
				course.add(""+cid);
				course.add(rs.getString(2));
				course.add(rs.getString(3));
				course.add(rs.getString(4));
				course.add(rs.getString(5));
				course.add(rs.getString(6));
				course.add(rs.getString(7));
				if (courseGroupMap.get(cid) != null)
					count = courseGroupMap.get(cid);
				else 
					count = 0;
				course.add(""+count);
				course.add(rs.getString(8));
				course.add(rs.getString(9)); //desc
				course.add(rs.getString(10)); //visible

				courseList.add(course);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return courseList;
	}

	/*
	 * @Retrun: a list of lists. The second list is an ordered list of {} for each 
	 */
	public ArrayList<ArrayList<String>> getResource(String course_id) {
		ArrayList<ArrayList<String>> resourceList = new ArrayList<ArrayList<String>>();
		try{
			stmt = conn.createStatement();
			String query = "SELECT resource_id,display_name"
							+ " FROM ent_resource"
							+ " where course_id = '"+course_id+"' order by `order` asc;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				ArrayList<String> res = new ArrayList<String>();
				res.add(rs.getString(1));
				res.add(rs.getString(2));
				resourceList.add(res);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return resourceList;
	}

	/*
	 * @Return: a list of providers_id for the given resource_id
	 */
	public ArrayList<String> getProviders(String resource_id) {
		ArrayList<String> resProvList = new ArrayList<String>();
		try{
			stmt = conn.createStatement();
			String query = " SELECT provider_id "
							+ " FROM rel_resource_provider"
							+ " where resource_id = '"+resource_id+"';";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				resProvList.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return resProvList;
	}

	/*
	 * @Retrun: a list of lists. The second list is an ordered list {unit_id,unit_name}
	 * units are the topics.
	 */
	public ArrayList<ArrayList<String>> getUnits(String course_id) {
		ArrayList<ArrayList<String>> unitList = new ArrayList<ArrayList<String>>();
		try{
			stmt = conn.createStatement();
			String query = "select topic_id, topic_name"
							+ " from ent_topic"
							+ " where course_id = '"+course_id+"'"
									+ "order by `order` asc;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				ArrayList<String> unit = new ArrayList<String>();
				unit.add(rs.getString(1));
				unit.add(rs.getString(2));
				unitList.add(unit);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return unitList;
	}

	//Both the Functions to retrieve JSON from a given Link
	// Added by hnv
		private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

		// Added by hnv
	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }
		  
	/*
	 * @Return: a list of lists. The second list is an ordered list {id,providerId,name,authorId,url,domain,tags}; 
	 * tags are created based on the available concepts inside the activity
	 */
	public ArrayList<ArrayList<String>> getActivities() throws IOException, JSONException {
		ArrayList<ArrayList<String>> activityList = new ArrayList<ArrayList<String>>();
		HashMap<String,String> contentTags = getContentTags();
		try{
			stmt = conn.createStatement();
			String query = "select content_id,content_name,provider_id,display_name,creator_id,url,domain from ent_content where visible = 1;";
			rs = stmt.executeQuery(query);
			while(rs.next()){
				ArrayList<String> act = new ArrayList<String>();
				act.add(rs.getString(1));
				act.add(rs.getString(3));
				act.add(rs.getString(4));
				act.add(rs.getString(5));
				act.add(rs.getString(6));
				act.add(rs.getString(7));
				act.add(contentTags.get(rs.getString(1)));
				activityList.add(act);
				//rdfid.add(rs.getString(2));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		
		
		//Added by HNV (starts here)
		ArrayList<String> webex_py = new ArrayList<String>();
		ArrayList<String> webex_java = new ArrayList<String>();
		ArrayList<String> webex_sql = new ArrayList<String>();
		ArrayList<String> animated_py = new ArrayList<String>();
		ArrayList<String> animated_java = new ArrayList<String>();
		ArrayList<String> parsons_py = new ArrayList<String>();
		ArrayList<String> videos_py = new ArrayList<String>();
		ArrayList<String> videos_java = new ArrayList<String>();
		ArrayList<String> quizjet = new ArrayList<String>();
		ArrayList<String> quizpet = new ArrayList<String>();
		
		//Added by HNV - Webex Python
	    //Get all the rdfid of annotated examples
	    try{
			stmt = conn.createStatement();
			String query2 = "select content_name from ent_content where content_type = 'example' AND domain = 'py';";
			rs = stmt.executeQuery(query2);
			while(rs.next()){
				webex_py.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_webex_py = readJsonFromUrl("http://localhost:3000/api/v1/content/annotated/annotated-python");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_webex_py = cb_webex_py.getJSONArray("content");

  	    for (int i = 0; i < json_webex_py.length(); i++) {
  	        JSONObject object = json_webex_py.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (webex_py.contains(cb_name)) {
        	    //System.out.print(" Python Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "example";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "py";
        		String cb_provider_id = "webex";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = object.getString("author_id");
        		String cb_privacy = "public";
        		String cb_author_name = object.getString("author");
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Webex Python: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    //Added by HNV - Webex Java
	    //Get all the rdfid of annotated examples
	    try{
			stmt = conn.createStatement();
			String query3 = "select content_name from ent_content where content_type = 'example' AND domain = 'java';";
			rs = stmt.executeQuery(query3);
			while(rs.next()){
				webex_java.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_webex_java = readJsonFromUrl("http://localhost:3000/api/v1/content/annotated/annotated-java");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_webex_java = cb_webex_java.getJSONArray("content");

  	    for (int i = 0; i < json_webex_java.length(); i++) {
  	        JSONObject object = json_webex_java.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (webex_java.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "example";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "java";
        		String cb_provider_id = "webex";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = object.getString("author_id");
        		String cb_privacy = "public";
        		String cb_author_name = object.getString("author");
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Webex Java: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    //Added by HNV - Webex SQL
	    //Get all the rdfid of annotated examples
	    try{
			stmt = conn.createStatement();
			String query3 = "select content_name from ent_content where content_type = 'example' AND domain = 'sql';";
			rs = stmt.executeQuery(query3);
			while(rs.next()){
				webex_sql.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_webex_sql = readJsonFromUrl("http://localhost:3000/api/v1/content/annotated/annotated-sql");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_webex_sql = cb_webex_sql.getJSONArray("content");

  	    for (int i = 0; i < json_webex_sql.length(); i++) {
  	        JSONObject object = json_webex_sql.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (webex_sql.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "example";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "sql";
        		String cb_provider_id = "webex";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = object.getString("author_id");
        		String cb_privacy = "public";
        		String cb_author_name = object.getString("author");
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Webex SQL: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    //Added by HNV - Animated Examples - Python
	    try{
			stmt = conn.createStatement();
			String query4 = "select content_name from ent_content where provider_id = 'animatedexamples' AND domain = 'py';";
			rs = stmt.executeQuery(query4);
			while(rs.next()){
				animated_py.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_animated_py = readJsonFromUrl("http://acos.cs.hut.fi/api/v1/content/jsvee/jsvee-python");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_animated_py = cb_animated_py.getJSONArray("content");

  	    for (int i = 0; i < json_animated_py.length(); i++) {
  	        JSONObject object = json_animated_py.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (animated_py.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "animatedexamples";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "py";
        		String cb_provider_id = "animatedexamples";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = "admin";
        		String cb_privacy = "public";
        		String cb_author_name = "Administrator";
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Animated Python: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    //Added by HNV - Animated Examples - Java
	    try{
			stmt = conn.createStatement();
			String query4 = "select content_name from ent_content where provider_id = 'animatedexamples' AND domain = 'java';";
			rs = stmt.executeQuery(query4);
			while(rs.next()){
				animated_java.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_animated_java = readJsonFromUrl("http://acos.cs.hut.fi/api/v1/content/jsvee/jsvee-java");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_animated_java = cb_animated_java.getJSONArray("content");

  	    for (int i = 0; i < json_animated_java.length(); i++) {
  	        JSONObject object = json_animated_java.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (animated_java.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "animated_examples";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "java";
        		String cb_provider_id = "animatedexamples";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = "admin";
        		String cb_privacy = "public";
        		String cb_author_name = "Administrator";
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Animated Java: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    //Added by HNV - Parsons - Python
	    try{
			stmt = conn.createStatement();
			String query5 = "select content_name from ent_content where content_type = 'parsons' and domain = 'py';";
			rs = stmt.executeQuery(query5);
			while(rs.next()){
				parsons_py.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_parsons_python = readJsonFromUrl("http://acos.cs.hut.fi/api/v1/content/jsparsons/jsparsons-python");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_parsons_python = cb_parsons_python.getJSONArray("content");

  	    for (int i = 0; i < json_parsons_python.length(); i++) {
  	        JSONObject object = json_parsons_python.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (parsons_py.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "parsons";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "py";
        		String cb_provider_id = "parsons";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = "admin";
        		String cb_privacy = "public";
        		String cb_author_name = "Administrator";
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Parsons Python: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    
  	    //Added by HNV - Videos - Python
	    try{
			stmt = conn.createStatement();
			String query6 = "select content_name from ent_content where content_type = 'educvideos' and domain = 'py';";
			rs = stmt.executeQuery(query6);
			while(rs.next()){
				videos_py.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_videos_py = readJsonFromUrl("http://columbus.exp.sis.pitt.edu/educvideos/api/v1/content/videos/videos-python");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_videos_py = cb_videos_py.getJSONArray("content");

  	    for (int i = 0; i < json_videos_py.length(); i++) {
  	        JSONObject object = json_videos_py.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (videos_py.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "educvideos";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "py";
        		String cb_provider_id = "educvideos";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = "shs174";
        		String cb_privacy = "public";
        		String cb_author_name = "Shruti Sabu Suresh";
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Videos Python: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	//Added by HNV - Videos - Python
	    try{
			stmt = conn.createStatement();
			String query6 = "select content_name from ent_content where content_type = 'educvideos' and domain = 'java';";
			rs = stmt.executeQuery(query6);
			while(rs.next()){
				videos_java.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_videos_java = readJsonFromUrl("http://columbus.exp.sis.pitt.edu/educvideos/api/v1/content/videos/videos-java");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_videos_java = cb_videos_java.getJSONArray("content");

  	    for (int i = 0; i < json_videos_java.length(); i++) {
  	        JSONObject object = json_videos_java.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (videos_java.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "educvideos";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "java";
        		String cb_provider_id = "educvideos";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = "shs174";
        		String cb_privacy = "public";
        		String cb_author_name = "Shruti Sabu Suresh";
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.println("Parsons Java: Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    
  	    
  	    
  	    
  	    
  	    
  	    
  	    //Added by HNV - Quiz - Quizjet
	    try{
			stmt = conn.createStatement();
			String query6 = "select content_name from ent_content where content_type = 'question' and domain = 'py';";
			rs = stmt.executeQuery(query6);
			while(rs.next()){
				quizpet.add(rs.getString(1));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
	    //Content Brokeing Data
  		JSONObject cb_quizpet = readJsonFromUrl("http://columbus.exp.sis.pitt.edu/api/v1/content/quiz/quizpet");
  	    //Accessing each element of the content brokering json object
  	    JSONArray json_quizpet = cb_quizpet.getJSONArray("content");

  	    for (int i = 0; i < json_quizpet.length(); i++) {
  	        JSONObject object = json_quizpet.getJSONObject(i);
  	        String cb_name = object.getString("name");

  	        if (quizpet.contains(cb_name)) {
        	    //System.out.print(" Java Found ");
        	} 
        	//If example not found then add it to ent_content
        	else {
        		String cb_content_type = "question";
        		String cb_display_name = object.getString("title");
        		String cb_url = object.getString("html_url");
        		String cb_domain = "py";
        		String cb_provider_id = "quizpet";
        		//Date cb_creation_date = new Date();
        		java.util.Date date= new java.util.Date();
        		Date cb_creation_date = (new Timestamp(date.getTime()));
        		String cb_creator_id = "contentbrokering";
        		String cb_privacy = "public";
        		String cb_author_name = object.getString("author");
        		
        		try{
        			stmt = conn.createStatement();
        			date = new Date();
        			String query = "insert into ent_content (content_name,content_type,display_name,url,provider_id,domain,creator_id,creation_date,privacy,author_name)"
        					+ " value ('"+cb_name+"','"+cb_content_type+"','"+cb_display_name+"','"+cb_url+"','"+cb_provider_id+"','"+cb_domain+"','"+cb_creator_id+"','"+cb_creation_date+"','"+cb_privacy+"','"+cb_author_name+"');";
        			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
        			rs = stmt.getGeneratedKeys();
        			this.releaseStatement(stmt,rs);
        			System.out.print("Added " + cb_name + " to the database");
        		}catch (SQLException ex) {
        			this.releaseStatement(stmt,rs);
        			System.out.println("SQLException: " + ex.getMessage()); 
        			System.out.println("SQLState: " + ex.getSQLState()); 
        			System.out.println("VendorError: " + ex.getErrorCode());
        		}finally{
        			this.releaseStatement(stmt,rs);
        		}
        	}
  	    }
  	    
  	    
  	    
  	    
  	    
		return activityList;
		
	}

	/*
	 * @Return: A hash map containing content as key, and its comma-separated tags as value
	 */
	private HashMap<String, String> getContentTags() {
		HashMap<String,String> contentTags = new HashMap<String,String>();
		try{
			stmt = conn.createStatement();
			String query = " select entity_id, group_concat(tag separator ',') as tags"
							+ " from ent_tagging"
							+ " group by entity_id;";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				contentTags.put(rs.getString(1),rs.getString(2));
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return contentTags;
	}

	/*
	 * @Return: a hash map containing the resource of the unit as key, and the activities inside that resource as value
	 */
	public HashMap<String, ArrayList<String>> getResourceActivity(String unit_id) {
		HashMap<String,ArrayList<String>> resActMap = new HashMap<String,ArrayList<String>>();
		try{
			stmt = conn.createStatement();
			String query = "SELECT resource_id,content_id"
							+ " FROM rel_topic_content"
							+ " where topic_id = '"+unit_id+"'"
									+ "order by display_order asc;";
			rs = stmt.executeQuery(query);	
			String res;
			String act;
			while(rs.next()){
				res = rs.getString(1);
				act = rs.getString(2);
				ArrayList<String> acts = resActMap.get(res);
				if (acts == null)
					acts = new ArrayList<String>();
				acts.add(act);
				resActMap.put(res, acts);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return resActMap;
	}

	public Integer addRes(String cid, String name, String usr) {
		Integer resid = null;
		int neworder = gentMaxResOrder(cid)+1;
		try{			
			stmt = conn.createStatement();
			date = new Date();
			String query = "insert into ent_resource (course_id,resource_name,display_name,`order`,creation_date,creator_id,visible)"
					+ " value ('"+cid+"','"+name+"','"+name+"','"+neworder+"','"+dateFormat.format(date)+"','"+usr+"','1');";
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
			rs = stmt.getGeneratedKeys();
	        if (rs.next()){
	            resid=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
			//create the resource_name from the name
			String[] nameParts = name.split(" ");
			String genid = "";
			String ch;
			for (String np : nameParts)
			{
				ch = np.substring(0,1).toLowerCase();
				if (ch.equals(" ") == false)
					genid += ch;
			}			
			genid += resid;
			stmt = conn.createStatement();				
			query = "update ent_resource set resource_name='"+genid+"' where resource_id = '"+resid+"';";					
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("AddResource", usr, resid.toString());
			return resid;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return resid;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	private int gentMaxResOrder(String cid) {
		int maxorder = 0;
		try{
			stmt = conn.createStatement();
			String query = "select max(`order`) from ent_resource where course_id = '"+cid+"';";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				maxorder = rs.getInt(1);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return maxorder;
	}

	public boolean editRes(String resid, String name) {
		try{
			stmt = conn.createStatement();
			String query = "update ent_resource set resource_name = '"+name+"', display_name='"+name+"' where resource_id = '"+resid+"';";
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("EditResource", "", resid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	public boolean deleteRes(String resid) {
		try{
			//step1: delete from resource_provider where resource_id =?
			stmt = conn.createStatement();
			String query = "delete from rel_resource_provider where resource_id = '"+resid+"';";
			stmt.executeUpdate(query);
			this.releaseStatement(stmt,rs);
			//step2: delete from rel_topic_content where resource_id =?
			stmt = conn.createStatement();
			query = "delete from rel_topic_content where resource_id = '"+resid+"';";
			stmt.executeUpdate(query);
			this.releaseStatement(stmt,rs);
			//step3: delete from ent_resource where resource_id = ?
			stmt = conn.createStatement();
			query = "delete from ent_resource where resource_id = '"+resid+"';";
			stmt.executeUpdate(query);
			this.releaseStatement(stmt,rs);
			log("DelResource", "", resid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}		
	}

	public boolean swapRes(String cid, String resid1, int idx, int idxDelta) {
		try{
			stmt = conn.createStatement();
			String query;
			int resid2=0,idx2=0,idx1=0;
			//find the res that will be swapped by the resid1
			query = "select resource_id,`order` from ent_resource where course_id = '"+cid+"' order by `order` asc limit "+idx+",1;"; 
			rs = stmt.executeQuery(query);				
	        if (rs.next()){
	            resid2=rs.getInt(1);
	            idx2=rs.getInt(2);
	        }
			this.releaseStatement(stmt,rs);
			//find the order of resid1
			stmt = conn.createStatement();
			query = "select `order` from ent_resource where resource_id = '"+resid1+"';";
			rs = stmt.executeQuery(query);				
	        if (rs.next()){
	            idx1=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
	        //set the order of the resid2 as idx1
			stmt = conn.createStatement();
			query = "update ent_resource set `order` = '"+idx1+"' where resource_id = '"+resid2+"';";
			stmt.executeUpdate(query);	
			this.releaseStatement(stmt,rs);
			//set the order of the resid1 as idx2
			stmt = conn.createStatement();
			query = "update ent_resource set `order` = '"+idx2+"' where resource_id = '"+resid1+"';";
			stmt.executeUpdate(query);	
			this.releaseStatement(stmt,rs);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	
	}

	public boolean addResProvider(String resid, String provid) {
		try{
			stmt = conn.createStatement();
			String query = "insert into rel_resource_provider (resource_id,provider_id) value ('"+resid+"','"+provid+"');";					
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("AddProvider", "", resid+", "+provid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	public boolean deleteResProvider(String resid, String provid) {
		try{
			stmt = conn.createStatement();
			String query = "delete from rel_resource_provider where resource_id='"+resid+"' and provider_id='"+provid+"';";					
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("DelProvider", "", resid+", "+provid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}
	}

	public Integer addUnit(String cid, String name, String usr) {
		Integer uid = null;
		int neworder = getMaxUnitOrder(cid)+1;
		try{
			stmt = conn.createStatement();
			date = new Date();
			String query = "insert into ent_topic (course_id,topic_name,display_name,`order`,creation_date,creator_id,visible,active)"
					+ " value ('"+cid+"','"+name+"','"+name+"','"+neworder+"','"+dateFormat.format(date)+"','"+usr+"','1','1');";
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
			rs = stmt.getGeneratedKeys();
	        if (rs.next()){
	            uid=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
			log("AddUnit", usr, uid.toString());
			return uid;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return uid;
		}finally{
			this.releaseStatement(stmt,rs);
		}		
	}

	private int getMaxUnitOrder(String cid) {
		int maxorder = 0;
		try{
			stmt = conn.createStatement();
			String query = "select max(`order`) from ent_topic where course_id = '"+cid+"';";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				maxorder = rs.getInt(1);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return maxorder;
	}

	public boolean editUnit(String uid, String name) {
		try{
			stmt = conn.createStatement();
			String query = "update ent_topic set topic_name = '"+name+"', display_name='"+name+"' where topic_id = '"+uid+"';";
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("EditUnit", "", uid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	public boolean deleteUnit(String uid) {
		try{
			//step1:delete from rel_topic_content where topic_id = ?
			stmt = conn.createStatement();
			String query = "delete from rel_topic_content where topic_id = '"+uid+"';";
			stmt.executeUpdate(query);
			//step2: delete from ent_topic where topic_id = ?
			query = "delete from ent_topic where topic_id = '"+uid+"';";
			stmt.executeUpdate(query);
			this.releaseStatement(stmt,rs);
			log("DelUnit", "", uid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}		
	}

	public boolean swapUnit(String cid, String uid1, int idx, int idxDelta) {
		try{
			stmt = conn.createStatement();
			String query;
			int uid2=0,idx2=0,idx1=0;
			//find the unit that will be swapped by the uid1
			query = "select topic_id,`order` from ent_topic where course_id = '"+cid+"' order by `order` asc limit "+idx+",1;"; 
			rs = stmt.executeQuery(query);				
	        if (rs.next()){
	            uid2=rs.getInt(1);
	            idx2=rs.getInt(2);
	        }
			this.releaseStatement(stmt,rs);
			//find the order of uid1
			stmt = conn.createStatement();
			query = "select `order` from ent_topic where topic_id = '"+uid1+"';";
			rs = stmt.executeQuery(query);				
	        if (rs.next()){
	            idx1=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
	        //set the order of the uid2 as idx1
			stmt = conn.createStatement();
			query = "update ent_topic set `order` = '"+idx1+"' where topic_id = '"+uid2+"';";
			stmt.executeUpdate(query);	
			this.releaseStatement(stmt,rs);
			//set the order of the uid1 as idx2
			stmt = conn.createStatement();
			query = "update ent_topic set `order` = '"+idx2+"' where topic_id = '"+uid1+"';";
			stmt.executeUpdate(query);	
			this.releaseStatement(stmt,rs);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	public boolean addUnitAct(String usr, String uid, String resid, String actid) {
		try{
			int neworder = getMaxUnitActOrder(uid, resid) + 1;
			stmt = conn.createStatement();
			date = new Date();
			String displayName = "";
			//get the display_name of the act
			String query = "select content_name from ent_content where content_id = '"+actid+"';";
			rs = stmt.executeQuery(query);	
			if (rs.next()){
				displayName = rs.getString(1);
			}
			this.releaseStatement(stmt,rs);
			stmt = conn.createStatement();
			query = "insert into rel_topic_content (topic_id,resource_id,content_id,display_name,`display_order`,creation_date,creator,visible)"
					+ " value ('"+uid+"','"+resid+"','"+actid+"','"+displayName+"', '"+neworder+"', '"+dateFormat.format(date)+"','"+usr+"','1');";
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("AddAct", usr, uid+", "+resid+", "+actid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}	
	
	private int getMaxUnitActOrder(String uid, String resid) {
		int maxorder = 0;
		try{
			stmt = conn.createStatement();
			String query = "select max(`display_order`) from rel_topic_content where "
					+ "topic_id = '"+uid+"' and resource_id = '"+resid+"';";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				maxorder = rs.getInt(1);
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return maxorder;
	}

	public boolean deleteUnitAct(String uid, String resid, String actid) {
		try{
			//delete from rel_topic_content where  topic_id = ? resource_id = ? content_id = ?
			stmt = conn.createStatement();
			String query = "delete from rel_topic_content where topic_id = '"+uid+"' and resource_id = '"+resid+"' and content_id='"+actid+"';";
			stmt.executeUpdate(query);			
			this.releaseStatement(stmt,rs);
			log("DeleteAct", "", uid+", "+resid+", "+actid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}		
	}

	public Integer cloneCourse(String cid,String name,String usr) {
		Integer newcid = null;
		try{
			Map<Integer,Integer> resOldNew = new HashMap<Integer,Integer>(); //key:resid, value:cloned resid
			Map<Integer,Integer> unitOldNew = new HashMap<Integer,Integer>(); //unitid, value:cloned unitid
			//Step1: select course information
			stmt = conn.createStatement();
			String query = "select `desc`,course_code,domain,visible from ent_course where course_id = '"+cid+"';";
			String desc="",course_code="",domain="",visible="";
			rs = stmt.executeQuery(query);	
			if (rs.next())
			{
				desc = rs.getString(1);
				course_code = rs.getString(2);
				domain = rs.getString(3);
				visible = rs.getString(4);
			}
			this.releaseStatement(stmt,rs);
			//Step2: insert the cloned record in ent_course
			stmt = conn.createStatement();
			date = new Date();
			query = "insert into ent_course(course_name,`desc`,course_code,domain,creation_date,creator_id,visible)"
					+ " value ('"+name+"','"+desc+"','"+course_code+"','"+domain+"','"+dateFormat.format(date)+"','"+usr+"','"+visible+"');";
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
			rs = stmt.getGeneratedKeys();
	        if (rs.next()){
	            newcid=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
			//Step3: select resources of the course that should be cloned 
			stmt = conn.createStatement();
	        query = "select resource_name,display_name,`desc`,`order`,visible,resource_id from ent_resource where course_id = '"+cid+"';";
			rs = stmt.executeQuery(query);	
			ResultSet tmpResRs = null,tmpProvRs = null;
			String resource_name="",display_name="",order="",provid;
			int resid,newresid=0;	
			Statement stmt2,stmt3;
			while (rs.next())
			{
				resource_name = rs.getString(1);
				display_name = rs.getString(2);
				desc = rs.getString(3);
				order = rs.getString(4);
				visible = rs.getString(5);
				resid = rs.getInt(6);
				//Step4: insert the cloned resource to ent_resource
				stmt2 = conn.createStatement();
				query = "insert into ent_resource(resource_name,display_name,`desc`,`order`,visible,course_id,creation_date,creator_id)" 
						+ " value ('"+resource_name+"','"+display_name+"','"+desc+"','"+order+"','"
		                  +visible+"', '"+newcid+"','"+dateFormat.format(date)+"','"+usr+"');";
				stmt2.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
				tmpResRs = stmt2.getGeneratedKeys();
		        if (tmpResRs.next()){
		        	newresid=tmpResRs.getInt(1); //new resid
		        	resOldNew.put(resid, newresid); //mapping btw old and cloned resid
		        }
				this.releaseStatement(stmt2,tmpResRs);
		    	//Step5: clone providers of the resource
				stmt2 = conn.createStatement();
				query = "select provider_id from rel_resource_provider where resource_id = '"+resid+"';";
				tmpProvRs = stmt2.executeQuery(query);
				while (tmpProvRs.next())
				{
					provid = tmpProvRs.getString(1);
					stmt3 = conn.createStatement();
					query = "insert into rel_resource_provider(resource_id,provider_id) value ('"+newresid+"','"+provid+"');";
					stmt3.executeUpdate(query);
					this.releaseStatement(stmt3, null);
				}	
				this.releaseStatement(stmt2,tmpProvRs);
			}	
			this.releaseStatement(stmt,rs);
			//Step6: select topics of the course
			stmt = conn.createStatement();
			query = "select topic_name,display_name,`order`,visible,active,topic_id from ent_topic where course_id = '"+cid+"';";
			String topic_name="",active="";
			int unitid=0;
			rs = stmt.executeQuery(query);
			ResultSet tmpTopicRs = null;
			int newUnitid;
			while (rs.next())
			{
				topic_name = rs.getString(1);
				display_name = rs.getString(2);
				order = rs.getString(3);
				visible = rs.getString(4);
				active = rs.getString(5);
				unitid = rs.getInt(6);
				//Step7: insert the cloned topic to ent_topic
				query = "insert into ent_topic (course_id,topic_name,display_name,`order`,creation_date,creator_id,visible,active)"
						+ " value ('"+newcid+"','"+topic_name+"','"+display_name+"','"+order+"','"+dateFormat.format(date)+"','"+usr+"','"+visible+"','"+active+"');";
				stmt2 = conn.createStatement();
				stmt2.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);
				tmpTopicRs = stmt2.getGeneratedKeys();
		        if (tmpTopicRs.next()){
		        	newUnitid=tmpTopicRs.getInt(1); //new unitid
		        	unitOldNew.put(unitid, newUnitid); //mapping btw old and cloned unitid
		        }
				this.releaseStatement(stmt2,tmpTopicRs);
			}
			this.releaseStatement(stmt,rs);
			//step8: select activities associated to each of the units
			stmt = conn.createStatement();
			String topicIds = "";
			for (int t : unitOldNew.keySet())
				topicIds += "'"+t+"',";
			if (topicIds.length() != 0)
			{
				topicIds = topicIds.substring(0, topicIds.length()-1); //for ignoring the last comma
				query = "select topic_id,resource_id,content_id,display_name,display_order,visible"
						+ " from rel_topic_content where topic_id in ("+topicIds+");";
			    rs = stmt.executeQuery(query);
			    int actid;
			    while (rs.next())
			    {
			    	unitid = rs.getInt(1);
			    	resid = rs.getInt(2);
			    	actid = rs.getInt(3);
			    	display_name = rs.getString(4);
			    	order = rs.getString(5);
			    	visible = rs.getString(6);
			    	//Step 9: insert the cloned act in the rel_topic_content
			    	query = "insert into rel_topic_content (topic_id,resource_id,content_id,display_name,`display_order`,creation_date,creator,visible)"
						+ " value ('"+unitOldNew.get(unitid)+"','"+resOldNew.get(resid)+"','"+actid+"','"+display_name+"', '"+order+"', '"+dateFormat.format(date)+"','"+usr+"','"+visible+"');";
			    	stmt2 = conn.createStatement();
			    	stmt2.executeUpdate(query);
					this.releaseStatement(stmt2,null);
			    }	
			}
				    
			this.releaseStatement(stmt,rs);
			log("CloneCourse", usr, newcid.toString());
			return newcid;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			ex.printStackTrace(); 
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return newcid;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	public ArrayList<String> getCourse(Integer newcid) {
		ArrayList<String> courseList = new ArrayList<String>();
		try{
			stmt = conn.createStatement();
			String query = " select c.course_id,cr.affiliation_code, c.course_name, c.course_code, cr.creator_name,c.creation_date,c.domain,cr.creator_id,c.desc,c.visible"
							+ " from ent_course c, ent_creator cr"
							+ " where c.creator_id = cr.creator_id and c.course_id = '"+newcid+"';";
			rs = stmt.executeQuery(query);	
			while(rs.next()){
				courseList.add(rs.getString(1));
				courseList.add(rs.getString(2));
				courseList.add(rs.getString(3));
				courseList.add(rs.getString(4));
				courseList.add(rs.getString(5));
				courseList.add(rs.getString(6));
				courseList.add(rs.getString(7));
				courseList.add("0"); // no group is assigned to the course right after cloning.
				courseList.add(rs.getString(8));
				courseList.add(rs.getString(9)); //desc
				courseList.add(rs.getString(10)); //visible
			}
			this.releaseStatement(stmt,rs);
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}
		return courseList;
	
	}

	public boolean deleteCourse(String cid) {
		try{
			//step1: get all resources in the cid
			stmt = conn.createStatement();
			String query = "select resource_id from ent_resource where course_id = '"+cid+"';";
			rs = stmt.executeQuery(query);	
			String resIds = "";
			while(rs.next()){
				resIds += rs.getString(1)+",";
			}			
			this.releaseStatement(stmt, rs);
			if (resIds.length() != 0)
			{
				resIds = resIds.substring(0,resIds.length()-1); //ignoring the last comma
				//step2: delete from resource_provider where resource_id in resIds
				stmt = conn.createStatement();
				query = "delete from rel_resource_provider where resource_id in ("+resIds+");";
				stmt.executeUpdate(query);
				this.releaseStatement(stmt, rs);
				//step3: delete from rel_topic_content where resource_id in resIds
				stmt = conn.createStatement();
				query = "delete from rel_topic_content where resource_id in ("+resIds+");";
				stmt.executeUpdate(query);
				this.releaseStatement(stmt, rs);
				//step4: delete from ent_resource where resource_id in resIds
				stmt = conn.createStatement();
				query = "delete from ent_resource where resource_id in ("+resIds+");";
				stmt.executeUpdate(query);			
				this.releaseStatement(stmt,rs);
			}
			//step5: delete units of cid
			stmt = conn.createStatement();
			query = "delete from ent_topic where course_id ='"+cid+"';";
			stmt.executeUpdate(query);			
			this.releaseStatement(stmt,rs);
			//step6: delete the course cid
			stmt = conn.createStatement();
			query = "delete from ent_course where course_id ='"+cid+"';";
			stmt.executeUpdate(query);			
			this.releaseStatement(stmt,rs);
			log("DeleteCourse", "", cid);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}		
	}

	public boolean swapUnitAct(String uid, String resid, String actid, int idx, int idxDelta) {
		try{
			stmt = conn.createStatement();
			String query;
			int actid2=0,idx2=0,idx1=0;
			//find the res that will be swapped by the actid
			query = "select content_id,`display_order` from rel_topic_content where topic_id = '"+uid+"' and resource_id = '"+resid+"' order by `display_order` asc limit "+idx+",1;"; 
			rs = stmt.executeQuery(query);				
	        if (rs.next()){
	            actid2=rs.getInt(1);
	            idx2=rs.getInt(2);
	        }
			this.releaseStatement(stmt,rs);
			//find the order of actid
			stmt = conn.createStatement();
			query = "select `display_order` from rel_topic_content where topic_id = '"+uid+"' and resource_id = '"+resid+"' and content_id = '"+actid+"';";
			rs = stmt.executeQuery(query);				
	        if (rs.next()){
	            idx1=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
	        //set the order of the actid2 as idx1
			stmt = conn.createStatement();
			query = "update rel_topic_content set `display_order` = '"+idx1+"' where topic_id = '"+uid+"' and resource_id = '"+resid+"' and content_id = '"+actid2+"';";
			stmt.executeUpdate(query);	
			this.releaseStatement(stmt,rs);
			//set the order of the actid1 as idx2
			stmt = conn.createStatement();
			query = "update rel_topic_content set `display_order` = '"+idx2+"' where topic_id = '"+uid+"' and resource_id = '"+resid+"' and content_id = '"+actid+"';";
			stmt.executeUpdate(query);	
			this.releaseStatement(stmt,rs);
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	public Integer addCourse(String name, String code, String desc,
			String domain, String visible, String usr) {		
		Integer newcid = null;
		try{
			stmt = conn.createStatement();
			date = new Date();
			String query = "insert into ent_course(course_name,`desc`,course_code,domain,creation_date,creator_id,visible)"
					+ " value ('"+name+"','"+desc+"','"+code+"','"+domain+"','"+dateFormat.format(date)+"','"+usr+"','"+visible+"');";
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
			rs = stmt.getGeneratedKeys();
	        if (rs.next()){
	            newcid=rs.getInt(1);
	        }
			this.releaseStatement(stmt,rs);
			log("AddCourse", usr, newcid.toString());
			return newcid;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			ex.printStackTrace(); 
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return newcid;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
			
	}

	public boolean editCourse(int cid, String name, String code, String desc,
			String domain, String visible, String usr) {
		try{
			stmt = conn.createStatement();
			String query = "update ent_course set course_name='"+name+"',`desc`='"+desc
					       +"',course_code='"+code+"',domain='"+domain+"',visible='"+visible+"'" +
					        " where course_id = '"+cid+"';";
			stmt.executeUpdate(query);				
			this.releaseStatement(stmt,rs);
			log("EditCourse", usr, Integer.toString(cid));
			return true;
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			ex.printStackTrace(); 
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}

	private void log(String action, String subject, String object){
		try{
			stmt = conn.createStatement();
			Long unixTime = System.currentTimeMillis()/1000L;
			String query = "INSERT INTO `aggregate`.`ent_log` (`action`, `time`, `subject`, `object`)" +
					"VALUES('" + action+ "', '" + unixTime.toString() + "', '" + subject + "', '" + object + "');";
			stmt.executeUpdate(query,Statement.RETURN_GENERATED_KEYS);				
			this.releaseStatement(stmt,rs);	
		}catch (SQLException ex) {
			this.releaseStatement(stmt,rs);
			System.out.println("SQLException: " + ex.getMessage()); 
			System.out.println("SQLState: " + ex.getSQLState()); 
			System.out.println("VendorError: " + ex.getErrorCode());
		}finally{
			this.releaseStatement(stmt,rs);
		}	
	}
}
