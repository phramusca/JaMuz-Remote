package com.beaglebuddy.id3.v24;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.exception.ParseException;
import com.beaglebuddy.id3.enums.ID3TagVersion;
import com.beaglebuddy.id3.enums.v24.FrameType;




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
 * This class provides methods for reading and writing an ID3V2.4 tag.  It provides direct low level access to the actual ID3v2.4 tag embedded in an .mp3 file.
 * The tag is where all the information about the .mp3 file is stored.  Information such as the song title, the track number, the lyrics of the song, the band who recorded
 * the song, etc. are stored inside the tag.  As shown below, a tag is composed of the four parts: {@link ID3v24TagHeader header}, {@link ID3v24TagExtendedHeader extended header},
 * {@link ID3v24Frame frames}, and either padding or a {@link ID3v24TagFooter footer}.
 * </p>
 * <p>
 * <table border="0">
 *    <tbody>
 *       <tr>
 *          <td>
 *             <img src="../../../../resources/mp3_format_ID3v2.4.gif" height="580" width="330" alt="mp3 format with an ID3v2.4 tag" usemap="#id3v24_map"/>
 *          </td>
 *          <td class="vert_align_top">
 *             &nbsp;
 *          </td>
 *          <td class="vert_align_top">
 *             <table class="beaglebuddy">
 *                <caption>ID3v2.4 Tag Fields</caption>
 *                <tr><th class="beaglebuddy">Field                                          </th><th class="beaglebuddy">Description                                                                                                                  </th></tr>
 *                <tr><td class="beaglebuddy">{@link ID3v24TagHeader header}                 </td><td class="beaglebuddy">Holds the ID3 tag identifier, the total tag size, etc.                                                                       </td></tr>
 *                <tr><td class="beaglebuddy">{@link ID3v24TagExtendedHeader extended header}</td><td class="beaglebuddy">Optional field which is present only if the <i>extendedHeaderPresent</i> flag is set in the {@link ID3v24TagHeader header}.
 *                                                                                                                        This extended header has fields which are not vital to the reading of the ID3v2.4 tag.                                       </td></tr>
 *
 *                <tr><td class="beaglebuddy">{@link ID3v24Frame frames}                     </td><td class="beaglebuddy">List of frames that contains the actual information about the .mp3 file.                                                     </td></tr>
 *                <tr><td class="beaglebuddy">padding                                        </td><td class="beaglebuddy">Unused area filled with 0's which allows the tag to grow without having to re-write the whole .mp3 file. For example, if the
 *                                                                                                                        user adds a frame specifying the track number, then the tag can take some space from the padding in order to add the frame
 *                                                                                                                        and doesn't need to re-write the entire .mp3 file.  However, if a footer is present, then no padding may appear in the tag.
 *                                                                                                                        That is, padding and the footer are mutually exclusive.  Only one of the two may be present in the ID3v2.4 tag.              </td></tr>
 *                <tr><td class="beaglebuddy">{@link ID3v24TagFooter footer}                 </td><td class="beaglebuddy">Optional field that holds the ID3v2.4 tag footer.  If present, then no padding may appear in the ID3v2.4 tag.                </td></tr>
 *             </table>
 *          </td>
 *       </tr>
 *    </tbody>
 * </table>
 * <map name="id3v24_map">
 *    <area shape="rect" coords=" 230, 170, 300, 185" href="ID3v24Tag.html"                                alt="ID3v2.4 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="ID3v24TagHeader.html"                          alt="ID3v2.4 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="ID3v24TagExtendedHeader.html"                  alt="ID3v2.4 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="ID3v24Frame.html"                              alt="ID3v2.4 Frame""/>
 *    <area shape="rect" coords="   6, 251, 198, 286" href="../../mp3/MP3Base.html#setID3v2xPadding(int)"  alt="ID3v2.4 Padding"/>
 *    <area shape="rect" coords="   6, 287, 198, 321" href="ID3v24TagFooter.html"                          alt="ID3v2.4 Tag Footer"/>
 *    <area shape="rect" coords="   6, 322, 198, 410" href="../../mpeg/MPEGFrame.html"                     alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 411, 198, 463" href="../../lyrics3/Lyrics3v2Tag.html"               alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 463, 198, 515" href="../../ape/APETag.html"                         alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 516, 198, 564" href="../v1/ID3v1Tag.html"                           alt="ID3V1 Tag"/>
 * </map>
 * </p>
 * <p class="beaglebuddy">
 * It is beyond the scope of this class description to explain the complete details of the ID3v2.4 tag.  However, if you need more information about the format of the
 * ID3v2.4 tag, please visit the official <a href="http://id3.org/id3v2.4.0-structure">ID3v2.4 website</a>.
 * </p>
 * </p class="beaglebuddy">
 * </p>
 * @see <a href="http://id3.org/id3v2.4.0-structure"                           target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"                             target="_blank">wikipedia history of ID3 tags</a>
 * @see <a href="http://www.gigamonkeys.com/book/practical-an-id3-parser.html" target="_blank">Gigamonkeys ID3 Tag parser</a>
 */
