package com.wurmonline.server.questions;

import java.util.Properties;
//import java.util.logging.Logger;

import org.coldie.tools.BmlForm;
import org.coldie.wurmunlimited.mods.traderoute.tradecrafteraction;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;

public class tradecrafterquestion extends Question
{
	
	//private static Logger logger = Logger.getLogger(traderoute.class.getName());
	  private boolean properlySent = false;	  
	  tradecrafterquestion(Creature aResponder, String aTitle, String aQuestion, int aType, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, aType, aTarget);
	  }
	  
	  public tradecrafterquestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, 79, aTarget);
	  }
	  
	  int g = 0;

	  public void answer(Properties answer)
	  {
	    if (!properlySent) {
	      return;
	    }

	    // check drop down and accepted
	    boolean accepted = (answer.containsKey("accept")) && (answer.get("accept") == "true"); 
	    if (accepted)
	    {
//edit selection
	    	
	
	    	
			}else{
//delete selection			      				

		
			}

	}

	  public void sendQuestion()
	  {
	    boolean ok = true;
	      try {
	        ok = false;
	        Action act = getResponder().getCurrentAction();
	        if (act.getNumber() == tradecrafteraction.actionId) {
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
	      f.addBoldText(getTarget()+"", new String[0]);

//drop down option

	      f.addText("\n", new String[0]);
	      f.addText("\n", new String[0]);
	      f.beginHorizontalFlow();
	 	  f.addButton("Edit Selection", "accept");
	      f.addText("               ", new String[0]);
	      f.addButton("Delete Selection", "decline");
	      
	      
	      f.endHorizontalFlow();
	      f.addText(" \n", new String[0]);
	      f.addText(" \n", new String[0]);
	      
	      getResponder().getCommunicator().sendBml(
	        250, 
	        250, 
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