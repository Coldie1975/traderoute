package com.wurmonline.server.questions;


import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.Zones;

import java.util.Properties;
//import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;


import org.coldie.wurmunlimited.mods.traderoute.tradeGMaction;
import org.coldie.tools.BmlForm;

public class tradeGMquestion extends Question
{
	//private static Logger logger = Logger.getLogger(traderoute.class.getName());
	  private boolean properlySent = false;	  
	  tradeGMquestion(Creature aResponder, String aTitle, String aQuestion, int aType, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, aType, aTarget);
	  }
	  
	  public tradeGMquestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, 79, aTarget);
	  }
	  
	  int g = 0;

	  public void answer(Properties answer)
	  {
	    if (!properlySent) {
	      return;
	    }

	    // check accepted
	    boolean accepted = (answer.containsKey("accept")) && (answer.get("accept") == "true"); 
	    if (accepted)
	    {
//edit selection
		      Connection dbcon3 = null;
		      PreparedStatement ps3 = null;
		      ResultSet rs3 = null;
		      boolean found = false;
		      int g = 0;
		      try
		      {	      
		      dbcon3 = ModSupportDb.getModSupportDb();
		      ps3 = dbcon3.prepareStatement("SELECT * FROM TradeLocations");
		      rs3 = ps3.executeQuery();

		      while (rs3.next()) {
		    	  g = g + 1;
		    	  	if (rs3.getLong("id") == getTarget()){
		    	  		found = true;
		    	  	}
		      }
		      rs3.close();
		      ps3.close();
		      dbcon3.close();
		    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }    	
	    	
	    	if (found == false){
			      Connection dbcon = null;
			      PreparedStatement ps = null;
			      try
			      {
		  
				      dbcon = ModSupportDb.getModSupportDb();
				      ps = dbcon.prepareStatement("INSERT INTO TradeLocations (id,startx,starty,endx,endy,location,name) VALUES(?,?,?,?,?,?,?)");
				      ps.setLong(1, getTarget());
				      ps.setInt(2,  Integer.parseInt(answer.getProperty("xstart")));
				      ps.setInt(3,  Integer.parseInt(answer.getProperty("ystart")));
				      ps.setInt(4,  Integer.parseInt(answer.getProperty("xend")));
				      ps.setInt(5,  Integer.parseInt(answer.getProperty("yend")));
				      ps.setString(6,  "location"+g);
				      Village v = Zones.getVillage( getResponder().getTileX(), getResponder().getTileY(), true);
				      if (v != null){
				      ps.setString(7,  v.getName());
				      }else{
				    	  ps.setString(7, "No deed so Name me");
				      }
				      ps.executeUpdate();
				      ps.close();
				      dbcon.close();
				      
				    }
				      catch (SQLException e) {
				          throw new RuntimeException(e);
				    }	    		
	    		
	    		getResponder().getCommunicator().sendNormalServerMessage("Added this trade storage to DB");
	    	}	    		    	
			}else{
//Cancel selection			      				

		
			}

	}

	  public void sendQuestion()
	  {
	    boolean ok = true;
	      try {
	        ok = false;
	        Action act = getResponder().getCurrentAction();
	        if (act.getNumber() == tradeGMaction.actionId) {
	          ok = true;
	        }
	      }
	      catch (NoSuchActionException act) {
	        throw new RuntimeException("No such action", act);
	      }
	    
	    if (ok) {


	      properlySent = true;
	      
	      BmlForm f = new BmlForm("");
	      f.addHidden("id", id+"");
	      f.addBoldText(getQuestion(), new String[0]);
	      f.addText("\n ", new String[0]);

//add stuff here
	      
	      f.addText("Insert start x coords here\t");
	      f.addInput("xstart", 10, "100");
	      f.addText("\n", new String[0]);
	      f.addText("Insert end x coords here\t");
	      f.addInput("xend", 10, "200");
	      f.addText("\n", new String[0]);
	      f.addText("\n", new String[0]);
	      
	      f.addText("Insert start y coords here\t");
	      f.addInput("ystart", 10, "500");
	      f.addText("\n", new String[0]);
	      f.addText("Insert end y coords here\t");
	      f.addInput("yend", 10, "600");
	      f.addText("\n", new String[0]);
	      f.addText("\n", new String[0]);
	      f.addText("Insert type you want, cotton is 7, dirt is 21\t");
	      f.addInput("type", 5, "7");
	      f.beginHorizontalFlow();
	 	  f.addButton("Activate trade storage unit", "accept");
	      f.addText("               ", new String[0]);
	      f.addButton("Cancel Selection", "decline");
	      
	      
	      f.endHorizontalFlow();
	      f.addText(" \n", new String[0]);
	      f.addText(" \n", new String[0]);
	      
	      getResponder().getCommunicator().sendBml(
	        250, 
	        350, 
	        true, 
	        true, 
	        f.toString(), 
	        150, 
	        150, 
	        200, 
	        title);
	    }
	  }

}