package com.beaglebuddy.ape;

import java.io.UnsupportedEncodingException;
import com.beaglebuddy.util.Utility;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This class provides methods for reading the APE Items in an {@link APETag APE tag}.  An Item consists of a {@link #getKey() key}/{@link #getTextValue() value} pair.
 * While the key part of an item is always a <i>String</i>, the value may be a <i>String</i> or it may be raw <i>binary</i> data, such as an image file.  The sample code
 * shown in the {@link APETag} demonstrates how to read the values stored in items an APE tag.  The APE specification(s) have defined a standardized
 * <a href="http://wiki.hydrogenaud.io/index.php?title=APE_key" target="_blank">list of keys</a>, but users are not restricted to this list.
 * </p>
 * <p>
 * <img src="../../../resources/ape_format.jpg" height="280" width="315" alt="APE Tag format" usemap="#ape_map"/>
 * <map name="ape_map">
 *    <area shape="rect" coords="  47,   9, 150,  25" href="APETag.html"      alt="APE Tag"/>
 *    <area shape="rect" coords=" 245, 134, 307, 150" href="APEItem.html"     alt="APE Tag Item"/>
 *    <area shape="rect" coords="  10,  40, 200,  74" href="APEHeader.html"   alt="APE Tag Header"/>
 *    <area shape="rect" coords="  10,  75, 200, 225" href="APEItem.html"     alt="APE Tag Item"/>
 *    <area shape="rect" coords="  10, 226, 200, 260" href="APEFooter.html"   alt="APE Tag Footer"/>
 * </map>
 * </p>
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv1_specification" target="_blank">APEv1 Specification</a>
 * @see <a href="http://wiki.hydrogenaud.io/index.php?title=APEv2_specification" target="_blank">APEv2 Specification</a>
 */
public class APEItem
{
   // class mnemonics
   private static final int  SIZE_VALUE = 4;    // value size field is 4 bytes long
   private static final int  SIZE_FLAGS = 4;    // flags      field is 4 bytes long

   // data members
   private int      size;     // size of APE item in bytes
   private APEFlags flags;    // flags
   private String   key;      // key associated with the binary/text value.
   private byte[]   value;    // used to hold the value which may either be binary data or a UTF-8 string



   /**
    * constructor.
    * @param bytes   APE Item bytes.
    * @param index   index into the byte array.
    */
   public APEItem(byte[] bytes, int index)
   {      int valueLength = Utility.littleEndianBytesToInt(bytes, index);
      flags = new APEFlags(bytes, index + SIZE_VALUE);
      key   = getString(bytes, index + SIZE_VALUE + SIZE_FLAGS);
      size  = SIZE_VALUE + SIZE_FLAGS + key.length() + 1 + valueLength;
      value = new byte[valueLength];
      System.arraycopy(bytes, index + SIZE_VALUE + SIZE_FLAGS + key.length() + 1, value, 0, value.length);
   }

   /**
    * constructs a string using the "ISO-8859-1" character encoding from the given bytes, starting starting at the specified index and ending at the first null character, 0x00.
    * @param bytes   bytes from an APE tag.
    * @param index   offset into the byte stream where the String begins.
    * @return the String formed from the given bytes until the first null terminator in the data.
    */
   private static String getString(byte[] bytes, int index)
   {
      int end=0;
      for(end=index; end < bytes.length && bytes[end] != 0; ++end);

      return new String(bytes, index, end - index);
   }

   /**
    * gets the size (inn bytes) of the APE item.
    * @return the size (inn bytes) of the APE item.
    */
   public int getSize()
   {
      return size;
   }

   /**
    * gets the flags associated with the APE item.
    * @return the flags associated with the APE item.
    */
   public APEFlags getFlags()
   {
      return flags;
   }

   /**
    * gets the key associated with the text/binary value.  The key is encoded as ISO-8859-1 text.
    * @return the key associated with the text/binary value.
    */
   public String getKey()
   {
      return key;
   }

   /**
    * gets the type of data stored in the value.
    * @return the type of data stored in the value.
    */
   public APEFlags.Type getType()
   {
      return flags.getType();
   }

   /**
    * gets whether the value is text.
    * @return whether the value is text.
    */
   public boolean isValueText()
   {
      return flags.getType() != APEFlags.Type.BINARY;
   }

   /**
    * gets whether the value is binary.
    * @return whether the value is binary.
    */
   public boolean isValueBinary()
   {
      return flags.getType() == APEFlags.Type.BINARY;
   }

   /**
    * gets the value as UTF-8 encoded text.
    * @return the value as UTF-8 encoded text.
    */
   public String getTextValue()
   {
      try
      {
         return new String(value, "UTF-8");
      }
      catch (UnsupportedEncodingException ex)
      {
         return new String(value);
      }
   }

   /**
    * gets the value as binary.
    * @return the value as binary.
    */
   public byte[] getBinaryValue()
   {
      return value;
   }

   /**
    * gets a string representation of the APE item.
    * @return a string representation of the APE item.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append(      "item"            + "\n");
      buffer.append("      size.: " + size  + " bytes\n");
      buffer.append("      key..: " + key   + "\n");
      buffer.append("      value: " + (flags.getType() == APEFlags.Type.BINARY ? Utility.hex(value, 13) : getTextValue()) + "\n");
      buffer.append("      flags: " + flags       );

      return buffer.toString();
   }
}
