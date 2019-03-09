package org.coldie.wurmunlimited.mods.traderoute;

import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.items.ItemTypes;

import java.io.IOException;

public class tradeitems implements WurmServerMod, ItemTypes, MiscConstants {
	
	public tradeitems() {
		try {

			com.wurmonline.server.items.ItemTemplateCreator.createItemTemplate(47101, 5, "Trade Storage", "Trades Storage", "excellent", "good", "ok", "poor", "A wooden cupboard, used to store materials for trade routes.", new short[] { 21, 40, 44, 52, 67, 51, 199, 31 }, (short)60, (short)1, 0, 9072000L, 135, 58, 180, -10, MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY, "model.furniture.wooden.storageunit.", 50.0F, 6500, (byte)14, 10000, true, -1);
			
		}catch(IOException e){
			
		}
	}	
}