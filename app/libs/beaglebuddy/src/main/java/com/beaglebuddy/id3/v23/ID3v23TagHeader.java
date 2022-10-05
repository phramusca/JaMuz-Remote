package com.beaglebuddy.id3.v23;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.id3.enums.ID3TagVersion;
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
 * This class provides methods for reading and writing the ID3V2.3 tag header.
 * The ID3v2 tag header is 10 bytes long and is the first information in the {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag}.
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
 * An ID3v2.3 tag header has the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>ID3v2.3 Tag Header Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.ID3TagVersion version}</td><td class="beaglebuddy">indicates which version of the ID3v2.x tag specification is stored in the .mp3 file. This value is always
 *                                                                                                                                                      {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_3 ID3V2_3}
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">tagSize                                                </td><td class="beaglebuddy">holds the total size of the ID3v2.3 tag, not including the header.
 *                                                                                                                                                      Simply put, since the tag header is 10 bytes long, the <i>tagSize</i> = total tag size - 10.                                         </td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">unsynchronization                                      </td><td class="beaglebuddy">flag used for correcting false synchronization bytes that occur in the ID3v2.3 tag header.
 *                                                                                                                                                      To understand why unsyncronization is used, it's helpful to understand a little about the format of .mp3 files as well as how an .mp3
 *                                                                                                                                                      file is played by a media player.  MP3 audio data is stored in an .mp3 file as a series of frames (not to be confused with the frames
 *                                                                                                                                                      that are found in the ID32.x tag in the .mp3 file).  Each .mp3 audio frame contains a small bit of digital music encoded in the MP3
 *                                                                                                                                                      format as well as some meta data about the frame itself.  At the beginning of each MP3 audio frame are 11 bits (sometimes 12) all set
 *                                                                                                                                                      to 1.  This is called the synch signal, and it's the pattern a media player looks for when attempting to play an .mp3 file or stream.
 *                                                                                                                                                      If the player finds this 11/12 bit sequence, then it knows it has found an MP3 audio frame which can be decoded and played back.
 *                                                                                                                                                      <p>
 *                                                                                                                                                      Thus, if an MP3 player finds a synch signal (11/12 bit sequence of all 1's) within the ID32.x tag, it will think it has found the
 *                                                                                                                                                      synch signal and try to play the ID32.x tag back as music, which it isn't.   Thus, the Id32.x tag may need to be unsynchonized so
 *                                                                                                                                                      that MP3 players properly skip over the tag and only try to play proper MP3 audio frames.
 *                                                                                                                                                      </p>
 *                                                                                                                                                      See <a href="http://www.id3.org/id3v2.3.0#sec5">unsynchronisation scheme</a> of the ID3v2.3 spec                                     </td></tr>
 *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">extendedHeaderPresent                                  </td><td class="beaglebuddy">flag indicating whether an {@link com.beaglebuddy.id3.v23.ID3v23TagExtendedHeader extended header} is present.  If so, then the
 *                                                                                                                                                      extended header will contain more version specific data.                                                                             </td></tr>
 *       <tr><td class="beaglebuddy">5.</td><td class="beaglebuddy">experimentalIndicator                                  </td><td class="beaglebuddy">flag indicating that the ID3v2.3 tag is in an experimental stage.                                                                    </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * @see <a href="http://www.id3.org/id3v2.3.0/"    target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23TagHeader
{
   // class members
                                                                                       /** size (in bytes) of the standard ID3v2.3 tag header */
   public  static final int  TAG_HEADER_SIZE                         = 10;

   // masks which define what each bit of the flag byte represents
   private static final byte TAG_HEADER_UNSYNCHRONIZATION_MASK       = (byte)0x80;
   private static final byte TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK = (byte)0x40;
   private static final byte TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK  = (byte)0x20;

   // data members
   private byte[]                  header;             // the raw binary data of the tag header.
   private boolean                 dirty;              // whether the tag header has been modified

   // standard header fields
   private ID3TagVersion           version;            // the version of the tag (ID3v2.3)
   private int                     tagSize;            // the total size of the ID3v2.3 tag excluding the tag header

   // standard header flags set in the flag byte
   private boolean unsynchronization;                 // whether or not unsynchronization is used.
   private boolean extendedHeaderPresent;             // whether or not the tag header is followed by an extended tag header
   private boolean experimentalIndicator;             // whether the tag is in an experimental stage.



   /**
    * The default constructor is called when creating a new ID3v2.3 tag header.
    * The default values used are:
    * <ul>
    *    <li>tag size is 0 bytes            </li>
    *    <li>no unsynchronization           </li>
    *    <li>no extended header present     </li>
    *    <li>experimental indicator is false</li>
    * </ul>
    */
   public ID3v23TagHeader()
   {
      header                = new byte[TAG_HEADER_SIZE];
      version               = ID3TagVersion.ID3V2_3;
      tagSize               = 0;
      unsynchronization     = false;
      extendedHeaderPresent = false;
      experimentalIndicator = false;
      dirty                 = true;  // the tag header has been created, but the values have not yet been written to the raw binary buffer

      System.arraycopy(version.getIdBytes(), 0, header, 0, ID3TagVersion.NUM_ID_BYTES);
   }

   /**
    * This constructor is called when reading in an existing ID3v2.3 tag header from an .mp3 file.
    * @param inputStream   input stream pointing to the tag header, after the ID3v2.3 id bytes, in the .mp3 file.
    * @throws IOException  if the tag header can not be read from the .mp3 file.
    */
   public ID3v23TagHeader(InputStream inputStream) throws IOException
   {
      this();

      // read in the rest of the ID3v2.3 tag header (the bytes after the ID3v2.3 id)
      byte[] buffer = new byte[TAG_HEADER_SIZE - ID3TagVersion.NUM_ID_BYTES];
      if (inputStream.read(buffer) != buffer.length)
         throw new IOException("Unable to read in the ID3v2.3 tag header from the .mp3 file.");
      System.arraycopy(buffer, 0, header, ID3TagVersion.NUM_ID_BYTES, buffer.length);

      // parse the flags
      unsynchronization     = (buffer[0] & TAG_HEADER_UNSYNCHRONIZATION_MASK      ) != 0;
      extendedHeaderPresent = (buffer[0] & TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK) != 0;
      experimentalIndicator = (buffer[0] & TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK ) != 0;
      // get the size of the tag (not counting this header)
      tagSize = ID3v23FrameBodyUtility.bytesToSynchsafeInt(buffer, 1);

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
    * gets    the version of the tag, which is always {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_3 ID3V2_3}.
    * @return the version of the tag, which is always {@link com.beaglebuddy.id3.enums.ID3TagVersion#ID3V2_3 ID3V2_3}.
    */
   public ID3TagVersion getVersion()
   {
      return version;
   }

   /**
    * gets the size of the ID3v2.3 tag excluding the tag header, ie (total tag size - 10).
    * @return the size of the ID3v2.3 tag excluding the standard tag header.  That is, total tag size - 10.
    * @see #setTagSize(int)
    */
   public int getTagSize()
   {
      return tagSize;
   }

   /**
    * sets the size of the ID3v2.3 tag excluding the tag header, ie (total tag size - 10).
    * @param tagSize the size of the ID3v2.3 tag excluding the tag header.
    * @see #getTagSize()
    */
   public void setTagSize(int tagSize)
   {
      if (tagSize < 0)
         throw new IllegalArgumentException("Invalid ID3v2.3 tag size, " + tagSize + ". It must be > 0.");

      if (this.tagSize != tagSize)
      {
         this.tagSize = tagSize;
         this.dirty   = true;
      }
   }

   /**
    * gets    the total number of bytes in the ID3v2.3 tag header.
    * @return the total number of bytes in the ID3v2.3 tag header.
    */
   public int getSize()
   {
      return TAG_HEADER_SIZE;
   }

   /**
    * gets whether unsynchronization was used to correct false synchronization bytes in the ID3v2.3 tag.
    * @return whether unsynchronization was used to correct false synchronization bytes in the ID3v2.3 tag.
    * @see #setUnsynchronization(boolean)
    * @see <a href="http://www.id3.org/id3v2.3.0#sec5">ID3v2.3 unsynchronization scheme</a>
    */
   public boolean isUnsynchronization()
   {
      return unsynchronization;
   }

   /**
    * sets whether unsynchronization is used.
    * @param unsynchronization    boolean indicating whether unsynchronization is used.
    * @see #isUnsynchronization()
    * @see <a href="http://www.id3.org/id3v2.3.0#sec5">ID3v2.3 unsynchronization scheme</a>
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
    * Indicates whether the ID3v2.3 tag is in an experimental stage.
    * @return whether the ID3v2.3 tag is in an experimental stage.
    * @see #setExperimentalIndicator(boolean)
    */
   public boolean isExperimentalIndicator()
   {
      return experimentalIndicator;
   }

   /**
    * sets whether the ID3v2.3 tag is in an experimental stage.
    * @param experimentalIndicator boolean indicating whether the ID3v2.3 tag is in an experimental stage.
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
    * indicates whether the optional extended header is present.  If it is, then you may access it via the {@link ID3v23Tag#getExtendedHeader()} method.
    * @return whether the optional extended header is present.
    * @see #setExtendedHeaderPresent(boolean)
    */
   public boolean isExtendedHeaderPresent()
   {
      return extendedHeaderPresent;
   }

   /**
    * sets whether the optional extended header is present.  if false, then the extendedHeader is set to null.  otherwise, the extendedHeader is initialized to default values.
    * @param extendedHeaderPresent boolean indicating whether the optional extended header is present.
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
    * if the ID3v2.3 tag header's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, reset the dirty flag to indicate that the buffer is up to date, and the tag header is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      System.arraycopy(version.getIdBytes(), 0, header, 0, ID3TagVersion.NUM_ID_BYTES);
      header[5] = (byte)(unsynchronization     ? header[5] | TAG_HEADER_UNSYNCHRONIZATION_MASK       : header[5] & ~TAG_HEADER_UNSYNCHRONIZATION_MASK      );
      header[5] = (byte)(extendedHeaderPresent ? header[5] | TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK : header[5] & ~TAG_HEADER_EXTENDED_HEADER_PRESENT_MASK);
      header[5] = (byte)(experimentalIndicator ? header[5] | TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK  : header[5] & ~TAG_HEADER_EXPERIMENTAL_INDICATOR_MASK );
      System.arraycopy(ID3v23FrameBodyUtility.synchsafeIntToBytes(tagSize), 0, header, 6, 4);

      dirty = false;
   }

   /**
    * save the ID3v2.3 tag header to the .mp3 file.
    * @param outputStream   output stream pointing to the starting location of the ID3v2.3 tag header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 tag header to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.3 tag header has been modified and requires setBuffer() to be called before it can be saved.");

      // save the ID3v2.3 tag header to the .mp3 file
      outputStream.write(header);
   }

   /**
    * save the ID3v2.3 tag header to the .mp3 file.
    * @param file   random access file pointing to the starting location of the ID3v2.3 tag header within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.3 tag header to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.3 tag header has been modified and requires setBuffer() to be called before it can be saved.");

      // save the ID3v2.3 standard tag header to the .mp3 file
      file.write(header);
   }

   /**
    * gets a string representation of the ID3v2.3 tag header showing the values of all the tag header's fields as well as the extended header's fields if it is present.
    * @return a string representation of the ID3v2.3 tag header.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.3 tag header\n");
      buffer.append("   bytes..................: " +  header.length                          + " bytes\n");
      buffer.append("                            " +  ID3v23FrameBodyUtility.hex(header, 38) + "\n");
      buffer.append("   version................: " + version                                 + "\n");
      buffer.append("   tag size...............: " + tagSize                                 + " bytes\n");
      buffer.append("   unsynchronization......: " + unsynchronization                       + "\n");
      buffer.append("   extended header present: " + extendedHeaderPresent                   + "\n");
      buffer.append("   experimental indicator.: " + experimentalIndicator                         );

      return buffer.toString();
   }
}
