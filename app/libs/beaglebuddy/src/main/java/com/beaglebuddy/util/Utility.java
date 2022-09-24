package com.beaglebuddy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.beaglebuddy.id3.enums.v24.Encoding;




/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * This class is a utility class which contains general purpose methods for dealing with raw binary data.
 */
public class Utility
{
   /**
    * default constructor.
    * this is needed by ID3v2xFrameBodyUtility.
    */
   public Utility()
   {
      // no code necessary
   }

   /**
    * formats a date as YYYYMMDD.
    * @param date the date to format.  If null, then the today's date is used.
    * @return the specified date formatted as YYYYMMDD.
    */
   public static String formateDate(Date date)
   {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

      return formatter.format(date == null ? new Date() : date);
   }

   /**
    * formats a single byte as a 2 digit hex value, ie, 0xbb, where 0 < b < F.<br/>
    * example: decimal: 12  -> hex: 0C<br/>
    * example: decimal: 178 -> hex: B2<br/>
    * @param data  byte data whose value will be converted to hex.
    * <br/><br/>
    * @return the hex representation of a byte.
    */
   public static String hex(byte data)
   {
      StringBuffer buffer  = new StringBuffer();
      char[]       hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

      buffer.append("0x");
      buffer.append(hexChar[(byte)((data & 0xF0) >> 4)]);
      buffer.append(hexChar[(byte)((data & 0x0F)     )]);

      return buffer.toString();
   }

   /**
    * formats raw binary byte data as a sequence of bytes in the format 0xbb 0xbb ... with 16 bytes per line.
    * @param data     raw binary data to be printed.
    * <br/><br/>
    * @return a string representation of the binary data as hexadecimal bytes.
    */
   public static String hex(byte[] data)
   {
      return hex(data, 0);
   }

   /**
    * formats raw binary byte data as a sequence of bytes in the format 0xbb 0xbb ... with 16 bytes per line.
    * @param data     raw binary data to be printed.
    * @param indent   number of spaces to indent each line of output before displaying the hex bytes.
    * <br/><br/>
    * @return a string representation of the binary data as hexadecimal bytes.
    */
   public static String hex(byte[] data, int indent)
   {
      StringBuffer buffer = new StringBuffer();

      // format the hex data
      for(int i=0; i<data.length; ++i)
      {
         if (i!=0 && i%16==0)
         {
            buffer.append("\n");
            buffer.append(pad(indent)); // indent the specified number of spaces
         }
         buffer.append(hex(data[i]));
         buffer.append(" ");
      }
      return buffer.toString();
   }

   /**
    * gets a string consisting of the specified number of blank spaces which can be used for padding (left or right) in order to format some output (ie, make columns line up, etc).
    * @return a string of spaces.
    * @param numSpaces number of spaces to return.
    */
   public static String pad(int numSpaces)
   {
      StringBuffer buffer = new StringBuffer();

      for(int i=0; i<numSpaces; ++i)
         buffer.append(" ");

      return buffer.toString();
   }

   /**
    * if a string is UTF-16 encoded, then make sure it starts with a BOM (byte order mark) of U+FEFF.
    * @see <a href="http://en.wikipedia.org/wiki/Byte_order_mark" target="_blank">Byte Order mark</a>
    * @return a properly encoded UTF-16 encoded string which starts with a byte order mark.
    * @param string   string to be checked for proper UTF-16 encoding.
    */
   public static String getUTF16String(String string)
   {
      String text = string;

      byte[] bytes = string.getBytes(Encoding.UTF_16.getCharacterSet());
      if (bytes.length < 2 || bytes[0] != (byte)0xFE || bytes[1] != (byte)0xFF)
      {
         byte[] bytez = new byte[bytes.length + 2];
         bytes[0] = (byte)0xFE;
         bytes[1] = (byte)0xFF;
         System.arraycopy(bytes, 0, bytez, 2, bytes.length);
         text = new String(bytez, Encoding.UTF_16.getCharacterSet());
      }
      return text;
   }

   /**
    * if a string is UTF-8 encoded, then make sure it starts with a BOM (byte order mark) of U+EF BB BF.
    * @see <a href="http://en.wikipedia.org/wiki/Byte_order_mark" target="_blank">Byte Order mark</a>
    * @return a properly encoded UTF-8 encoded string which starts with a byte order mark.
    * @param string   string to be checked for proper UTF-8 encoding.
    */
   public static String getUTF8String(String string)
   {
      String text = string;

      byte[] bytes = string.getBytes(Encoding.UTF_8.getCharacterSet());
      if (bytes.length < 3 || bytes[0] != (byte)0xEF || bytes[1] != (byte)0xBB || bytes[2] != (byte)0xBF)
      {
         byte[] bytez = new byte[bytes.length + 2];
         bytes[0] = (byte)0xEF;
         bytes[1] = (byte)0xBB;
         bytes[1] = (byte)0xBF;
         System.arraycopy(bytes, 0, bytez, 3, bytes.length);
         text = new String(bytez, Encoding.UTF_8.getCharacterSet());
      }
      return text;
   }

   /**
    * converts an integer value to a 4 byte array.
    * @return a 4 byte array holding the integer value.
    * @param n  the integer value to be converted to a byte array.
    */
   public static byte[] intToBytes(int n)
   {
      byte[] buffer = new byte[4];

      buffer[0] = (byte)((n & 0xFF000000) >> 24);
      buffer[1] = (byte)((n & 0x00FF0000) >> 16);
      buffer[2] = (byte)((n & 0x0000FF00) >> 8);
      buffer[3] = (byte) (n & 0x000000FF);

      return buffer;
   }

