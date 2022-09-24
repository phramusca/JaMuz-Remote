package com.beaglebuddy.id3.enums;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * The ID3v2.x tag versions available in .mp3 files.
 */
public enum ID3TagVersion
{
                                                                                                 /** The .mp3 file does not contain an ID3v2.x tag.                                             */
   NONE          ("none"          , (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00), /** The .mp3 file contains an <a href="http://id3.org/id3v2-00"           >ID3v2.2</a> tag.    */
   ID3V2_2       ("ID3v2.2"       , (byte)0x49, (byte)0x44, (byte)0x33, (byte)0x02, (byte)0x00), /** The .mp3 file contains an <a href="http://id3.org/id3v2.3.0"          >ID3v2.3</a> tag.    */
   ID3V2_3       ("ID3v2.3"       , (byte)0x49, (byte)0x44, (byte)0x33, (byte)0x03, (byte)0x00), /** The .mp3 file contains an <a href="http://id3.org/id3v2.4.0-structure">ID3v2.4</a> tag.    */
   ID3V2_4       ("ID3v2.4"       , (byte)0x49, (byte)0x44, (byte)0x33, (byte)0x04, (byte)0x00), /** The .mp3 file contains an <a href="http://id3.org/id3v2.4.0-structure">ID3v2.4 footer</a>. */
   ID3V2_4_FOOTER("ID3v2.4 footer", (byte)0x33, (byte)0x44, (byte)0x49, (byte)0x04, (byte)0x00);

   // class members
                                                                                                 /** The number of bytes which identifies an ID3v2.x tag.                                       */
   public static final int NUM_ID_BYTES = 5;

   // data members
   private String description;          // description of the ID3v2.x tag version.
   private byte[] idBytes;              // byte sequence which uniquely identifies the ID3v2.x tag version


   /**
    * constructor.
    * @param description   description of the ID3v2.x version.
    * @param idByte0       first  byte in byte sequence which uniquely identifies the ID3v2.x tag version.
    * @param idByte1       second byte in byte sequence which uniquely identifies the ID3v2.x tag version.
    * @param idByte2       third  byte in byte sequence which uniquely identifies the ID3v2.x tag version.
    * @param idByte3       fourth byte in byte sequence which uniquely identifies the ID3v2.x tag version.
    * @param idByte4       fifth  byte in byte sequence which uniquely identifies the ID3v2.x tag version.
    */
   private ID3TagVersion(String description, byte idByte0, byte idByte1, byte idByte2, byte idByte3, byte idByte4)
   {
      this.description = description;
      this.idBytes       = new byte[NUM_ID_BYTES];
      this.idBytes[0]    = idByte0;
      this.idBytes[1]    = idByte1;
      this.idBytes[2]    = idByte2;
      this.idBytes[3]    = idByte3;
      this.idBytes[4]    = idByte4;
   }

   /**
    * read in the ID3v2.x id bytes from the mp3 file and see if an ID3v2.x header has been found.
    * @return the version of the ID3v2.x tag found in the mp3 file, including none, if no tag is found.
    * @param inputStream   input stream pointing to the beginning of an ID3v2.x header in an mp3 file.
    * @throws IOException  if the version bytes can not be read from the input stream.
    */
   public static ID3TagVersion readVersion(InputStream inputStream) throws IOException
   {
      byte[] buffer = new byte[NUM_ID_BYTES];

      // see if the mp3 file contains an ID3v2.x tag
      return inputStream.read(buffer) == buffer.length ? getVersion(buffer) : ID3TagVersion.NONE;
   }

   /**
    * gets description of the ID3v2.x version.
    * @return description of the ID3v2.x version.
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * gets the bytes which uniquely identify the version of an ID3v2.x tag.
    * @return the bytes which uniquely identify the version of an ID3v2.x tag.
    */
   public byte[] getIdBytes()
   {
      return idBytes;
   }

   /**
    * determines if the specified id bytes are valid ID3v2.x id bytes, and if so, which version they represent.
    * @return the ID3v2.x tag version, or NONE, if the id bytes do not contain a valid ID3v2.x tag id.
    * @param idBytes  id bytes from an .mp3 tag.
    */
   public static final ID3TagVersion getVersion(byte[] idBytes)
   {
      for (ID3TagVersion v : ID3TagVersion.values())
         if (Arrays.equals(idBytes, v.getIdBytes()))
            return v;
      return NONE;
   }

   /**
    * gets a string representation of the ID3v2.x version.
    * @return a string representation of the ID32.x version.
    */
   public String toString()
   {
      return description;
   }
}
