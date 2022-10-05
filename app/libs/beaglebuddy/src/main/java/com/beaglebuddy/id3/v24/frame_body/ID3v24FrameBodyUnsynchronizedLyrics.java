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
 * An <i>unsynchronized lyrics</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#UNSYCHRONIZED_LYRICS USLT} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to include the lyrics
 * to a song or a text transcription of other vocal activities in an .mp3 file.  This is similar to the {@link ID3v24FrameBodySynchronizedLyricsText synchronized lyrics/text} frame, but the lyrics
 * in this, the <i>unsynchronized lyrics</i> frame body, are not synchronized with the audio of the .mp3 file.  Just like the lyrics that are printed on a CD booklet, the lyrics
 * found in this frame are the entire lyrics for song.
 * The <i>unsynchronized lyrics</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Terms of Use Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v24.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>description</i> and <i>text</i> fields.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.Language language}</td><td class="beaglebuddy">language that the <i>text</i> field is written in.                                                        </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">description                                        </td><td class="beaglebuddy">description of the lyrics/text stored in the <i>text</i> field.                                           </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">text                                               </td><td class="beaglebuddy">the actual text of the lyrics.                                                                            </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may be more than one <i>unsynchronized lyrics</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}, but only one with the same
 * <i>language</i> and <i>description</i> fields.  Thus, you may have the english lyrics, the spanish lyrics, and the german lyrics all in the same tag.
 * </p>
 * <p>
 * example:
 * <code>
 *    <pre class="beaglebuddy">
 * import java.io.IOException;
 * import java.util.List;
 *
 * import com.beaglebuddy.id3.enums.Language;
 * import com.beaglebuddy.id3.enums.v24.Encoding;
 * import com.beaglebuddy.id3.enums.v24.FrameType;
 * import com.beaglebuddy.id3.v24.ID3v24Frame;
 * import com.beaglebuddy.id3.v24.ID3v24Tag;
 * import com.beaglebuddy.id3.v24.frame_body.ID3v24FrameBodyUnsynchronizedLyrics;
 * import com.beaglebuddy.mp3.MP3;
 *
 *
 * public class Lyrics
 * {
 *    public static void main(String[] args)
 *    {
 *       try
 *       {
 *          // add Bon Jovi's "Livin on a Prayer" lyrics to the .mp3
 *          MP3               mp3       = new MP3("c:\\music\\bon jovi\\livin on a prayer.mp3");
 *          ID3v24Tag         iD3v24Tag = mp3.getID3v24Tag();
 *          List&lt;ID3v24Frame&gt; frames    = iD3v24Tag.getFrames();
 *          String            lyrics    = "Tommy used to work on the docks, Union's been on strike, He's down on his luck It's tough, ....";
 *
 *          // if there was any invalid information (ie, frames) in the .mp3 file, then display the errors to the user
 *          if (mp3.hasErrors())
 *          {
 *             mp3.displayErrors(System.out);      // display the errors that were found
 *             mp3.save();                         // discard the invalid information (frames) and
 *          }                                      // save only the valid frames back to the .mp3 file
 *
 *          // add the unsynchronized lyrics frame to the ID3v2.4 tag
 *          ID3v24Frame frame = new ID3v24Frame(FrameType.UNSYCHRONIZED_LYRICS, new ID3v24FrameBodyUnsynchronizedLyrics(Encoding.UTF_16, Language.ENG, "lyrics", lyrics));
 *          frames.add(frame);
 *          // save the ID3v2.4 tag to the .mp3 file
 *          mp3.save();
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
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see ID3v24FrameBodySynchronizedLyricsText
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyUnsynchronizedLyrics extends ID3v24FrameBody
{
   // data members
   private Encoding encoding;      // charset used to encode the description and the text (lyrics)
   private Language language;      // ISO-639-2 language code
   private String   description;   // short description of the lyrics.
   private String   text;          // text




   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>UTF-16 encoding </li>
    *    <li>english language code</li>
    *    <li>no description</li>
    *    <li>no lyrics</li>
    * </ul>
    */
   public ID3v24FrameBodyUnsynchronizedLyrics()
   {
      this(Encoding.UTF_16, Language.ENG, "", " ");
   }

   /**
    * This constructor is called when creating a new frame.
    * <br/><br/>
    * @param encoding      character set used to encode the description and the lyrics.
    * @param language      ISO-639-2 {@link com.beaglebuddy.id3.enums.Language} code the lryics are written in.
    * @param description   description of the lyrics.
    * @param text          lyrics to the song in the specified language.
    */
   public ID3v24FrameBodyUnsynchronizedLyrics(Encoding encoding, Language language, String description, String text)
   {
      super(FrameType.UNSYCHRONIZED_LYRICS);

      setEncoding   (encoding);
      setLanguage   (language);
      setDescription(description);
      setText       (text);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an unsychronized lyrics/text transcription frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyUnsynchronizedLyrics(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.UNSYCHRONIZED_LYRICS, frameBodySize);
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
      nullTerminatorIndex = getNextNullTerminator(4, encoding);
      description = new String(buffer, 4, nullTerminatorIndex-4, encoding.getCharacterSet()).trim();
      nullTerminatorIndex += encoding.getNumBytesInNullTerminator();
      text = (nullTerminatorIndex == buffer.length ? "" : new String(buffer, nullTerminatorIndex, buffer.length - nullTerminatorIndex, encoding.getCharacterSet()).trim());
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the description and the text (lyrics).
    * @return the character encoding of the description and the text (lyrics).
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the description and the text (lyrics).
    * @param encoding    the character set used to encode the description and the text (lyrics).  Only ISO 8859-1 and UTF-16 are allowed.
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
    * gets the {@link com.beaglebuddy.id3.enums.Language language} the unsynchronized lyrics are written in.
    * @return the {@link com.beaglebuddy.id3.enums.Language language} the unsynchronized lyrics are written in.
    * @see #setLanguage(Language)
    */
   public Language getLanguage()
   {
      return language;
   }

   /**
    * sets the {@link com.beaglebuddy.id3.enums.Language language} the unsynchronized lyrics are written in.
    * @param language   {@link com.beaglebuddy.id3.enums.Language language} the unsynchronized lyrics are written in.
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
    * gets the description of the lyrics.
    * @return the description of the lyrics.
    * @see #setDescription(String)
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * sets the description of the lyrics.
    * @param description   the description of the lyrics.
    * @see #getDescription()
    */
   public void setDescription(String description)
   {
      this.dirty       = true;
      this.description = description == null || description.trim().length() == 0 ? "" : description;
   }

   /**
    * gets the lyrics to the song.
    * @return the lyrics.
    * @see #setText(String)
    */
   public String getText()
   {
      return text;
   }

   /**
    * sets the lyrics to the song.
    * @param text   the text of the lyrics.
    * @see #getText()
    */
   public void setText(String text)
   {
      if (text == null || text.length() == 0)
         throw new IllegalArgumentException("The text field in the " + frameType.getId() + " frame may not be empty.");

      this.text  = text;
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
         byte[] languageBytes    = language.getCodeBytes();
         byte[] descriptionBytes = stringToBytes(encoding, description);
         byte[] textBytes        = stringToBytes(encoding, text       );
         int    index            = 0;

         buffer = new byte[1 + languageBytes.length + descriptionBytes.length + textBytes.length];

         buffer[index] = (byte)encoding.ordinal();
         index = 1;
         System.arraycopy(languageBytes   , 0, buffer, index, languageBytes.length);
         index += languageBytes.length;
         System.arraycopy(descriptionBytes, 0, buffer, index, descriptionBytes.length);
         index += descriptionBytes.length;
         System.arraycopy(textBytes       , 0, buffer, index, textBytes.length);

         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>unsynchronized lyrics</i> frame body and shows the values of all its fields.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: unsychronized lyrics/text transcription\n");
      buffer.append("   bytes.....: " + this.buffer.length   + " bytes\n");
      buffer.append("               " + hex(this.buffer, 15) + "\n");
      buffer.append("   encoding..: " + encoding             + "\n");
      buffer.append("   language..: " + language             + "\n");
      buffer.append("   decription: " + description          + "\n");
      buffer.append("   text......: " + text                 + "\n");

      return buffer.toString();
   }
}
