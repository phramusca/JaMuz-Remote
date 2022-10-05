package com.beaglebuddy.id3.v24;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.exception.ParseException;
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
 * This class provides methods for reading and writing the ID3V2.4 tag footer.
 * The ID3v2.4 tag footer is 10 bytes long and is the last information in the {@link com.beaglebuddy.id3.v24.ID3v24Tag ID3v2.4 tag}.
 * It is an optional part of the ID3v2.4 tag, and if present, is found just before the audio portion of the .mp3 file.
 * If an ID3v2.4 tag footer is present, then no padding can appear in the ID3v2.4 tag.  That is, either padding may be present or the footer may be present,
 * but not both.
 * </p>
 * If the Id3v2.4 tag is found at the end of the file instead of the beginning, then a footer is required.  This is because it would be prohibitively difficult
 * to find the beginning of the ID3v2.4 tag.  Thus, to speed up the process of locating an ID3v2.4 tag when searching from the end of a file, a footer must be
 * added to the tag.
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
 * An Id3v2.4 tag footer has the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>ID3v2.4 Tag Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.ID3TagVersion version}</td><td class="beaglebuddy">indicates which version of the ID3v2.x tag specification is stored in the .mp3 file.  This value is always
 *                                                                                                                                                      {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_4_FOOTER ID3V2_4_FOOTER}                                                        </td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">tagSize                                                </td><td class="beaglebuddy">holds the total size of the ID3 tag, not including the header or the footer.
 *                                                                                                                                                      Simply put, since the tag header and footer are each 10 bytes long, the <i>tagSize</i> = total tag size - 20.                        </td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">unsynchronization                                      </td><td class="beaglebuddy">flag used for correcting false synchronization bytes that occur in the ID3v2.4 tag footer.
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
 *       <tr><td class="beaglebuddy">6.</td><td class="beaglebuddy">footerPresent                                          </td><td class="beaglebuddy">flag indicating whether a {@link ID3v24TagFooter footer} is present.  This field is always true.                                     </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://id3.org/id3v2.4.0-structure" target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"   target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24TagFooter
{
   // class members
                                                                                       /** size (in bytes) of the standard ID3v2.4 tag footer */
   public  static final int  TAG_FOOTER_SIZE                         = 10;

   // masks which define what each bit of the flag byte represents
   private static final byte TAG_FOOTER_UNSYNCHRONIZATION_MASK       = (byte)0x80;
   private static final byte TAG_FOOTER_EXTENDED_HEADER_PRESENT_MASK = (byte)0x40;
   private static final byte TAG_FOOTER_EXPERIMENTAL_INDICATOR_MASK  = (byte)0x20;
   private static final byte TAG_FOOTER_FOOTER_PRESENT_MASK          = (byte)0x10;

   // data members
   private byte[]                  footer;             // the raw binary data of the tag footer.
   private boolean                 dirty;              // whether the tag footer has been modified

   // standard footer fields
   private ID3TagVersion           version;            // the version of the tag (ID3v2.4 footer)
   private int                     tagSize;            // the total size of the ID3v2.4 tag excluding the tag header

   // standard footer flags set in the flag byte
   private boolean unsynchronization;                 // whether or not unsynchronization is used.
   private boolean extendedHeaderPresent;             // whether or not the tag header is followed by an extended tag header
   private boolean experimentalIndicator;             // whether the tag is in an experimental stage.
   private boolean footerPresent;                     // whether the tag has a footer at the end of the mp3 file




   /**
    * The default constructor is called when creating a new ID3v2.4 tag footer.
    * The default values used are:
    * <ul>
    *    <li>tag size is 0 bytes            </li>
    *    <li>no unsynchronization           </li>
    *    <li>no extended header present     </li>
    *    <li>experimental indicator is false</li>
    *    <li>footer present                 </li>
    * </ul>
    */
   public ID3v24TagFooter()
   {
      footer                = new byte[TAG_FOOTER_SIZE];
      version               = ID3TagVersion.ID3V2_4_FOOTER;
      tagSize               = 0;
      unsynchronization     = false;
      extendedHeaderPresent = false;
      experimentalIndicator = false;
      footerPresent         = true;
      dirty                 = true;  // the tag footer has been created, but the values have not yet been written to the raw binary buffer

      System.arraycopy(version.getIdBytes(), 0, footer, 0, ID3TagVersion.NUM_ID_BYTES);
   }

   /**
    * The constructor is called when creating a new ID3v2.4 tag footer which should be identical to an ID3v2.4 tag header except for the id bytes.
    * @param header    the ID3v2.4 tag header whose values will be used to initialize the footer.
    */
   public ID3v24TagFooter(ID3v24TagHeader header)
   {
      this();
      tagSize               = header.getTagSize();
      unsynchronization     = header.isUnsynchronization();
      extendedHeaderPresent = header.isExtendedHeaderPresent();
      experimentalIndicator = header.isExperimentalIndicator();
      footerPresent         = true;
      dirty                 = true;              // the tag footer has been created, but the values have not yet been written to the raw binary buffer
   }

   /**
    * This constructor is called when reading in an existing ID3v2.4 tag footer from an .mp3 file.
    * @param inputStream   input stream pointing to the beginning of the standard ID3v2.4 tag footer in the .mp3 file.
    * @throws IOException     if the tag footer can not be loaded from the .mp3 file.
    * @throws ParseException  if the footer is read in, but the footer's id bytes are not correct.
    */
   public ID3v24TagFooter(InputStream inputStream) throws IOException, ParseException
   {
      this();

      // read in the footer id bytes
      version = ID3TagVersion.readVersion(inputStream);
      if (version != ID3TagVersion.ID3V2_4_FOOTER)
         throw new ParseException("Invalid ID3v2.4 tag footer id.");

      // read in the rest of the ID3v2.4 tag footer (the bytes after the ID3v2.4 id)
      byte[] buffer = new byte[TAG_FOOTER_SIZE - ID3TagVersion.NUM_ID_BYTES];
      if (inputStream.read(buffer) != buffer.length)
         throw new IOException("Unable to read in the ID3v2.4 tag footer from the .mp3 file.");
      System.arraycopy(buffer, 0, footer, ID3TagVersion.NUM_ID_BYTES, buffer.length);

      // parse the flags
      unsynchronization     = (buffer[0] & TAG_FOOTER_UNSYNCHRONIZATION_MASK      ) != 0;
      extendedHeaderPresent = (buffer[0] & TAG_FOOTER_EXTENDED_HEADER_PRESENT_MASK) != 0;
      experimentalIndicator = (buffer[0] & TAG_FOOTER_EXPERIMENTAL_INDICATOR_MASK ) != 0;
      footerPresent         = (buffer[0] & TAG_FOOTER_FOOTER_PRESENT_MASK         ) != 0;

      // get the size of the tag (not counting this footer)
      tagSize = ID3v24FrameBodyUtility.bytesToSynchsafeInt(buffer, 1);

      dirty = false;
   }

   /**
    * indicates whether or not the tag footer's fields have been modified.
    * @return whether or not the tag footer has been modified.
    * @see #setBuffer()
    */
   public boolean isDirty()
   {
      return dirty;
   }

   /**
    * gets    the version of the tag, which is always {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_4_FOOTER ID3V2_4_FOOTER}.
    * @return the version of the tag, which is always {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_4_FOOTER ID3V2_4_FOOTER}.
    */
   public ID3TagVersion getVersion()
   {
      return version;
   }

   /**
    * gets the size of the ID3v2.4 tag excluding the tag header and the footer.
    * @return the size of the ID3v2.4 tag excluding the tag header and the footer.  Since both the tag header and the tag footer are each 10 bytes long,
    * the value returned is, total tag size - 20.
    * @see #setTagSize(int)
    */
   public int getTagSize()
   {
      return tagSize;
   }

   /**
    * sets the size of the ID3v2.4 tag excluding this tag header and footer.
    * Since both the tag header and the tag footer are each 10 bytes long, this value should be set to (total tag size - 20).
    * @param tagSize the size of the ID3v2.4 tag excluding the tag header and the footer.
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
    * gets    the total number of bytes in the ID3v2.4 tag footer.
    * @return the total number of bytes in the ID3v2.4 tag footer.
    */
   public int getSize()
   {
      return TAG_FOOTER_SIZE;
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
    * Indicates whether the ID3v2.4 tag has a footer which follows the frames.
    * @return whether the ID3v2.4 tag contains a footer and follows the frames.
    */
   public boolean isFooterPresent()
   {
      return footerPresent;
   }

   /**
    * if the ID3v2.4 tag footer's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, reset the dirty flag to indicate that the buffer is up to date, and the tag footer is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      System.arraycopy(version.getIdBytes(), 0, footer, 0, ID3TagVersion.NUM_ID_BYTES);
      footer[5] = (byte)(unsynchronization     ? footer[5] | TAG_FOOTER_UNSYNCHRONIZATION_MASK       : footer[5] & ~TAG_FOOTER_UNSYNCHRONIZATION_MASK      );
      footer[5] = (byte)(extendedHeaderPresent ? footer[5] | TAG_FOOTER_EXTENDED_HEADER_PRESENT_MASK : footer[5] & ~TAG_FOOTER_EXTENDED_HEADER_PRESENT_MASK);
      footer[5] = (byte)(experimentalIndicator ? footer[5] | TAG_FOOTER_EXPERIMENTAL_INDICATOR_MASK  : footer[5] & ~TAG_FOOTER_EXPERIMENTAL_INDICATOR_MASK );
      footer[5] = (byte)(footerPresent         ? footer[5] | TAG_FOOTER_FOOTER_PRESENT_MASK          : footer[5] & ~TAG_FOOTER_FOOTER_PRESENT_MASK         );
      System.arraycopy(ID3v24FrameBodyUtility.synchsafeIntToBytes(tagSize), 0, footer, 6, 4);

      dirty = false;
   }

   /**
    * save the ID3v2.4 tag footer to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.4 tag footer within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag footer to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 tag footer has been modified and requires setBuffer() to be called before it can be saved.");

      // save the ID3v2.4 tag footer to the .mp3 file
      outputStream.write(footer);

      dirty = false;
   }

   /**
    * save the ID3v2.4 tag footer to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.4 tag footer within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag footer to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 tag footer has been modified and requires setBuffer() to be called before it can be saved.");

      // save the ID3v2.4 standard tag footer to the .mp3 file
      file.write(footer);

      dirty = false;
   }

   /**
    * gets a string representation of the ID3v2.4 tag footer showing the values of the ID3v2.4 tag footer's fields.
    * @return a string representation of the ID3v2.4 tag footer.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.4 tag footer\n");
      buffer.append("   bytes..................: " + footer.length                          + " bytes\n");
      buffer.append("                            " + ID3v24FrameBodyUtility.hex(footer, 27) + "\n");
      buffer.append("   version................: " + version                                + "\n");
      buffer.append("   tag size...............: " + tagSize                                + " bytes\n");
      buffer.append("   unsynchronization......: " + unsynchronization                      + "\n");
      buffer.append("   extended header present: " + extendedHeaderPresent                  + "\n");
      buffer.append("   experimental indicator.: " + experimentalIndicator                  + "\n");
      buffer.append("   footer present.........: " + footerPresent                          + "\n");

      return buffer.toString();
   }
}
