package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v24.FrameType;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * A <i>seek</i> frame body indicates where other tags in an mp3 file/stream can be found.
 * <table class="beaglebuddy">
 *    <caption><b>Seek Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">offset to next tag</td><td class="beaglebuddy">indicates the number of bytes from the end of this tag to the beginning of the next tag.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <br/><br/>
 * <p class="beaglebuddy">
 * There may only be one <i>seek</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"   target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3"  target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodySeek extends ID3v24FrameBody
{
   // data members
   private int offsetToNextTag;   // the number of bytes from the end of this tag to the beginning of the next tag



   /**
    * This default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>offset to next tag of 0</li>
    * </ul>
    * <br/><br/>
    */
   public ID3v24FrameBodySeek()
   {
      this(0);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param offsetToNextTag   the number of bytes from the end of this tag to the beginning of the next tag.
    */
   public ID3v24FrameBodySeek(int offsetToNextTag)
   {
      super(FrameType.SEEK_FRAME);

      setOffsetToNextTag(offsetToNextTag);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a seek frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodySeek(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.SEEK_FRAME, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      // read in the offset to the next tag
      setOffsetToNextTag(bytesToInt(buffer, 0));
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the offset to the next tag.
    * @return the number of bytes from the end of this tag to the beginning of the next tag.
    * @see #setOffsetToNextTag(int)
    */
   public int getOffsetToNextTag()
   {
      return offsetToNextTag;
   }

   /**
    * sets the offset to the next tag.
    * @param offsetToNextTag   the number of bytes from the end of this tag to the beginning of the next tag.
    * @throws IllegalArgumentException  if the offset is < 0.
    * @see #getOffsetToNextTag()
    */
   public void setOffsetToNextTag(int offsetToNextTag)
   {
      if (offsetToNextTag < 0)
         throw new IllegalArgumentException("The offset to the next tag field in the ID3v2.4 " + frameType.getId() + " frame contains an invalid value, " + offsetToNextTag + ".  It must be >= 0.");

      this.offsetToNextTag = offsetToNextTag;
      this.dirty           = true;
   }

   /**
    * If the frame body's values have been modified, then resize the raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   @Override
   public void setBuffer()
   {
      if (isDirty())
      {
         System.arraycopy(intToBytes(offsetToNextTag), 0, buffer, 0, 4);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>seek</i> frame body.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: seek\n");
      buffer.append("   bytes.............: " + this.buffer.length   + " bytes\n");
      buffer.append("                       " + hex(this.buffer, 22) + "\n");
      buffer.append("   offset to next tag: " + offsetToNextTag);

      return buffer.toString();
   }
}
