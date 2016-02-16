package edu.cmu.ml.rtw.contextless;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import edu.cmu.ml.rtw.generic.util.FileUtil;

public class NELLTypes {

  static final String entityTypes = "typed_nps_new.tsv";
  HashMap<String, HashSet<String>> typedNPS = new HashMap<String, HashSet<String>>();

  public NELLTypes() {
    try {
      typedNPS = getNELLTypes(entityTypes);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public HashMap<String, HashSet<String>> getNELLTypes(String filename) throws NumberFormatException, IOException {
    HashMap<String, HashSet<String>> nellTypes = new HashMap<String, HashSet<String>>();
    String line;
    BufferedReader br = FileUtil.getFileReader(filename);
    while ((line = br.readLine()) != null) {
      String[] parts = line.split("\t");
      if (parts.length < 2) continue;
      int i = 0;
      String entity = parts[i++];
      HashSet<String> types = new HashSet<String>();
      while (i < parts.length) {
        types.add(parts[i++]);
      }
      nellTypes.put(entity, types);
    }

    return nellTypes;
  }
}
