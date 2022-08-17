package com.beaglebuddy.id3.pojo;

import com.beaglebuddy.id3.enums.EventType;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * An event code is simply an event occurring at specified time.  There are 256 predefined {@link EventType types} of events in the ID3v2.x specification.
 * <p class="beaglebuddy">
 * <table class="beaglebuddy">
 *    <caption><b>Event Code Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link EventType eventType}</td><td class="beaglebuddy">the type of event that has occurred.                                  </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">timeStamp                  </td><td class="beaglebuddy">when the event occurred.
 *                                                                                                                           See {@link com.beaglebuddy.id3.enums.TimeStampFormat} for a list of
 *                                                                                                                           the supported kinds of time stamps.                                   </td></tr>
 *    </tbody>
 * </table>
 * </p>
 * <p class="beaglebuddy">
 * Terminating the start events such as "<i>intro start</i>" is not required. You may use any of the <i>USER_DEFINED_EVENT_X</i> codes to define your own event.  You might want to synchronise your
 * music to something, like setting off an explosion on-stage, or turning on your screensaver, etc.
 * </p>
 */
public class EventCode
{
   // data members   private EventType eventType;
   private int       timeStamp;

   /**
    * constructor for specifying when a type of event occurred.
    * @param eventType   the type of event that occurred.
    * @param timeStamp   when it occurred.
    */
   public EventCode(EventType eventType, int timeStamp)
   {
      if (timeStamp < 0)
         throw new IllegalArgumentException("Invalid time stamp, " + timeStamp + ".  It must be >= 0.");

      this.eventType = eventType;
      this.timeStamp = timeStamp;
   }

   /**
    * gets the type of event that occurred.
    * @return the type of event that occurred.
    */
   public EventType getEventType()
   {
      return eventType;
   }

   /**
    * gets when event occurred.
    * @return the timestamp when the event occurred.
    */
   public int getTimeStamp()
   {
      return timeStamp;
   }
}
