package edu.cmu.ml.rtw.contextless;

import edu.cmu.ml.rtw.generic.util.StringSerializable;

public class TypedNP implements StringSerializable {

  String np;
  String type;

  public TypedNP(String np, String type) {
    this.np = np;
    this.type = type;
  }

 String getType(){
   return type;
 }
 
 String getNP(){
   return np;
 }
  public String toString() {

    StringBuilder sb = new StringBuilder(np).append("|");
    sb.append(type);
    return sb.toString();
  }

  public boolean fromString(String arg0) {
    return false;
  }

}
