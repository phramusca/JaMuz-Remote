package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.FrameType;





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
 * A <i>recommended buffer size</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#RECOMMENDED_BUFFER_SIZE RBUF} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to indicate a buffer size to
 * a server from which an .mp3 file is being streamed. Sometimes the server is aware of transmission or coding problems resulting in interruptions in the audio stream. In these cases,
 * the size of the buffer can be recommended by the server using this frame.  Embedded tags are generally not recommended since this could render unpredictable behaviour from present
 * software/hardware.
 * </p>
 * <p class="beaglebuddy">
 * For applications like streaming audio it might be an idea to embed tags into the audio stream though. If the client connects to individual connections
 * like HTTP and there is a possibility to begin every transmission with a tag, then this tag should include a <i>recommended buffer size</i> frame. If the client
 * is connected to an arbitrary point in the stream, such as radio or multicast, then the <i>recommended buffer size</i> frame should be included in every tag.
 * Every tag that is picked up after the initial/first tag is to be considered as an update of the previous one. For example, if there is a {@link ID3v23FrameBodyTextInformation TIT2}
 * frame in the first received tag and one in the second tag, then the first should be 'replaced' with the second.
 * </p>
 * <p class="beaglebuddy">
 * The <i>recommended buffer</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Recommended Buffer Size Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">bufferSize      </td><td class="beaglebuddy">the recommended size of the buffer                                                                                    </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">embeddedInfoFlag</td><td class="beaglebuddy">indicates that an ID3 tag with the maximum size described in the <i>bufferSize</i> field may occur in the audiostream.
 *                                                                                                                In such case the tag should reside between two MPEG frames, if the audio is MPEG encoded.                             </td></tr>
 *
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">offsetToNextTag </td><td class="beaglebuddy">If the position of the next tag is known, the offset is calculated from the end of tag in which this frame resides to
 *                                                                                                                the first byte of the header in the next. This field is optional and may be omitted.                                  </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * The 'buffer size' should be kept to a minimum.  There may be only one <i>recommended buffer size</i> frame in a tag.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyRecommendedBufferSize extends ID3v23FrameBody
{
   // data members
   private int     bufferSize;        // size of the buffer in bytes.
   private boolean embeddedInfoFlag;  // indicates whether an ID3 tag with the maximum size described in 'buffer size' may occur in the audiostream.
   private int     offsetToNextTag;   // optional field indicating the number of bytes until the next tag.




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>0 byte buffer size</li>
    *    <li>embedded info flag set to false</li>
    *    <li>0 offset to next tag</li>
    * </ul>
    */
   public ID3v23FrameBodyRecommendedBufferSize()
   {
      this(0, false, 0);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param bufferSize        size of the buffer in bytes.
    * @param embeddedInfoFlag  indicates whether an ID3 tag with the maximum size described in 'buffer size' may occur in the audiostream.
    * @param offsetToNextTag   optional field indicating the number of bytes until the next tag.
    */
   public ID3v23FrameBodyRecommendedBufferSize(int bufferSize, boolean embeddedInfoFlag, int offsetToNextTag)
   {
      super(FrameType.RECOMMENDED_BUFFER_SIZE);

      setBufferSize      (bufferSize);
      setEmbeddedInfoFlag(embeddedInfoFlag);
      setOffsetToNextTag (offsetToNextTag);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a recommended buffer size frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyRecommendedBufferSize(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.RECOMMENDED_BUFFER_SIZE, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      bufferSize       = ((buffer[0] & 0xFF) << 16) + ((buffer[1] & 0xFF) << 8) + (buffer[2] & 0xFF);
      embeddedInfoFlag = (buffer[3] & 0x01) == 1;
      offsetToNextTag  = buffer.length == 8 ? ((buffer[4] & 0xFF) << 24) + ((buffer[5] & 0xFF) << 16) + ((buffer[6] & 0xFF) << 8) + (buffer[7] & 0xFF) : 0;
      dirty            = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the buffer size.
    * @return size of the buffer in bytes.
    * @see #setBufferSize(int)
    */
   public int getBufferSize()
   {
      return bufferSize;
   }

   /**
    * sets the size of the buffer.
    * @param bufferSize size of the buffer in bytes.
    * @see #getBufferSize()
    */
   public void setBufferSize(int bufferSize)
   {
      if (bufferSize < 0)
         throw new IllegalArgumentException("The buffer size field in the " + frameType.getId() + " frame contains an onvalid value, " + bufferSize + ".  It must be >= 0.");

      this.bufferSize = bufferSize;
      this.dirty      = true;
   }

   /**
    * gets the flag indicating whether an ID3 tag with the maximum size described in 'Buffer size' may occur in the audiostream.
    * @return whether an ID3 tag with the maximum size described in 'Buffer size' may occur in the audiostream.
    * @see #setEmbeddedInfoFlag(boolean)
    */
   public boolean isEmbeddedInfoFlag()
   {
      return embeddedInfoFlag;
   }

   /**
    * sets whether an ID3 tag with the maximum size described in 'Buffer size' may occur in the audiostream.
    * @param embeddedInfoFlag boolean indicating whether an ID3 tag may occur in the audiostream.
    * @see #isEmbeddedInfoFlag()
    */
   public void setEmbeddedInfoFlag(boolean embeddedInfoFlag)
   {
      this.embeddedInfoFlag = embeddedInfoFlag;
      this.dirty            = true;
   }

   /**
    * gets the number of bytes until the next tag.
    * @return number of bytes until the next tag.
    * @see #setOffsetToNextTag(int)
    */
   public int getOffsetToNextTag()
   {
      return offsetToNextTag;
   }

   /**
    * sets number of bytes until the next tag.
    * @param offsetToNextTag number of bytes to the next tag.
    * @see #getOffsetToNextTag()
    */
   public void setOffsetToNextTag(int offsetToNextTag)
   {
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
         buffer = new byte[4 + 1 + (offsetToNextTag == 0 ? 0 : 4)];

         System.arraycopy(intToBytes(bufferSize), 0, buffer, 0, 4);
         buffer[4] = (byte)(embeddedInfoFlag ? 0x01 : 0x00);
         if (offsetToNextTag != 0)
            System.arraycopy(intToBytes(offsetToNextTag), 0, buffer, 5, 4);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>recommended buffer size</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: recommended buffer size\n");
      buffer.append("   bytes.............: " + this.buffer.length   + " bytes\n");
      buffer.append("                       " + hex(this.buffer, 24) + "\n");
      buffer.append("   buffer size.......: " + bufferSize           + "\n");
      buffer.append("   embedded info flag: " + embeddedInfoFlag     + "\n");
      buffer.append("   offset to next tag: " + offsetToNextTag      + "\n");

      return buffer.toString();
   }
}
