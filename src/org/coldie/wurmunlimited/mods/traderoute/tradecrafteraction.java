package org.coldie.wurmunlimited.mods.traderoute;


import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.Items;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.players.Player;

import java.util.List;
import java.util.logging.Logger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class tradecrafteraction implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer {
	private static Logger logger = Logger.getLogger(traderoute.class.getName());
	public static short actionId;
	static ActionEntry actionEntry;

	public tradecrafteraction() {
		actionId = (short) ModActions.getNextActionId();
		actionEntry = ActionEntry.createEntry(actionId, "Cargo load/unload", "Cargo load/unload", new int[]{ 
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
		int clicked = target.getTemplateId();
		// add check for if event active
		Item vehicle = null;
		if (performer instanceof Player ) {
			if (clicked == 47101 && performer.isVehicleCommander()) 
				try{
					vehicle = Items.getItem(performer.getVehicle());
					}catch (NoSuchItemException localNoSuchItemException) {}			
					if (vehicle != null && vehicle.isBoat()) {
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
	      Item vehicle = null;
	      int cargo = 1;
			try{
				vehicle = Items.getItem(performer.getVehicle());
				}catch (NoSuchItemException localNoSuchItemException) {}
			if (vehicle != null){
		      try
		      {	 
			      dbcon = ModSupportDb.getModSupportDb();
			      ps = dbcon.prepareStatement("SELECT * FROM TradeVehicleData");
			      rs = ps.executeQuery();
			      while(rs.next()){
			    	  if (vehicle.getWurmId() == (rs.getLong("id"))){ 
			    		  found = true;
			    		  origin = rs.getString("origin");
			    		  cargo = rs.getInt("cargo");
			    		  }
			      }
			      ps.close();
			      rs.close();
			      dbcon.close();
		    }
	      catch (SQLException e) {
	          throw new RuntimeException(e);
	        }	
		      
		String localation = "";
		float adjust = 1;
	      try
	      {	 
		      dbcon = ModSupportDb.getModSupportDb();
		      ps = dbcon.prepareStatement("SELECT * FROM TradeLocations where id = ?");
		      ps.setLong(1, target.getWurmId());
		      rs = ps.executeQuery();
		      localation = rs.getString("location");
		      adjust = rs.getFloat("Adjustment");
		      ps.close();
		      rs.close();
		      dbcon.close();
	    }
      catch (SQLException e) {
          throw new RuntimeException(e);
        }	
			      
		if (found == true){
			int price = 0;
		      try
		      {	 
			      dbcon = ModSupportDb.getModSupportDb();
			      ps = dbcon.prepareStatement("SELECT * FROM Tradeprices where id = ?");
			      ps.setString(1, origin);
			      rs = ps.executeQuery();
			      price = rs.getInt(localation);
			      ps.close();
			      rs.close();
			      dbcon.close();
		    }
	      catch (SQLException e) {
	          throw new RuntimeException(e);
	        }
		      if (origin.equals(localation))performer.getCommunicator().sendNormalServerMessage("This is where you loaded cargo from");
		      if (price > 0 && !origin.equals(localation)){
				  int payment = (int)Math.floor(price*cargo*adjust);
				  //pay the person here.
				  
				try {
					performer.addMoney((long) payment);
					String bountymessage = Economy.getEconomy().getChangeFor((long) payment).getChangeString();
					performer.getCommunicator().sendSafeServerMessage("you got "+bountymessage+" deposited into your bank.");		  					
					  logger.info(performer.getName()+" just got "+payment+" iron for doing a trade route.");

				} catch (IOException e1) {
					e1.printStackTrace();
				}

				  try
			      {	 
				     Connection dbcon5 = ModSupportDb.getModSupportDb();
				     PreparedStatement ps5 = dbcon5.prepareStatement("DELETE FROM TradeVehicleData where id = ?");
				      ps5.setLong(1, vehicle.getWurmId());
				      ps5.executeUpdate();
				      ps5.close();
				      dbcon5.close();		      
				    }
			      catch (SQLException e) {
			          //throw new RuntimeException(e);
			        }	
	      
		      }		
		
		}else{

		      Connection dbcon2 = null;
		      PreparedStatement ps2 = null;
		      try
		      {
		      dbcon2 = ModSupportDb.getModSupportDb();
		      ps2 = dbcon2.prepareStatement("INSERT INTO TradeVehicleData (id,cargo,origin) VALUES(?,?,?)");
		      ps2.setLong(1, vehicle.getWurmId());
		      ps2.setInt(2,  traderoute.getvolume(vehicle.getTemplateId()));
		      ps2.setString(3,  localation);
		      ps2.executeUpdate();
		      ps2.close();
		      dbcon2.close();
		    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		    }		
		      performer.getCommunicator().sendNormalServerMessage("Added cargo to your vehicle");			
		}
		}else{
			 performer.getCommunicator().sendNormalServerMessage("no vehicle, WTF you haxor, calling the FBI");
			
		}
		return true;
	}
}