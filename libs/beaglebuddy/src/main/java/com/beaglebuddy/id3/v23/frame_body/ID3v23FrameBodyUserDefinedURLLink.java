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
 * A <i>user defined url link</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#USER_DEFINED_URL_LINK_FRAME WXXX} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to include URL links concerning the
 * audio file in a similar way to the other {@link ID3v23FrameBodyURLLink url link} frames.  The <i>user defined url link</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>User Defined URL Link Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>description</i> field.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">description                                        </td><td class="beaglebuddy">description of the url.                                                                  </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">url                                                </td><td class="beaglebuddy">URL of a website.                                                                        </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>user defined url link</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}, but only one with the same <i>description</i> field.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyUserDefinedURLLink extends ID3v23FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the description
   private String   description;   // description of url
   private String   url;           // url




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>empty description</li>
    *    <li>empty URL</li>
    * <ul>
    */
   public ID3v23FrameBodyUserDefinedURLLink()
   {
      this(Encoding.ISO_8859_1, "", " ");
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding       character set used to encode the description.
    * @param description    description of the url.
    * @param url            url.
    */
   public ID3v23FrameBodyUserDefinedURLLink(Encoding encoding, String description, String url)
   {
      super(FrameType.USER_DEFINED_URL_LINK_FRAME);

      setEncoding   (encoding);
      setDescription(description);
      setURL        (url);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a user defined url link frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyUserDefinedURLLink(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.USER_DEFINED_URL_LINK_FRAME, frameBodySize);
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
      nullTerminatorIndex  = getNextNullTerminator(1, encoding);
      description          = new String(buffer, 1, nullTerminatorIndex-1, encoding.getCharacterSet()).trim();
      nullTerminatorIndex += encoding.getNumBytesInNullTerminator();
      url                  = new String(buffer, nullTerminatorIndex, buffer.length - nullTerminatorIndex, Encoding.ISO_8859_1.getCharacterSet()).trim();
      dirty                = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the description.
    * @return the character encoding of the description.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the description.
    * @param encoding    the character set used to encode the description.  Only ISO 8859-1 and UTF-16 are allowed.
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
    * gets the description of the url.
    * @return the description url.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the url.
    * @param description   the description of the url.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * gets the URL.
    * @return the url.
    * @see #setURL(String)
    */
   public String getURL()
   {
      return url;
   }

   /**
    * sets the URL.
    * @param url   the url.
    * @see #getURL()
    */
   public void setURL(String url)
   {
      if (url == null || url.length() == 0)
         throw new IllegalArgumentException("The url field in the " + frameType.getId() + " frame may not be empty.");

      this.url   = url;
      this.dirty = true;
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
         byte[]  descriptionBytes = stringToBytes(encoding           , description);
         byte[]  urlBytes         = stringToBytes(Encoding.ISO_8859_1, url        );

         buffer = new byte[1 + descriptionBytes.length + urlBytes.length];

         buffer[0] = (byte)encoding.ordinal();
         int index = 1;
         System.arraycopy(descriptionBytes, 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(urlBytes        , 0, buffer, index, urlBytes        .length);

         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>user defined url link</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: user defined url link\n");
      buffer.append("   encoding...: " + encoding    + "\n");
      buffer.append("   description: " + description + "\n");
      buffer.append("   url........: " + url         + "\n");

      return buffer.toString();
   }
}
