package com.beaglebuddy.id3.v23.frame_body;

import java.util.List;

import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.pojo.Price;
import com.beaglebuddy.util.Utility;





/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * Base class for the body of ID3V2.3 frames.  This class is simply a collection of utility methods and is not a frame body type.
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyUtility extends Utility
{
   // utility members
   protected int nullTerminatorIndex;        // used by derived classes to find the end of a string in the data buffer.
   protected int nextNullTerminatorIndex;    // used by derived classes to find the end of a string in the data buffer.




   /**
    * default constructor.
    */
   public ID3v23FrameBodyUtility()
   {
      // no code necessary
   }

   /**
    * finds the next null terminating character in the raw data stream according to the specified character encoding.
    * A null character is represented as a single 0x00 byte in some character encodings and as two bytes 0x00 0x00 in others.
    * @param buffer         the raw binary bytes in which to find the next null terminator character.
    * @param startingFrom   the index at which to start searching from in the data.
    * @param encoding       the character set used to encode the string (and hence determine the null terminating character).
    * <br/><br/>
    * @return the index of the next null terminator in the data.
    */
   public static int getNextNullTerminator(byte[] buffer, int startingFrom, Encoding encoding)
   {
      int index=0;
      if (encoding.getNumBytesInNullTerminator() == 1)
         for(index=startingFrom; index<buffer.length && buffer[index] != 0; ++index);
      else
         for(index=startingFrom; index+1<buffer.length && !(buffer[index] == 0 && buffer[index+1] == 0); index += 2);

      return index;
   }

   /**
    * converts a String to a byte array using a specified character set encoding.  The String class has a getBytes() method, but that method does
    * not include a null terminator in the byte array that it returns.  This method does.
    * @return a byte array holding the string value in the specified encoding with null terminating bytes.
    * @param encoding   the encoding to use when converting the string to a byte array.
    * @param string     the string to be converted to a byte array.
    */
   public static byte[] stringToBytes(Encoding  encoding, String string)
   {
      byte[] data = string.getBytes(encoding.getCharacterSet());
      byte[] bytes = new byte[data.length + encoding.getNumBytesInNullTerminator()];
      System.arraycopy(data, 0, bytes, 0, data.length);
      bytes[data.length] = (byte)0x00;
      if (encoding.getNumBytesInNullTerminator() == 2)
         bytes[data.length+1] = (byte)0x00;

      return bytes;
   }

   /**
    * convert a list of prices to a string, with each price separated by the "/" character.<br/>
    * example: USD0.99/EUR1.00/GBP0.65<br/>
    * @param prices   list of prices.
    * @return a string representation of the list of price(s).
    */
   public static String pricesToString(List<Price> prices)
   {
      StringBuffer priceString = new StringBuffer();
      for (Price price : prices)
         priceString.append((priceString.length() == 0 ? "" : "/") + price);

      return priceString.toString();
   }
}
