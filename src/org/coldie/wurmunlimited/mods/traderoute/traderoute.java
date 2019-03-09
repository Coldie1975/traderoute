package org.coldie.wurmunlimited.mods.traderoute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;


import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Actions;

public class traderoute implements WurmServerMod, Configurable, ItemTemplatesCreatedListener, PreInitable, Initable, ServerStartedListener  {
	static Logger logger = Logger.getLogger(traderoute.class.getName());	
	static int sailing = 7;
	static int cog = 15;	
	static int knarr = 20;	
	static int corbita = 25;
	static int caravel = 30;
	
	@Override
	public void preInit() {

		
	}

	@Override
	public void configure(Properties properties) {
		knarr = Integer.parseInt(properties.getProperty("knarr", Integer.toString(knarr)));
		corbita = Integer.parseInt(properties.getProperty("corbita", Integer.toString(corbita)));
		cog = Integer.parseInt(properties.getProperty("cog", Integer.toString(cog)));
		caravel = Integer.parseInt(properties.getProperty("caravel", Integer.toString(caravel)));
		sailing = Integer.parseInt(properties.getProperty("sailing", Integer.toString(sailing)));
		
	}

	public static String getname(String location){
	      String name = "";
		      try
		      {	 
		    	  Connection dbcon = ModSupportDb.getModSupportDb();
		    	  PreparedStatement ps = dbcon.prepareStatement("SELECT * FROM TradeLocations WHERE location = '"+location+"'");
		    	  ResultSet rs = ps.executeQuery();
			      name = rs.getString("name");
			      ps.close();
			      rs.close();

		    }
	      catch (SQLException e) {
	          throw new RuntimeException(e);
	        }		
		return name;
	}
	
	public static int getvolume(int boat){
		if(boat == 540){//cog
			return cog;
		}else if(boat == 541){//corbita
			return corbita;
		}else if (boat == 542){//knarr
			return knarr;
		}else if (boat == 543){//caravel
			return caravel;
		}else if (boat == 491){//sailing
			return sailing;
		}
		return 3; //rowing, default
	}
	
	@Override
	public void onItemTemplatesCreated() {
		new tradeitems();
	}

	@Override
	public void init() {

	}
	
	@Override
	public void onServerStarted() {
		ModActions.registerAction(new tradevehicleaction());
		ModActions.registerAction(new tradecrafteraction());
		ModActions.registerAction(new tradeGMaction());
		try {
			ReflectionUtil.setPrivateField(Actions.actionEntrys[tradecrafteraction.actionId],
			ReflectionUtil.getField(ActionEntry.class, "maxRange"), 12);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassCastException e1) {
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		}			
	    try
	    {
	      Connection con = ModSupportDb.getModSupportDb();
	      String sql = "";
	      
	      if (!ModSupportDb.hasTable(con, "TradeLocations")) {
	        sql = "CREATE TABLE TradeLocations (\t\tid\t\t\t\tLONG\t\t\tNOT NULL UNIQUE,"
	        		+ "\t\tstartx\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tstarty\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tendx\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tendy\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"	        		
	        		+"\t\tlocation\t\t\t\tVARCHAR(20)\t\t\tNOT NULL DEFAULT'unknown',"
	        		+"\t\tname\t\t\t\tVARCHAR(20)\t\t\tNOT NULL DEFAULT'unknown',"
	        		+ "\t\tAdjustment\t\t\t\t\tFLOAT\t\tNOT NULL DEFAULT 1)";
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.execute();
	        ps.close();
	        con.close();

	        Connection dbcon = ModSupportDb.getModSupportDb();
		    ps = dbcon.prepareStatement("INSERT INTO TradeLocations (id,startx,starty,endx,endy,location,name,Adjustment) VALUES(0,100,100,100,100,'location0','nowhere',1)");
		    ps.executeUpdate();
		    ps.close();	
		    dbcon.close();
	      }
	    }
	    catch (SQLException e)
	    {
	    	throw new RuntimeException(e);
	    }
	    
	    try
	    {
	      Connection con = ModSupportDb.getModSupportDb();
	      String sql = "";
	      
	      if (!ModSupportDb.hasTable(con, "TradeVehicleData")) {
	        sql = "CREATE TABLE TradeVehicleData (\t\tid\t\t\t\tLONG\t\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tcargo\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\torigin\t\t\t\t\tVARCHAR(20)\t\t\tNOT NULL DEFAULT'unknown')";

	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.execute();
	        ps.close();
	        con.close();

	        Connection dbcon = ModSupportDb.getModSupportDb();
		    ps = dbcon.prepareStatement("INSERT INTO TradeVehicleData (id,cargo,origin) VALUES(1,1,'location0')");
		    ps.executeUpdate();
		    ps.close();	       
		    dbcon.close();
	      }
	    }
	    catch (SQLException e)
	    {
	    	throw new RuntimeException(e);
	    }	
	    try
	    {
	      Connection con = ModSupportDb.getModSupportDb();
	      String sql = "";
	      
	      if (!ModSupportDb.hasTable(con, "Tradeprices")) {
	        sql = "CREATE TABLE Tradeprices (\t\tid\t\t\t\tVARCHAR(20)\t\t\tNOT NULL DEFAULT 'unknown',"
	        		+ "\t\tlocation1\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation2\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation3\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation4\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation5\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation6\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation7\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation8\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation9\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\tlocation10\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0)";
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.execute();
	        ps.close();
	        con.close();

	        Connection dbcon = ModSupportDb.getModSupportDb();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location1',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location2',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location3',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location4',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location5',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location6',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location7',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location8',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location9',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps = dbcon.prepareStatement("INSERT INTO Tradeprices (id,location1,location2,location3,location4,location5,location6,location7,location8,location9,location10) VALUES('location10',0,0,0,0,0,0,0,0,0,0)");
		    ps.executeUpdate();
		    ps.close();	
		    dbcon.close();
	      }
	    }
	    catch (SQLException e)
	    {
	    	throw new RuntimeException(e);
	    }	    
	    
	}

}