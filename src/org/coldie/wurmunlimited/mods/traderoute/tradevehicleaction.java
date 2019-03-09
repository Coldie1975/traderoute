package org.coldie.wurmunlimited.mods.traderoute;


import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.players.Player;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class tradevehicleaction implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer {

	public static short actionId;
	static ActionEntry actionEntry;

	public tradevehicleaction() {
		actionId = (short) ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(actionId, "Check Cargo", "Checking Cargo", new int[]{ 
				});
		ModActions.registerAction(actionEntry);
	}

	@Override
	public BehaviourProvider getBehaviourProvider() {
		return this;
	}

	@Override
	public ActionPerformer getActionPerformer() {
		return this;
	}

	@Override
	public short getActionId() {
		return actionId;
	}
	
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
		return getBehavioursFor(performer, target);
	}
	
	@Override
	public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
		if (performer instanceof Player ) {
			if (target.isBoat() && performer.isVehicleCommander()) {
				return (List<ActionEntry>) Arrays.asList(actionEntry);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter) {
		return action(act, performer, target, action, counter);
	}
	
	@Override
	public boolean action(Action act, Creature performer, Item target, short action, float counter) {
		
	      Connection dbcon = null;
	      PreparedStatement ps = null;	 
	      ResultSet rs = null;
	      boolean found = false;
	      String origin = "";
		      try
		      {	 
			      dbcon = ModSupportDb.getModSupportDb();
			      ps = dbcon.prepareStatement("SELECT * FROM TradeVehicleData");
			      rs = ps.executeQuery();
			      while(rs.next()){
			    	  if (target.getWurmId() == (rs.getLong("id"))){ 
			    		  found = true;
			    		  origin = rs.getString("origin");
			    		  }
			      }
			      ps.close();
			      rs.close();

		    }
	      catch (SQLException e) {
	          throw new RuntimeException(e);
	        }		
		    
		    if (found == true && !origin.equals("")) {
		    	performer.getCommunicator().sendSafeServerMessage("Destinations");
			      try
			      {	 
			    	  Connection dbcon4 = ModSupportDb.getModSupportDb();
			    	  PreparedStatement ps4 = dbcon4.prepareStatement("SELECT * FROM TradePrices where id = ?");
				      ps4.setString(1, origin);
			    	  ResultSet rs4 = ps4.executeQuery();
				      for (int count=1;count <= 10;count++){
				    	  int price = 0;
				    	  price = rs4.getInt("location"+count);
				      	  if (price > 0)performer.getCommunicator().sendNormalServerMessage(traderoute.getname("location"+count)+" price is "+price);
				      }
				      ps4.close();
				      rs4.close();
				      dbcon4.close();
			    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }
			      int cargo = 1;
			      try
			      {	 
				      dbcon = ModSupportDb.getModSupportDb();
				      ps = dbcon.prepareStatement("SELECT * FROM TradeVehicleData");
				      rs = ps.executeQuery();
				      while(rs.next()){
				    	  if (target.getWurmId() == (rs.getLong("id"))){ 
				    		  cargo = rs.getInt("cargo");
				    		  }
				      }
				      ps.close();
				      rs.close();
				      
			    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }
			      performer.getCommunicator().sendSafeServerMessage("Your vehicle holds "+cargo+" lots of cargo");
		   
		    
			      try
			      {	 
				      dbcon = ModSupportDb.getModSupportDb();
				      ps = dbcon.prepareStatement("SELECT * FROM TradeLocations where Adjustment != 1");
				      rs = ps.executeQuery();
				      while(rs.next()){
				    	  performer.getCommunicator().sendSafeServerMessage(rs.getString("name")+" is currently paying "+rs.getFloat("Adjustment")+" times their normal price");			    	  
				      }
				      ps.close();
				      rs.close();
				      dbcon.close();
			    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }		    
		    
		    
		    
		    
		    }else{
		    	performer.getCommunicator().sendSafeServerMessage("No cargo in this vehicle");
		    	
		    }   
			return true;
	}
}