package com.beaglebuddy.id3.v23.frame_body;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v23.Encoding;
import com.beaglebuddy.id3.enums.v23.FrameType;
import com.beaglebuddy.id3.enums.PictureType;
import com.beaglebuddy.id3.pojo.AttachedPicture;



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
 * An <i>attached picture</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#ATTACHED_PICTURE APIC} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to include pictures in the .mp3 file.
 * This allows you to include album covers, band pictures, band logos, etc. right in the .mp3 file.  The <i>attached picture</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Attached Picture Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}                      </td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>description</i> field.                                                                              </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy"><a href="http://www.iana.org/assignments/media-types/index.html">mimeType</a></td><td class="beaglebuddy">the MIME type and subtype for the image, such as <i>image/jpg</i> or <i>image/gif</i>. In the event that the MIME media type name is omitted, "image/" will be implied.</td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.PictureType pictureType}                    </td><td class="beaglebuddy">indicates which of the 21 pre-defined {@link #setPictureType(PictureType) picture types} the attached image is.                                                        </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">description                                                                  </td><td class="beaglebuddy">short description (maximum of 64 characters) of the picture.  This field is optional and may be left blank.                                                            </td></tr>
 *       <tr><td class="beaglebuddy">5. </td><td class="beaglebuddy">image                                                                        </td><td class="beaglebuddy">raw binary data of the actual image.                                                                                                                                   </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>attached picture</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same <i>description</i> field.
 * There may only be one <i>attached picture</i> frame with the picture type declared as picture type {@link com.beaglebuddy.id3.enums.PictureType#SMALL_ICON SMALL_ICON} and
 * {@link com.beaglebuddy.id3.enums.PictureType#OTHER_ICON OTHER_ICON} respectively.
 * </p>
 * <p>
 * example:
 * <code>
 *    <pre class="beaglebuddy">
 * import java.io.IOException;
 * import java.util.List;
 *
 * import com.beaglebuddy.mp3.MP3;
 * import com.beaglebuddy.id3.enums.PictureType;
 * import com.beaglebuddy.id3.enums.v23.FrameType;
 * import com.beaglebuddy.id3.pojo.AttachedPicture;
 * import com.beaglebuddy.id3.v23.ID3v23Frame;
 * import com.beaglebuddy.id3.v23.ID3v23Tag;
 * import com.beaglebuddy.id3.v23.frame_body.ID3v23FrameBodyAttachedPicture;
 *
 *
 * public class Pictures
 * {
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          // read in the mp3 file
 *          MP3 mp3 = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");
 *
 *          // if there was any invalid information (ie, frames) in the .mp3 file, then display the errors to the user
 *          if (mp3.hasErrors())
 *          {
 *             mp3.displayErrors(System.out);      // display the errors that were found
 *             mp3.save();                         // discard the invalid information (frames) and
 *          }                                      // save only the valid frames back to the .mp3 file
 *
 *          // get a list of all the types of pictures stored in your mp3
 *          List&lt;AttachedPicture&gt; pictures = mp3.getPictures();
 *
 *          // print out the picture's type and the optional description of the picture
 *          for(AttachedPicture picture : pictures)
 *             System.out.println("picture type = " + picture.getPictureType() + "   -    " + picture.getDescription());
 *
 *          // add a picture of the band's album cover to the ID3v2.3 tag
 *          AttachedPicture   picture   = new AttachedPicture(PictureType.FRONT_COVER, "c:\\music\\bon jovi\\livin on a prayer.album_cover.jpg");
 *          ID3v23Tag         iD3v23Tag = mp3.getID3v23Tag();
 *          ID3v23Frame       frame     = new ID3v23Frame(FrameType.ATTACHED_PICTURE, new ID3v23FrameBodyAttachedPicture(picture));
 *          List&lt;ID3v23Frame&gt; frames    = iD3v23Tag.getFrames();
 *          frames.add(frame);
 *
 *          // save the ID3v2.3 tag to the .mp3 file
 *          mp3.save();
 *
 *          // display the internal details of the mp3 file
 *          System.out.println(mp3);
 *       }
 *       catch (IOException ex)
 *       {
 *          // an error occurred reading/saving the .mp3 file.
 *          // you may try to read it again to see if the error still occurs.
 *          ex.printStackTrace();
 *       }
 *    }
 * }
 *    </pre>
 * </code>
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyAttachedPicture extends ID3v23FrameBody
{
   // class members
   private static final int DESCRIPTION_MAX_LENGTH = 64;

   // data members
   private Encoding    encoding;      // charset used to encode the description
   private String      mimeType;      // MIME type of the picture
   private PictureType pictureType;   // type of picture
   private String      description;   // description of the image.
   private byte[]      image;         // raw binary data of image

   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>image/ mime type</li>
    *    <li>other as the picture type - see {@link #setPictureType}</li>
    *    <li>"picture" as the description</li>
    *    <li>no image</li>
    * </ul>
    */
   public ID3v23FrameBodyAttachedPicture()
   {
      this(Encoding.ISO_8859_1, null, PictureType.OTHER, "picture", new byte[1]);
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param picture   the attached picture which holds all the information about the picture being added.
    */
   public ID3v23FrameBodyAttachedPicture(AttachedPicture picture)
   {
      this(Encoding.UTF_16, picture.getMimeType(), picture.getPictureType(), picture.getDescription(), picture.getImage());
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding       character set used to encode the description field.
    * @param mimeType       MIME type of the image.
    * @param pictureType    type of picture.  see {@link #setPictureType}
    * @param description    description of the image.
    * @param image          raw binary data of image.
    */
   public ID3v23FrameBodyAttachedPicture(Encoding encoding, String mimeType, PictureType pictureType, String description, byte[] image)
   {
      super(FrameType.ATTACHED_PICTURE);

      setEncoding   (encoding);
      setMimeType   (mimeType);
      setPictureType(pictureType);
      setDescription(description);
      setImage      (image);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a attached picture frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyAttachedPicture(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.ATTACHED_PICTURE, frameBodySize);
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
      nullTerminatorIndex = getNextNullTerminator(1, Encoding.ISO_8859_1);
      setMimeType(new String(buffer, 1, nullTerminatorIndex - 1, Encoding.ISO_8859_1.getCharacterSet()).trim());
      try
      {
         setPictureType(PictureType.valueOf(buffer[nullTerminatorIndex + 1]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to other so we can continue parsing the tag
         setPictureType(PictureType.OTHER);
      }
      nullTerminatorIndex += 2;
      nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
      setDescription(new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex - nullTerminatorIndex, encoding.getCharacterSet()).trim());
      nullTerminatorIndex     = nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
      image = new byte[(nullTerminatorIndex >= buffer.length ? 0 : buffer.length - nullTerminatorIndex)];
      System.arraycopy(buffer, nullTerminatorIndex, image, 0, image.length);
      setImage(image);
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the description field.
    * @return the character encoding of the description field.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the description field.
    * @param encoding    the character set used to encode the description field. Only ISO 8859-1 and UTF-16 are allowed.
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
    * gets the <a href="http://www.iana.org/assignments/media-types/index.html">mime type</a> of the attached imaged.
    * @return the mime type of the attached inmage.
    * @see #setMimeType(String)
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * sets the <a href="http://www.iana.org/assignments/media-types/index.html">mime type</a> of the attached image.
    * @param mimeType   the mime type of the attached image.
    * @see #getMimeType()
    */
   public void setMimeType(String mimeType)
   {
      this.dirty    = true;
      this.mimeType = mimeType == null || mimeType.trim().length() == 0 ? "image/" : mimeType;
   }

   /**
    * gets the type of image.
    * @return the picture type of the attached image.
    * @see #setPictureType(PictureType)
    */
   public PictureType getPictureType()
   {
      return pictureType;
   }

   /**
    * sets the picture type of the attached image.
    * There are 21 picture types as listed below:
    * <table class="beaglebuddy">
    *    <thead>
    *       <tr><th class="beaglebuddy">Picture Type</th><th class="beaglebuddy">Description</th></tr>
    *    </thead>
    *    <tbody>
    *       <tr><td class="beaglebuddy"> 0</td><td class="beaglebuddy">Other                               </td><tr>
    *       <tr><td class="beaglebuddy"> 1</td><td class="beaglebuddy">32x32 pixels 'file icon' (PNG only) </td><tr>
    *       <tr><td class="beaglebuddy"> 2</td><td class="beaglebuddy">Other file icon                     </td><tr>
    *       <tr><td class="beaglebuddy"> 3</td><td class="beaglebuddy">Cover (front)                       </td><tr>
    *       <tr><td class="beaglebuddy"> 4</td><td class="beaglebuddy">Cover (back)                        </td><tr>
    *       <tr><td class="beaglebuddy"> 5</td><td class="beaglebuddy">Leaflet page                        </td><tr>
    *       <tr><td class="beaglebuddy"> 6</td><td class="beaglebuddy">Media (e.g. lable side of CD)       </td><tr>
    *       <tr><td class="beaglebuddy"> 7</td><td class="beaglebuddy">Lead artist/lead performer/soloist  </td><tr>
    *       <tr><td class="beaglebuddy"> 8</td><td class="beaglebuddy">Artist/performer                    </td><tr>
    *       <tr><td class="beaglebuddy"> 9</td><td class="beaglebuddy">Conductor                           </td><tr>
    *       <tr><td class="beaglebuddy">10</td><td class="beaglebuddy">Band/Orchestra                      </td><tr>
    *       <tr><td class="beaglebuddy">11</td><td class="beaglebuddy">Composer                            </td><tr>
    *       <tr><td class="beaglebuddy">12</td><td class="beaglebuddy">Lyricist/text writer                </td><tr>
    *       <tr><td class="beaglebuddy">13</td><td class="beaglebuddy">Recording Location                  </td><tr>
    *       <tr><td class="beaglebuddy">14</td><td class="beaglebuddy">During recording                    </td><tr>
    *       <tr><td class="beaglebuddy">15</td><td class="beaglebuddy">During performance                  </td><tr>
    *       <tr><td class="beaglebuddy">16</td><td class="beaglebuddy">Movie/video screen capture          </td><tr>
    *       <tr><td class="beaglebuddy">17</td><td class="beaglebuddy">A bright coloured fish              </td><tr>
    *       <tr><td class="beaglebuddy">18</td><td class="beaglebuddy">Illustration                        </td><tr>
    *       <tr><td class="beaglebuddy">19</td><td class="beaglebuddy">Band/artist logotype                </td><tr>
    *       <tr><td class="beaglebuddy">20</td><td class="beaglebuddy">Publisher/Studio logotype           </td><tr>
    *    </tbody>
    * </table>
    * @param pictureType    one of the 21 defined picture types.
    * @see #getPictureType()
    */
   public void setPictureType(PictureType pictureType)
   {
      if (pictureType == null)
         throw new IllegalArgumentException("The picture type field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty       = true;
      this.pictureType = pictureType;
   }

   /**
    * gets the description of the attached image.
    * @return the description of the attached image.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the attached image.
    * @param description   the description of the attached image.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      if (description != null && description.trim().length() > DESCRIPTION_MAX_LENGTH)
         throw new IllegalArgumentException("The description field in the " + frameType.getId() + " frame may not excede " + DESCRIPTION_MAX_LENGTH + " characters in length.");

      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description.trim();
   }

   /**
    * gets the raw binary data of the attached image.
    * @return the raw binary data of the attached image.
    * @see #setImage(byte[])
    */
   public byte[] getImage()
   {
      return image;
   }

   /**
    * sets the raw binary data of the attached image.
    * @param image   the raw binary data of the attached image.
    * @throws IllegalArgumentException   if the image is null or an empty array.
    * @see #getImage()
    * @see #setImage(File)
    */
   public void setImage(byte[] image)
   {
      if (image == null || image.length == 0)
         throw new IllegalArgumentException("The image field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty = true;
      this.image = image;
   }

   /**
    * loads the image and saves the raw binary data of the image.
    * @param file   a file pointing to the image
    * @throws IOException   if the image file can not be read.
    * @see #getImage()
    * @see #setImage(byte[])
    */
   public void setImage(File file) throws IOException
   {
      byte[]          data = new byte[(int)file.length()];
      FileInputStream in   = new FileInputStream(file);

      int bytesRead = in.read(data);
      in.close();
      if (bytesRead != data.length)
         throw new IOException("An error occured while trying to read in " + data.length + " bytes from the image file " + file.getPath() + " for the " + frameType.getId() + " frame.");

      this.dirty = true;
      this.image = data;
   }

   /**
    * gets the picture stored in the frame.
    * @return the picture stored in the frame.
    * @see #setAttachedPicture(AttachedPicture)
    */
   public AttachedPicture getAttachedPicture()
   {
      return new AttachedPicture(getPictureType(),getMimeType(), getDescription(), getImage());
   }

   /**
    * sets the picture stored in the frame.
    * @param attachedPicture the picture stored in the frame.
    * @see #getAttachedPicture()
    */
   public void  setAttachedPicture(AttachedPicture attachedPicture)
   {
      if (attachedPicture == null)
         throw new IllegalArgumentException("The attached picture in a " + frameType.getId() + " frame may not be empty.");

      setMimeType   (attachedPicture.getMimeType());
      setDescription(attachedPicture.getDescription());
      setImage      (attachedPicture.getImage());
      setPictureType(attachedPicture.getPictureType());
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
         byte[] mimeTypeBytes    = stringToBytes(Encoding.ISO_8859_1, mimeType   );
         byte[] descriptionBytes = stringToBytes(encoding           , description);
         int    index            = 0;

         buffer = new byte[1 + mimeTypeBytes.length + 1 + descriptionBytes.length + image.length];

         buffer[index] = (byte)encoding.ordinal();
         index++;
         System.arraycopy(mimeTypeBytes, 0, buffer, index, mimeTypeBytes.length);
         index += mimeTypeBytes.length;
         buffer[index] = (byte)pictureType.ordinal();
         index++;
         System.arraycopy(descriptionBytes, 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(image, 0, buffer, index, image.length);
         dirty = false;    // data has already been saved
      }
   }

   /**
    * gets a string representation of the <i>attached picture</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: attached picture\n");
      buffer.append("   bytes..........: " + this.buffer.length    + " bytes\n");
//    buffer.append("                    " + hex(this.buffer, 20)  + "\n");                 // images could be tens of thousands of bytes, so don't print them out
      buffer.append("   encoding.......: " + encoding              + "\n");
      buffer.append("   image mime type: " + mimeType              + "\n");
      buffer.append("   picture type...: " + pictureType           + "\n");
      buffer.append("   description....: " + description           + "\n");
      buffer.append("   image..........: " + image.length          + " bytes\n");

      return buffer.toString();
   }
}