public class ID3v24Tag
{
   // class mnemonics
                                            /** amount of padding (in bytes) that will be added when the ID3v2.4 tag runs out of room for frames and needs to be resized. */
   public static int DEFAULT_PADDING_SIZE = 256;

   // data members
   private ID3v24TagHeader         header;          // tag header
   private ID3v24TagExtendedHeader extendedHeader;  // optional ID3v2.4 tag extended header
   private List<ID3v24Frame> frames;                // valid frames
   private List<ID3v24Frame> invalidFrames;         // invalid frames encountered while reading in a tag - this allows the invalid frames to be skipped over (ignored)
   private byte[]            padding;               // unused padding (filled with 0x00's)
   private ID3v24TagFooter   footer;                // optional ID3v2.4 tag footer




   /**
    * default constructor.  Called when creating a new ID3v2.4 tag.
    */
   public ID3v24Tag()
   {
      header         = new ID3v24TagHeader();
      extendedHeader = null;
      frames         = new Vector<ID3v24Frame>();
      invalidFrames  = new Vector<ID3v24Frame>();
      padding        = new byte[DEFAULT_PADDING_SIZE];
      footer         = null;
   }

   /**
    * constructor.  Called when reading an existing ID3v2.4 tag from an .mp3 file.  If any invalid frames are encountered while parsing the ID3v2.4 tag, then the frames are
    * simply tagged as invalid, and are added to the list of {@link #getInvalidFrames() invalid frames}.  See the {@link ID3v24FrameHeader} class for more details on the
    * Beaglebuddy MP3 library's error handling.
    * @param inputStream        input stream mp3 file whose ID3v2.4 tag is to be read.  The input stream has already read in the ID3v2.4 tag header id/version bytes and is pointing
    *                           to the remainder of the header.
    * @throws IOException       if there was an error while loading the ID3v2.4 tag from the .mp3 file.
    */
   public ID3v24Tag(InputStream inputStream) throws IOException
   {
      header = new ID3v24TagHeader(inputStream);

      // after the tag header comes the tag extended header
      if (header.isExtendedHeaderPresent())
         extendedHeader = new ID3v24TagExtendedHeader(inputStream);

      // after the tag extended header come the tag frames.
      // although the tag header tells you how many bytes long the tag is, that number includes the padding that can follow the frame data.
      // since the tag header doesn't tell you how many frames the tag contains, the only way to tell when you've hit the padding is to look
      // for a null byte where you'd expect a frame identifier.
                        frames        = new Vector<ID3v24Frame>();
                        invalidFrames = new Vector<ID3v24Frame>();
      int               tagSize       = ID3v24TagHeader.TAG_HEADER_SIZE + header.getTagSize();
      int               paddingSize   = 0;
      int               numBytesRead  = header.getSize() + (header.isExtendedHeaderPresent() ? extendedHeader.getSize() : 0);
      ID3v24FrameHeader frameHeader   = null;

       // read frames until we reach a null byte (0x00), which indicates that we've read the last frame and that we've now hit the padding
      while (numBytesRead < tagSize && !(frameHeader = new ID3v24FrameHeader(inputStream)).isPadding())
      {
         ID3v24Frame frame = new ID3v24Frame(frameHeader, inputStream);       // read in the frame body
         if (frame.isValid())
            frames.add(frame);
         else
            invalidFrames.add(frame);
         numBytesRead += frame.getHeader().getSize();
         numBytesRead += frame.getBody  ().getSize();
      }

      // after the tag header comes either the tag footer or the padding
      if (header.isFooterPresent())
      {
         try
         {  // try to read in and parse the footer
            footer = new ID3v24TagFooter(inputStream);
         }
         catch (ParseException ex)
         {  // if a valid footer is not found, then throw an IOException
            throw new IOException(ex.getMessage());
         }
         paddingSize = 0;
         padding     = new byte[paddingSize];
      }
      else
      {
         paddingSize = tagSize - numBytesRead;
         padding     = new byte[paddingSize];

         // read in the padding if present (which should be filled with 0x00's)
         if (numBytesRead < tagSize)
         {
            // since we read in 1 byte (0x00) to detect the beginning of the padding, subtract 1 from the number of bytes left to be read in the padding.
            byte[] buffer = new byte[paddingSize-1];
            inputStream.read(buffer);
            System.arraycopy(buffer, 0, padding, 1, buffer.length);
         }
         footer = null;
      }
   }

