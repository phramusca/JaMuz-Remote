package com.beaglebuddy.id3.v23;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.v23.frame_body.*;





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
 * This class provides methods for reading and writing ID3v2.3 frames.  As the .mp3 file format shows below, an {@link com.beaglebuddy.id3.v23.ID3v23Tag ID3v2.3 tag} is
 * comprised mostly of <i>frames</i>.  It is in these <i>frames</i> where the actual information about the .mp3 file is contained.  Information such as the song title, the
 * track number, the lyrics of the song, the band who recorded the song, etc. are stored in the frames.  The <a href="http://id3.org/id3v2.3.0">ID3v2.3</a> Specification
 * defines 74 different {@link com.beaglebuddy.id3.enums.v23.FrameType types of frames}.  A frame consists of a {@link ID3v23FrameHeader header} and a
 * {@link com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBody body}. The frame header contains some information about the frame body (such as its size, type, etc.) and as
 * such does not vary much between the different frame types.  This is in contrast to the {@link com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBody body}, which contains
 * the actual information (song title, etc.) and hence varies widely between the different types of frames.
 * </p>
 * <p>
 * <img src="../../../../resources/mp3_format_ID3v2.3.gif" height="550" width="330" alt="mp3 format with an ID3v2.3 tag" usemap="#id3v23_map"/>
 * <map name="id3v23_map">
 *    <area shape="rect" coords=" 230, 145, 300, 165" href="ID3v23Tag.html"                                alt="ID3v2.3 Tag"/>
 *    <area shape="rect" coords="   6,  42, 198,  75" href="ID3v23TagHeader.html"                          alt="ID3v2.3 Tag Header"/>
 *    <area shape="rect" coords="   6,  76, 198, 108" href="ID3v23TagExtendedHeader.html"                  alt="ID3v2.3 Tag Extended Header"/>
 *    <area shape="rect" coords="   6, 109, 198, 250" href="ID3v23Frame.html"                              alt="ID3v2.3 Frame""/>
 *    <area shape="rect" coords="   6, 251, 198, 286" href="../../mp3/MP3Base.html#setID3v2xPadding(int)"  alt="ID3v2.3 Padding"/>
 *    <area shape="rect" coords="   6, 287, 198, 374" href="../../mpeg/MPEGFrame.html"                     alt="MPEG Audio Frame"/>
 *    <area shape="rect" coords="   6, 375, 198, 425" href="../../lyrics3/Lyrics3v2Tag.html"               alt="Lyrics3 Tag"/>
 *    <area shape="rect" coords="   6, 426, 198, 479" href="../../ape/APETag.html"                         alt="APE Tag"/>
 *    <area shape="rect" coords="   6, 480, 198, 530" href="../v1/ID3v1Tag.html"                           alt="ID3V1 Tag"/>
 * </map>
 * </p>
 * <p class="beaglebuddy">
 * A very common scenario is to read in an existing .mp3 file from your hard drive, and then to view the information about it or to modify the information.
 * Reading in an existing .mp3 file raises the question of what to do when an error occurs.
 * There are three types of errors that can happen and they are listed below, as well as how the Beaglebuddy MP3 library handles them.
 * <table class="beaglebuddy">
 *    <caption><b>Potential Errors When Reading in an .mp3 File</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Error Type</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">IO Exception                                                                  </td><td class="beaglebuddy">This error is pretty straight forward and the easiest to explain.  Simply put, an error occurrs while trying to read in the .mp3 file from disk.
 *                                                                                                                                                                              Possible causes include bad sectors on your hard drive, the .mp3 file itself is corrupt (maybe some bytes were lost when you downloaded it/ripped
 *                                                                                                                                                                              it), etc.  There is not much that can be done about this, other than to try reading it in again.  If that doesn't work, you might need to
 *                                                                                                                                                                              obtain a new copy of the .mp3 file.                                                                                                                 </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">an invalid frame id is encountered                                            </td><td class="beaglebuddy">The <a href="http://id3.org/id3v2.3.0">ID3v2.3 Specification</a> defines 74 frame types, each with its own unique 4 character
 *                                                                                                                                                                              {@link com.beaglebuddy.id3.enums.v23.FrameType frame id}.  If the Beaglebuddy MP3 library encounters a frame with an invalid frame id, then it tries
 *                                                                                                                                                                              to skip over the invalid frame, and to continue reading in the rest of the .mp3 file in the hope that the invalid frame was just an isolated bad
 *                                                                                                                                                                              frame and that the remainder of the frames will be valid.  From tests run at Beaglebuddy on about 12,000 mp3 files, this has been the case, and
 *                                                                                                                                                                              the rest of the .mp3 file can be read in, the bad frame discarded, and the .mp3 file saved so that it contains only valid ID3v2.3 frames.           </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">a valid frame id was read, but it contained an invalid value in the frame body</td><td class="beaglebuddy">This is the most common type of error that you will encounter.  In this case, we have one of the valid 74 frame types, but the frame contains some
 *                                                                                                                                                                              invalid field value(s).  Examples of invalid field values include an invalid {@link com.beaglebuddy.id3.enums.Language language code}, an invalid
 *                                                                                                                                                                              {@link com.beaglebuddy.id3.enums.v23.Encoding character encoding} (must be 0 or 1), an invalid {@link com.beaglebuddy.id3.enums.TimeStampFormat time
 *                                                                                                                                                                              stamp format} (must be 1 or 2), a required field is left blank (such as the comments being blank in a Comment frame), etc.  In each case, a suitable
 *                                                                                                                                                                              valid value is used to replace the invalid value.  For example, if an invalid language code is specified, then the Beaglebuddy MP3 library will
 *                                                                                                                                                                              replace the invalid value with ENG (english).  If a suitable replacement value can not be found or doesn't make sense, then the frame will be marked
 *                                                                                                                                                                              as invalid, and the Beaglebuddy MP3 library will discard the invalid frame and continue to parse the remaining frames.  For example, if the
 *                                                                                                                                                                              <i>comments</i> field in a Comments frame is empty, there is no reasonable replacement value that can be used, and since a Comments frame without a
 *                                                                                                                                                                              comment doesn't make any sense, the frame is flagged as invalid, which essentially means it is discarded, and the Beaglebuddy MP3 library skips over
 *                                                                                                                                                                              the invalid frame and moves on to the next frame to try and read the remaining frames.                                                              </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * When an invalid frame can not be fixed (as described above in error types 2 and 3), it is tagged as invalid so that the Beaglebuddy MP3 library knows to ignore it, and not to include it when the
 * user finishes his work and goes to save the .mp3 file back to disk.  This tagging is done by setting the {@link #getInvalidMessage() invalidMessage} field with a description of the error.
 * To determine if a frame is valid or not, you can simply call the {@link #isValid()} method.
 * </p>
 * @see <a href="http://www.id3.org/id3v2.3.0/"    target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23Frame
{
   // data members
   private ID3v23FrameHeader header;
   private ID3v23FrameBody   body;
   private String            invalidMessage;  // indicates whether the frame read in is a valid ID3v2.3 frame
                                              // this allows invalid frames to be read from the mp3 file and to be ignored



   /**
    * constructor. called when creating a new frame.
    * @param frameType   the ID3v2.3 type of frame to create.
    */
   public ID3v23Frame(FrameType frameType)
   {
      // most frame body classes have a default constructor.
      // however, ID3v23FrameBodyTextInformation and ID3v23FrameBodyURLLink do not, since they are used to instantiate multiple frame body type and therefore require a frame type
      // when they are being constructed.
      try
      {
         Class<?>       frameBodyClass = null;
         Constructor<?> constructor    = null;

         header         = new ID3v23FrameHeader(frameType);
         frameBodyClass = frameType.getFrameBodyClass();
         if (frameBodyClass.getName().endsWith("ID3v23FrameBodyTextInformation") || frameBodyClass.getName().endsWith("ID3v23FrameBodyURLLink"))
         {
            constructor = frameBodyClass.getConstructor(new Class<?>[] {FrameType.class});
            body        = (ID3v23FrameBody)constructor.newInstance(frameType);
         }
         else
         {
            constructor = frameBodyClass.getConstructor(new Class<?>[] {});
            body        = (ID3v23FrameBody)constructor.newInstance();
         }
      }
      catch (NoSuchMethodException ex)
      {  // this can never happen
         ex.printStackTrace();
      }
      catch (InstantiationException ex)
      {  // this can never happen
         ex.printStackTrace();
      }
      catch (IllegalAccessException ex)
      {  // this can never happen
         ex.printStackTrace();
      }
      catch (InvocationTargetException ex)
      {  // this can never happen
         ex.printStackTrace();
      }
   }

   /**
    * constructor. called when creating a new frame.
    * @param frameType    the ID3v2.3 id of the type of frame to create.
    * @param frameBody    the ID3v2.3 frame body corresponding to the frame type being created.
    * @throws IllegalArgumentException   if the class of the frame body does not correspond that specified by the <i>frameType</i>.
    *                                    For example, if you have a frameType of FrameType.ATTACHED_PICTURE and a frame body of type ID3v23FrameBodyTextInformation instead of ID3v23FrameBodyAttachedPicture.
    */
   public ID3v23Frame(FrameType frameType, ID3v23FrameBody frameBody) throws IllegalArgumentException
   {
      Class<?> frameBodyClass = frameType.getFrameBodyClass();    // make sure the frame body is of the appropriate type
      if (!frameBodyClass.equals(frameBody.getClass()))
         throw new IllegalArgumentException("Invalid frame body type, " + frameBody.getClass().getName() + " specified.  ID3v2.3 frame id requires a frame body of type " + frameBodyClass.getName() + ".");

      header  = new ID3v23FrameHeader(frameType);
      body    = frameBody;
      setBuffer();
   }

   /**
    * constructor.  called when reading in an existing frame header from an .mp3 file.
    * If a parsing error occurs while reading the frame, then the frame is tagged as being invalid by setting the {@link #getInvalidMessage() invalidMessage} field.
    * @param header   the frame's header.
    * @param  inputStream                input stream pointing to the next frame in the ID3v2.3 tag.
    * @throws IOException                if there is an error while reading the frame.
    * @throws IllegalArgumentException   if a valid frame id was read, and the bytes for the frame body were read, but an invalid value was encountered while parsing the frame body.
    *                                    Examples of such errors include an invalid language code (must be 3 character ISO-639-2 code), an invalid encoding (must be 0 or 1), an invalid
    *                                    time stamp format (must be 1 or 2), a comments frame with no comments, an attached picture frame with no picture data, etc.
    *                                    since all the bytes for the frame were read, this frame will simply be marked as being invalid, and processing of the remainder of the ID3 v2.3 tag will continue.
    */
   public ID3v23Frame(ID3v23FrameHeader header, InputStream inputStream) throws IOException, IllegalArgumentException
   {
      this.header = header;

      Class<?>       frameBodyClass = null;
      Constructor<?> constructor    = null;

      try
      {
         frameBodyClass = header.getFrameType().getFrameBodyClass();
         constructor    = header.getFrameType().getFrameBodyConstructor();
         body           = (ID3v23FrameBody)(constructor.getParameterTypes().length == 2 ? constructor.newInstance(inputStream,                        header.getFrameBodySize())
                                                                                        : constructor.newInstance(inputStream, header.getFrameType(), header.getFrameBodySize()));
         body.parse();
      }
      catch (InstantiationException ex)
      {
         // this can never happen
         ex.printStackTrace();
      }
      catch (IllegalAccessException ex)
      {
         // this can never happen
         ex.printStackTrace();
      }
      catch (InvocationTargetException ex)
      {
         // this can never happen
         ex.printStackTrace();
      }
      catch (NullPointerException ex)
      {
         // an invalid frame id encountered
         if (frameBodyClass == null)
         {
            // try and read in the bytes of the body so that we can continue parsing the remaining frames in the ID3v2.3 tag
            body           = new ID3v23FrameBody(inputStream, header.getInvalidFrameId(), header.getFrameBodySize());
            invalidMessage = "Invalid ID3v2.3 frame id " + header.getInvalidFrameId();
         }
         else
         {
            invalidMessage = ex.getMessage();
            throw ex;
         }
      }
      catch (IllegalArgumentException ex)
      {
         // the frame body had an illegal value, such as a bad language code, no comments for a comment frame, etc.
         // mark the frame as invalid an continue on
         invalidMessage = ex.getMessage();
      }
      catch (IndexOutOfBoundsException ex)
      {
         // the frame body ran out of bytes while trying to parse it's required fields
         // mark the frame as invalid an continue on
         invalidMessage = "The frame body of an ID3v2.3 frame " + header.getFrameType().getId() + " has an insufficient size, " + header.getFrameBodySize() + ".";
      }
   }

   /**
    * gets the <i>header</i> part of the frame.
    * @return the ID3v2.3 frame header;
    */
   public ID3v23FrameHeader getHeader()
   {
      return header;
   }

   /**
    * sets the ID3v2.3 frame's header.
    * @param header    the fraame's header.
    */
   public void setHeader(ID3v23FrameHeader header)
   {
      this.header = header;
   }

   /**
    * gets the <i>body</i> part of the frame.
    * @return the ID3v2.3 frame body;
    */
   public ID3v23FrameBody getBody()
   {
      return body;
   }

   /**
    * sets the ID3v2.3 frame's body to one of the 74 types defined by the <a href="http://www.id3.org">ID32.3</a> Specification.
    * @param body    the frame's body.
    */
   public void setBody(ID3v23FrameBody body)
   {
      this.body = body;
   }

   /**
    * returns a boolean indicating whether the frame has been modified.  If any part of the frame's header or body have been modified, then <i>true</i> is returned.
    * Otherwise, if no changes have been made since the last time the frame was saved, then <i>false</i> is returned.  If the frame has been modified and the <i>dirty</i> flag
    * returns true, then the frame's {@link #setBuffer()} method must be called prior to calling the {@link #save(OutputStream) save()} method.
    * @return whether the frame has been modified.
    */
   public boolean isDirty()
   {
      return header.isDirty() || body.isDirty();
   }

   /**
    * indicates whether the frame read in is a valid ID3v2.3 frame. This prevents invalid frames that are encountered while parsing an .mp3 file from interrupting the parsing of the rest
    * of the .mp3 file.  If an invalid frame is encountered while reading in an .mp3 file, then the invalid frame is simply tagged as being invalid by setting the
    * {@link #getInvalidMessage() invalidMessage} field.  Parsing then continues with the next frame.
    *  @return whether the frame id read in from the .mp3 file is a valid ID3v2.3 frame id.
    */
   public boolean isValid()
   {
      return invalidMessage == null;
   }

   /**
    * if the frame is invalid, then the reason why the frame is not valid is returned.
    * @return if the frame is invalid, the reason why the frame is not a valid ID3v2.3 frame is returned.  Otherwise, null is returned.
    */
   public String getInvalidMessage()
   {
      return invalidMessage;
   }

   /**
    * gets a description of the frame's type.
    * @return the ID3v2.3 description of the frame.
    */
   public String getDescription()
   {
      return header.getFrameType().getName();
   }

   /**
    * gets the size (in bytes) of the frame.
    * @return the size (in bytes) of the frame.
    */
   public int getSize()
   {
      return header.getSize() + body.getSize();
   }

   /**
    * saves the frame to the .mp3 file.
    * @param file   file output stream pointing to the starting location of the ID3v2.3 tag within the .mp3 file.
    * @throws IllegalStateException   if the frame has been modified ({@link #isDirty() dirty} flag is true) and the frame's {@link #setBuffer()}) method has not yet been called.
    * @throws IOException             if there was an error writing the ID3v2.3 tag to the .mp3 file.
    */
   public void save(OutputStream file) throws IOException, IllegalStateException
   {
      if (isDirty())
         throw new IllegalStateException("The frame " + header.getFrameType().getId() + "\'s save() method has been called before the frame\'s setBuffer() method.");

      header.setFrameBodySize(body.getSize());
      header.setBuffer();
      header.save(file);
      body  .save(file);

   }

   /**
    * save the frame to the .mp3 file.  Before calling save(), make sure you have called {@link #setBuffer()} so that any changes to the frame can first be written to the frame's
    * internal byte buffer.  Failure to do so will result in an IllegalStateException being thrown.
    * @param file   random access file pointing to the starting location of the ID3v2.3 tag within the .mp3 file.
    * @throws IllegalStateException   if the frame has been modified but {@link #setBuffer()} has not been called to write the changes to the frame's internal byte buffer.
    * @throws IOException             if there was an error writing the ID3v2.3 tag to the .mp3 file.
    */
   public void save(RandomAccessFile file) throws IOException
   {
      if (isDirty())
         throw new IllegalStateException("The frame " + header.getFrameType().getId() + "\'s save() method has been called before the frame\'s saveBuffer() method.");

      header.setFrameBodySize(body.getSize());
      header.setBuffer();
      header.save(file);
      body  .save(file);
   }

   /**
    * If the frame header or body's values have been modified, then resize the frame's internal raw binary buffer and store the new values there.
    * When finished, the dirty flag is reset to indicate that the internal buffer is up to date, and the frame is now ready to be saved to the .mp3 file.
    */
   public void setBuffer()
   {
      body  .setBuffer();
      header.setFrameBodySize(body.getSize());
      header.setBuffer();
   }

   /**
    * gets a string representation of the ID3v2.3 tag frame.
    * @return a string representation of the ID3v2.3 tag frame.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("ID3v2.3 frame: " + getDescription() + "\n");
      buffer.append(header);
      buffer.append(body  );

      return buffer.toString();
   }
}
