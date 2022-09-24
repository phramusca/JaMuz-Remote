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
 * A relative volume specifies the volume level on a particular speaker channel.  It contains the following fields.
 * <p class="beaglebuddy">
 * <table class="beaglebuddy">
 *    <caption><b>Level Fields</b></caption>
 *    <thead>
 *       <tr><th class="beaglebuddy">&nbsp;</th><th class="beaglebuddy">Field</th><th class="beaglebuddy">Description</th></tr>
 *    </thead>
 *    <tbody>
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link RelativeVolume.Channel channel}                       </td><td class="beaglebuddy">the speaker channel whose volume will be adjusted.                                          </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">volume                                                       </td><td class="beaglebuddy">the amount to increase/decrease the volume in db.                                           </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">{@link RelativeVolume.PeakVolumeSize num bits in peak volume}</td><td class="beaglebuddy">the number of bits used to store the peak volume.
 *                                                                                                                                                             while the ID3v2.4 spec allows this to be any number between 0  and 255, the Beaglebuddy MP3
 *                                                                                                                                                             library only supports 0 and 32 bit peak volumes.                                            </td></tr>
 *       <tr><td class="beaglebuddy">4. </td><td class="beaglebuddy">peak volume                                                  </td><td class="beaglebuddy">the peak volume for the given speaker channel.                                              </td></tr>
 *    </tbody>
 * </table>
 * </p>
 */
public class RelativeVolume
{
   /** valid ID3v2.4 channel types */
   public enum Channel
   {                         /** other type of channel */
      OTHER          ,       /** master volume channel */
      MASTER_VOLUME  ,       /** front right   channel */
      FRONT_RIGHT    ,       /** front left    channel */
      FRONT_LEFT     ,       /** back  right   channel */
      BACK_RIGHT     ,       /** back left     channel */
      BACK_LEFT      ,       /** front center  channel */
      FRONT_CENTER   ,       /** back  center  channel */
      BACK_CENTER    ,       /** sub woofer    channel */
      SUB_WOOFER     ;

      /** @return a string representation of the Channel */
      public String toString()  {return "" + ordinal() + " - " + super.toString().toLowerCase() + " channel";}

      /**
       * convert an integral value to its corresponding enum.
       * @param channel   the integral value that is to be converted to a Channel enum.
       * @return the Channel enum whose ordinal value corresponds to the given integral value.
       * @throws IllegalArgumentException   if there is no Channel enum whose ordinal value corresponds to the given integral value.
       */
      public static Channel getChannel(int channel)
      {
         for (Channel c : Channel.values())
            if (channel == c.ordinal())
               return c;
         throw new IllegalArgumentException("Invalid speaker channel " + channel + ".  It must be 0 <= channel <= " + SUB_WOOFER + ".");
      }
   }

   /**
    * Peak Volume sizes supported by the Beaglebuddy MP3 library.
    * While the ID3v2.4 spec allows this to be any number between 0 and 255, the Beaglebuddy MP3 library only supports 0 and 32 bit peak volumes.
    */
   public enum PeakVolumeSize
   {                 /** no peak volume     */
      SIZE_NONE   ,  /** 32 bit peak volume */
      SIZE_32_BITS;

      /** @return a string representation of the peak volume size */
      public String toString()  {return "" + ordinal() + " - " + (this == SIZE_NONE ? "none" : "32 bits");}

      /**
       * convert an integral value to its corresponding enum.
       * @param size   the integral value that is to be converted to a PeakVolumeSize enum.
       * @return the PeakVolumeSize enum whose ordinal value corresponds to the given integral value.
       * @throws IllegalArgumentException   if there is no PeakVolumeSize enum whose ordinal value corresponds to the given integral value.
       */
      public static PeakVolumeSize getPeakVolumeSize(int size)
      {
         for (PeakVolumeSize s : PeakVolumeSize.values())
            if (size == s.ordinal())
               return s;
         throw new IllegalArgumentException("Invalid peak volume size " + size + ".  It must be " + SIZE_NONE.ordinal() + " or " + SIZE_32_BITS.ordinal() + ".");
      }
   }

