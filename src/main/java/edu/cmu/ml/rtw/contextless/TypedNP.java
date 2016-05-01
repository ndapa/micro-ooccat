package edu.cmu.ml.rtw.contextless;

import edu.cmu.ml.rtw.generic.util.StringSerializable;

public class TypedNP implements StringSerializable {

  String np;
  String type;
  double confidence;

  public TypedNP(String np, String type, double confidence) {
    this.np = np;
    this.type = type;
    this.confidence = confidence;
  }

 public String getType(){
   return type;
 }
 
 public String getNP(){
   return np;
 }
 
 public double getConf(){
   return confidence;
 }
  public String toString() {

    StringBuilder sb = new StringBuilder(np).append("|");
    sb.append(type).append("|");
    sb.append(confidence);
    return sb.toString();
  }

  public boolean fromString(String arg0) {
    return false;
  }

}
