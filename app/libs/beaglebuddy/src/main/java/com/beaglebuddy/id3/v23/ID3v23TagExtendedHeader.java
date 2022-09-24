package com.beaglebuddy.id3.v23;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyUtility;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * This class provides methods for reading and writing the optional ID3v2.3 tag extended header.  It contains fields that are not vital to the processing of the ID3v2.3 tag and hence are optional.
 * The ID3v2.3 tag extended header, if present, is at least 10 bytes long and directly follows the standard tag header.
 * </p>
 * <p class="beaglebuddy">
 * The structure of an .mp3 file containing an ID3v2.3 tag is shown below.</br/><br/>
 * <img src="../../../../resources/mp3_format_ID3v2.3.gif" height="550" width="330" alt="mp3 format containing an ID3v2.3 tag" usemap="#id3v23_map"/>
 * <map name="id3v23_map">
 *    <area shape="rect" coords=" 230, 145, 300, 165" href="ID3v23Tag.html"                  alt="ID3v2.3 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="ID3v23TagHeader.html"            alt="ID3v2.3 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="ID3v23TagExtendedHeader.html"    alt="ID3v2.3 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="ID3v23Frame.html"                alt="ID3v2.3 Frame""/>
 *    <area shape="rect" coords="   6, 287, 198, 374" href="../../mpeg/MPEGFrame.html"       alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 375, 198, 425" href="../../lyrics3/Lyrics3v2Tag.html" alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 426, 198, 479" href="../../ape/APETag.html"           alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 480, 198, 530" href="../v1/ID3v1Tag.html"             alt="ID3V1 Tag"/>
 * </map>
 * <br/><br/><br/>
 * </p>
 * <p class="beaglebuddy">
 * An ID3v2.3 extended header contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>ID3v2.3 Tag Extended Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">size          </td><td class="beaglebuddy">specifies the size, in bytes, of the extended header.                                                                                                </td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">CRCDataPresent</td><td class="beaglebuddy">flag indicating whether a CRC (Cyclical Redundancy Check) for the .mp3 file has been calculated and the result stored in in the <i>CRCData</i> field.</td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">paddingSize   </td><td class="beaglebuddy">specifies the number of bytes of padding stored after the ID3v2.3 tag and before the audio data in the .mp3 file.                                    </td></tr>
 *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">CRCData       </td><td class="beaglebuddy">an optional field which is valid only if the <i>CRCDataPresent</i> flag is set, this field contains the actual CRC (Cyclical Redundancy Check) data. </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://id3.org/id3v2.3.0"                       target="_blank">ID3v2.3 tag</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"               target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23TagExtendedHeader
{
   // class members
   private static final int  TAG_EXTENDED_HEADER_SIZE_FIELD_SIZE = 4;    // number of bytes in the extended header, not including the size field itself
   private static final int  TAG_EXTENDED_HEADER_SIZE            = 10;   // number of bytes in the base extended header
   private static final int  TAG_EXTENDED_HEADER_CRC_DATA_SIZE   = 4;    // number of bytes in the optional CRC data, if present.

   // extended header flag masks
   private static final byte TAG_EXTENDED_HEADER_CRC_MASK        = (byte)0x80;

   // data members
   private byte[]  extendedHeader;            // the raw bytes of the ID3v2.x tag extended header
   private boolean dirty;                     // whether the extended header has been modified

   // extended header fields
   private boolean CRCDataPresent;           // whether CRC data is present.
   private int     paddingSize;              // the size of the padding after the last frame and before the start of the audio data.
   private byte[]  CRCData;                  // raw binary data containing the CRC.




   /**
    * The default constructor is called when creating a new ID3v2.3 tag extended header.
    * The default values used are:
    * <ul>
    *    <li>extended header size is 0</li>
    *    <li>padding size is 0        </li>
    *    <li>no CRC                   </li>
    * </ul>
    */
   public ID3v23TagExtendedHeader()
   {
      extendedHeader = new byte[TAG_EXTENDED_HEADER_SIZE];
      CRCDataPresent = false;
      paddingSize    = 0;
      CRCData        = new byte[0];
      dirty          = true;   // the extended tag header has been created, but the values have not yet been written to the raw binary buffer
   }

   /**
    * This constructor is called when reading in an existing ID3v2.3 tag extended header from an .mp3 file.
    * @param inputStream   input stream pointing to the end of the beginning of the ID3v2.3 tag extended header in the .mp3 file.
    * @throws IOException  if the ID3v2.3 tag extended header can not be loaded from the .mp3 file.
    */
   public ID3v23TagExtendedHeader(InputStream inputStream) throws IOException
   {
      this();

      if (inputStream.read(extendedHeader) != TAG_EXTENDED_HEADER_SIZE)
         throw new IOException("Unable to read the ID3v2.3 tag extended header.");

      CRCDataPresent = (extendedHeader[4] & TAG_EXTENDED_HEADER_CRC_MASK) != 0;
      paddingSize    = ID3v23FrameBodyUtility.bytesToInt(extendedHeader, 6);

      // if CRC data is present, read it in
      if (CRCDataPresent)
      {
         int extendedHeaderSize = ID3v23FrameBodyUtility.bytesToInt(extendedHeader, 0);
         if (extendedHeaderSize != TAG_EXTENDED_HEADER_SIZE)
            throw new IllegalStateException("The ID3v2.3 tag extended header has the CRC data present flag set to true but the specified size is " + extendedHeaderSize + " bytes.  It must be " + TAG_EXTENDED_HEADER_SIZE + " bytes.");

         CRCData = new byte[TAG_EXTENDED_HEADER_CRC_DATA_SIZE];
         if (inputStream.read(CRCData) != TAG_EXTENDED_HEADER_CRC_DATA_SIZE)
            throw new IOException("Unable to read the ID3v2.3 CRC data from the ID3v2.3 extended tag header.");
      }
      setBuffer();
   }

   /**
    * indicates whether or not the tag's extended header's fields have been modified.
    * @return whether or not the extended header has been modified.
    * @see #setBuffer()
    */
   public boolean isDirty()
   {
      return dirty;
   }

   /**
    * if the tag's extended header's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, reset the dirty flag to indicate that the buffer is up to date, and the extended header is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      int extendedHeaderSize = TAG_EXTENDED_HEADER_SIZE - TAG_EXTENDED_HEADER_SIZE_FIELD_SIZE + (CRCDataPresent ? TAG_EXTENDED_HEADER_CRC_DATA_SIZE : 0);
      System.arraycopy(ID3v23FrameBodyUtility.intToBytes(extendedHeaderSize), 0, extendedHeader, 0, 4);
      System.arraycopy(ID3v23FrameBodyUtility.intToBytes(paddingSize       ), 0, extendedHeader, 6, 4);

      extendedHeader[4] = (CRCDataPresent ? TAG_EXTENDED_HEADER_CRC_MASK : 0x00);
      extendedHeader[5] = 0x00;

      this.dirty = false;
   }

   /**
    * gets    the total number of bytes in the ID3v2.3 tag extended header.
    * @return the total number of bytes in the ID3v2.3 tag extended header.
    */
   public int getSize()
   {
      if (dirty)
         setBuffer();

      return extendedHeader.length;
   }

   /**
    * gets the size of the padding, which is an area filled with 0's and is found after the end of the ID3v2.3 tag and before the actual audio portion of the .mp3 file.
    * @return the size of the padding which is found at the end of the ID3v2.3 tag.
    * @see #setPaddingSize(int)
    */
   public int getPaddingSize()
   {
     return paddingSize;
   }

   /**
    * sets the size of the padding, which is an area filled with 0's and found after the end of the ID3v2.3 tag and before the actual audio portion of the .mp3 file.
    * @param paddingSize   the size of the padding.
    * @see #getPaddingSize()
    */
   public void setPaddingSize(int paddingSize)
   {
      if (paddingSize < 0)
         throw new IllegalArgumentException("Invalid padding size, " + paddingSize + ". It must be >= 0.");

      if (this.paddingSize != paddingSize)
      {
         this.paddingSize = paddingSize;
         this.dirty       = true;
      }
   }

   /**
    * gets whether the ID3v2.3 tag extended header contains a Cyclic Redundancy Check (CRC).
    * @return true if the ID3v2.3 tag extended header contains an optional Cyclic Redundancy Check (CRC) and false otherwise.
    * @see #setPaddingSize(int)
    */
   public boolean isCRCDataPresent()
   {
     return CRCDataPresent;
   }

   /**
    * gets the CRC data.
    * @return the CRC data.
    * @exception IllegalStateException  if the <i>CRCDataPresent</i> flag is not set.
    * @see #isCRCDataPresent()
    * @see #setCRCData(byte[])
    */
   public byte[] getCRCData() throws IllegalStateException
   {
      if (!CRCDataPresent)
         throw new IllegalStateException("CRC Data may not be read from the ID3v2.3 extended tag header when the CRCDataPresent flag is false.");

      return CRCData;
   }

   /**
    * sets the CRC data and the <i>CRCDataPresent</i> flag.
    * @param CRCData   the data for the Cyclic Redundancy Check.  The CRC data will be cleared if the CRCdata is null or an empty byte array of length 0, ie new byte[0].
    *                  Otherwise, it must be a valid CRC and be 4 bytes in length.
    * @see #getCRCData()
    */
   public void setCRCData(byte[] CRCData)
   {
      if (CRCData == null || CRCData.length == 0)
      {
         this.CRCData        = new byte[0];
         this.CRCDataPresent = false;
      }
      else if (CRCData.length == TAG_EXTENDED_HEADER_CRC_DATA_SIZE)
      {
         this.CRCData        = CRCData;
         this.CRCDataPresent = true;
      }
      else
      {
         throw new IllegalArgumentException("Invalid CRC length, " + CRCData.length + " bytes.  It must be " + TAG_EXTENDED_HEADER_CRC_DATA_SIZE + " bytes long.");
      }
      this.dirty = true;
   }

   /**
    * save the ID3v2.3 tag extended header to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.3 tag extended header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 tag extended header to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.3 tag extended header has been modified and requires setBuffer() to be called before it can be saved.");

      outputStream.write(extendedHeader);
   }

   /**
    * save the ID3v2.3 tag extended header to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.3 tag extended header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 tag extended header to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.3 tag extended header has been modified and requires setBuffer() to be called before it can be saved.");

      file.write(extendedHeader);
   }

   /**
    * gets a string representation of the ID3v2.3 tag extended header showing the values of its fields.
    * @return a string representation of the ID3v2.3 tag extended header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.3 tag extended header"                                                              + "\n");
      buffer.append("   bytes.............................: " + getSize()                                      + " bytes\n");
      buffer.append("                                       " + ID3v23FrameBodyUtility.hex(extendedHeader, 38) + "\n");
      buffer.append("   padding size......................: " + paddingSize                                    + "\n");
      buffer.append("   crc data present..................: " + CRCDataPresent                                 + "\n");
      buffer.append("   CRC...............................: " + ID3v23FrameBodyUtility.hex(CRCData       , 38)       );

      return buffer.toString();
   }
}