   /**
    * gets the ID3v2.4 tag's header.
    * @return the ID3v2.4 tag header.
    * @see #setHeader(ID3v24TagHeader)
    */
   public ID3v24TagHeader getHeader()
   {
      return header;
   }

   /**
    * sets the ID3v2.4 tag's header.
    * @param header    the ID3v2.4 tag's header.
    * @see #getHeader()
    */
   public void setHeader(ID3v24TagHeader header)
   {
      this.header = header;
   }

   /**
    * get the optional ID3v2.4 tag extended header.  This method may only be called when the {@link ID3v24TagHeader tag header's } <i>extendedHeaderPresent</i> flag is set.
    * @return the optional ID3v2.4 extended header.  If not present, then null is returned.
    * @throws IllegalStateException  if the <i>extendedHeaderPresent</i> flag is not set.
    * @see #setExtendedHeader(ID3v24TagExtendedHeader)
    * @see ID3v24TagHeader#isExtendedHeaderPresent()
    * @see ID3v24TagHeader#setExtendedHeaderPresent(boolean)
    */
   public ID3v24TagExtendedHeader getExtendedHeader() throws IllegalStateException
   {
      if (!header.isExtendedHeaderPresent())
         throw new IllegalStateException("The extended header may not be read from the ID3v2.4 tag when the extendedHeaderPresent flag is false.");

      return extendedHeader;
   }

   /**
    * sets the optional ID3v2.4 extended header as well as the {@link ID3v24TagHeader tag header}'s <i>extendedHeaderPresent</i> flag.
    * @param extendedHeader    an optional ID3v24 extended header.
    * @see #getExtendedHeader()
    * @see ID3v24TagHeader#isExtendedHeaderPresent()
    * @see ID3v24TagHeader#setExtendedHeaderPresent(boolean)
    */
   public void setExtendedHeader(ID3v24TagExtendedHeader extendedHeader)
   {
      this.extendedHeader = extendedHeader;
      header.setExtendedHeaderPresent(extendedHeader != null);
   }


   /**
    * gets the ID3v2.4 tag's footer.
    * @return the ID3v2.4 tag footer.
    * @throws IllegalStateException  if the {@link ID3v24TagHeader#isFooterPresent() footerPresent} flag is not set.
    * @see #setFooter(ID3v24TagFooter)
    */
   public ID3v24TagFooter getFooter() throws IllegalStateException
   {
      if (!header.isFooterPresent())
         throw new IllegalStateException("The footer may not be read from the ID3v2.4 tag when the footerPresent flag is false.");

      return footer;
   }

   /**
    * sets the ID3v2.4 tag's footer.
    * @param footer    the ID3v2.4 tag's footer.
    * @see #getFooter()
    */
   public void setFooter(ID3v24TagFooter footer)
   {
      this.footer = footer;
   }

   /**
    * add a default constructed ID3v2.4 frame of the specified type to the ID3v2.4 tag.
    * @param frameType   type of ID3v2.4 frame to add to the ID3v2.4 tag.
    * @return the new ID3v2.4 frame that was added to the ID3v2.4 tag.
    */
   public ID3v24Frame addFrame(FrameType frameType)
   {
      return addFrame(new ID3v24Frame(frameType));
   }

   /**
    * add the specified ID3v2.4 frame to the ID3v2.4 tag.
    * @param frame   ID3v2.4 frame to add to the ID3v2.4 tag.
    * @return the new ID3v2.4 frame that was added to the ID3v2.4 tag.
    */
   public ID3v24Frame addFrame(ID3v24Frame frame)
   {
      // do some error checking
      // TODO: is only one frame with the specified frame type allowed, etc.

      frames.add(frame);

      return frame;
   }