   /**
    * converts 4 bytes to an integer.
    * @return the integer value obtained from converting the first four binary bytes of the given buffer.
    * @param buffer  the byte array from which the next four sequential bytes will be converted to an integer.
    */
   public static int bytesToInt(byte[] buffer)
   {
      return bytesToInt(buffer, 0);
   }

   /**
    * converts the 4 little endian bytes starting at the specified index to an integer.
    * @return the integer value obtained from starting at the specified index in the byte array and converting the next four sequential bytes.
    * @param bytes  the byte array from which the next four sequential bytes will be converted to an integer.
    * @param index  specifies that the bytes at index, index + 1, index +2, and index + 3 will be converted to an integer.
    */
   public static int littleEndianBytesToInt(byte[] bytes, int index)
   {
      return ((bytes[index] & 0xFF )) | ((bytes[index + 1] & 0xFF) << 8) | ((bytes[index + 2] & 0xFF) << 16) | ((bytes[index + 3] & 0xFF) << 24);
   }

   /**
    * converts the 4 bytes starting at the specified index to an integer.
    * @return the integer value obtained from starting at the specified index in the byte array and converting the next four sequential bytes.
    * @param buffer  the byte array from which the next four sequential bytes will be converted to an integer.
    * @param index   specifies that the bytes at index, index + 1, index +2, and index + 3 will be converted to an integer.
    */
   public static int bytesToInt(byte[] buffer, int index)
   {
      // note: java treats byte as a signed value, while the ID3v2.x spec treats bytes as unsigned.
      //       this necessitates converting each byte to a larger value (integer) and then shifting and adding them.
      return ((buffer[index] & 0xFF ) << 24) + ((buffer[index + 1] & 0xFF) << 16) + ((buffer[index + 2] & 0xFF) << 8) + (buffer[index + 3] & 0xFF);
   }

   /**
    * converts the 4 bytes starting at the specified index to a synchsafe integer.
    * @return the synchsafe integer value obtained from starting at the specified index in the byte array and converting the next four sequential bytes.
    * @param buffer  the byte array from which the next four sequential bytes will be converted to a synchsafe integer.
    * @param index   specifies that the bytes at index, index + 1, index +2, and index + 3 will be converted to a synchsafe integer.
    * @see "http://stackoverflow.com/questions/5223025/why-are-there-synchsafe-integer"
    */
   public static int bytesToSynchsafeInt(byte[] buffer, int index)
   {
      // note: java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
      //       this necessitates converting each byte to a larger value (integer) and then shifting and adding them.
      return ((buffer[index] & 0xFF ) << 21) + ((buffer[index + 1] & 0xFF) << 14) + ((buffer[index + 2] & 0xFF) << 7) + (buffer[index + 3] & 0xFF);
   }

   /**
    * converts a synchsafe integer value to a 4 byte array.
    * Synchsafe integers are integers that keep its highest bit (bit 7) zeroed, making seven bits out of eight available.
    * Thus a 32 bit synchsafe integer can store 28 bits of information.
    * @return a 4 byte array holding the synchsafe integer value.
    * @param n  the synchsafe integer value to be converted to a byte array.
    */
   public static byte[] synchsafeIntToBytes(int n)
   {
      byte[] buffer = new byte[4];

      buffer[0] = (byte)((n & 0x0FE00000) >> 21);
      buffer[1] = (byte)((n & 0x001FC000) >> 14);
      buffer[2] = (byte)((n & 0x00003F80) >> 7);
      buffer[3] = (byte) (n & 0x0000007F);

      return buffer;
   }

   /**
    * converts a short value to a 2 byte array.
    * @return a 2 byte array holding the short value.
    * @param n  the short value to be converted to a byte array.
    */
   public static byte[] shortToBytes(int n)
   {
      byte[] buffer = new byte[2];

      buffer[0] = (byte)((n & 0xFF00) >> 8);
      buffer[1] = (byte) (n & 0x00FF);

      return buffer;
   }

   /**
    * converts 2 bytes to a short.
    * @return the short value obtained from converting the first two binary bytes of the given data.
    * @param data  the byte array from which the next two sequential bytes will be converted to a short.
    */
   public static short bytesToShort(byte[] data)
   {
      return bytesToShort(data, 0);
   }

   /**
    * converts the 2 bytes starting at the specified index to a short.
    * @return the short value obtained from starting at the specified index in the byte array and converting the next two sequential bytes.
    * @param buffer  the byte array from which the next to sequential bytes will be converted to a short.
    * @param index   specifies that the bytes at index and at index + 1 will be converted to a short.
    */
   public static short bytesToShort(byte[] buffer, int index)
   {
      // note: java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
      //       this necessitates converting each byte to a larger value (integer) and then shifting and adding them.
      return (short)(((buffer[index] & 0xFF ) << 8) + (buffer[index + 1] & 0xFF));
   }

   /**
    * converts the 2 bytes starting at the specified index to a synchsafe short.
    * @return the synchsafe short value obtained from starting at the specified index in the byte array and converting the next two sequential bytes.
    * @param buffer  the byte array from which the next to sequential bytes will be converted to a synchsafe short.
    * @param index   specifies that the bytes at index and at index + 1 will be converted to a synchsafe short.
    */
   public static short bytesToSynchsafeShort(byte[] buffer, int index)
   {
      // note: java treats byte as a signed value, while the ID3v2.3 spec treats bytes as unsigned.
      //       this necessitates converting each byte to a larger value (integer) and then shifting and adding them.
      return (short)(((buffer[index] & 0xFF ) << 7) + (buffer[index + 1] & 0xFF));
   }
}
