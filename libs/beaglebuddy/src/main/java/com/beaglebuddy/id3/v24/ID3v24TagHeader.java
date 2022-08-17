package com.beaglebuddy.id3.v24;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.id3.enums.ID3TagVersion;
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
 * This class provides methods for reading and writing the ID3V2.4 tag header.
 * The ID3v2.4 tag header is 10 bytes long and is the first information in the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag}.
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
 * An Id3v2.4 tag header has the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>ID3v2.4 Tag Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.ID3TagVersion version}</td><td class="beaglebuddy">indicates which version of the ID3v2.x tag specification is stored in the .mp3 file.  This value is always
 *                                                                                                                                                      {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_4 ID3V2_4}                                                                      </td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">tagSize                                                </td><td class="beaglebuddy">holds the total size of the ID3v2.4 tag, not including the header and, if present, the footer.
 *                                                                                                                                                      Simply put, since the tag header is 10 bytes long, the <i>tagSize</i> = total tag size - 10.
 *                                                                                                                                                      If the footer is present, then the <i>tagSize</i> = total tag size - 20.                                                             </td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">unsynchronization                                      </td><td class="beaglebuddy">flag used for correcting false synchronization bytes that occur in the ID3v2.4 tag header.
 *                                                                                                                                                      To understand why unsyncronization is used, it's helpful to understand a little about the format of .mp3 files as well as how an .mp3
 *                                                                                                                                                      file is played by a media player.  MP3 audio data is stored in an .mp3 file as a series of frames (not to be confused with the frames
 *                                                                                                                                                      that are found in the ID3v2.4 tag in the .mp3 file).  Each .mp3 audio frame contains a small bit of digital music encoded in the MP3
 *                                                                                                                                                      format as well as some meta data about the frame itself.  At the beginning of each MP3 audio frame are 11 bits (sometimes 12) all set
 *                                                                                                                                                      to 1.  This is called the synch signal, and it's the pattern a media player looks for when attempting to play an .mp3 file or stream.
 *                                                                                                                                                      If the player finds this 11/12 bit sequence, then it knows it has found an MP3 audio frame which can be decoded and played back.
 *                                                                                                                                                      <p>
 *                                                                                                                                                      Thus, if an MP3 player finds a synch signal (11/12 bit sequence of all 1's) within the ID3v2.4 tag, it will think it has found the
 *                                                                                                                                                      synch signal and try to play the ID3v2.4 tag back as music, which it isn't.   Thus, the ID3v2.4 tag may need to be unsynchonized so
 *                                                                                                                                                      that MP3 players properly skip over the tag and only try to play proper MP3 audio frames.
 *                                                                                                                                                      </p>
 *                                                                                                                                                      See <a href=" http://id3.org/id3v2.4.0-structure">sec 6.1 - unsynchronisation scheme</a> of the ID3v2.4 spec                         </td></tr>
 *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">extendedHeaderPresent                                  </td><td class="beaglebuddy">flag indicating whether an {@link ID3v24TagExtendedHeader extended header} is present.  If so, then the extended header will contain
 *                                                                                                                                                      more version specific data.                                                                                                          </td></tr>
 *       <tr><td class="beaglebuddy">5.</td><td class="beaglebuddy">experimentalIndicator                                  </td><td class="beaglebuddy">flag indicating that the ID3v2.4 tag is in an experimental stage.                                                                    </td></tr>
 *       <tr><td class="beaglebuddy">6.</td><td class="beaglebuddy">footerPresent                                          </td><td class="beaglebuddy">flag indicating whether a {@link ID3v24TagFooter footer} is present.  If so, then the footer follows the frames, and no padding is
 *                                                                                                                                                      present.                                                                                                                             </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://id3.org/id3v2.4.0-structure" target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"   target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24TagHeader
{
   // class members
                                                                                       /** size (in bytes) of the standard ID3v2.4 tag header */
   public  static final int  TAG_HEADER_SIZE                           = 10;

   // masks which define what each bit of the flag byte represents
   private static final byte TAG_HEADER_UNSYNCHRONIZATION_MASK       = (byte)0x80;
   private static final byte TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK = (byte)0x40;
   private static final byte TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK  = (byte)0x20;
   private static final byte TAG_HEADER_FOOTER_PRESENT_MASK          = (byte)0x10;

   // data members
   private byte[]                  header;             // the raw binary data of the tag header.
   private boolean                 dirty;              // whether the tag header has been modified

   // standard header fields
   private ID3TagVersion           version;            // the version of the tag (ID3v2.4)
   private int                     tagSize;            // the total size of the ID3v2.4 tag excluding the tag header and footer

   // standard header flags set in the flag byte
   private boolean unsynchronization;                 // whether or not unsynchronization is used.
   private boolean extendedHeaderPresent;             // whether or not the tag header is followed by an extended tag header
   private boolean experimentalIndicator;             // whether the tag is in an experimental stage.
   private boolean footerPresent;                     // whether the tag has a footer at the end of the mp3 file





   /**
    * The default constructor is called when creating a new ID3v2.4 tag header.
    * The default values used are:
    * <ul>
    *    <li>tag size is 0 bytes            </li>
    *    <li>unsynchronization              </li>
    *    <li>no extended header present     </li>
    *    <li>experimental indicator is false</li>
    *    <li>no footer present              </li>
    * </ul>
    */
   public ID3v24TagHeader()
   {
      header                = new byte[TAG_HEADER_SIZE];
      version               = ID3TagVersion.ID3V2_4;
      tagSize               = 0;
      unsynchronization     = true;
      extendedHeaderPresent = false;
      experimentalIndicator = false;
      footerPresent         = false;
      dirty                 = true;  // the tag header has been created, but the values have not yet been written to the raw binary buffer

      System.arraycopy(version.getIdBytes(), 0, header, 0, ID3TagVersion.NUM_ID_BYTES);
   }

   /**
    * This constructor is called when reading in an existing ID3v2.4 tag header from an .mp3 file.
    * @param inputStream   input stream pointing to the end of the standard ID3v2.4 tag header in the .mp3 file.
    * @throws IOException  if the tag header can not be loaded from the .mp3 file.
    */
   public ID3v24TagHeader(InputStream inputStream) throws IOException
   {
      this();

      // read in the rest of the ID3v2.4 tag header (the bytes after the ID3v2.4 id)
      byte[] buffer = new byte[TAG_HEADER_SIZE - ID3TagVersion.NUM_ID_BYTES];
      if (inputStream.read(buffer) != buffer.length)
         throw new IOException("Unable to read in the ID3v2.4 tag header from the .mp3 file.");
      System.arraycopy(buffer, 0, header, ID3TagVersion.NUM_ID_BYTES, buffer.length);

      // parse the flags
      unsynchronization     = (buffer[0] & TAG_HEADER_UNSYNCHRONIZATION_MASK      ) != 0;
      extendedHeaderPresent = (buffer[0] & TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK) != 0;
      experimentalIndicator = (buffer[0] & TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK ) != 0;
      footerPresent         = (buffer[0] & TAG_HEADER_FOOTER_PRESENT_MASK         ) != 0;

      // get the size of the tag (not counting this header)
      tagSize = ID3v24FrameBodyUtility.bytesToSynchsafeInt(buffer, 1);

      dirty = false;
   }

   /**
    * indicates whether or not the tag header's fields have been modified.
    * @return whether or not the tag header has been modified.
    * @see #setBuffer()
    */
   public boolean isDirty()
   {
      return dirty;
   }

   /**
    * gets    the version of the tag, which is always {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_4 ID3V2_4}.
    * @return the version of the tag, which is always {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_4 ID3V2_4}.
    */
   public ID3TagVersion getVersion()
   {
      return version;
   }

   /**
    * gets the size of the ID3v2.4 tag excluding the tag header and, if present, the tag footer.
    * @return the size of the ID3v2.4 tag excluding the standard tag header.  That is, (total tag size - 10).
    *         If a tag footer is present, then the value returned is (total tag size - 20).
    * @see #setTagSize(int)
    */
   public int getTagSize()
   {
      return tagSize;
   }

   /**
    * sets the size of the ID3v2.4 tag excluding the tag header, and if present, the tag footer.
    * That is, if a tag footer is not present, then the tag size should be set to (total tag size - 10).
    * If a tag footer is present, then the tag size should be set to (total tag size - 20).
    * @param tagSize the size of the ID3v2.4 tag excluding the tag header and, if present, the tag footer.
    * @see #getTagSize()
    */
   public void setTagSize(int tagSize)
   {
      if (tagSize < 0)
         throw new IllegalArgumentException("Invalid ID3v2.4 tag size, " + tagSize + ". It must be > 0.");

      if (this.tagSize != tagSize)
      {
         this.tagSize = tagSize;
         this.dirty   = true;
      }
   }

   /**
    * gets    the total number of bytes in the ID3v2.4 tag header.
    * @return the total number of bytes in the ID3v2.4 tag header.
    */
   public int getSize()
   {
      return TAG_HEADER_SIZE;
   }

   /**
    * gets whether unsynchronization was used to correct false synchronization bytes in the ID3v2.4 tag.
    * @return whether unsynchronization was used to correct false synchronization bytes in the ID3v2.4 tag.
    * @see #setUnsynchronization(boolean)
    * @see <a href="http://id3.org/id3v2.4.0-structure">section 6.1 - unsynchronization scheme in the ID3v2.4 specification</a>
    */
   public boolean isUnsynchronization()
   {
      return unsynchronization;
   }

   /**
    * sets whether unsynchronization is used.
    * @param unsynchronization    boolean indicating whether unsynchronization is used.
    * @see #isUnsynchronization()
    * @see <a href="http://id3.org/id3v2.4.0-structure">section 6.1 - unsynchronization scheme in the ID3v2.4 specification</a>
    */
   public void setUnsynchronization(boolean unsynchronization)
   {
      if (this.unsynchronization != unsynchronization)
      {
         this.unsynchronization = unsynchronization;
         this.dirty             = true;
      }
   }

   /**
    * Indicates whether the ID3v2.4 tag is in an experimental stage.
    * @return whether the ID3v2.4 tag is in an experimental stage.
    * @see #setExperimentalIndicator(boolean)
    */
   public boolean isExperimentalIndicator()
   {
      return experimentalIndicator;
   }

   /**
    * sets whether the ID3v2.4 tag is in an experimental stage.
    * @param experimentalIndicator boolean indicating whether the ID3v2.4 tag is in an experimental stage.
    * @see #isExperimentalIndicator()
    */
   public void setExperimentalIndicator(boolean experimentalIndicator)
   {
      if (this.experimentalIndicator != experimentalIndicator)
      {
         this.experimentalIndicator = experimentalIndicator;
         this.dirty                 = true;
      }
   }

   /**
    * indicates whether the optional {@link ID3v24TagExtendedHeader extended header} is present.  If it is, then you may acess it via the {@link ID3v24Tag#getExtendedHeader()} method.
    * @return   whether the optional {@link ID3v24TagExtendedHeader extended header} is present.
    * @see #setExtendedHeaderPresent(boolean)
    */
   public boolean isExtendedHeaderPresent()
   {
      return extendedHeaderPresent;
   }

   /**
    * sets whether the optional {@link ID3v24TagExtendedHeader extended header} is present.
    * @param extendedHeaderPresent boolean indicating whether the optional {@link ID3v24TagExtendedHeader extended header} is present.
    * @see #isExtendedHeaderPresent()
    */
   public void setExtendedHeaderPresent(boolean extendedHeaderPresent)
   {
      if (this.extendedHeaderPresent != extendedHeaderPresent)
      {
         this.extendedHeaderPresent = extendedHeaderPresent;
         this.dirty                 = true;
      }
   }

   /**
    * Indicates whether the ID3v2.4 tag has a footer which follows the padding.
    * @return whether the ID3v2.4 tag contains a footer and follows the padding.
    * @see #setFooterPresent(boolean)
    */
   public boolean isFooterPresent()
   {
      return footerPresent;
   }

   /**
    * sets whether the ID3v2.4 tag footer is present.
    * @param footerPresent boolean indicating whether the footer is present.
    * @see #isFooterPresent()
    */
   public void setFooterPresent(boolean footerPresent)
   {
      if (this.footerPresent != footerPresent)
      {
         this.footerPresent = footerPresent;
         this.dirty         = true;
      }
   }

   /**
    * if the ID3v2.4 tag header's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, reset the dirty flag to indicate that the buffer is up to date, and the tag header is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      System.arraycopy(version.getIdBytes(), 0, header, 0, ID3TagVersion.NUM_ID_BYTES);
      header[5] = (byte)(unsynchronization     ? header[5] | TAG_HEADER_UNSYNCHRONIZATION_MASK       : header[5] & ~TAG_HEADER_UNSYNCHRONIZATION_MASK      );
      header[5] = (byte)(extendedHeaderPresent ? header[5] | TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK : header[5] & ~TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK);
      header[5] = (byte)(experimentalIndicator ? header[5] | TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK  : header[5] & ~TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK );
      header[5] = (byte)(footerPresent         ? header[5] | TAG_HEADER_FOOTER_PRESENT_MASK          : header[5] & ~TAG_HEADER_FOOTER_PRESENT_MASK         );
      System.arraycopy(ID3v24FrameBodyUtility.synchsafeIntToBytes(tagSize), 0, header, 6, 4);

      dirty = false;
   }

   /**
    * save the ID3v2.4 tag header to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.4 tag header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag header to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 tag header has been modified and requires setBuffer() to be called before it can be saved.");

      // save the ID3v2.4 tag header to the .mp3 file
      outputStream.write(header);

      dirty = false;
   }

   /**
    * save the ID3v2.4 tag header to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.4 tag header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag header to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 tag header has been modified and requires setBuffer() to be called before it can be saved.");

      // save the ID3v2.4 standard tag header to the .mp3 file
      file.write(header);

      dirty = false;
   }

   /**
    * gets a string representation of the ID3v2.4 tag header showing the values of the standard ID3v2.4 tag header's fields.
    * @return a string representation of the ID3v2.4 tag header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.4 tag header\n");
      buffer.append("   bytes..................: " + header.length                          + " bytes\n");
      buffer.append("                            " + ID3v24FrameBodyUtility.hex(header, 27) + "\n");
      buffer.append("   version................: " + version                                + "\n");
      buffer.append("   tag size...............: " + tagSize                                + " bytes\n");
      buffer.append("   unsynchronization......: " + unsynchronization                      + "\n");
      buffer.append("   extended header present: " + extendedHeaderPresent                  + "\n");
      buffer.append("   experimental indicator.: " + experimentalIndicator                  + "\n");
      buffer.append("   footer present.........: " + footerPresent                                );

      return buffer.toString();
   }
}