   // data members
   private Channel        channel;        // speaker channel whose volume will be adjusted
   private short          volume;         // volume    (in db) in the range �64 db.         note: internally, the volume    is stored in increments of 1/512 = 0.001953125 db.
                                          // ex:  2 db =  1024 / 512, so to get a 2 db volume increase, we would store  1024 (0x04 00)
                                          // ex: -2 db = -1024 / 512, so to get a 2 db volume decrease, we would store -1024 (0xFC 00)
   private PeakVolumeSize peakVolumeSize; // how many bits are used to store the peak volume.  Again, what brain dead monkey thought this was a good idea?
   private int            peakVolume;     // peak volume


   /**
    * The default constructor is called when creating a relative volume.
    * The default values used are:
    * <ul>
    *    <li>other speaker channel</li>
    *    <li>0 db</li>
    *    <li>no peak volume</li>
    * </ul>
    */
   public RelativeVolume()
   {
      this(Channel.OTHER, (short)0, PeakVolumeSize.SIZE_NONE, 0);
   }

   /**
    * constructor.
    * @param channel         speaker channel
    * @param volume          volume    (in db)
    * @param peakVolumeSize  peak volume size.
    * @param peakVolume      peak volume.
    */
   public RelativeVolume(Channel channel, double volume, PeakVolumeSize peakVolumeSize, int peakVolume)
   {
      setChannel       (channel);
      setVolume        (volume);
      setPeakVolumeSize(peakVolumeSize);
      setPeakVolume    (peakVolume);
   }

   /**
    * get the speaker channel whose volume is being adjusted.
    * @return the speaker channel whose volume is being adjusted.
    * @see #setChannel(Channel)
    */
   public Channel getChannel()
   {
      return channel;
   }

   /**
    * set the speaker channel whose volume is being adjusted.
    * @param channel   the speaker channel whose volume is being adjusted.
    * @see #getChannel()
    */
   public void setChannel(Channel channel)
   {
      this.channel = channel;
   }

   /**
    * get the volume adjustment (in db) for this speaker channel.
    * @return the amount of volume adjustment (in db) for this equalization level.
    * @see #setVolume(double)
    */
   public double getVolume()
   {
      return volume / 512;
   }

   /**
    * set the amount of volume adjustment (in db) for this speaker channel..<br/>
    * Ex: +2.5 db<br/>
    * Ex: -3.0 db<br/>
    * @param volume   the amount of volume (in db) to adjust this equalization level.
    * @throws IllegalArgumentException  if the volume is not in the range of �64 db.
    * @see #getVolume()
    */
   public void setVolume(double volume)
   {
      if (volume < -64.0 || volume > 64.0)
         throw new IllegalArgumentException("Invalid volume " + volume + ".  It must be in the range of �64 db.");

      this.volume = (short)Math.round(volume * 512);
   }

   /**
    * set the number of bits used to store the peak volume.  The monkeys who designed the ID3v2.4 spec thought making the number of bits to store the peak volume was a good idea.
    * The Beaglebuddy MP3 library strongly disagrees and hence has restricted the values to either 0, which means no peak volume, or 32 bits.
    * @return the number of bits used to store the peak volume.
    * @see #setPeakVolumeSize(PeakVolumeSize)
    */
   public PeakVolumeSize getPeakVolumeSize()
   {
      return peakVolumeSize;
   }

   /**
    * set the number of bits used to store the peak volume.
    * @param peakVolumeSize   the number of bits used to store the peak volume.
    * @see #getPeakVolumeSize()
    */
   public void setPeakVolumeSize(PeakVolumeSize peakVolumeSize)
   {
      this.peakVolumeSize = peakVolumeSize;
   }

   /**
    * set the peak volume for this speaker channel.
    * @param peakVolume   the peak volume for this speaker channel.
    * @see #getPeakVolume()
    */
   public void setPeakVolume(int peakVolume)
   {
      this.peakVolume = peakVolume;
   }

   /**
    * get the peak volume for this speaker channel.
    * @return the peak volume for this speaker channel.
    * @see #setPeakVolume(int)
    */
   public int getPeakVolume()
   {
      return peakVolume;
   }

   /**
    * get a string representation of a relative volume adjustment.
    * @return a string representation of a relative volume adjustment.
    */
   public String toString()
   {
      return channel + " " + (volume / 512.0) + " db with " + (peakVolumeSize == PeakVolumeSize.SIZE_NONE ? " no peak volume" : "32 bit peak volume of " + peakVolume);
   }
}
