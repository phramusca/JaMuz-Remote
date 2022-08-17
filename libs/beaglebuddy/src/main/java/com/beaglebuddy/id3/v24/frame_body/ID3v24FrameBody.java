package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.beaglebuddy.id3.enums.v24.Encoding;
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
 * This is the base class for all ID3v2.4 frame bodies.  Although <a href="http://id3.org/id3v2.4.0-frames">ID3v2.4</a> defines 83 different types of frames, many groups of frame bodies have the
 * same fields, and thus are implemented by a single class.  For example, there are 45 <i>text information</i> frames, and thus the corresponding frame bodies are implemented in one
 * {@link ID3v24FrameBodyTextInformation} class.  Similarly, there are 8 <i>url link</i> frames, but all are implemented using the {@link ID3v24FrameBodyURLLink} class.
 * </p>
 * <p class="beaglebuddy">
 * Each frame body has a raw, binary buffer, which is nothing more than a byte array.  This is what is read in from the .mp3 file.  This byte array holds all of the values for a
 * given frame body.  When a frame body is read in from an .mp3 file (more accurately, from the ID3v2.4 tag in the .mp3 file), this binary buffer is parsed and the values are stored in
 * the frame body's data members.  Users are then free to change the values of the frame body's data members.  When the user is finished making changes, he must call the {@link #setBuffer()}
 * method in order to write all the frame body's data members back to the binary buffer, before calling {@link #save(OutputStream)}.  However, you should not be invoking these methods,
 * as calling {@link com.beaglebuddy.mp3.MP3#save()} will handle all of this for you.
 * </p>
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBody extends ID3v24FrameBodyUtility
{
   // data members                  /** raw binary data of the frame body */
   protected byte[]    buffer;      /** indicates whether the raw byte buffer contains the same values as the the frame body's data members.  ie, whether a user has modified the frame body's values, and if so, whether have been saved back to the raw byte buffer. */
   protected boolean   dirty;       /** the frame type which is used by derived classes when creating error messages. */
   protected FrameType frameType;



   /**
    * This constructor is called when creating a new frame body.
    * @param frameType  the type of ID3v2.4 frame that is to be created.
    */
   public ID3v24FrameBody(FrameType frameType)
   {
      this.frameType = frameType;
      this.buffer    = new byte[0];
   }

   /**
    * constructor.  called when reading in an existing frame body from an .mp3 file.
    * @param inputStream    input stream pointing to a frame body in the .mp3 file.
    * @param frameType      the type of ID3v2.4 frame to create.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading in the frame body.
    */
   public ID3v24FrameBody(InputStream inputStream, FrameType frameType, int frameBodySize) throws IOException
   {
      this.buffer    = new byte[frameBodySize];
      this.frameType = frameType;

/*    int n = inputStream.read(buffer);
      if (n != buffer.length)
         throw new IOException("Error reading the body of a " + frameType.getId() + " frame.  Tried to read " + buffer.length + " bytes but got only " + n + " bytes");
*/
      int numBytesRead      = inputStream.read(buffer, 0, frameBodySize);
      int totalNumBytesRead = numBytesRead;

      while (totalNumBytesRead != frameBodySize && numBytesRead != -1)
      {
         numBytesRead       = inputStream.read(buffer, totalNumBytesRead, frameBodySize - totalNumBytesRead);
         totalNumBytesRead += numBytesRead;
      }
      if (totalNumBytesRead != frameBodySize)
         throw new IOException("Error reading the body (" + frameBodySize  + " bytes) of a " + frameType.getId() + " frame.  " + totalNumBytesRead + " bytes were read.");
   }

   /**
    * constructor.  called when reading in an existing invalid frame body from an .mp3 file.
    * this allows invalid frames to be read in and skipped over in an attempt to read in the remaining valid frames.
    * @param inputStream      input stream pointing to a frame body in the .mp3 file.
    * @param invalidFrameId   the id of the invalid frame.
    * @param frameBodySize    size (in bytes) of the frame's body.
    * @throws IOException     if there is an error while reading in the frame body.
    */
   public ID3v24FrameBody(InputStream inputStream, String invalidFrameId, int frameBodySize) throws IOException
   {
      // see if the frame is just an unsupported/unofficial frame, or if the ID3v2.3 tag is entirely corrupted and unreadable
      // this is done by seeing if the frame id contains letters, or if it is filled with binary garbage and whether the
      // frame body size that was read in was something reasonable
      if (Character.isLetter(invalidFrameId.charAt(0)) &&
          Character.isLetter(invalidFrameId.charAt(1)) &&
         (Character.isLetter(invalidFrameId.charAt(2)) || Character.isDigit(invalidFrameId.charAt(2)) || Character.isSpaceChar(invalidFrameId.charAt(2))) &&
         (Character.isLetter(invalidFrameId.charAt(3)) || Character.isDigit(invalidFrameId.charAt(3)) || Character.isSpaceChar(invalidFrameId.charAt(3)) || invalidFrameId.charAt(3) == 0x00) &&
          frameBodySize > 0 && frameBodySize < 200000)
      {
         this.buffer = new byte[frameBodySize];

         if (inputStream.read(buffer) != buffer.length)
            throw new IOException("Error reading the body of an invalid ID3v2.4 frame with id " + invalidFrameId + ".");
      }
      else
      {
         throw new IOException("The mp3 file contains a corrupt ID3v2.4 tag with an invalid frame (" + invalidFrameId + ") which is not readable.");
      }
   }

   /**
    * gets the frame body's ID3v2.4 type.
    * @return the frame body's ID3v2.4 type.
    */
   public FrameType getFrameType()
   {
      return frameType;
   }

   /**
    * finds the next null terminating character according to the character encoding in the raw data stream.
    * @param startingFrom   the index at which to start searching from in the buffer.
    * @param encoding       the character set used to encode the string (and hence determine the null terminating character).
    * <br/><br/>
    * @return the index of the next null terminator in the data.
    */
   protected int getNextNullTerminator(int startingFrom, Encoding encoding)
   {
      return getNextNullTerminator(buffer, startingFrom, encoding);
   }

   /**
    * gets the size (in bytes) of the frame body.
    * @return the size (in bytes) of the frame body.
    */
   public int getSize()
   {
      return buffer.length;
   }

   /**
    * gets whether any value(s) in the frame's body have been modified.
    * @return whether any value(s) in the frame's body have been modified.
    */
   public boolean isDirty()
   {
      return dirty;
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame body's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   public void parse() throws IllegalArgumentException
   {
      // note: this method should ideally be abstract, since there is no default implementation for it.
      //       however, when the ID3v24Frame class reads in a frame header with an invalid frame id, then it creates an instance of this class
      //       in an effort to skip over the invalid frame by simply reading in the number of bytes for the frame body.
   }

   /**
    * If the frame body's values have been modified, then resize the raw byte buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    * <p>
    * this method should be abstract.  However, the ID3v24Frame constructor needs to be able to instanitate instances of a frame body
    * in the case where it encounters an invalid frame.  For this reason, this method was made concrete, and simply throws an exception
    * if it is ever called in this base class.  However, derived classes implement this method to correctly update the raw byte buffer.
    * </p>
    */
   public void setBuffer()
   {
      // todo: make this abstract
      throw new IllegalArgumentException("The ID3v24FrameBody.setBuffer() method should be abstract and never called directly.");
   }

   /**
    * save the frame body to the .mp3 file.
    * @param outputStream   an output stream pointing to where the next frame body should be written.
    * @throws IllegalStateException   If the any of the frame body's values have been modified, then the setBuffer() method must be called prior to calling save()
    *                                 in order to save the modifications to the frame body's raw byte buffer.
    * @throws IOException             if an error occurs while writing the raw byte data in the buffer to the file.
    */
   public void save(OutputStream outputStream) throws IllegalStateException, IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 frame " + frameType.getId() + " has been modified and requires setBuffer() to be called before it can be saved.");

      outputStream.write(buffer);
   }

   /**
    * save the frame body to the .mp3 file.
    * @param file   a random access file pointing to where the next frame body should be written.
    * @throws IllegalStateException   If the any of the frame body's values have been modified, then the setBuffer() method must be called prior to calling save()
    *                                 in order to save the modifications to the frame body's raw byte buffer.
    * @throws IOException             if an error occurs while writing the raw byte data in the buffer to the file.
    */
   public void save(RandomAccessFile file) throws IllegalStateException, IOException
   {
      if (dirty)
         throw new IllegalStateException("The ID3v2.4 frame " + frameType.getId() + "  has been modified and requires setBuffer() to be called before it can be saved.");

      file.write(buffer);
   }
}
