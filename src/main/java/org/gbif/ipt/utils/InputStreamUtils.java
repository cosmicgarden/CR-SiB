package org.gbif.ipt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.Logger;

public class InputStreamUtils {

  protected static final Logger log = Logger.getLogger(InputStreamUtils.class);

  public InputStream classpathStream(String path) {
    InputStream in = null;
    // relative path. Use classpath instead
    URL url = getClass().getClassLoader().getResource(path);
    if (url != null) {
      try {
        in = url.openStream();
      } catch (IOException e) {
        log.warn(e);
      }
    }
    return in;
  }
  
  public InputStreamReader classpathStreamRea(String path) {
	  	InputStreamReader in = null;
	    // relative path. Use classpath instead
	    URL url = getClass().getClassLoader().getResource(path);
	    if (url != null) {
	      try {
	    	
	        in = new InputStreamReader(url.openStream(), "UTF-8");
	      } catch (IOException e) {
	        log.warn(e);
	      }
	    }
	    return in;
  }
}
