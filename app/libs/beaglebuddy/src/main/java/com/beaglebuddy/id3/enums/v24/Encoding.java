package com.beaglebuddy.id3.enums.v24;

import java.nio.charset.Charset;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * A character set encoding indicates how to interpret bytes when constructing or displaying a String.
 * A list of standard character sets can be found at <a href="http://www.iana.org/assignments/character-sets">IANA Character sets</a>.
 * The ID3v2.4 specification allows only four of these encodings to be used.
 * For more information about character set encodings, see the javadocs for the java.nio.charset.Charset class.
 * @see <a href="http://en.wikipedia.org/wiki/Byte_order_mark" target="_blank">Byte Order mark</a>
 */
public enum Encoding
{                                                              /** standard ascii character set                              - null terminator = 0x00      */
   ISO_8859_1("ISO-8859-1", Charset.forName("ISO-8859-1"), 1), /** 2 byte unicode character set with    BOM (0xFF 0xFE)      - null terminator = 0x00 0x00 */
   UTF_16    ("UTF-16"    , Charset.forName("UTF-16")    , 2), /** 2 byte unicode character set without BOM                  - null terminator = 0x00 0x00 */
   UTF_16BE  ("UTF-16BE"  , Charset.forName("UTF-16BE")  , 2), /** 1 byte unicode character set with    BOM (0xEF 0xBB 0xBF) - null terminator = 0x00      */
   UTF_8     ("UTF-8"     , Charset.forName("UTF-8")     , 1);

   // data members
   private String  name;
   private Charset characterSet;
   private int     numBytesInNullTerminator;


   /**
    * constructor.
    * @param name                       name of the encoding.
    * @param characterSet               character set implementing the encoding.
    * @param numBytesInNullTerminator   number of bytes the encoding uses to represent a null terminator character.
    */
   private Encoding(String name, Charset characterSet, int numBytesInNullTerminator)
   {
      this.name                     = name;
      this.characterSet             = characterSet;
      this.numBytesInNullTerminator = numBytesInNullTerminator;
   }

   /**
    * gets the name of the encoding.
    * @return the name of the encoding.
    */
   public String getName()
   {
      return name;
   }

   /**
    * gets the character set implementing the encoding.
    * @return the character set implementing the encoding.
    */
   public Charset getCharacterSet()
   {
      return characterSet;
   }

   /**
    * gets the number of bytes the encoding uses to represent a null terminator character.
    * @return the number of bytes the encoding uses to represent a null terminator character.
    */
   public int getNumBytesInNullTerminator()
   {
      return numBytesInNullTerminator;
   }

   /**
    * convert an integral value to its corresponding ID3v2.4 encoding enum.
    * @return the ID3v2.4 Encoding enum corresponding to the integral value.
    * @param encoding  integral value to be converted to an ID3v2.4 Encoding enum.
    * @throws IllegalArgumentException   if the integral value does not correspond to a valid ID3v2.4 Encoding.
    */
   public static Encoding valueOf(byte encoding) throws IllegalArgumentException
   {
      for (Encoding e : Encoding.values())
         if (encoding == e.ordinal())
            return e;
      throw new IllegalArgumentException("Invalid ID3v2.4 encoding " + encoding + ".  It must be either 0, 1, 2, or 3.");
   }

   /**
    * gets a string representation of the encoding enum.
    * @return a string representation of the encoding enum.
    */
   public String toString()
   {
      return "" + ordinal() + " - " + name;
   }
}
