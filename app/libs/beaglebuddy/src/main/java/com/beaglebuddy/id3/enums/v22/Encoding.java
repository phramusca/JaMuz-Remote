package com.beaglebuddy.id3.enums.v22;

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
 * The ID3v2.2 specification allows only two of these encodings to be used.
 * For more information about character set encodings, see the javadocs for the java.nio.charset.Charset class.
 */
public enum Encoding
{                                                              /** standard ascii character set */
   ISO_8859_1("ISO-8859-1", Charset.forName("ISO-8859-1"), 1), /** 2 byte unicode character set */
   UTF_16    ("UTF-16"    , Charset.forName("UTF-16")    , 2);

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
    * gets number of bytes the encoding uses to represent a null terminator character.
    * @return the number of bytes the encoding uses to represent a null terminator character.
    */
   public int getNumBytesInNullTerminator()
   {
      return numBytesInNullTerminator;
   }

   /**
    * convert an integral value to its corresponding ID3v2.2 encoding enum.
    * @return the ID3v2.2 Encoding enum corresponding to the integral value.
    * @param encoding  integral value to be converted to an ID3v2.2 Encoding enum.
    * @throws IllegalArgumentException   if the integral value does not correspond to a valid ID3v2.2 Encoding.
    */
   public static Encoding valueOf(byte encoding) throws IllegalArgumentException
   {
      for (Encoding e : Encoding.values())
         if (encoding == e.ordinal())
            return e;
      throw new IllegalArgumentException("Invalid ID3v2.2 encoding " + encoding + ".  It must be either 0 or 1.");
   }

   /**
    * gets  a string representation of the encoding enum.
    * @return a string representation of the encoding enum.
    */
   public String toString()
   {
      return "" + ordinal() + " - " + name;
   }
}