   /**
    * finds the first ID3v2.4 frame in the ID3v2.4 tag with the specified ID3v2.4 frame id.
    * @param frameType   type of ID3v2.4 frame to search for.
    * @return the first ID3v2.4 frame with the given ID3v2.4 frame id found in the ID3v2.4 tag, or null if no frame with the specified id can be found.
    */
   public ID3v24Frame getFrame(FrameType frameType)
   {
      for(ID3v24Frame frame : frames)
         if (frame.getHeader().getFrameType() == frameType)
            return frame;

      return null;
   }

   /**
    * gets the list of ID3v2.4 frames stored in the ID3v2.4 tag.
    * @return a list of the frames.
    * @see #setFrames(List)
    */
   public List<ID3v24Frame> getFrames()
   {
      return frames;
   }

   /**
    * finds all the ID3v2.4 frames in the ID3v2.4 tag with the specified ID3v2.4 frame id.
    * @param frameType   type of ID3v2.4 frames to retrieve from the ID3v2.4 tag.
    * @return a list of all the ID3v2.4 frames with the given ID3v2.4 frame id that were found in the ID3v2.4 tag,
    *         or an empty collection of size 0 if no ID3v2.4 frame with the specified ID3v2.4 id can be found.
    */
   public List<ID3v24Frame> getFrames(FrameType frameType)
   {
      Vector<ID3v24Frame> found = new Vector<ID3v24Frame>();

      for(ID3v24Frame frame : frames)
         if (frame.getHeader().getFrameType() == frameType)
            found.add(frame);

      return found;
   }

   /**
    * removes the first ID3v2.4 frame with the specified ID3v2.4 frame id from the ID3v2.4 tag.
    * @param frameType   type of type ID3v2.4 frame to remove from the ID3v2.4 tag.
    * @return the ID3v2.4 frame in the ID3v2.4 tag with the given ID3v2.4 frame id that was removed, or null if no ID3v2.4 frame with the specified id was found.
    */
   public ID3v24Frame removeFrame(FrameType frameType)
   {
      ID3v24Frame found = null;
      for(ID3v24Frame frame : frames)
      {
         if (frame.getHeader().getFrameType() == frameType)
         {
            found = frame;
            break;
         }
      }
      if (found != null)
        frames.remove(found);

      return found;
   }

   /**
    * removes all the ID3v2.4 frames with the specified ID3v2.4 frame id from the ID3v2.4 tag.
    * @param frameType   type of ID3v2.4 frame to remove from the ID3v2.4 tag.
    * @return a list of all the ID3v2.4 frames with the given ID3v2.4 frame id that were removed from the ID3v2.4 tag,
    *         or an empty collection of size 0 if no ID3v2.4 frames with the specified ID3v2.4 id could be found.
    */
   public List<ID3v24Frame> removeFrames(FrameType frameType)
   {
      // get a list of all the  frames of type frameId
      Vector<ID3v24Frame> found = new Vector<ID3v24Frame>();

      for(ID3v24Frame frame : frames)
      {
         if (frame.getHeader().getFrameType() == frameType)
            found.add(frame);
      }
      // remove them from the ID3v2.4 tag
      for(ID3v24Frame frame : found)
        frames.remove(frame);

      return found;
   }

   /**
    * removes all the ID3v2.4 frames from the ID3v2.4 tag.
    */
   public void removeFrames()
   {
      this.frames = new Vector<ID3v24Frame>();
   }

   /**
    * sets the list of frames and stores them in the ID3v2.4 tag.
    * @param frames   a list of ID3v2.4 frames containing information about the .mp3 song.
    * @see #getFrames()
    */
   public void setFrames(List<ID3v24Frame> frames)
   {
      this.frames = frames;
   }

   /**
    * gets the list of the invalid frames encountered while reading in the ID3v2.4 tag.
    * @return a list of the invalid frames.
    * @see #setFrames(List)
    */
   public List<ID3v24Frame> getInvalidFrames()
   {
      return invalidFrames;
   }

   /**
    * gets the amount of padding at the end of the ID3v2.4 tag.  This padding dictactes how much room remains for the frames to expand within the ID3v2.4 tag
    * before the ID3v2.4 tag has to be resized and thus the .mp3 file rewritten.
    * @return the length of the padding after the ID3v2.4 tag.
    * @see #setPadding(int)
    */
   public byte[] getPadding()
   {
      return padding;
   }

   /**
    * sets the amount of padding that should be reserved at the end of the ID3v2.4 tag.
    * @param size   the size (in bytes) of the new padding.
    * @see #getPadding()
    */
   public void setPadding(int size)
   {
      padding = new byte[size];
   }

