package com.beaglebuddy.id3.pojo.v24;



/**
 * <table class="logos_width">
 *    <tbody>
 *       <tr>
 *          <td                               ><img src="../../../../../resources/id3v2.gif"                     width="56"  height="54"  alt="ID3 logo"        /></td>
 *          <td class="logos_horz_align_right"><img src="../../../../../resources/beaglebuddy_software_logo.gif" width="340" height="110" alt="Beaglebuddy logo"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 * A Level is a point on an EQ curve.  It contains the following fields.
 * <p class="beaglebuddy">
 * <table class="beaglebuddy">
 *    <caption><b>Level Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">frequency</td><td class="beaglebuddy">the frequency (in hz) at which the point on EQ curve is located.
 *                                                                                                         Allowed values are from [0 hz - 32767 hz].   ex: 500 hz </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">volume   </td><td class="beaglebuddy">the amount to increase/decrease the volume in db.
 *                                                                                                         Allowed values are from [-64 db - +64 db].   ex: -2.5 db</td></tr>
 *    </tbody>
 * </table>
 * </p>
 */
public class Level
{
   // data members
   private short frequency;   // frequency (in hz) in the range 0 - 32767 hz.   note: internally, the frequency is stored in increments of 1/2   = 0.5         hz.
                              // ex: 500hz =              1,000 / 2, we would store  1,000 (0x03 E8)
                              // ex: 16khz = 16,000 hz = 32,000 / 2, we would store 32,000 (0x7D 00)
   private short volume;      // volume    (in db) in the range �64 db.         note: internally, the volume    is stored in increments of 1/512 = 0.001953125 db.
                              // ex:  2 db =  1024 / 512, so to get a 2 db volume increase, we would store  1024 (0x04 00)
                              // ex: -2 db = -1024 / 512, so to get a 2 db volume decrease, we would store -1024 (0xFC 00)



   /**
    * The default constructor is called when creating a flat level.
    * The default values used are:
    * <ul>
    *    <li>0 hz</li>
    *    <li>0 db</li>
    * </ul>
    */
   public Level()
   {
      this((short)0, (short)0);
   }

   /**
    * constructor.
    * @param frequency   frequency (in hz)
    * @param volume      volume    (in db)
    */
   public Level(short frequency, double volume)
   {
      setFrequency(frequency );
      setVolume   (volume);
   }

   /**
    * get the frequency (in hz) at which the equalization level occurs.
    * @return the frequency (in hz) at which this equalization level occurs.
    */
   public short getFrequency()
   {
      return (short)(frequency / 2);
   }

   /**
    * set the frequency (in hz) at which the equalization level occurs.
    * @param frequency   the frequency (in hz) at which the equalization level occurs.  Allowed values are from 0 hz - 32767 hz.
    * @throws IllegalArgumentException  if the specified frequency is less than 0 hz or greater than to 32767 hz.
    */
   public void setFrequency(short frequency)
   {
      if (frequency < 0 || frequency > 32767)
         throw new IllegalArgumentException("Invalid frequency, " + frequency + ".  It must be 0 hz <= frequency <= 32767 hz.");

      this.frequency = (short)(frequency * 2);
   }

   /**
    * get the volume adjustment (in db) for this frequency.
    * @return the amount of volume adjustment (in db) for this equalization level.
    */
   public double getVolume()
   {
      return volume / 512;
   }

   /**
    * set the amount of volume adjustment (in db) for this frequency.<br/>
    * Ex: +2.5 db<br/>
    * Ex: -3.0 db<br/>
    * @param volume   the amount of volume (in db) to adjust this equalization level.  Allowed values are from -64 db - +64 db.
    * @throws IllegalArgumentException  if the volume is not in the range of �64 db.
    */
   public void setVolume(double volume)
   {
      if (volume < -64.0 || volume > 64.0)
         throw new IllegalArgumentException("Invalid volume " + volume + ".  It must be in the range of �64 db.");

      this.volume = (short)Math.round(volume * 512);
   }

   /**
    * get a string representation of an ID3v2.4 equalization level.
    * @return a string representation of an ID3v2.4 equalization level.
    */
   public String toString()
   {
      return (frequency / 2) + " hz: " + (volume / 512) + " db";
   }
}
