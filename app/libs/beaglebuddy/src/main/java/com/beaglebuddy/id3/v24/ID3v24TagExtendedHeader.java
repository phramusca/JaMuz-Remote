package com.beaglebuddy.id3.v24;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import com.beaglebuddy.id3.enums.v24.ImageEncodingRestriction;
import com.beaglebuddy.id3.enums.v24.ImageSizeRestriction;
import com.beaglebuddy.id3.enums.v24.TagSizeRestriction;
import com.beaglebuddy.id3.enums.v24.TextEncodingRestriction;
import com.beaglebuddy.id3.enums.v24.TextSizeRestriction;
import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyUtility;



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
 * This class provides methods for reading and writing the optional ID3v2.4 tag extended header.  It contains fields that are not vital to the processing of the ID3v2.4 tag and hence is optional.
 * The ID3v2.4 tag extended header, if present, is at least 10 bytes long and directly follows the standard tag header.
 * </p>
 * <p class="beaglebuddy">
 * The structure of an .mp3 file containing an ID3v2.4 tag is shown below.</br/><br/>
 * <img src="../../../../resources/mp3_format_ID3v2.4.gif" height="580" width="330" alt="mp3 format containing an ID3v2.4 tag" usemap="#id3v24_map"/>
 * <map name="id3v24_map">
 *    <area shape="rect" coords=" 230, 170, 300, 185" href="ID3v24Tag.html"                  alt="ID3v2.4 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="ID3v24TagHeader.html"            alt="ID3v2.4 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="ID3v24TagExtendedHeader.html"    alt="ID3v2.4 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="ID3v24Frame.html"                alt="ID3v2.4 Frame""/>
 *    <area shape="rect" coords="   6, 287, 198, 321" href="ID3v24TagFooter.html"            alt="ID3v2.4 Tag Footer"/>
 *    <area shape="rect" coords="   6, 322, 198, 410" href="../../mpeg/MPEGFrame.html"       alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 411, 198, 463" href="../../lyrics3/Lyrics3v2Tag.html" alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 463, 198, 515" href="../../ape/APETag.html"           alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 516, 198, 564" href="../v1/ID3v1Tag.html"             alt="ID3V1 Tag"/>
 * </map>
 * <br/><br/><br/>
 * </p>
 * <p class="beaglebuddy">
 * An ID3v2.4 extended header contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>ID3v2.4 Tag Extended Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">size                     </td><td class="beaglebuddy">specifies the size, in bytes, of the extended header.                                                                                                                </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">tagIsAnUpdate            </td><td class="beaglebuddy">this flag indicates whether the tag is an update of a tag found earlier in the present .mp3 file or stream.                                                          </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">CRCDataPresent           </td><td class="beaglebuddy">this flag indicates whether the CRC for the .mp3 file has been calculated and the result stored in the <i>CRCData</i> field.                                         </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">tagHasRestrictions       </td><td class="beaglebuddy">this flag indicates whether whether the ID3v2.4 tag had any restrictions when it was encoded.                                                                        </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">CRCData                  </td><td class="beaglebuddy">valid only if the <i>CRCDataPresent</i>     flag is set, this field contains the actual CRC data for the .mp3 file.                                                  </td></tr>
 *       <tr><td class="beaglebuddy">6. </td><td class="beaglebuddy">tagSizeRestriction       </td><td class="beaglebuddy">valid only if the <i>tagHasRestrictions</i> flag is set, this field specifies the restrictions on the size of the ID3v2.4 tag and on the number of frames in the tag.</td></tr>
 *       <tr><td class="beaglebuddy">7. </td><td class="beaglebuddy">textEncodingRestriction  </td><td class="beaglebuddy">valid only if the <i>tagHasRestrictions</i> flag is set, this field specifies the restrictions on the which character sets may be used to encode strings.            </td></tr>
 *       <tr><td class="beaglebuddy">8. </td><td class="beaglebuddy">textSizeRestriction      </td><td class="beaglebuddy">valid only if the <i>tagHasRestrictions</i> flag is set, this field specifies the restrictions on the size of text strings within the ID3v2.4 tag.                   </td></tr>
 *       <tr><td class="beaglebuddy">9. </td><td class="beaglebuddy">imageEncodingRestrictiond</td><td class="beaglebuddy">valid only if the <i>tagHasRestrictions</i> flag is set, this field specifies the restrictions on the which image formats may be used to encode images.              </td></tr>
 *       <tr><td class="beaglebuddy">10.</td><td class="beaglebuddy">imageSizeRestriction     </td><td class="beaglebuddy">valid only if the <i>tagHasRestrictions</i> flag is set, this field specifies the restrictions on the dimensions of images within the ID3v2.4 tag.                   </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://id3.org/id3v2.4.0-structure" target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"   target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24TagExtendedHeader
{
   // class members
   private static final int  TAG_EXTENDED_HEADER_BASE_SIZE                        = 6;             // number of bytes in the base extended header
   private static final int  TAG_EXTENDED_HEADER_NUM_FLAG_BYTES                   = 1;             // number of bytes used to store the flag in the extended header
   private static final byte TAG_EXTENDED_HEADER_TAG_IS_UPDATE_DATA_LENGTH        = (byte)0x00;    // number of bytes in the tag is update       , if present
   private static final byte TAG_EXTENDED_HEADER_CRC_DATA_LENGTH                  = (byte)0x05;    // number of bytes in the optional CRC data   , if present.
   private static final byte TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_DATA_LENGTH     = (byte)0x01;    // number of bytes in the tag has restrictions, if present

   // masks which define what each bit of the extended header flag byte represents
   private static final byte TAG_EXTENDED_HEADER_TAG_IS_UPDATE_MASK               = (byte)0x40;
   private static final byte TAG_EXTENDED_HEADER_CRC_MASK                         = (byte)0x20;
   private static final byte TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_MASK            = (byte)0x10;

   private static final byte TAG_EXTENDED_HEADER_RESTRICTIONS_TAG_SIZE_MASK       = (byte)0xC0;
   private static final byte TAG_EXTENDED_HEADER_RESTRICTIONS_TEXT_ENCODING_MASK  = (byte)0x20;
   private static final byte TAG_EXTENDED_HEADER_RESTRICTIONS_TEXT_SIZE_MASK      = (byte)0x18;
   private static final byte TAG_EXTENDED_HEADER_RESTRICTIONS_IMAGE_ENCODING_MASK = (byte)0x04;
   private static final byte TAG_EXTENDED_HEADER_RESTRICTIONS_IMAGE_SIZE_MASK     = (byte)0x03;

   // extended header fields
   private boolean                  tagIsAnUpdate;               // whether the tag is an update of a tag found earlier in the present file or stream.   0 bytes data.
   private boolean                  CRCDataPresent;              // whether CRC data is present.                                                         5 bytes data.
   private boolean                  tagHasRestrictions;          // whether the tag had any restrictions before encoding.                                1 byte data.

   private byte[]                   CRCData;                     // raw binary data containing the CRC.
   private TagSizeRestriction       tagSizeRestriction;          // restrictions on the size of the ID3v2.4 tag and on the number of frames in the tag.
   private TextEncodingRestriction  textEncodingRestriction;     // restrictions on the which character sets may be used to encode strings.
   private TextSizeRestriction      textSizeRestriction;         // restrictions on the size of text strings within the ID3v2.4 tag.
   private ImageEncodingRestriction imageEncodingRestriction;    // restrictions on the which image formats may be used to encode images.
   private ImageSizeRestriction     imageSizeRestriction;        // restrictions on the dimensions of images within the ID3v2.4 tag.

   // data members
   private byte[]  extendedHeader;            // the raw bytes of the ID3v2.x tag extended header
   private boolean dirty;                     // whether the extended header has been modified




   /**
    * The default constructor is called when creating a new ID3v2.4 tag extended header.
    * The default values used are:
    * <ul>
    *    <li>tag is not an update          </li>
    *    <li>tag does not have restrictions</li>
    *    <li>no CRC                        </li>
    * </ul>
    */
   public ID3v24TagExtendedHeader()
   {
      extendedHeader           = new byte[TAG_EXTENDED_HEADER_BASE_SIZE];
      tagIsAnUpdate            = false;
      CRCDataPresent           = false;
      tagHasRestrictions       = false;
      CRCData                  = new byte[0];
      tagSizeRestriction       = null;
      textEncodingRestriction  = null;
      textSizeRestriction      = null;
      imageEncodingRestriction = null;
      imageSizeRestriction     = null;
      dirty                    = true;  // the extended tag header has been created, but the values have not yet been written to the raw binary buffer
   }

   /**
    * This constructor is called when reading in an existing ID3v2.tag 4 extended header from an .mp3 file.
    * @param inputStream   input stream pointing to the end of the beginning of the ID3v2.4 tag extended header in the .mp3 file.
    * @throws IOException  if the ID3v2.4 tag extended header can not be loaded from the .mp3 file.
    */
   public ID3v24TagExtendedHeader(InputStream inputStream) throws IOException
   {
      this();

      if (inputStream.read(extendedHeader) != TAG_EXTENDED_HEADER_BASE_SIZE)
         throw new IOException("Unable to read the ID3v2.4 tag extended header.");

      if (extendedHeader[4] != TAG_EXTENDED_HEADER_NUM_FLAG_BYTES)
         throw new IOException("Invalid value for the number of flag bytes, " + extendedHeader[0] + " in the ID3v2.4 extended tag header.  It must be " + TAG_EXTENDED_HEADER_NUM_FLAG_BYTES + ".");

      tagIsAnUpdate      = (extendedHeader[5] & TAG_EXTENDED_HEADER_TAG_IS_UPDATE_MASK   ) != 0;
      CRCDataPresent     = (extendedHeader[5] & TAG_EXTENDED_HEADER_CRC_MASK             ) != 0;
      tagHasRestrictions = (extendedHeader[5] & TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_MASK) != 0;

      int  extendedHeaderSize = ID3v24FrameBodyUtility.bytesToSynchsafeInt(extendedHeader, 0);
      byte dataLength         = (byte)0x00;

      if (tagIsAnUpdate)
      {
         // read in the data length byte, which will be 0x00
         dataLength = (byte)inputStream.read();
         if (dataLength == -1)
            throw new IOException("Unable to read the ID3v2.4 \"tag is an update\" data length byte from the ID3v2.4 extended tag header.");
         if (dataLength != TAG_EXTENDED_HEADER_TAG_IS_UPDATE_DATA_LENGTH)
            throw new IOException("Invalid \"tag is an update\" data length value, " + dataLength + ", read from the ID3v2.4 tag extended header.  It must be " + TAG_EXTENDED_HEADER_TAG_IS_UPDATE_DATA_LENGTH + ".");
      }

      // if CRC data is present, read it in
      if (CRCDataPresent)
      {
         dataLength = (byte)inputStream.read();
         if (dataLength == -1)
            throw new IOException("Unable to read the ID3v2.4 \"crc data present\" data length byte from the ID3v2.4 extended tag header.");
         if (dataLength != TAG_EXTENDED_HEADER_CRC_DATA_LENGTH)
            throw new IOException("Invalid \"crc data present\" data length value, " + dataLength + ", read from the ID3v2.4 tag extended header.  It must be " + TAG_EXTENDED_HEADER_CRC_DATA_LENGTH + ".");

         CRCData = new byte[TAG_EXTENDED_HEADER_CRC_DATA_LENGTH];
         if (inputStream.read(CRCData) != TAG_EXTENDED_HEADER_CRC_DATA_LENGTH)
            throw new IOException("Unable to read the CRC data from the ID3v2.4 extended tag header.");

         if (extendedHeaderSize < (2 + TAG_EXTENDED_HEADER_CRC_DATA_LENGTH))
            throw new IllegalStateException("The ID3v2.4 tag extended header has the CRC data present flag set to true but the specified size is " + extendedHeaderSize + ".");
      }

      // if the tag has restrictions, read in the restriction flag
      if (tagHasRestrictions)
      {
         dataLength = (byte)inputStream.read();
         if (dataLength == -1)
            throw new IOException("Unable to read the ID3v2.4 \"tag has restrictions\" data length byte from the ID3v2.4 extended tag header.");
         if (dataLength != TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_DATA_LENGTH)
            throw new IOException("Invalid \"tag has restrictions\" data length value, " + dataLength + ", read from the ID3v2.4 tag extended header.  It must be " + TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_DATA_LENGTH + ".");

         byte flag = (byte)0x00;
         if ((flag = (byte)inputStream.read()) == -1)
            throw new IOException("Unable to read the \"tag has restrictions\" flag from the ID3v2.4 extended tag header.");

         tagSizeRestriction       = TagSizeRestriction      .valueOf((flag & TAG_EXTENDED_HEADER_RESTRICTIONS_TAG_SIZE_MASK      ) >> 6);
         textEncodingRestriction  = TextEncodingRestriction .valueOf((flag & TAG_EXTENDED_HEADER_RESTRICTIONS_TEXT_ENCODING_MASK ) >> 5);
         textSizeRestriction      = TextSizeRestriction     .valueOf((flag & TAG_EXTENDED_HEADER_RESTRICTIONS_TEXT_SIZE_MASK     ) >> 3);
         imageEncodingRestriction = ImageEncodingRestriction.valueOf((flag & TAG_EXTENDED_HEADER_RESTRICTIONS_IMAGE_ENCODING_MASK) >> 2);
         imageSizeRestriction     = ImageSizeRestriction    .valueOf((flag & TAG_EXTENDED_HEADER_RESTRICTIONS_IMAGE_SIZE_MASK    )     );
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
      int extendedHeaderSize = TAG_EXTENDED_HEADER_BASE_SIZE                                                  +
                              (tagIsAnUpdate      ? 1 + TAG_EXTENDED_HEADER_TAG_IS_UPDATE_DATA_LENGTH    : 0) +
                              (CRCDataPresent     ? 1 + TAG_EXTENDED_HEADER_CRC_DATA_LENGTH              : 0) +
                              (tagHasRestrictions ? 1 + TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_DATA_LENGTH : 0);
      extendedHeader = new byte[extendedHeaderSize];

      // save the size of the extended header (not including the 4 bytes used for the size field)
      System.arraycopy(ID3v24FrameBodyUtility.synchsafeIntToBytes(extendedHeaderSize - 4), 0, extendedHeader, 0, 4);

      extendedHeader[4] = (byte)TAG_EXTENDED_HEADER_NUM_FLAG_BYTES;
      extendedHeader[5] = (byte)(tagIsAnUpdate      ? extendedHeader[5] | TAG_EXTENDED_HEADER_TAG_IS_UPDATE_MASK    : extendedHeader[5] & ~TAG_EXTENDED_HEADER_TAG_IS_UPDATE_MASK   );
      extendedHeader[5] = (byte)(CRCDataPresent     ? extendedHeader[5] | TAG_EXTENDED_HEADER_CRC_MASK              : extendedHeader[5] & ~TAG_EXTENDED_HEADER_CRC_MASK             );
      extendedHeader[5] = (byte)(tagHasRestrictions ? extendedHeader[5] | TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_MASK : extendedHeader[5] & ~TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_MASK);

      int index = 6;
      if (tagIsAnUpdate)
      {
         extendedHeader[index] = TAG_EXTENDED_HEADER_TAG_IS_UPDATE_DATA_LENGTH;
         index++;
      }

      if (CRCDataPresent)
      {
         extendedHeader[index] = TAG_EXTENDED_HEADER_CRC_DATA_LENGTH;
         index++;
         System.arraycopy(CRCData, 0, extendedHeader, index, TAG_EXTENDED_HEADER_CRC_DATA_LENGTH);
         index += TAG_EXTENDED_HEADER_CRC_DATA_LENGTH;
      }

      if (tagHasRestrictions)
      {
         extendedHeader[index] = TAG_EXTENDED_HEADER_TAG_RESTRICTIONS_DATA_LENGTH;
         index++;
         extendedHeader[index] = 0x00;   // reset all tag restriction flags

         if (tagSizeRestriction       != null) extendedHeader[index] = (byte)(extendedHeader[index] | tagSizeRestriction      .getMask());
         if (textEncodingRestriction  != null) extendedHeader[index] = (byte)(extendedHeader[index] | textEncodingRestriction .getMask());
         if (textSizeRestriction      != null) extendedHeader[index] = (byte)(extendedHeader[index] | textSizeRestriction     .getMask());
         if (imageEncodingRestriction != null) extendedHeader[index] = (byte)(extendedHeader[index] | imageEncodingRestriction.getMask());
         if (imageSizeRestriction     != null) extendedHeader[index] = (byte)(extendedHeader[index] | imageSizeRestriction    .getMask());
      }
      this.dirty = false;
   }

   /**
    * gets    the total number of bytes in the ID3v2.4 tag extended header.
    * @return the total number of bytes in the ID3v2.4 tag extended header.
    */
   public int getSize()
   {
      if (dirty)
         setBuffer();

      return extendedHeader.length;
   }

   /**
    * get whether this ID3v2.4 tag is an update of a tag found earlier in the .mp3 file or stream.
    * @return whether this ID3v2.4 tag is an update of a tag found earlier in the .mp3 file or stream.
    * @throws IllegalStateException  if the <i>extendedHeaderPresent</i> flag is not true.  The <i>extendedHeaderPresent</i> flag must be true in order to call this method.
    * @see #setTagIsAnUpdate(boolean)
    */
   public boolean isTagIsAnUpdate() throws IllegalStateException
   {
      return tagIsAnUpdate;
   }

   /**
    * set whether this ID3v2.4 tag is an update of a tag found earlier in the .mp3 file or stream. If frames defined as unique are found in the current tag, they are to override any
    * corresponding ones found in the earlier ID3v2.4 tag.
    * @param tagIsAnUpdate   whether this ID3v2.4 tag is an update of a tag found earlier in the .mp3 file or stream.
    * @see #isTagIsAnUpdate()
    */
   public void setTagIsAnUpdate(boolean tagIsAnUpdate)
   {
      this.tagIsAnUpdate = tagIsAnUpdate;
      this.dirty         = true;
   }

   /**
    * get whether this ID3v2.4 tag has a Cyclic Redundancy Check (CRC-32 [ISO-3309]).
    * @return whether this ID3v2.4 tag contains CRC data.
    * @see #setCRCDataPresent(boolean)
    */
   public boolean isCRCDataPresent()
   {
      return CRCDataPresent;
   }

   /**
    * set whether this ID3v2.4 tag contains CRC data.
    * corresponding ones found in the earlier ID3v2.4 tag.
    * @param CRCDataPresent   whether this ID3v2.4 tag contains CRC data.
    * @see #isTagIsAnUpdate()
    */
   public void setCRCDataPresent(boolean CRCDataPresent)
   {
      this.CRCDataPresent = CRCDataPresent;
      if (!CRCDataPresent)
         CRCData = new byte[0];
   }

   /**
    * gets the CRC data for this .mp3 file..
    * @return the CRC data for this .mp3 file..
    * @throws IllegalStateException  if the <i>CRCDataPresent</i> flag is not set.
    * @see #setCRCData(byte[])
    */
   public byte[] getCRCData() throws IllegalStateException
   {
      if (!CRCDataPresent)
         throw new IllegalStateException("You may not get the \"CRC data\" field in the ID3v2.4 extended tag header when the CRCDataPresent flag is false.");

     return CRCData;
   }

   /**
    * sets the CRC data for this .mp3 file.  The CRC is calculated on all the data between the header and footer as indicated by the header's tag length field, minus the extended header.
    * Note that this includes the padding (if there is any), but excludes the footer. The CRC-32 is stored as a 35 bit synchsafe integer, leaving the upper four bits always zeroed.
    * @param CRCData   the CRC data for this .mp3 file.
    * @see #getCRCData()
    */
   public void setCRCData(byte[] CRCData)
   {
      if (CRCData == null || CRCData.length == 0)
      {
         this.CRCData   = new byte[0];
         CRCDataPresent = false;
      }
      else if (CRCData.length == TAG_EXTENDED_HEADER_CRC_DATA_LENGTH)
      {
         this.CRCData   = CRCData;
         CRCDataPresent = true;
      }
      else
      {
         throw new IllegalArgumentException("Invalid CRC data length, " + CRCData.length + ".  It must be " + TAG_EXTENDED_HEADER_CRC_DATA_LENGTH + " bytes long.");
      }
      this.dirty = true;
   }

   /**
    * @return if the ID3v2.4 extended header has tag restrictions.
    * @see #setTagHasRestrictions(boolean)
    */
   public boolean tagHasRestrictions()
   {
      return tagHasRestrictions;
   }

   /**
    * set whether the ID3v2.4 tag has any restrictions.
    * @param tagHasRestrictions  whether the ID3v2.4 tag has any restrictions.
    * @see #tagHasRestrictions()
    */
   public void setTagHasRestrictions(boolean tagHasRestrictions)
   {
      if (this.tagHasRestrictions != tagHasRestrictions)
      {
         this.tagHasRestrictions = true;                      // turn on restrictions so that all of the restricted settings can be reset

         setTagSizeRestriction      (TagSizeRestriction      .TAG_SIZE_1_MB_AND_128_FRAMES);
         setTextEncodingRestriction (TextEncodingRestriction .NO_RESTRICTIONS);
         setTextSizeRestriction     (TextSizeRestriction     .NO_RESTRICTIONS);
         setImageEncodingRestriction(ImageEncodingRestriction.NO_RESTRICTIONS);
         setImageSizeRestriction    (ImageSizeRestriction    .NO_RESTRICTIONS);

         this.tagHasRestrictions = tagHasRestrictions;        // now set the restrictions setting to what the user wants
         this.dirty              = true;
      }
   }

   /**
    * @return gets the restriction on the size of the ID3v2.4 tag and on the number of frames it may contain.
    * @throws IllegalStateException  if the <i>tagHasRestrictions</i> flag is not set.
    * @see #setTagSizeRestriction(TagSizeRestriction)
    */
   public TagSizeRestriction getTagSizeRestriction() throws IllegalStateException
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not get the \"tagSizeRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      return tagSizeRestriction;
   }

   /**
    * sets the restriction on the size of the ID3v2.4 tag and on the number of frames it may contain.
    * @param tagSizeRestriction  the restriction on the total size of the ID3v2.4 tag and on the number of frames it may contain.
    * @see #getTagSizeRestriction()
    */
   public void setTagSizeRestriction(TagSizeRestriction tagSizeRestriction)
   {
      this.tagSizeRestriction = tagSizeRestriction;
      this.dirty              = true;
   }

   /**
    * @return gets the restriction on which character sets may be used to encode text strings within the ID3v2.4 tag.
    * @throws IllegalStateException  if the <i>tagHasRestrictions</i> flag is not set.
    * @see #setTextEncodingRestriction(TextEncodingRestriction)
    */
   public TextEncodingRestriction getTextEncodingRestriction() throws IllegalStateException
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not get the \"textEncodingRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      return textEncodingRestriction;
   }

   /**
    * set the restriction on which character sets may be used to encode text strings within the ID3v2.4 tag.
    * @param textEncodingRestriction  the restriction on which character sets may be used to encode text strings within the ID3v2.4 tag.
    * @see #getTextEncodingRestriction()
    */
   public void setTextEncodingRestriction(TextEncodingRestriction textEncodingRestriction)
   {
      this.textEncodingRestriction = textEncodingRestriction;
      this.dirty                   = true;
   }

   /**
    * @return gets the restriction on the size of text strings within the ID3v2.4 tag.
    * @throws IllegalStateException  if the <i>tagHasRestrictions</i> flag is not set.
    * @see #setTextSizeRestriction(TextSizeRestriction)
    */
   public TextSizeRestriction getTextSizeRestriction() throws IllegalStateException
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not get the \"textSizeRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      return textSizeRestriction;
   }

   /**
    * sets the restriction on the size of text strings within the ID3v2.4 tag.
    * @param textSizeRestriction  the restriction on the size of text strings within the ID3v2.4 tag.
    * @see #getTextSizeRestriction()
    */
   public void setTextSizeRestriction(TextSizeRestriction textSizeRestriction)
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not set the \"textSizeRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      this.textSizeRestriction = textSizeRestriction;
      this.dirty               = true;
   }

   /**
    * @return gets the restriction on which character sets may be used to encode image strings within the ID3v2.4 tag.
    * @throws IllegalStateException  if the <i>tagHasRestrictions</i> flag is not set.
    * @see #setImageEncodingRestriction(ImageEncodingRestriction)
    */
   public ImageEncodingRestriction getImageEncodingRestriction() throws IllegalStateException
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not get the \"imageEncodingRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      return imageEncodingRestriction;
   }

   /**
    * set the restriction on which character sets may be used to encode image strings within the ID3v2.4 tag.
    * @param imageEncodingRestriction  the restriction on which character sets may be used to encode image strings within the ID3v2.4 tag.
    * @see #getImageEncodingRestriction()
    */
   public void setImageEncodingRestriction(ImageEncodingRestriction imageEncodingRestriction)
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not set the \"imageEncodingRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      this.imageEncodingRestriction = imageEncodingRestriction;
      this.dirty                    = true;
   }

   /**
    * @return gets the restriction on the size of image strings within the ID3v2.4 tag.
    * @throws IllegalStateException  if the <i>tagHasRestrictions</i> flag is not set.
    * @see #setImageSizeRestriction(ImageSizeRestriction)
    */
   public ImageSizeRestriction getImageSizeRestriction() throws IllegalStateException
   {
      if (!tagHasRestrictions)
         throw new IllegalStateException("You may not get the \"imageSizeRestriction\" field in the ID3v2.4 extended tag header when the tagHasRestrictions flag is false.");

      return imageSizeRestriction;
   }

   /**
    * sets the restriction on the size of image strings within the ID3v2.4 tag.
    * @param imageSizeRestriction  the restriction on the size of image strings within the ID3v2.4 tag.
    * @see #getImageSizeRestriction()
    */
   public void setImageSizeRestriction(ImageSizeRestriction imageSizeRestriction)
   {
      this.imageSizeRestriction = imageSizeRestriction;
      this.dirty                = true;
   }

   /**
    * save the ID3v2.4 tag extended header to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.4 tag extended header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag extended header to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 tag extended header has been modified and requires setBuffer() to be called before it can be saved.");

      outputStream.write(extendedHeader);
   }

   /**
    * save the ID3v2.4 tag extended header to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.4 tag extended header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag extended header to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 tag extended header has been modified and requires setBuffer() to be called before it can be saved.");

      file.write(extendedHeader);
   }

   /**
    * gets a string representation of the ID3v2.4 tag extended header showing the values of its fields.
    * @return a string representation of the ID3v2.4 tag extended header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.4 tag extended header"                                                      + "\n");
      buffer.append("   bytes.....................: " + getSize()                                      + " bytes\n");
      buffer.append("                               " + ID3v24FrameBodyUtility.hex(extendedHeader, 30) + "\n");
      buffer.append("   tag is an update..........: " + tagIsAnUpdate                                  + "\n");
      buffer.append("   crc data present..........: " + CRCDataPresent                                 + "\n");
      buffer.append("   tag has restrictions......: " + tagHasRestrictions                             + "\n");
      buffer.append("   CRC.......................: " + ID3v24FrameBodyUtility.hex(CRCData       , 30) + "\n");
      buffer.append("   tag size restriction......: " + tagSizeRestriction                             + "\n");
      buffer.append("   text encoding restriction.: " + textEncodingRestriction                        + "\n");
      buffer.append("   text size restriction.....: " + textSizeRestriction                            + "\n");
      buffer.append("   image encoding restriction: " + imageEncodingRestriction                       + "\n");
      buffer.append("   image size restriction....: " + imageSizeRestriction                                 );

      return buffer.toString();
   }
}
