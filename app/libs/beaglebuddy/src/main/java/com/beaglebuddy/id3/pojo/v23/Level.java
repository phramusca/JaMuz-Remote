package com.beaglebuddy.id3.pojo.v23;


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
 *       <tr><td class="beaglebuddy">1. </td><td class="beaglebuddy">{@link Direction direction}</td><td class="beaglebuddy">whether the point on the EQ curve is above or below the midpoint.        </td></tr>
 *       <tr><td class="beaglebuddy">2. </td><td class="beaglebuddy">frequency                  </td><td class="beaglebuddy">the frequency (in hz) at which the point on EQ curve is located.         </td></tr>
 *       <tr><td class="beaglebuddy">3. </td><td class="beaglebuddy">adjustment                 </td><td class="beaglebuddy">the amount above/below the midpoint where the point lies on the EQ curve.</td></tr>
 *    </tbody>
 * </table>
 * </p>
 */
public class Level
{
   /**
    * indicates whether an equalization level is above or below the midline.
    */
   public enum Direction
   {              /** eq level is below the midpoint level */
      DECREMENT,  /** eq level is above the midpoint level */
      INCREMENT;

      /**
       * convert an integral value to its corresponding direction enum.
       * @return the Direction enum corresponding to the integral value.
       * @param direction  integral value to be converted to an Direction enum.
       * @throws IllegalArgumentException   if the integral value does not correspond to a valid Direction.
       */
      public static Direction getDirection(int direction) throws IllegalArgumentException
      {
         for (Direction d : Direction.values())
            if (direction == d.ordinal())
               return d;
         throw new IllegalArgumentException("Invalid direction " + direction + ".  It must be either 0 or 1.");
      }
   }

   // data members
   private Direction direction;       // increment or decrement
   private short     frequency;       // frequency (in hz)
   private int       adjustment;      // adjustment from 0.



   /**
    * The default constructor is called when creating a flat level.
    * The default values used are:
    * <ul>
    *    <li>INCREMENT</li>
    *    <li>0 hz</li>
    *    <li>amount of adjustment set to 1</li>
    * </ul>
    */
   public Level()
   {
      this(Direction.INCREMENT, (short)0, 1);
   }

   /**
    * constructor.
    * @param direction     increment or decrement, ie, above or below the midline.
    * @param frequency     frequency (in hz)
    * @param adjustment    adjustment from 0.
    */
   public Level(Direction direction, int frequency, int adjustment)
   {
      setDirection (direction );
      setFrequency (frequency );
      setAdjustment(adjustment);
   }

   /**
    * get the direction of the equalization level, ie whether it is above or below the midline.
    * @return the {@link Direction direction of the equalization level}.
    * @see #setDirection(Direction)
    */
   public Direction getDirection()
   {
      return direction;
   }

   /**
    * sets the direction of the equalization level, ie whether it is above or below the midline.
    * @param direction   the direction of the equalization level.
    * @see #getDirection()
    */
   public void setDirection(Direction direction)
   {
      this.direction = direction;
   }

   /**
    * get the frequency (in hz) at which the equalization level occurs.
    * @return the frequency (in hz) at which the equalization level occurs.
    */
   public short getFrequency()
   {
      return frequency;
   }

   /**
    * set the frequency (in hz) at which the equalization level occurs.
    * @param frequency   the frequency (in hz) at which the equalization level occurs.
    * @throws IllegalArgumentException  if the specified frequency is less than 0 hz or greater than or equal to 32768 hz.
    */
   public void setFrequency(int frequency)
   {
      if (frequency < 0 || frequency >= 32768)
         throw new IllegalArgumentException("Invalid frequency, " + frequency + ".  It must be 0 hz <= frequency < 32768 hz.");

      this.frequency = (short)frequency;
   }

   /**
    * get the amount of adjustment for the equalization level.
    * @return the amount of adjustment for the equalization level.
    */
   public int getAdjustment()
   {
      return adjustment;
   }

   /**
    * set the amount of adjustment for the equalization level.
    * @param adjustment   the amount of adjustment for the equalization level.
    * @throws IllegalArgumentException  if the adjustment less than or equal to 0.
    */
   public void setAdjustment(int adjustment)
   {
      if (adjustment <= 0)
         throw new IllegalArgumentException("Invalid adjustment, " + adjustment + ".  It must be > 0.");

      this.adjustment = adjustment;
   }

   /**
    * get a string representation of an equalization level.
    * @return a string representation of an equalization level.
    */
   public String toString()
   {
      return frequency + " hz: " + direction + " " + adjustment;
   }
}
