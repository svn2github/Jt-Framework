

package Jt;
import java.io.*;

/**
  * Implements a data buffer. The buffer representation is an array of bytes.
  */

public class JtBuffer extends JtObject  {
  
  byte buffer[];

  public JtBuffer() {
  }

  /**
    * Returns the buffer which is basically an array of bytes.
    */

  public byte[] getBuffer() {
    return buffer;
  }

  /**
    * Sets the buffer.
    */

  public void setBuffer(byte newBuffer[]) {
    buffer = newBuffer;
  }

/*
  void show () {
    System.out.println (buffer.toString ());
    char c = (char) buffer[0];
    char c1 = (char) buffer[1];
    System.out.println (c + c1);
  }
*/

  // processMessageEvent: process messages

/*
  public Object processMessage (Object event) {
    return (null);    
  }
*/
}