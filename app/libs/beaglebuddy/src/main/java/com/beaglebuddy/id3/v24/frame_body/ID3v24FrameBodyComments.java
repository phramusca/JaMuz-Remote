package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;

import com.beaglebuddy.id3.enums.v24.Encoding;
import com.beaglebuddy.id3.enums.v24.FrameType;
import com.beaglebuddy.id3.enums.Language;




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
 * A <i>comments</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#COMMENTS COMM} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is intended for any kind of full text information that does not fit
 * in any other frame.  The <i>comments</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Comments Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v24.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>description</i> and <i>text</i> fields.</td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.Language language}</td><td class="beaglebuddy">{@link #setLanguage(Language) language} the comments are written in.                                      </td></tr>
 *       <tr><td class="beaglebuddy">3.</td><td class="beaglebuddy">description                                        </td><td class="beaglebuddy">short description of the comments.  This field is optional and be left empty ("").                        </td></tr>
 *       <tr><td class="beaglebuddy">4.</td><td class="beaglebuddy">text                                               </td><td class="beaglebuddy">actual comments about the .mp3 file.                                                                      </td></tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * There may be more than one <i>comment</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}, but only one with the same <i>language</i> and
 * <i>description</i> fields.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyComments extends ID3v24FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the description and the text fields.
   private Language language;      // ISO-639-2 language code.
   private String   description;   // short description of the comments.
   private String   text;          // text



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>english language</li>
    *    <li>empty description</li>
    *    <li>empty comments</li>
    * </ul>
    */
   public ID3v24FrameBodyComments()
   {
      this(Encoding.ISO_8859_1, Language.ENG, "", " ");
   }

   /**
    * This constructor is called when creating a new frame.
    * @param encoding       character set used to encode the description field.
    * @param language       ISO-639-2 language code indicating the language the comments were written in.
    * @param description    short description of the comment.
    * @param text           actual comment text.
    */
   public ID3v24FrameBodyComments(Encoding encoding, Language language, String description, String text)
   {
      super(FrameType.COMMENTS);

      setEncoding   (encoding);
      setLanguage   (language);
      setDescription(description);
      setText       (text);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to a comments frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyComments(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.COMMENTS, frameBodySize);
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
      try
      {
         setLanguage(Language.getLanguage(new String(buffer, 1, 3, Encoding.ISO_8859_1.getCharacterSet())));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to english so we can continue parsing the tag
         setLanguage(Language.ENG);
      }
      nullTerminatorIndex  = getNextNullTerminator(4, encoding);
      setDescription(new String(buffer, 4, nullTerminatorIndex-4, encoding.getCharacterSet()).trim());
      nullTerminatorIndex += encoding.getNumBytesInNullTerminator();
      setText((nullTerminatorIndex == buffer.length ? "" : new String(buffer, nullTerminatorIndex, buffer.length - nullTerminatorIndex, encoding.getCharacterSet()).trim()));
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the description and text fields.
    * @return the character encoding of the description and text fields.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the description and text fields.
    * @param encoding    the character set used to encode the description and text fields. Only ISO 8859-1 and UTF-16 are allowed.
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
    * gets the {@link com.beaglebuddy.id3.enums.Language language} the comments are written in.
    * @return the language the comments are written in.
    * @see #setLanguage(Language)
    */
   public Language getLanguage()
   {
      return language;
   }

   /**
    * sets the {@link com.beaglebuddy.id3.enums.Language language} the comments are written in.
    * @param language   {@link com.beaglebuddy.id3.enums.Language language} the comments are written in.
    * @see #getLanguage()
    */
   public void setLanguage(Language language)
   {
      if (language == null)
         throw new IllegalArgumentException("The language field in the " + frameType.getId() + " frame may not be null.");

      this.dirty    = true;
      this.language = language;
   }

   /**
    * gets the description.
    * @return the description.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description.
    * @param description   the description of the content.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * get the actual comment(s) about the .mp3 file.
    * @return the comment(s) about the .mp3 file.
    * @see #setText(String)
    */
   public String getText()
   {
      return text;
   }

   /**
    * sets the actual comment(s) about the .mp3 file.
    * @param text   comment(s) about the .mp3 file.
    * @see #getText()
    */
   public void setText(String text)
   {
      if (text == null || text.length() == 0)
         throw new IllegalArgumentException("The text field in the " + frameType.getId() + " frame may not be empty.");

      this.dirty   = true;
      this.text    = text;
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
         byte[] languageBytes    = language.getCodeBytes();
         byte[] descriptionBytes = stringToBytes(encoding, description);
         byte[] textBytes        = stringToBytes(encoding, text       );
         int    index            = 0;

         buffer = new byte[1 + languageBytes.length + descriptionBytes.length + textBytes.length];

         buffer[0] = (byte)encoding.ordinal();
         System.arraycopy(languageBytes   , 0, buffer, 1, languageBytes.length);
         index = 4;
         System.arraycopy(descriptionBytes, 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(textBytes       , 0, buffer, index, textBytes.length);
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>comments</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: comments\n");
      buffer.append("   bytes.....: " + this.buffer.length   + " bytes\n");
      buffer.append("               " + hex(this.buffer, 15) + "\n");
      buffer.append("   encoding..: " + encoding             + "\n");
      buffer.append("   language..: " + language             + "\n");
      buffer.append("   decription: " + description          + "\n");
      buffer.append("   text......: " + text                 + "\n");

      return buffer.toString();
   }
}