   /**
    * gets the size (in bytes) of the ID3v2.4 tag in the .mp3 file.
    * This includes the tag header, frames, padding, and footer.
    * @return the size of the ID3v2.4 tag.
    */
   public int getSize()
   {
      return header.getSize() + header.getTagSize() + (header.isFooterPresent() ? footer.getSize() : 0);
   }

   /**
    * gets the version of the ID3 tag stored in the .mp3 song which holds all of the information about the .mp3 file.
    * @return the version of the ID3 tag stored in the .mp3 song which holds all of the information about the .mp3 file.
    */
   public ID3TagVersion getVersion()
   {
      return ID3TagVersion.ID3V2_4;
   }

   /**
    * gets whether the ID3v2.4 tag has been modified since the last time it was saved.
    * @return whether the ID3v2.4 tag has been modified.
    */
   public boolean isDirty()
   {
      boolean dirty = header.isDirty();

      if (header.isExtendedHeaderPresent())
         dirty = dirty || extendedHeader.isDirty();

      if (!dirty)
      {
         // see if any of the frames have been modified
         for(ID3v24Frame frame : frames)
         {
            if (frame.isDirty())
               dirty = true;
         }
      }
      return dirty;
   }

   /**
    * if the tag's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the <i>dirty</i> flag is reset to indicate that the buffer is up to date, and the tag is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      int tagSize = 0;  // new size of the ID3v2.4 tag

      header.setBuffer();
      tagSize += header.getSize();

      if (header.isExtendedHeaderPresent())
      {
         extendedHeader.setBuffer();
         tagSize += extendedHeader.getSize();
      }

      // have all the frame's save their data to their local data buffer so that they are ready to be saved and will return an accurate size
      for(ID3v24Frame frame : frames)
      {
         frame.setBuffer();
         tagSize += frame.getSize();
      }

      if (header.isFooterPresent())
      {
         footer.setBuffer();
         tagSize += footer.getSize();
         // since a footer is present, set the padding to 0
         setPadding(0);
         // set the new tag size in the header
         header.setTagSize(tagSize - header.getSize() - footer.getSize());
         header.setBuffer();
         // set the new tag size in the footer
         footer.setTagSize(tagSize - header.getSize() - footer.getSize());
         footer.setBuffer();
      }
      else
      {
         tagSize += getPadding().length;
         // set the new tag size in the header
         header.setTagSize(tagSize - header.getSize());
         header.setBuffer();
      }
   }

   /**
    * save the ID3v2.4 tag to the .mp3 file.
    * <br/><br/>
    * @param outputStream   output stream pointing to the starting location of the ID3v2.4 tag within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag to the .mp3 file.
    */
   public void save(OutputStream outputStream) throws IOException
   {
      setBuffer();

      header.save(outputStream);

      if (header.isExtendedHeaderPresent())
         extendedHeader.save(outputStream);

      // save the frames
      for(ID3v24Frame frame : frames)
         frame.save(outputStream);

      // save the footer if present, otherwise save the padding
      if (header.isFooterPresent())
         footer.save(outputStream);
      else
         outputStream.write(padding);
   }

   /**
    * save the ID3v2.4 tag to the .mp3 file.
    * <br/><br/>
    * @param file   random access file pointing to the starting location of the ID3v2.4 tag within the .mp3 file.
    * @throws IOException   if there was an error writing the ID3v2.4 tag to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      setBuffer();

      header.save(file);

      if (header.isExtendedHeaderPresent())
         extendedHeader.save(file);

      // save the frames
      for(ID3v24Frame frame : frames)
         frame.save(file);

      // save the footer if present, otherwise save the padding
      if (header.isFooterPresent())
         footer.save(file);
      else
         file.write(padding);
   }

   /**
    * gets a string representation of the ID3v2.4 tag.
    * @return a string representation of the ID3v2.4 tag.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.4 tag\n");
      buffer.append("   num frames: " + frames.size()  + "\n" );
      buffer.append("   tag size..: " + (getSize() - padding.length) + " bytes\n");
      buffer.append("   padding...: " + padding.length + " bytes\n");
      buffer.append(header.toString() + "\n");
      if (header.isExtendedHeaderPresent())
         buffer.append(extendedHeader.toString() + "\n");
      for(ID3v24Frame frame : frames)
         buffer.append(frame.toString());
      buffer.append("ID3v2.4 tag footer: " + (footer == null ? "none" : footer));

      return buffer.toString();
   }
}
