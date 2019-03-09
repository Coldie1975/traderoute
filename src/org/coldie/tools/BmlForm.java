package org.coldie.tools;

import java.util.logging.Level;
import java.util.logging.Logger;


public class BmlForm
{
  private static Logger logger = Logger.getLogger(BmlForm.class.getName());
  private final StringBuffer buf = new StringBuffer();
  
  //private static final String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
  private int openBorders = 0;
  private int openCenters = 0;
  private int openVarrays = 0;
  private int openScrolls = 0;
  private int openHarrays = 0;
  
  private int openTrees = 0;
  private int openRows = 0;
  private int openColumns = 0;
  
  private int openTables = 0;
  
  private int indentNum = 0;
  private boolean beautify = false;
  
  private boolean closeDefault = false;
  


  public BmlForm() {}
  


  public BmlForm(String formTitle)
  {
    addDefaultHeader(formTitle);
  }
  
  public void addDefaultHeader(String formTitle)
  {
    if (closeDefault) {
      return;
    }
    
    beginBorder();
    beginCenter();
    addBoldText(formTitle, new String[0]);
    endCenter();
    
    beginScroll();
    beginVerticalFlow();
    
    closeDefault = true;
  }
  
  public void beginBorder() {
    buf.append(indent("border{"));indentNum += 1;openBorders += 1; }
  public void endBorder() { indentNum -= 1;buf.append(indent("}"));openBorders -= 1; }
  
  public void beginCenter() { buf.append(indent("center{"));indentNum += 1;openCenters += 1; }
  public void endCenter() { indentNum -= 1;buf.append(indent("};null;"));openCenters -= 1; }
  
  public void beginVerticalFlow() { buf.append(indent("varray{rescale=\"true\";"));indentNum += 1;openVarrays += 1; }
  public void endVerticalFlow() { indentNum -= 1;buf.append(indent("}"));openVarrays -= 1; }
  
  public void beginScroll() { buf.append(indent("scroll{vertical=\"true\";horizontal=\"false\";"));indentNum += 1;openScrolls += 1; }
  public void endScroll() { indentNum -= 1;buf.append(indent("};null;null;"));openScrolls -= 1; }
  
  public void beginHorizontalFlow() { buf.append(indent("harray {"));indentNum += 1;openHarrays += 1; }
  public void endHorizontalFlow() { indentNum -= 1;buf.append(indent("}"));openHarrays -= 1;
  }
  
  public void beginTable(int rowCount, String[] columns) {
    buf.append(indent("table {rows=\"" + rowCount + "\"; cols=\"" + columns.length + "\";"));
    
    indentNum += 1;
    for (String c : columns) {
      addLabel(c);
    }
    indentNum -= 1;
    
    indentNum += 1;
    openTables += 1; }
  
  public void endTable() { indentNum -= 1;buf.append(indent("}"));openTables -= 1;
  }
  

  public void addBoldText(String text, String... args)
  {
    addText(text, "bold", args);
  }
  
  public void addHidden(String name, String val)
  {
    buf.append(indent("passthrough{id=\"" + name + "\";text=\"" + val + "\"}"));
  }
  
  public void addText(String text, String... args)
  {
    addText(text, "", args);
  }
  
  private String indent(String s)
  {
    return beautify ? getIndentation() + s + "\r\n" : s;
  }
  
  private String getIndentation()
  {
    if (indentNum > 0) {
      return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t".substring(0, indentNum);
    }
    return "";
  }
  
  public void addRaw(String s)
  {
    buf.append(s);
  }
  
  public void addImage(String url, int height, int width)
  {
    addImage(url, height, width, "");
  }
  
  public void addImage(String url, int height, int width, String tooltip)
  {
    buf.append("image{src=\"");
    buf.append(url);
    buf.append("\";size=\"");
    buf.append(height + "," + width);
    buf.append("\";text=\"" + tooltip + "\"}");
  }
  
  public void addLabel(String text)
  {
    buf.append("label{text='" + text + "'};");
  }
  
  public void addInput(String id, int maxChars, String defaultText)
  {
    buf.append("input{id='" + id + "';maxchars='" + maxChars + "';text=\"" + defaultText + "\"};");
  }
  
  private void addText(String text, String type, String... args)
  {
    String[] lines = text.split("\n");
    
    for (String l : lines) {
      if (beautify) {
        buf.append(getIndentation());
      }
      
      buf.append("text{");
      if (!type.equals("")) {
        buf.append("type='" + type + "';");
      }
      buf.append("text=\"");
      
      buf.append(String.format(l, args));
      buf.append("\"}");
      
      if (beautify) {
        buf.append("\r\n");
      }
    }
  }
  
  public void addButton(String name, String id)
  {
    buf.append(indent("button{text='  " + name + "  ';id='" + id + "'}"));
  }
  
  public String toString()
  {
    if (closeDefault) {
      endVerticalFlow();
      endScroll();
      endBorder();
      closeDefault = false;
    }
    
    if ((openCenters != 0) || (openVarrays != 0) || (openScrolls != 0) || (openHarrays != 0) || (openBorders != 0) || (openTrees != 0) || (openRows != 0) || (openColumns != 0) || (openTables != 0)) {
      logger.log(Level.SEVERE, "While finalizing BML unclosed (or too many closed) blocks were found (this will likely mean the BML will not work!): center: " + 
        openCenters + 
        " vert-flows: " + openVarrays + 
        " scroll: " + openScrolls + 
        " horiz-flows: " + openHarrays + 
        " border: " + openBorders + 
        " trees: " + openTrees + 
        " rows: " + openRows + 
        " columns: " + openColumns + 
        " tables: " + openTables);
    }
    

    return buf.toString();
  }
}
