package com.beaglebuddy.id3.v24.frame_body;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.beaglebuddy.id3.enums.EventType;
import com.beaglebuddy.id3.enums.v24.FrameType;
import com.beaglebuddy.id3.enums.TimeStampFormat;
import com.beaglebuddy.id3.pojo.EventCode;



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
 * An <i>event timing codes</i> frame body is associated with an ID3v2.4 {@link com.beaglebuddy.id3.enums.v24.FrameType#EVENT_TIMING_CODES ETCO} {@link com.beaglebuddy.id3.v24.ID3v24Frame frame} which is used to synchronize key events in a song.
 * The <i>event timing codes</i> frame body contains the following fields:
 * <table class="beaglebuddy">
 *    <caption><b>Event Timing Codes Frame Body Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1.</td><td class="beaglebuddy">{@link com.beaglebuddy.id3.enums.TimeStampFormat time stamp format}</td><td class="beaglebuddy">{@link #setTimeStampFormat(TimeStampFormat) units} of the <i>time stamp</i> field in the {@link com.beaglebuddy.id3.pojo.EventCode event codes} field.</td></tr>
 *       <tr><td class="beaglebuddy">2.</td><td class="beaglebuddy">eventCodes                                                         </td><td class="beaglebuddy">list of {@link com.beaglebuddy.id3.pojo.EventCode events}.                                                                                            </td></tr>
 *    </tbody>
 * </table>
 * <p class="beaglebuddy">
 * There may be only one <i>event timing codes</i> frame in an ID3v2.4 {@link com.beaglebuddy.id3.v24.ID3v24Tag tag}.
 * </p>
 * @see com.beaglebuddy.id3.v24.ID3v24Frame
 * @see <a href="http://id3.org/id3v2.4.0-frames"  target="_blank">ID3 tag version 2.4.0 standard</a>
 * @see <a href="http://en.wikipedia.org/wiki/ID3" target="_blank">wikipedia history of ID3 tags</a>
 */
public class ID3v24FrameBodyEventTimingCodes extends ID3v24FrameBody
{
   // data members
   private TimeStampFormat timeStampFormat;   // units of the event code time stamps
   private List<EventCode> eventCodes;        // list of event codes



   /**
    * The default constructor is called when creating a new frame.
    * The default values used are:
    * <ul>
    *    <li>time stamp format in milliseconds</li>
    *    <li>no event codes</li>
    * <ul>
    */
   public ID3v24FrameBodyEventTimingCodes()
   {
      this(TimeStampFormat.MS, new Vector<EventCode>());
   }

   /**
    * This constructor is called when creating a new frame.
    * @param timeStampFormat   units of the time stamp.
    * @param eventCodes        list of event codes.
    */
   public ID3v24FrameBodyEventTimingCodes(TimeStampFormat timeStampFormat, List<EventCode> eventCodes)
   {
      super(FrameType.EVENT_TIMING_CODES);

      setTimeStampFormat(timeStampFormat);
      setEventCodes     (eventCodes);
   }

   /**
    * This constructor is called when reading in an existing frame from an .mp3 file.  This is then followed by a call to the parse() method.
    * @param inputStream    input stream pointing to an event timing codes frame body in the .mp3 file.
    * @param frameBodySize  size (in bytes) of the frame's body.
    * @throws IOException   if there is an error while reading the frame body.
    */
   public ID3v24FrameBodyEventTimingCodes(InputStream inputStream, int frameBodySize) throws IOException
   {
      super(inputStream, FrameType.EVENT_TIMING_CODES, frameBodySize);
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
         setTimeStampFormat(TimeStampFormat.valueOf(buffer[0]));
      }
      catch (IllegalArgumentException ex)
      {  // ignore the bad value and set it to milliseconds so we can continue parsing the tag
         setTimeStampFormat(TimeStampFormat.MS);
      }
      eventCodes = new Vector<EventCode>();
      int index  = 1;

      while (index < buffer.length)
      {
         EventType eventType = EventType.valueOf(buffer[index]);
         int       timeStamp = ((buffer[index + 1] & 0xFF ) << 24) + ((buffer[index + 2] & 0xFF) << 16) + ((buffer[index + 3] & 0xFF) << 8) + (buffer[index + 4] & 0xFF);
         index += 5;
         eventCodes.add(new EventCode(eventType, timeStamp));
      }
      dirty = false;    // we just read in the frame info, so the frame body's internal byte buffer is up to date
   }

   /**
    * get the time stamp format.
    * @return the time stamp format.
    * @see #setTimeStampFormat(TimeStampFormat)
    */
   public TimeStampFormat getTimeStampFormat()
   {
      return timeStampFormat;
   }

   /**
    * sets the time stamp format.
    * @param timeStampFormat    the units of the time stamp.
    * @see #getTimeStampFormat()
    */
   public void setTimeStampFormat(TimeStampFormat timeStampFormat)
   {
      if (timeStampFormat == null)
         throw new IllegalArgumentException("The time stamp format field in the " + frameType.getId() + " frame may not be null.");

      this.dirty           = true;
      this.timeStampFormat = timeStampFormat;
   }

   /**
    * gets the list of events and the time when they occurred in the .mp3 song.
    * @return the list of events and the time when they occurred in the .mp3 song.
    * @see #setEventCodes(List)
    */
   public List<EventCode> getEventCodes()
   {
      return eventCodes;
   }

   /**
    * sets the list of events and the time when they occurred in the .mp3 song.
    * @param eventCodes  list of events and the time when they occurred in the .mp3 song.
    * @see #getEventCodes()
    */
   public void setEventCodes(List<EventCode> eventCodes)
   {
      this.eventCodes = eventCodes == null ? new Vector<EventCode>() : eventCodes;
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
         buffer = new byte[1 + eventCodes.size() * 5];

         buffer[0] = (byte)timeStampFormat.getValue();
         int index = 1;

         for(EventCode eventCode : eventCodes)
         {
            buffer[index] = (byte)eventCode.getEventType().ordinal();
            index++;
            System.arraycopy(intToBytes(eventCode.getTimeStamp()), 0, buffer, index, 4);   // int is 4 bytes
            index += 4;
         }
         dirty = false;
      }
   }

   /**
    * gets a string representation of the <i>event timing codes</i> frame body showing all of the frame's fields and their values.
    * @return a string representation of the frame body.
    */
   @Override
   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append("frame body: event timing codes\n");
      buffer.append("   bytes............: " + this.buffer.length   + " bytes\n");
      buffer.append("                      " + hex(this.buffer, 22) + "\n");
      buffer.append("   time stamp format: " + timeStampFormat      + "\n");
      buffer.append("   event codes......: " + eventCodes.size()    + "\n");
      for(EventCode eventCode : eventCodes)
         buffer.append(pad(24) + eventCode + "\n");

      return buffer.toString();
   }
}
