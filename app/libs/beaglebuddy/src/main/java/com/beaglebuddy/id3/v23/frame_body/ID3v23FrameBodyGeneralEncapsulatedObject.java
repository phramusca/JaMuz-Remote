package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.Encoding;
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
 * A <i>general encapsulated object</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#GENERAL_ENCAPSULATED_OBJECT GEOB} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to embed a file in the .mp3 file.
 * The file may be of any type.  This is a generalization of the {@link com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyAttachedPicture attached picture} frame which only allows image
 * files to be embedded in the .mp3 file.  The <i>general encapsulated object</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>General Encapsulated Object Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}                          </td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>filename</i> and <i>description</i> fields.   </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy"><a href="http://www.iana.org/assignments/media-types/index.html">mimeType</a></td><td class="beaglebuddy">the MIME type and subtype for the encapsulated object. This field is optional and may be left blank.             </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">filename                                                                     </td><td class="beaglebuddy">name of the file from which the encapsulated object was read from.  This field is optional and may be left blank.</td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">description                                                                  </td><td class="beaglebuddy">description of the encapsulated object.                                                                          </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">object                                                                       </td><td class="beaglebuddy">raw binary data of the encapsulated object.                                                                      </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>general encapsulated object</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same
 * <i>description</i> field.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyGeneralEncapsulatedObject extends ID3v23FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the filename and description fields.
   private String   mimeType;      // MIME type of the encapsulated object
   private String   filename;      // filename
   private String   description;   // content description
   private byte[]   object;        // raw binary data of the encapsulated object, usually a file



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>UTF-16 encoding</li>
    *    <li>empty mime type</li>
    *    <li>empty filename</li>
    *    <li>"encapsulated object" as the description</li>
    *    <li>0 byte object</li>
    * </ul>
    */
   public ID3v23FrameBodyGeneralEncapsulatedObject()
   {
      this(Encoding.UTF_16, "", "", "encapsulated object", new byte[0]);
   }

   /**
    * This constructor is called when creating a new frame.
    * @param encoding      character set used to encode the description field.
    * @param mimeType      mime type of the encapsulated object.                this field is optional, and may be empty, as is the filename field.
    * @param filename      filename that held the encapuslated object on disk.
    * @param description   description of the content.
    * @param object        raw binary data of the encapsulated object.
    */
   public ID3v23FrameBodyGeneralEncapsulatedObject(Encoding encoding, String mimeType, String filename, String description, byte[] object)
   {
      super(FrameType.GENERAL_ENCAPSULATED_OBJECT);

      setEncoding   (encoding);
      setMimeType   (mimeType);
      setFilename   (filename);
      setDescription(description);
      setObject     (object);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a general encapsulated object frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyGeneralEncapsulatedObject(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.GENERAL_ENCAPSULATED_OBJECT, frameBodySize);
   }

   /**
    * parses the raw bytes of the frame body and stores the parsed values in the frame's fields.
    * @throws IllegalArgumentException  if an invalid value is detected while parsing the frame body's raw bytes.
    */
   @Override
   public void parse() throws IllegalArgumentException
   {
      try
      {
         setEncoding(Encoding.valueOf(buffer[0]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to ISO-8859-1 so we can continue parsing the tag
         setEncoding(Encoding.ISO_8859_1);
      }
      nullTerminatorIndex     = getNextNullTerminator(1, Encoding.ISO_8859_1);
      mimeType = new String(buffer, 1, nullTerminatorIndex-1, Encoding.ISO_8859_1.getCharacterSet()).trim();
      nullTerminatorIndex++;
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
      filename                = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex - nullTerminatorIndex, encoding.getCharacterSet()).trim();
      nullTerminatorIndex     = nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
      description             = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex - nullTerminatorIndex, encoding.getCharacterSet()).trim();
      nullTerminatorIndex     = nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
      object                  = new byte[buffer.length - nullTerminatorIndex];
      System.arraycopy(buffer, nullTerminatorIndex, object, 0, object.length);
      dirty                   = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the filename and desctiption fields.
    * @return the character encoding of the filename and desctiption fields.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the filename and desctiption fields.
    * @param encoding    the character set used to encode the filename and desctiption fields. Only ISO 8859-1 and UTF-16 are allowed.
    * @see #getEncoding()
    */
   public void setEncoding(Encoding encoding)
   {
      if (encoding == null)
         throw new IllegalArgumentException("The encoding field in the " + frameType.getId() + " frame may not be null.");

      this.encoding = encoding;
      this.dirty    = true;
   }

   /**
    * gets the <a href="http://www.iana.org/assignments/media-types/index.html">mime type</a> of the encapsulated object.
    * @return the mime type of the encapsulated object.
    * @see #setMimeType(String)
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * sets the <a href="http://www.iana.org/assignments/media-types/index.html">mime type</a> of the encapsulated object.  This field is optional and may be left blank.
    * @param mimeType   the mime type.
    * @see #getMimeType()
    */
   public void setMimeType(String mimeType)
   {
      this.dirty    = true;
      this.mimeType = (mimeType == null || mimeType.trim().length() == 0 ? "" : mimeType);
   }

   /**
    * gets the name of the file from which the encapsulated object was read from.
    * @return the name of the file from which the encapsulated object was read from.
    * @see #setFilename(String)
    */
   public String getFilename()
   {
      return filename;
   }

   /**
    * sets the name of the file from which the encapsulated object was read from.
    * @param filename   the name of the file from which the encapsulated object was read from.
    * @see #getFilename()
    */
   public void setFilename(String filename)
   {
      this.dirty    = true;
      this.filename = (filename == null || filename.trim().length() == 0 ? "" : filename);
   }

   /**
    * gets the description of the content.
    * @return the description of the content.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the content.
    * @param description   the description of the content.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * gets the raw binary data of the encapsulated object.
    * @return the raw binary data of the encapsulated object.
    * @see #setObject(byte[])
    */
   public byte[] getObject()
   {
      return object;
   }

   /**
    * sets the raw binary data of the encapsulated object.
    * @param object   byte[] array containing the raw binary data of the encapsulated object.
    * @see #getObject()
    */
   public void setObject(byte[] object)
   {
      this.dirty  = true;
      this.object = (object == null ? new byte[0] : object);
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
         byte[]  mimeTypeBytes    = stringToBytes(Encoding.ISO_8859_1, mimeType   );
         byte[]  filenameBytes    = stringToBytes(encoding           , filename   );
         byte[]  descriptionBytes = stringToBytes(encoding           , description);
         int     index             = 0;

         buffer = new byte[1 + mimeTypeBytes.length + filenameBytes.length + descriptionBytes.length + object.length];

         buffer[index] = (byte)encoding.ordinal();
         System.arraycopy(mimeTypeBytes   , 0, buffer, 0    , mimeTypeBytes.length);
         index = mimeTypeBytes.length;
         System.arraycopy(filenameBytes   , 0, buffer, index, filenameBytes.length);
         index += filenameBytes.length;
         System.arraycopy(descriptionBytes, 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(object          , 0, buffer, index, object.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>general encapsulated object</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: encapsulated\n");
      buffer.append("   bytes......: " + this.buffer.length   + " bytes\n");
      buffer.append("                " + hex(this.buffer, 16) + "\n");
      buffer.append("   encoding...: " + encoding             + "\n");
      buffer.append("   mime type..: " + mimeType             + "\n");
      buffer.append("   file name..: " + filename             + "\n");
      buffer.append("   description: " + description          + "\n");
      buffer.append("   object.....: " + object.length        + " bytes\n");

      return buffer.toString();
   }
}
