package com.beaglebuddy.id3.v23.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

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
 * An <i>involved people list</i> frame body is associated with an ID3v2.3 {@link com.beaglebuddy.id3.enums.v23.FrameType#INVOLVED_PEOPLE_LIST IPLS} {@link com.beaglebuddy.id3.v23.ID3v23Frame frame} which is used to list the people who were involved
 * in the song.  The <i>involved people list</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Involved People List</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.v23.Encoding encoding}</td><td class="beaglebuddy">character set used to {@link #setEncoding(Encoding) encode} the <i>involved people</i> field.</td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">involvedPeople                                     </td><td class="beaglebuddy">List of people involved in making the .mp3 song.                                             </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * There may only be one <i>involved people list</i> frame in an ID3v2.3 {@link com.beaglebuddy.id3.v23.ID3v23Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v23.ID3v23Frame
 * @see <a href="http://id3.org/id3v2.3.0"         target="_blank">ID3 tag version 2.3.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v23FrameBodyInvolvedPeopleList extends ID3v23FrameBody
{
   /**
    * contains the involvement (what the person did) and the person's name.
    */
   public static class InvolvedPerson
   {
      // data members
      private String person;
      private String involvement;

      /**
       * constructor
       * @param involvement   what the person did, ie, how they were involved.
       * @param person        the involved peron's name.
       */
      public InvolvedPerson(String person, String involvement)
      {
         this.person      = person;
         this.involvement = involvement;
      }

      /**
       * get the name of the person involved.
       * @return the involved person's name.
       */
      public String getPerson()
      {
         return person;
      }

      /**
       * set the name of the person involved.
       * @param person  the name of the person involved.
       */
      public void setPerson(String person)
      {
         this.person = person;
      }

      /**
       * get the person's involvement.
       * @return the person's involvement.
       */
      public String getInvolvement()
      {
         return involvement;
      }

      /**
       * set the person's involvement.
       * @param involvement    the person's involvement.
       */
      public void setInvolvement(String involvement)
      {
         this.involvement = involvement;
      }
   }

   // data members
   private Encoding             encoding;         // character set used to encode the involved people list.
   private List<InvolvedPerson> involvedPeople;   // list of involved people



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>ISO-8859-1 encoding</li>
    *    <li>empty involved people list</li>
    * </ul>
    */
   public ID3v23FrameBodyInvolvedPeopleList()
   {
      this(Encoding.ISO_8859_1, new Vector<InvolvedPerson>());
   }

   /**
    * This constructor is called when creating a new frame.
    * @param encoding         character set used to encode the entries of a person and their involvement.
    * @param involvedPeople   list of involved people and their roles.
    */
   public ID3v23FrameBodyInvolvedPeopleList(Encoding encoding, List<InvolvedPerson> involvedPeople)
   {
      super(FrameType.INVOLVED_PEOPLE_LIST);

      setEncoding      (encoding);
      setInvolvedPeople(involvedPeople);
      dirty = true;      // values have not yet been saved to the frame body's internal byte buffer
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an involved people list frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v23FrameBodyInvolvedPeopleList(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.INVOLVED_PEOPLE_LIST, frameBodySize);
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
      nullTerminatorIndex = 1;      // start at byte 1, after the encoding byte
      involvedPeople      = new  Vector<InvolvedPerson>();

      // read in the list of involved people
      String         involvement    = null;
      String         person         = null;
      InvolvedPerson involvedPerson = null;

      while (nullTerminatorIndex < buffer.length)
      {
         nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
         involvement             = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex, encoding.getCharacterSet()).trim();
         nullTerminatorIndex     = nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
         nextNullTerminatorIndex = getNextNullTerminator(nullTerminatorIndex, encoding);
         person                  = new String(buffer, nullTerminatorIndex, nextNullTerminatorIndex, encoding.getCharacterSet()).trim();
         nullTerminatorIndex     = nextNullTerminatorIndex + encoding.getNumBytesInNullTerminator();
         involvedPerson          = new InvolvedPerson(person, involvement);
         involvedPeople.add(involvedPerson);
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * gets the character encoding of the involved people.
    * @return the character encoding of the involved people.
    * @see #setEncoding(Encoding)
    */
   public Encoding getEncoding()
   {
      return encoding;
   }

   /**
    * sets the character encoding of the involved people.
    * @param encoding    the character set used to encode the involved people. Only ISO 8859-1 and UTF-16 are allowed.
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
    * gets the list of people involved with the song.
    * @return the list of involved people.
    * @see #setInvolvedPeople(List)
    */
   public List<InvolvedPerson> getInvolvedPeople()
   {
     return involvedPeople;
   }

   /**
    * sets the list of involved people and their role.
    * @param involvedPeople   the involved people.
    * @see #getInvolvedPeople()
    */
   public void setInvolvedPeople(List<InvolvedPerson> involvedPeople)
   {
      this.involvedPeople = (involvedPeople == null ? new Vector<InvolvedPerson>() : involvedPeople);
      this.dirty          = true;
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
         // calculate how many bytes we need to store all the involved people
         int length  = 0;

         for(InvolvedPerson involvedPerson : involvedPeople)
         {
            length += stringToBytes(encoding, involvedPerson.getInvolvement()).length;
            length += stringToBytes(encoding, involvedPerson.getPerson     ()).length;
         }
         buffer = new byte[1 + length];

         buffer[0] = (byte)encoding.ordinal();
         int index = 1;
         for(InvolvedPerson involvedPerson : involvedPeople)
         {
            byte[] involvementBytes = stringToBytes(encoding, involvedPerson.getInvolvement());
            byte[] personBytes      = stringToBytes(encoding, involvedPerson.getPerson     ());
            System.arraycopy(involvementBytes, 0, buffer, index, involvementBytes.length);
            index += involvementBytes.length;
            System.arraycopy(personBytes     , 0, buffer, index, personBytes     .length);
            index += personBytes.length;
         }
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>involved people list</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: involved people\n");
      buffer.append("   bytes...: " + this.buffer.length    + " bytes\n");
      buffer.append("             " + hex(this.buffer, 13)  + "\n");
      buffer.append("   encoding: " + encoding              + "\n");
      buffer.append("   people..: " + involvedPeople.size() + "\n");
      for(InvolvedPerson involvedPerson : involvedPeople)
         buffer.append("             " + involvedPerson.getPerson() + ", " + involvedPerson.getInvolvement() + "\n");

      return buffer.toString();
   }
}
